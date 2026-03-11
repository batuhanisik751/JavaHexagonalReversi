package cs3500.reversi.view;

/**
 * Utility class for flip animation calculations.
 */
public final class FlipAnimationUtils {

  private FlipAnimationUtils() {
    // no instantiation
  }

  /**
   * Computes the horizontal scale factor for a flip animation at the given progress.
   * Progress 0.0 to 0.5: piece shrinks from 1.0 to 0.0 (old color).
   * Progress 0.5 to 1.0: piece grows from 0.0 to 1.0 (new color).
   * @param progress animation progress from 0.0 to 1.0.
   * @return horizontal scale factor from 0.0 to 1.0.
   */
  public static double computeFlipScaleX(double progress) {
    if (progress < 0.5) {
      return 1.0 - (progress * 2.0);
    } else {
      return (progress - 0.5) * 2.0;
    }
  }
}
