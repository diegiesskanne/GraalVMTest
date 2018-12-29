package de.eso.bytebuddy;

import java.util.ArrayList;
import java.util.List;
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
  private static List<ClassEvent> classEvents = new ArrayList<>();

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
    ClassWriter.classEvents.add(classEvent);
  }

  /** DO NOT FUCKING CHANGE THE VISIBILITY OF THIS METHOD */
  public static void writeOut(String fileName) {
    List<ClassEvent> classEvents = new ArrayList<>(ClassWriter.classEvents);
    String collect =
        classEvents.stream().distinct().map(ClassEvent::toString).collect(Collectors.joining("\n"));
    System.out.println(collect);
  }
}
