package de.eso.bytebuddy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class ImmutableClassEvent implements ClassEvent {
  private final String className;
  private final List<EventType> eventTypes;
  private final List<String> fieldNames;
  private final List<String> methodNames;

  private ImmutableClassEvent(
      List<EventType> eventTypes,
      String className,
      List<String> fieldNames,
      List<String> methodNames) {
    Objects.requireNonNull(eventTypes);
    Objects.requireNonNull(className);
    Objects.requireNonNull(fieldNames);
    Objects.requireNonNull(methodNames);

    this.className = className;
    this.fieldNames = new ArrayList<>(fieldNames);
    this.eventTypes = new ArrayList<>(eventTypes);
    this.methodNames = new ArrayList<>(methodNames);
  }

  public static ClassEvent build(EventType eventType, String className) {
    return new ImmutableClassEvent(
        Collections.singletonList(eventType),
        className,
        Collections.emptyList(),
        Collections.emptyList());
  }

  public static ClassEvent build(
      String className,
      List<EventType> eventTypes,
      List<String> fieldNames,
      List<String> methodNames) {
    return new ImmutableClassEvent(eventTypes, className, fieldNames, methodNames);
  }

  public static ClassEvent buildFieldNames(
      EventType eventType, String className, List<String> fieldNames) {
    return new ImmutableClassEvent(
        Collections.singletonList(eventType), className, fieldNames, Collections.emptyList());
  }

  public static ClassEvent buildMethodNames(
      EventType eventType, String className, List<String> methodNames) {
    return new ImmutableClassEvent(
        Collections.singletonList(eventType), className, Collections.emptyList(), methodNames);
  }

  private static void writeArray(String fName, List<?> list, StringBuffer buffer) {
    buffer.append("\"").append(fName).append("\":");
    buffer
        .append("[")
        .append(
            list.stream().map(eT -> "\"" + eT.toString() + "\"").collect(Collectors.joining(", ")))
        .append("]");
  }

  @Override
  public String className() {
    return className;
  }

  @Override
  public List<EventType> eventTypes() {
    return Collections.unmodifiableList(eventTypes);
  }

  @Override
  public List<String> fieldNames() {
    return Collections.unmodifiableList(fieldNames);
  }

  @Override
  public List<String> methodNames() {
    return Collections.unmodifiableList(methodNames);
  }

  @Override
  public int hashCode() {
    return 1;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (!(obj instanceof ImmutableClassEvent)) return false;

    ImmutableClassEvent classEventObj = (ImmutableClassEvent) obj;
    return classEventObj.className().equals(className)
        && classEventObj.eventTypes().equals(eventTypes)
        && classEventObj.fieldNames().equals(fieldNames)
        && classEventObj.methodNames().equals(methodNames);
  }

  @Override
  public String toString() {
    // TODO: overwrite toString to write json
    StringBuffer json = new StringBuffer();

    json.append("{");
    json.append("\"className\":");
    json.append("\"").append(className).append("\"");
    json.append(",");

    writeArray("eventTypes", eventTypes(), json);
    json.append(",");
    writeArray("fieldNames", fieldNames(), json);
    json.append(",");
    writeArray("methodNames", methodNames(), json);

    json.append("}");

    return json.toString();
  }
}
