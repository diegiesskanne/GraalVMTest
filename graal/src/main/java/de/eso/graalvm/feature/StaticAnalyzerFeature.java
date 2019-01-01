package de.eso.graalvm.feature;

import com.oracle.svm.core.annotate.AutomaticFeature;
import com.oracle.svm.core.graal.GraalFeature;
import de.eso.graalvm.LoggerImpl;
import jdk.vm.ci.meta.ResolvedJavaMethod;
import org.graalvm.compiler.api.replacements.SnippetReflectionProvider;
import org.graalvm.compiler.nodes.graphbuilderconf.GraphBuilderContext;
import org.graalvm.compiler.nodes.graphbuilderconf.InvocationPlugin;
import org.graalvm.compiler.nodes.graphbuilderconf.InvocationPlugins;
import org.graalvm.compiler.phases.util.Providers;

@AutomaticFeature
final class StaticAnalyzerFeature implements GraalFeature {
  @Override
  public void registerInvocationPlugins(
      Providers providers,
      SnippetReflectionProvider snippetReflection,
      InvocationPlugins invocationPlugins,
      boolean analysis,
      boolean hosted) {

    System.out.println("---------------------------------------------------->");

    InvocationPlugins.Registration proxyRegistration =
        new InvocationPlugins.Registration(invocationPlugins, LoggerImpl.class);
    proxyRegistration.register1(
        "debug",
        String.class,
        new InvocationPlugin() {
          @Override
          public boolean apply(
              GraphBuilderContext b, ResolvedJavaMethod targetMethod, Receiver receiver) {
            System.out.println("ILOGGER INVOKE WITH ");
            return false;
          }
        });
  }
}
