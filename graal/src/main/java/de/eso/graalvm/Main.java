package de.eso.graalvm;

import com.google.common.base.Preconditions;
import com.google.common.reflect.Reflection;
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
 * <p>1. Dynamic-Proxy:
 *
 * <p>- create -H:DynamicProxyConfigurationFiles JSON -> contains all classes which must be proxied!
 *
 * <p>--> All DSIs and Public-Service-Interfaces (? extends DSIListener; Public interface +
 * ProxyHandle (Provider); Public-interface + ProxyHandle (Client)
 *
 * <p>2. Reflection
 *
 * <p>- create @AutomaticFeature class implementing Feature (e.g. {@link
 * RuntimeReflectionRegistrationFeature}) via Annotation-Processing + Classpath-Scanning
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
    T proxyInstance =
        (T)
            Proxy.newProxyInstance(
                Main.class.getClassLoader(),
                new Class[] {interfaceType, ProxyHandle.class},
                new DynamicInvocationHandlerGuavaObs());
    return proxyInstance;
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
