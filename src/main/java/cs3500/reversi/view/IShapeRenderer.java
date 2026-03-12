package cs3500.reversi.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;

import cs3500.reversi.model.Coordinate;
import cs3500.reversi.model.IReadOnlyReversiModel;

/**
 * Interface for shape-specific rendering logic used by FxReversiCanvas.
 * Implementations handle drawing cells, coordinate mapping, and keyboard navigation
 * for different board geometries.
 */
interface IShapeRenderer {

  /**
   * Computes the pixel center (x, y) for a cell at the given board position.
   * @param model the game model.
   * @param row the row index.
   * @param col the column index.
   * @return array of two doubles: {centerX, centerY}.
   */
  double[] cellCenter(IReadOnlyReversiModel model, int row, int col);

  /**
   * Draws the cell shape (border and fill) at the given pixel center.
   * Does NOT draw pieces — that is handled by the canvas.
   * @param gc the graphics context.
   * @param centerX pixel x of cell center.
   * @param centerY pixel y of cell center.
   * @param theme the color theme.
   * @param isSelected whether this cell is the selected/valid-move highlight.
   * @param isCursor whether this cell is the cursor focus.
   * @param isPlacedHighlight whether this cell is the last-placed highlight.
   * @param isFlippedHighlight whether this cell is a flipped highlight.
   */
  void drawCell(GraphicsContext gc, double centerX, double centerY, FxTheme theme,
                boolean isSelected, boolean isCursor, boolean isPlacedHighlight,
                boolean isFlippedHighlight);

  /**
   * Returns the piece size (radius) for drawing pieces in cells.
   */
  double pieceRadius();

  /**
   * Converts a pixel click (x, y) to board coordinates.
   * @param model the game model.
   * @param x pixel x.
   * @param y pixel y.
   * @return the board coordinate, or null if outside the board.
   */
  Coordinate pixelToCoord(IReadOnlyReversiModel model, double x, double y);

  /**
   * Computes the next cursor position for keyboard navigation.
   * @param model the game model.
   * @param curRow current cursor row.
   * @param curCol current cursor column.
   * @param direction the arrow key direction.
   * @return the new cursor coordinate.
   */
  Coordinate computeNextPosition(IReadOnlyReversiModel model, int curRow, int curCol,
                                 KeyCode direction);

  /**
   * Computes the preferred canvas width for the given model.
   * @param model the game model.
   * @return preferred width in pixels.
   */
  double computeCanvasWidth(IReadOnlyReversiModel model);

  /**
   * Computes the preferred canvas height for the given model.
   * @param model the game model.
   * @return preferred height in pixels.
   */
  double computeCanvasHeight(IReadOnlyReversiModel model);
}
