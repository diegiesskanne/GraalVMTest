package de.eso.bytebuddy;

import org.immutables.value.Value;

import java.util.List;
import java.util.stream.Collectors;

@Value.Immutable
@Value.Style(visibility = Value.Style.ImplementationVisibility.PUBLIC)
public abstract class ClassEvent {
  private static void writeArray(String fName, List<?> list, StringBuffer buffer) {
    buffer.append("\"").append(fName).append("\":");
    buffer
        .append("[")
        .append(
            list.stream().map(eT -> "\"" + eT.toString() + "\"").collect(Collectors.joining(", ")))
        .append("]");
  }

  public static ClassEvent build(EventType eventType, String className) {
    return ImmutableClassEvent.builder().addEventTypes(eventType).className(className).build();
  }

  public static ClassEvent buildFieldNames(
      EventType eventType, String className, List<String> fieldNames) {
    return ImmutableClassEvent.builder()
        .addEventTypes(eventType)
        .className(className)
        .fieldNames(fieldNames)
        .build();
  }

  public static ClassEvent buildMethodNames(
      EventType eventType, String className, List<String> methodNames) {
    return ImmutableClassEvent.builder()
        .addEventTypes(eventType)
        .className(className)
        .methodNames(methodNames)
        .build();
  }

  public abstract String className();

  public abstract List<EventType> eventTypes();

  public abstract List<String> fieldNames();

  public abstract List<String> methodNames();

  @Override
  public String toString() {
    StringBuffer json = new StringBuffer();

    json.append("{");
    json.append("\"className\":");
    json.append("\"").append(className()).append("\"");
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
