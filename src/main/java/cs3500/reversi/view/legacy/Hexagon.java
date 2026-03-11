package cs3500.reversi.view.legacy;

import java.awt.geom.Path2D;

/**
 * Represents a Hexagon shape for graphical rendering.
 */
class Hexagon extends Path2D.Double {

  /**
   * Constructs a Hexagon with the specified coordinates and size.
   * @param centerX The x-coordinate of the hexagon's center.
   * @param centerY The y-coordinate of the hexagon's center.
   * @param size        The size of the hexagon.
   */
  public Hexagon(double centerX, double centerY, double size) {
    double angle = Math.PI / 6.0;  // Starting angle for a corner facing down
    moveTo(centerX + size * Math.cos(angle), centerY + size * Math.sin(angle));
    for (int i = 1; i < 6; i++) {
      angle += 2 * Math.PI / 6.0;
      lineTo(centerX + size * Math.cos(angle), centerY + size * Math.sin(angle));
    }
    closePath();
  }
}