package cs3500.reversi.view;

/**
 * Utility class for computing hexagon vertex coordinates for JavaFX Canvas rendering.
 */
class FxHexagon {

  /**
   * Computes the x-coordinates of the 6 vertices of a hexagon.
   * @param centerX the x-coordinate of the hexagon's center.
   * @param size the size (radius) of the hexagon.
   * @return array of 6 x-coordinates.
   */
  static double[] xPoints(double centerX, double size) {
    double[] xs = new double[6];
    double angle = Math.PI / 6.0;
    for (int i = 0; i < 6; i++) {
      xs[i] = centerX + size * Math.cos(angle);
      angle += 2 * Math.PI / 6.0;
    }
    return xs;
  }

  /**
   * Computes the y-coordinates of the 6 vertices of a hexagon.
   * @param centerY the y-coordinate of the hexagon's center.
   * @param size the size (radius) of the hexagon.
   * @return array of 6 y-coordinates.
   */
  static double[] yPoints(double centerY, double size) {
    double[] ys = new double[6];
    double angle = Math.PI / 6.0;
    for (int i = 0; i < 6; i++) {
      ys[i] = centerY + size * Math.sin(angle);
      angle += 2 * Math.PI / 6.0;
    }
    return ys;
  }
}
