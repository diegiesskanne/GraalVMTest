package de.eso.bytebuddy;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Make sure this class is only loaded by fucking Bootstrap-Classloader. When calling
 * Writer#write-Method from the JVM-Agent method (Application-Classloader) the _events-List will
 * always be empty, because Writer class has been loaded by the Bootstrap-Classloader and not
 * Application-Classloader
 */
public final class Writer {
  private static List<String> _events = new CopyOnWriteArrayList<>();

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
          writeOut("LMS");
        },
        5,
        10,
        TimeUnit.SECONDS);
  }

  private Writer() {}

  /** DO NOT FUCKING CHANGE THE VISIBILITY OF THIS METHOD */
  public static void write(Type type, String className) {
    if (className.contains("jdk.")
        || className.contains("org.graalvm.")
        || className.contains("sun.")
        // || className.contains("java.")
        || className.contains("net.bytebuddy")) {
      return;
    }
    _events.add(className);
  }

  /** DO NOT FUCKING CHANGE THE VISIBILITY OF THIS METHOD */
  public static void writeOut(String fileName) {
    System.out.println(
        "Writing out some files: " + _events.stream().distinct().collect(Collectors.joining("|")));
  }

  public enum Type {
    CLASS_FOR_NAME,
    CLASS_NEW_INSTANCE,
    CLASS_GET_DECLARED_METHODS,
    CLASS_GET_FIELDS,
    CLASS_GET_METHODS,
    CLASS_GET_DECLARED_FIELDS,
    CLASS_GET_CONSTRUCTORS
  }
}
