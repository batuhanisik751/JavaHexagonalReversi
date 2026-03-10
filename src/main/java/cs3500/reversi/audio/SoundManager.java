package cs3500.reversi.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * Generates and plays short synthesized tones for game events.
 * All sounds are created programmatically — no external files needed.
 * Sounds play on a daemon thread so the EDT is never blocked.
 */
public final class SoundManager {
  private static boolean muted = false;
  private static final float SAMPLE_RATE = 44100;

  private SoundManager() {
    // utility class
  }

  /**
   * Plays the named sound effect asynchronously.
   * @param sound one of "move", "invalid", "pass", "undo", "gameOver".
   */
  public static void play(String sound) {
    if (muted) {
      return;
    }
    Thread t = new Thread(() -> playSound(sound));
    t.setDaemon(true);
    t.start();
  }

  public static boolean isMuted() {
    return muted;
  }

  public static void setMuted(boolean m) {
    muted = m;
  }

  public static void toggleMute() {
    muted = !muted;
  }

  private static void playSound(String sound) {
    switch (sound) {
      case "move":
        playTone(520, 100, 0.4);
        break;
      case "invalid":
        playTone(200, 150, 0.3);
        break;
      case "pass":
        playTone(400, 80, 0.25);
        playTone(320, 80, 0.25);
        break;
      case "undo":
        playTone(440, 60, 0.25);
        playTone(520, 60, 0.25);
        break;
      case "gameOver":
        playTone(523, 120, 0.4);
        playTone(659, 120, 0.4);
        playTone(784, 200, 0.4);
        break;
      default:
        break;
    }
  }

  private static void playTone(double freq, int durationMs, double volume) {
    int numSamples = (int) (SAMPLE_RATE * durationMs / 1000);
    byte[] buf = new byte[numSamples];
    for (int i = 0; i < numSamples; i++) {
      double angle = 2.0 * Math.PI * i * freq / SAMPLE_RATE;
      // Apply fade-in/out envelope to avoid clicks
      double envelope = 1.0;
      int fadeLen = Math.min(numSamples / 5, 400);
      if (i < fadeLen) {
        envelope = (double) i / fadeLen;
      } else if (i > numSamples - fadeLen) {
        envelope = (double) (numSamples - i) / fadeLen;
      }
      buf[i] = (byte) (Math.sin(angle) * 127 * volume * envelope);
    }
    AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
    try (SourceDataLine line = AudioSystem.getSourceDataLine(af)) {
      line.open(af);
      line.start();
      line.write(buf, 0, buf.length);
      line.drain();
    } catch (LineUnavailableException e) {
      // No audio device — silently ignore
    }
  }
}
