package de.eso.graalvm.feature;

import com.oracle.svm.core.annotate.AutomaticFeature;
import org.graalvm.nativeimage.Feature;

/**
 * Registers all interfaces extending {@code ? extends DSIBase}-interface.
 *
 * <p>TODO: Only register, when native-generation is for "Product"-usage (ServiceAdminSimProxy)
 */
@AutomaticFeature
final class DynamicProxyDSIBaseFeature extends AbstractDynamicProxyDSIBaseFeature
    implements Feature {

  @Override
  String className() {
    return "de.eso.api.DSIBase";
  }

  @Override
  public void beforeAnalysis(BeforeAnalysisAccess beforeAnalysisAccess) {
    if (Boolean.getBoolean("IsProduct")) {
      super.beforeAnalysis(beforeAnalysisAccess);
    }
  }
}
