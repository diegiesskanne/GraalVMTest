package de.eso.graalvm.feature;

import com.oracle.svm.core.annotate.AutomaticFeature;
import com.oracle.svm.core.jdk.proxy.DynamicProxyRegistry;
import com.oracle.svm.hosted.FeatureImpl;
import io.vavr.collection.Array;
import org.graalvm.nativeimage.Feature;
import org.graalvm.nativeimage.ImageSingletons;

import java.util.List;

@AutomaticFeature
final class ServiceFeature implements Feature {
  @Override
  public void beforeAnalysis(BeforeAnalysisAccess beforeAnalysisAccess) {
    DynamicProxyRegistry lookup = ImageSingletons.lookup(DynamicProxyRegistry.class);
    FeatureImpl.BeforeAnalysisAccessImpl access =
        (FeatureImpl.BeforeAnalysisAccessImpl) beforeAnalysisAccess;

    Class<?> proxyHandle = access.findClassByName("de.eso.api.ProxyHandle");
    Class<?> serviceInterface = access.findClassByName("de.eso.api.Service");
    List<Class<?>> implementations = (List<Class<?>>) access.findSubclasses(serviceInterface);

    // get all impl. of Service
    // get all interfaces implemented by impl minus de.eso.dsi.Service
    // register each service interface for proxy usage with "ProxyHandle"
    Array.ofAll(implementations)
        .filter(aClass -> !aClass.isInterface())
        .flatMap(aClass -> Array.of(aClass.getInterfaces()).remove(serviceInterface))
        .forEach(aClass -> lookup.addProxyClass(aClass, proxyHandle));
  }
}
