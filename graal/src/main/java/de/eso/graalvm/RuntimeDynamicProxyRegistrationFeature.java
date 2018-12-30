package de.eso.graalvm;

import com.oracle.svm.core.annotate.AutomaticFeature;
import com.oracle.svm.core.jdk.proxy.DynamicProxyRegistry;
import org.graalvm.nativeimage.Feature;
import org.graalvm.nativeimage.ImageSingletons;

import java.util.Map;

@AutomaticFeature
public final class RuntimeDynamicProxyRegistrationFeature implements Feature {
  @Override
  public void beforeAnalysis(BeforeAnalysisAccess access) {
    DynamicProxyRegistry lookup = ImageSingletons.lookup(DynamicProxyRegistry.class);

    lookup.addProxyClass(Map.class);
    lookup.addProxyClass(MyService.class, ProxyHandle.class);
  }
}
