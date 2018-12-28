package de.eso.bytebuddy;

import java.util.List;

public interface ClassEvent {
  String className();

  List<EventType> eventTypes();

  List<String> fieldNames();

  List<String> methodNames();
}
