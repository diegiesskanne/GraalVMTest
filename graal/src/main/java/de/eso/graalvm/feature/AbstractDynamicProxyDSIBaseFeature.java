package de.eso.graalvm.feature;

import com.oracle.svm.core.jdk.proxy.DynamicProxyRegistry;
import com.oracle.svm.hosted.FeatureImpl;
import io.vavr.collection.Array;
import org.graalvm.nativeimage.ImageSingletons;
import org.graalvm.nativeimage.hosted.Feature;

abstract class AbstractDynamicProxyDSIBaseFeature implements Feature {
  abstract String className();

  @Override
  public void beforeAnalysis(BeforeAnalysisAccess beforeAnalysisAccess) {
    DynamicProxyRegistry lookup = ImageSingletons.lookup(DynamicProxyRegistry.class);
    FeatureImpl.BeforeAnalysisAccessImpl access = (FeatureImpl.BeforeAnalysisAccessImpl) beforeAnalysisAccess;

    Class<?> dsiListenerInterface = access.findClassByName(className());
    Array.ofAll(access.findSubclasses(dsiListenerInterface))
        // only register interfaces
        .filter(Class::isInterface)
        .forEach(lookup::addProxyClass);
  }
}
