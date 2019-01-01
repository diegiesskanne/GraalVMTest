package de.eso.graalvm.feature;

import io.vavr.collection.Array;
import jdk.vm.ci.meta.Constant;
import jdk.vm.ci.meta.ResolvedJavaMethod;
import org.graalvm.compiler.nodes.ValueNode;
import org.graalvm.compiler.nodes.graphbuilderconf.GraphBuilderContext;
import org.graalvm.compiler.nodes.graphbuilderconf.NodePlugin;

public class ServiceBuilderNodePlugin implements NodePlugin {
  @Override
  public boolean handleInvoke(GraphBuilderContext b, ResolvedJavaMethod method, ValueNode[] args) {
    if (method.getName().equals("serviceImpl")) {
      System.out.println(
          "handleInvoke "
              + method.getDeclaringClass().getName()
              + " --- "
              + method.getName()
              + " --- "
              + Array.of(args).mkString("____"));
    }

    if (method.getDeclaringClass().getName().equals("Lde/eso/api/ServiceBuilder;")
        && method.getName().equals("serviceImpl")) {
      System.out.println("JOOOOOOOP");
      System.out.println("JOOOOOOOP" + args[1].asConstant().toValueString());
      Constant constant = args[1].asConstant();

    }
    return false;
  }
}
