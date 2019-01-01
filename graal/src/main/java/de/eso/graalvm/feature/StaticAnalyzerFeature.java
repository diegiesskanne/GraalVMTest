package de.eso.graalvm.feature;

import com.oracle.svm.core.annotate.AutomaticFeature;
import com.oracle.svm.core.graal.GraalFeature;
import de.eso.api.ServiceBuilder;
import jdk.vm.ci.meta.MetaAccessProvider;
import jdk.vm.ci.meta.ResolvedJavaMethod;
import org.graalvm.compiler.api.replacements.SnippetReflectionProvider;
import org.graalvm.compiler.nodes.ValueNode;
import org.graalvm.compiler.nodes.graphbuilderconf.GraphBuilderConfiguration;
import org.graalvm.compiler.nodes.graphbuilderconf.GraphBuilderContext;
import org.graalvm.compiler.nodes.graphbuilderconf.InvocationPlugin;
import org.graalvm.compiler.nodes.graphbuilderconf.InvocationPlugins;
import org.graalvm.compiler.phases.util.Providers;

@AutomaticFeature
final class StaticAnalyzerFeature implements GraalFeature {
  @Override
  public void registerNodePlugins(
      MetaAccessProvider metaAccess,
      GraphBuilderConfiguration.Plugins plugins,
      boolean analysis,
      boolean hosted) {
    if (hosted && analysis) {
      System.out.println("|| hosted Node ---------------------------------------------------->");
      plugins.appendNodePlugin(new ServiceBuilderNodePlugin());
    }
  }

  @Override
  public void registerInvocationPlugins(
      Providers providers,
      SnippetReflectionProvider snippetReflection,
      InvocationPlugins invocationPlugins,
      boolean analysis,
      boolean hosted) {

    if (true) {
      InvocationPlugins.Registration proxyRegistration =
          new InvocationPlugins.Registration(invocationPlugins, ServiceBuilder.class);
      proxyRegistration.register1(
          "serviceImpl",
          Class.class,
          new InvocationPlugin() {
            @Override
            public boolean apply(
                GraphBuilderContext b,
                ResolvedJavaMethod targetMethod,
                Receiver receiver,
                ValueNode serviceImplClassNode) {
              System.out.println("registerInvocationPlugins");

              if (serviceImplClassNode.isConstant()) {
                Class<?> clazz =
                    snippetReflection.asObject(Class.class, serviceImplClassNode.asJavaConstant());
                if (clazz != null) {}
              }

              return false;
            }
          });
    }
  }
}
