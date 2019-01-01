package de.eso.graalvm.feature;

import com.oracle.svm.core.annotate.AutomaticFeature;
import de.eso.api.ILogger;
import de.eso.graalvm.MyClazz;
import de.eso.graalvm.MyInterface;
import de.eso.graalvm.MyServiceImpl;
import org.graalvm.nativeimage.Feature;
import org.graalvm.nativeimage.RuntimeReflection;

import java.util.Map;

/**
 * This class must be generated with a Annotation-Processor, which scans all files in the classpath
 * and searches for certain patterns
 */
@AutomaticFeature
final class RuntimeReflectionRegistrationFeature implements Feature {
  @Override
  public void beforeAnalysis(BeforeAnalysisAccess access) {
    try {
      RuntimeReflection.register(Map.class);
      RuntimeReflection.register(MyServiceImpl.class);
      RuntimeReflection.register(MyServiceImpl.class.getField("publicString"));
      RuntimeReflection.register(MyServiceImpl.class.getFields());
      RuntimeReflection.register(MyServiceImpl.class.getDeclaredFields());
      RuntimeReflection.register(ILogger.class);
      RuntimeReflection.register(Map.class.getFields());
      RuntimeReflection.register(MyInterface.class);
      RuntimeReflection.register(MyClazz.class);
      RuntimeReflection.registerForReflectiveInstantiation(MyClazz.class);
      RuntimeReflection.register(MyInterface.class.getDeclaredMethods());
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }
}
