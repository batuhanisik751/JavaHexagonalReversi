package cs3500.reversi.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * Manages a per-turn countdown timer for the Reversi game.
 * Uses a JavaFX Timeline to tick every second and invokes callbacks for
 * display updates, warnings, and timeouts.
 */
public class MoveTimer {
  private final int turnSeconds;
  private final Runnable onTick;
  private final Runnable onTimeout;
  private final Runnable onWarning;
  private int secondsRemaining;
  private Timeline countdown;
  private boolean paused;

  /**
   * Constructs a MoveTimer with the given time limit and callbacks.
   * @param turnSeconds the time limit per turn in seconds (0 = no limit).
   * @param onTick called each second (use getSecondsRemaining() for display).
   * @param onTimeout called when time runs out.
   * @param onWarning called once at 5 seconds remaining.
   */
  public MoveTimer(int turnSeconds, Runnable onTick, Runnable onTimeout, Runnable onWarning) {
    this.turnSeconds = turnSeconds;
    this.onTick = onTick;
    this.onTimeout = onTimeout;
    this.onWarning = onWarning;
    this.secondsRemaining = turnSeconds;
    this.paused = false;
  }

  /**
   * Returns whether this timer is active (has a non-zero time limit).
   */
  public boolean isActive() {
    return turnSeconds > 0;
  }

  /**
   * Returns the number of seconds remaining in the current turn.
   */
  public int getSecondsRemaining() {
    return secondsRemaining;
  }

  /**
   * Resets the timer to full time and starts counting down.
   * If the timer is not active (turnSeconds = 0), does nothing.
   */
  public void reset() {
    if (!isActive()) {
      return;
    }
    stop();
    secondsRemaining = turnSeconds;
    paused = false;

    countdown = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
      if (paused) {
        return;
      }
      secondsRemaining--;
      if (secondsRemaining == 5 && onWarning != null) {
        onWarning.run();
      }
      if (onTick != null) {
        onTick.run();
      }
      if (secondsRemaining <= 0) {
        stop();
        if (onTimeout != null) {
          onTimeout.run();
        }
      }
    }));
    countdown.setCycleCount(Animation.INDEFINITE);
    countdown.play();

    // Initial tick to display full time
    if (onTick != null) {
      onTick.run();
    }
  }

  /**
   * Stops the countdown. Called on game over, undo, load, or when the player moves.
   */
  public void stop() {
    if (countdown != null) {
      countdown.stop();
      countdown = null;
    }
    paused = false;
  }

  /**
   * Pauses the countdown (e.g., during AI thinking).
   */
  public void pause() {
    paused = true;
  }

  /**
   * Resumes the countdown after a pause.
   */
  public void resume() {
    paused = false;
  }
}
