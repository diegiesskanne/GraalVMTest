package de.eso.bytebuddy;

import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.build.Plugin;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;

import java.io.IOException;

public class ILoggerPlugin implements Plugin {
  @Override
  public boolean matches(TypeDescription target) {
    return true;
  }

  @Override
  public DynamicType.Builder<?> apply(
      DynamicType.Builder<?> builder,
      TypeDescription typeDescription,
      ClassFileLocator classFileLocator) {

    AsmVisitorWrapper.ForDeclaredMethods foo =
        new AsmVisitorWrapper.ForDeclaredMethods()
            .method(
                // go through all methods - dc
                ElementMatchers.named("wurst"),
                new AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper() {
                  @Override
                  public MethodVisitor wrap(
                      TypeDescription instrumentedType,
                      MethodDescription instrumentedMethod,
                      MethodVisitor methodVisitor,
                      Implementation.Context implementationContext,
                      TypePool typePool,
                      int writerFlags,
                      int readerFlags) {
                    return new MethodReplaceMethodVisitor(methodVisitor);
                  }
                });
    return builder.visit(foo);
  }

  @Override
  public void close() throws IOException {
    System.out.println("CLOSE");
  }
}
