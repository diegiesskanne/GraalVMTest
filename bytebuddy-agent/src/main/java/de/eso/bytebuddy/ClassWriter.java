package de.eso.bytebuddy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Make sure this class is only loaded by fucking Bootstrap-Classloader. When calling
 * ClassWriter#write-Method from the JVM-Agent method (Application-Classloader) the
 * _Class_events-List will always be empty, because ClassWriter class has been loaded by the
 * Bootstrap-Classloader and not Application-Classloader
 */
public final class ClassWriter {
  private static Map<String, ClassEvent> classEvents = new HashMap<>();

  // yep! nothing to see here
  static {
    ScheduledExecutorService executorService =
        Executors.newSingleThreadScheduledExecutor(
            r -> {
              Thread thread = new Thread(r);
              // make sure JVM will stay alive
              thread.setDaemon(false);
              return thread;
            });

    executorService.scheduleAtFixedRate(
        () -> {
          // write events periodically to file
          // TODO: write events to file somehow
          writeOut("LMS");
        },
        5,
        30,
        TimeUnit.SECONDS);
  }

  private ClassWriter() {}

  /** DO NOT FUCKING CHANGE THE VISIBILITY OF THIS METHOD */
  public static void write(ClassEvent classEvent) {
    String className = classEvent.className();
    if (className.contains("jdk.")
        || className.contains("org.graalvm.")
        || className.contains("sun.")
        // || className.contains("java.")
        || className.contains("net.bytebuddy")) {
      return;
    }
    classEvents.compute(
        className,
        (cName, currentValue) -> {
          if (currentValue == null) {
            return classEvent;
          } else {
            List<EventType> distinctEventTypes =
                copyDistinct(currentValue.eventTypes(), classEvent.eventTypes());
            List<String> distinctFieldNames =
                copyDistinct(currentValue.fieldNames(), classEvent.fieldNames());
            List<String> distinctMethodNames =
                copyDistinct(currentValue.methodNames(), classEvent.methodNames());

            return ImmutableClassEvent.build(
                className, distinctEventTypes, distinctFieldNames, distinctMethodNames);
          }
        });
  }

  /** DO NOT FUCKING CHANGE THE VISIBILITY OF THIS METHOD */
  public static void writeOut(String fileName) {
    String collect =
        classEvents.values().stream().map(ClassEvent::toString).collect(Collectors.joining("\n"));
    System.out.println(collect);
  }

  @SafeVarargs
  private static <T> List<T> copyDistinct(List<T> current, List<T>... appending) {
    List<T> copy = new ArrayList<>(current);
    for (List<T> app : appending) {
      copy.addAll(app);
    }
    List<T> distinct = copy.stream().distinct().collect(Collectors.toList());
    return distinct;
  }
}
