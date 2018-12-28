package de.eso.graalvm;

import com.oracle.svm.core.annotate.AutomaticFeature;
import de.eso.api.ILogger;
import org.graalvm.nativeimage.Feature;
import org.graalvm.nativeimage.RuntimeReflection;

import java.util.Map;

/**
 * This class must be generated with a Annotation-Processor, which scans all files in the classpath
 * and searches for certain patterns
 */
@AutomaticFeature
public class RuntimeReflectionRegistrationFeature implements Feature {
  public void beforeAnalysis(BeforeAnalysisAccess access) {
    RuntimeReflection.register(Map.class);
    RuntimeReflection.register(ILogger.class);
    RuntimeReflection.register(Map.class.getFields());
    RuntimeReflection.register(MyInterface.class);
    RuntimeReflection.register(MyClazz.class);
    RuntimeReflection.registerForReflectiveInstantiation(MyClazz.class);
    RuntimeReflection.register(MyInterface.class.getDeclaredMethods());
  }
}
