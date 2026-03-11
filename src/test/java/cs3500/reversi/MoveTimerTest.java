package cs3500.reversi;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import cs3500.reversi.controller.MoveTimer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the MoveTimer class. Tests the logic without requiring JavaFX Timeline
 * (Timeline won't run without the JavaFX toolkit, so we test state and configuration).
 */
public class MoveTimerTest {

  @Test
  public void testIsActiveWithPositiveSeconds() {
    MoveTimer timer = new MoveTimer(30, null, null, null);
    assertTrue(timer.isActive());
  }

  @Test
  public void testIsActiveWithZeroSeconds() {
    MoveTimer timer = new MoveTimer(0, null, null, null);
    assertFalse(timer.isActive());
  }

  @Test
  public void testInitialSecondsRemaining() {
    MoveTimer timer = new MoveTimer(60, null, null, null);
    assertEquals(60, timer.getSecondsRemaining());
  }

  @Test
  public void testResetDoesNothingWhenInactive() {
    List<String> events = new ArrayList<>();
    MoveTimer timer = new MoveTimer(0, () -> events.add("tick"), null, null);
    timer.reset();
    // No tick should fire since timer is inactive
    assertTrue(events.isEmpty());
  }

  @Test
  public void testStopDoesNotCrashWhenNoTimeline() {
    MoveTimer timer = new MoveTimer(30, null, null, null);
    // Should not throw
    timer.stop();
  }

  @Test
  public void testPauseResumeDoesNotCrash() {
    MoveTimer timer = new MoveTimer(30, null, null, null);
    timer.pause();
    timer.resume();
    // Should not throw
  }

  @Test
  public void testMultipleStopCalls() {
    MoveTimer timer = new MoveTimer(10, null, null, null);
    timer.stop();
    timer.stop();
    timer.stop();
    // Should not throw
  }

  @Test
  public void testDifferentTimerConfigurations() {
    int[] configs = {5, 10, 30, 60};
    for (int seconds : configs) {
      MoveTimer timer = new MoveTimer(seconds, null, null, null);
      assertTrue("Timer with " + seconds + "s should be active", timer.isActive());
      assertEquals(seconds, timer.getSecondsRemaining());
    }
  }
}
