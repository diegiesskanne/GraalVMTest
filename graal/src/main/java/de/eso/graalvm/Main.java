package de.eso.graalvm;

import com.google.common.base.Preconditions;
import com.google.common.reflect.Reflection;
import de.eso.api.ProxyHandle;
import de.eso.dsi.DSIOnlineBase;
import de.eso.dsi.DSIOnlineListener;
import de.eso.dsi.DSIWLANListener;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.vavr.collection.Array;
import io.vavr.collection.Stream;
import io.vavr.control.Option;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * TODO's:
 *
 * <p>2. Reflection ---
 *
 * <p>- Create Class from Providers with @AutomaticFeature: {@link
 * RuntimeReflectionRegistrationFeature}
 *
 * <p>- Pitfalls:
 *
 * <p>--> Clazz#newInstance must be registered
 *
 * <p>--> Clazz<?> must be registered
 *
 * <p>--> Clazz#getDeclaredMethods must be registered
 */
public class Main {
  public static void main(String[] args) {
    MyServiceImpl myService = new MyServiceImpl();
    myService.test("X");

    LoggerImpl logger = new LoggerImpl();
    logger.debug("debug");
    logger.info("info");

    Class<MyServiceImpl> myServiceClass = MyServiceImpl.class;
    Field[] myServiceImplFields = myServiceClass.getFields();
    Constructor<?>[] constructors = myServiceClass.getConstructors();
    Method[] myServiceClassMethods = myServiceClass.getMethods();
    System.out.println(myServiceImplFields);
    System.out.println(constructors);
    System.out.println(myServiceClassMethods);

    Option<Class<?>> iLoggerClazz = forName("de.eso.api.ILogger");
    System.out.println(iLoggerClazz);

    Observable.fromIterable(Array.of(1, 2, 3))
        .subscribe(integer -> System.out.println("obs$ " + integer));

    Observable.fromArray(1, 2, 3)
        .subscribeOn(Schedulers.computation())
        .doOnNext(integer -> System.out.println(Thread.currentThread().getName() + "-" + integer))
        .observeOn(Schedulers.io())
        .doOnNext(integer -> System.out.println(Thread.currentThread().getName() + "-" + integer))
        .blockingLast();

    String simpleName = Main.class.getSimpleName();
    System.out.println("CLAZZ NAME LOGGER " + simpleName);

    Map proxyInstance = Reflection.newProxy(Map.class, new DynamicInvocationHandlerGuavaImpl());

    DSIWLANListener dsiwlanListener =
        Reflection.newProxy(DSIWLANListener.class, new DynamicInvocationHandlerGuavaImpl());
    DSIOnlineListener dsiOnlineListener =
        Reflection.newProxy(DSIOnlineListener.class, new DynamicInvocationHandlerGuavaImpl());
    dsiwlanListener.responseSetWpsKeypadPin(666);
    dsiOnlineListener.updateRole(42, 42);

    DSIOnlineBase dsiOnlineBase =
        Reflection.newProxy(DSIOnlineBase.class, new DynamicInvocationHandlerGuavaImpl());
    dsiOnlineBase.test();

    ServiceBuilderImpl.create() //
        .serviceImpl(MyServiceImpl.class)
        .asPublicApi(MyService.class)
        .build();

    Class<Map> mapClass = Map.class;
    Field[] mapClassFields = mapClass.getFields();
    System.out.println(mapClassFields);
    Constructor<?>[] mapClassConstructors = mapClass.getConstructors();
    System.out.println(mapClassConstructors);
    Method[] mapClassMethods = mapClass.getMethods();
    System.out.println(mapClassMethods);
    Field[] mapClassDeclaredFields = mapClass.getDeclaredFields();
    System.out.println(mapClassDeclaredFields);
    Method[] mapClassDeclaredMethods = mapClass.getDeclaredMethods();
    System.out.println(mapClassDeclaredMethods);

    MyService service = createProxyInterface(MyService.class);
    service.getTime().subscribe(aLong -> System.out.println("why is this working " + aLong));

    proxyInstance.put("hello", "world");
    int hello = (int) proxyInstance.get("hello"); // 42
    System.out.println(hello);

    Option<Class<MyClazz>> myClazz =
        forName("de.eso.graalvm.MyClazz").map(clazz -> (Class<MyClazz>) clazz);
    myClazz.forEach(
        myClazzClass -> {
          try {
            MyClazz clazz = myClazzClass.newInstance();
            clazz.wurst();
          } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
          }
        });

    Option<Class<?>> aClass = forName("de.eso.graalvm.MyInterface");
    System.out.println(aClass.map(Class::getSimpleName).getOrElse("FAIL"));
    aClass
        .map(Class::getDeclaredMethods)
        .forEach(
            methods -> Stream.of(methods).forEach(method -> System.out.println(method.getName())));
  }

  private static <T> T createProxyInterface(Class<T> interfaceType) {
    Preconditions.checkArgument(interfaceType.isInterface(), "must be interface");
    return (T)
        Proxy.newProxyInstance(
            Main.class.getClassLoader(),
            new Class[] {interfaceType, ProxyHandle.class},
            new DynamicInvocationHandlerGuavaObs());
  }

  private static Option<Class<?>> forName(String s) {
    Class<?> aClass = null;
    try {
      aClass = Class.forName(s);
    } catch (ClassNotFoundException e) {
      return Option.none();
    }
    return Option.of(aClass);
  }
}
