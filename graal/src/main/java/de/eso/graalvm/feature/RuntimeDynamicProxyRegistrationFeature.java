package de.eso.graalvm.feature;

import com.oracle.svm.core.annotate.AutomaticFeature;
import com.oracle.svm.core.jdk.proxy.DynamicProxyRegistry;
import org.graalvm.nativeimage.Feature;
import org.graalvm.nativeimage.ImageSingletons;

import java.util.Map;

@AutomaticFeature
final class RuntimeDynamicProxyRegistrationFeature implements Feature {
  @Override
  public void beforeAnalysis(BeforeAnalysisAccess access) {
    DynamicProxyRegistry lookup = ImageSingletons.lookup(DynamicProxyRegistry.class);

    try {
      lookup.addProxyClass(Map.class);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }
}
