package de.eso.bytebuddy;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Writer {
  private static List<String> _events = new CopyOnWriteArrayList<>();

  static {
    System.out.println("LOOOOOOOOOOOOOOOOAD" + Writer.class.getClassLoader());

    ScheduledExecutorService executorService =
        Executors.newSingleThreadScheduledExecutor(
            r -> {
              final Thread thread = new Thread(r);
              thread.setDaemon(false); // change me
              return thread;
            });
    ((ScheduledExecutorService) executorService)
        .scheduleAtFixedRate(
            () -> {
              writeOut("LMS");
            },
            5,
            10,
            TimeUnit.SECONDS);
  }

  private Writer() {}

  /** DO NOT FUCKING CHANGE THE VISIBILITY OF THIS METHOD */
  public static void write(Type type, String s) {
    _events.add(s);
  }

  /** DO NOT FUCKING CHANGE THE VISIBILITY OF THIS METHOD */
  public static void writeOut(String fileName) {
    System.out.println(
        "Writing out some files: " + _events.stream().distinct().collect(Collectors.joining("|")));
  }

  public enum Type {
    CLASS_FOR_NAME
  }
}
