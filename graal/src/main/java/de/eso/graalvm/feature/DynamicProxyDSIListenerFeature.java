package de.eso.graalvm.feature;

import com.oracle.svm.core.annotate.AutomaticFeature;
import org.graalvm.nativeimage.Feature;

/** Register all {@code ? extends DSIListener}-interfaces for Proxy-usage */
@AutomaticFeature
final class DynamicProxyDSIListenerFeature extends AbstractDynamicProxyDSIBaseFeature
    implements Feature {

  @Override
  String className() {
    return "de.eso.api.DSIListener";
  }
}
