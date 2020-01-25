package dev.aisandbox.client.profiler;

import java.util.Map;
import java.util.TreeMap;
import lombok.AccessLevel;
import lombok.Getter;

public class ProfileStep {
  private long cursor;

  @Getter(AccessLevel.PROTECTED)
  private Map<String, Long> timings = new TreeMap<>();

  public ProfileStep() {
    cursor = System.currentTimeMillis();
  }

  public void addStep(String name) {
    long time = System.currentTimeMillis();
    timings.put(name, time - cursor);
    cursor = time;
  }
}
