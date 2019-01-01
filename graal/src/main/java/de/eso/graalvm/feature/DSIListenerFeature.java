package de.eso.graalvm.feature;

import com.oracle.svm.core.annotate.AutomaticFeature;
import com.oracle.svm.core.jdk.proxy.DynamicProxyRegistry;
import com.oracle.svm.hosted.FeatureImpl;
import org.graalvm.nativeimage.Feature;
import org.graalvm.nativeimage.ImageSingletons;

/** Register all {@code ? extends DSIListener}-interfaces for Proxy-usage */
@AutomaticFeature
final class DSIListenerFeature implements Feature {
  @Override
  public void beforeAnalysis(BeforeAnalysisAccess beforeAnalysisAccess) {
    DynamicProxyRegistry lookup = ImageSingletons.lookup(DynamicProxyRegistry.class);

    FeatureImpl.BeforeAnalysisAccessImpl access =
        (FeatureImpl.BeforeAnalysisAccessImpl) beforeAnalysisAccess;

    Class<?> dsiListenerInterface = access.findClassByName("de.eso.api.DSIListener");
    access
        .findSubclasses(dsiListenerInterface)
        .forEach(
            aClass -> {
              lookup.addProxyClass(aClass);
            });
  }
}
