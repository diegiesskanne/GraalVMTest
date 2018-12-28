package de.eso.bytebuddy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

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
}
