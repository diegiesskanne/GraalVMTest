package de.eso.bytebuddy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImmutableClassEventTest {
  @Test
  void name() {
    ClassEvent build =
        ImmutableClassEvent.build(
            "de.eso.wurst",
            Collections.singletonList(EventType.CLASS_FOR_NAME),
            Collections.emptyList(),
            Collections.emptyList());

    String s = build.toString();

    Assertions.assertTrue(() -> s.contains("CLASS_FOR_NAME"));
  }

  @Test
  void name2() {
    ClassEvent build1 =
        ImmutableClassEvent.build(
            "de.eso.wurst",
            Collections.singletonList(EventType.CLASS_FOR_NAME),
            Collections.emptyList(),
            Collections.emptyList());
    ClassEvent build2 =
        ImmutableClassEvent.build(
            "de.eso.wurst",
            Collections.singletonList(EventType.CLASS_FOR_NAME),
            Collections.emptyList(),
            Collections.emptyList());
    ClassEvent build3 =
        ImmutableClassEvent.build(
            "de.eso.wurst1",
            Collections.singletonList(EventType.CLASS_FOR_NAME),
            Collections.emptyList(),
            Collections.emptyList());

    List<ClassEvent> collect = Stream.of(build1, build2).distinct().collect(Collectors.toList());
    List<ClassEvent> collect2 = Stream.of(build1, build3).distinct().collect(Collectors.toList());
    assertEquals(1, collect.size());
    assertEquals(1, collect2.size());

    boolean equals1 = Objects.equals(build1, build2);
    assertTrue(equals1);
    boolean equals = build1.equals(build2);
    assertTrue(equals);
  }
}
