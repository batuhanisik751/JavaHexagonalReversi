package cs3500.reversi.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;

import cs3500.reversi.model.Coordinate;
import cs3500.reversi.model.IReadOnlyReversiModel;

/**
 * Renderer for triangular board cells.
 * Cells alternate between up-pointing (even col) and down-pointing (odd col) triangles.
 */
class TriangularRenderer implements IShapeRenderer {
  static final double TRI_SIDE = 50;
  static final double TRI_HEIGHT = TRI_SIDE * Math.sqrt(3) / 2;
  static final double PADDING = 40;

  @Override
  public double[] cellCenter(IReadOnlyReversiModel model, int row, int col) {
    int totalRows = model.getBoard().size();
    int maxCols = 2 * totalRows - 1;

    // Horizontal offset: center the row within the max width
    int colsInRow = 2 * row + 1;
    double rowIndent = (maxCols - colsInRow) * (TRI_SIDE / 4.0);

    double centerX;
    double centerY;

    if (col % 2 == 0) {
      // Up-pointing triangle: centroid is 1/3 from base
      centerX = PADDING + rowIndent + (col / 2.0) * TRI_SIDE + TRI_SIDE / 2.0;
      centerY = PADDING + row * TRI_HEIGHT + TRI_HEIGHT * 2.0 / 3.0;
    } else {
      // Down-pointing triangle: centroid is 1/3 from top
      centerX = PADDING + rowIndent + ((col + 1) / 2.0) * TRI_SIDE;
      centerY = PADDING + row * TRI_HEIGHT + TRI_HEIGHT * 1.0 / 3.0;
    }
    return new double[]{centerX, centerY};
  }

  @Override
  public void drawCell(GraphicsContext gc, double centerX, double centerY, FxTheme theme,
                       boolean isSelected, boolean isCursor, boolean isPlacedHighlight,
                       boolean isFlippedHighlight) {
    // We need to reconstruct triangle vertices from center.
    // This is approximate since drawCell only gets centerX/centerY.
    // The actual polygon drawing is done via the draw loop using row/col info.
    // For simplicity, draw a small diamond shape as the cell indicator.

    // Fill
    if (isSelected) {
      gc.setFill(theme.selectedHex());
    } else {
      gc.setFill(theme.hexFill());
    }

    // We'll draw a diamond placeholder; actual triangle drawing is in drawTriangle
    double r = TRI_SIDE / 3.0;
    gc.fillOval(centerX - r, centerY - r, 2 * r, 2 * r);

    if (isPlacedHighlight) {
      gc.setStroke(theme.placedHighlight());
      gc.setLineWidth(2);
      gc.strokeOval(centerX - r, centerY - r, 2 * r, 2 * r);
      gc.setLineWidth(1);
    } else if (isFlippedHighlight) {
      gc.setStroke(theme.flippedHighlight());
      gc.setLineWidth(2);
      gc.strokeOval(centerX - r, centerY - r, 2 * r, 2 * r);
      gc.setLineWidth(1);
    }

    if (isCursor) {
      gc.setLineWidth(3);
      gc.setStroke(theme.focusedHex());
      gc.strokeOval(centerX - r, centerY - r, 2 * r, 2 * r);
      gc.setLineWidth(1);
    }
  }

  /**
   * Draws a triangle cell at the given board position with full polygon.
   * Called directly from the canvas draw loop which has row/col context.
   */
  void drawTriangleCell(GraphicsContext gc, IReadOnlyReversiModel model, int row, int col,
                        FxTheme theme, boolean isSelected, boolean isCursor,
                        boolean isPlacedHighlight, boolean isFlippedHighlight) {
    int totalRows = model.getBoard().size();
    int maxCols = 2 * totalRows - 1;
    int colsInRow = 2 * row + 1;
    double rowIndent = (maxCols - colsInRow) * (TRI_SIDE / 4.0);

    double[] xs = new double[3];
    double[] ys = new double[3];

    if (col % 2 == 0) {
      // Up-pointing triangle
      double baseX = PADDING + rowIndent + (col / 2.0) * TRI_SIDE;
      xs[0] = baseX + TRI_SIDE / 2.0;  // top vertex
      ys[0] = PADDING + row * TRI_HEIGHT;
      xs[1] = baseX;                     // bottom-left
      ys[1] = PADDING + (row + 1) * TRI_HEIGHT;
      xs[2] = baseX + TRI_SIDE;          // bottom-right
      ys[2] = PADDING + (row + 1) * TRI_HEIGHT;
    } else {
      // Down-pointing triangle
      double baseX = PADDING + rowIndent + ((col - 1) / 2.0) * TRI_SIDE + TRI_SIDE / 2.0;
      xs[0] = baseX;                     // top-left
      ys[0] = PADDING + row * TRI_HEIGHT;
      xs[1] = baseX + TRI_SIDE;          // top-right
      ys[1] = PADDING + row * TRI_HEIGHT;
      xs[2] = baseX + TRI_SIDE / 2.0;    // bottom vertex
      ys[2] = PADDING + (row + 1) * TRI_HEIGHT;
    }

    // Fill
    if (isSelected) {
      gc.setFill(theme.selectedHex());
    } else {
      gc.setFill(theme.hexFill());
    }
    gc.fillPolygon(xs, ys, 3);

    // Border
    gc.setStroke(theme.hexBorder());
    gc.strokePolygon(xs, ys, 3);

    // Highlight rings
    if (isPlacedHighlight) {
      gc.setStroke(theme.placedHighlight());
      gc.setLineWidth(2);
      gc.strokePolygon(xs, ys, 3);
      gc.setLineWidth(1);
    } else if (isFlippedHighlight) {
      gc.setStroke(theme.flippedHighlight());
      gc.setLineWidth(2);
      gc.strokePolygon(xs, ys, 3);
      gc.setLineWidth(1);
    }

    // Cursor
    if (isCursor) {
      gc.setLineWidth(3);
      gc.setStroke(theme.focusedHex());
      gc.strokePolygon(xs, ys, 3);
      gc.setLineWidth(1);
    }
  }

  @Override
  public double pieceRadius() {
    return TRI_SIDE / 6.0;
  }

  @Override
  public Coordinate pixelToCoord(IReadOnlyReversiModel model, double x, double y) {
    // Determine row from y
    int row = (int) ((y - PADDING) / TRI_HEIGHT);
    if (row < 0 || row >= model.getBoard().size()) {
      return null;
    }

    int totalRows = model.getBoard().size();
    int maxCols = 2 * totalRows - 1;
    int colsInRow = 2 * row + 1;
    double rowIndent = (maxCols - colsInRow) * (TRI_SIDE / 4.0);

    // Determine which column within the row
    double localX = x - PADDING - rowIndent;
    if (localX < 0 || localX > colsInRow * TRI_SIDE / 2.0) {
      return null;
    }

    // Which half-cell are we in?
    int halfCell = (int) (localX / (TRI_SIDE / 2.0));
    int col = halfCell;
    if (col < 0 || col >= colsInRow) {
      return null;
    }
    return new Coordinate(row, col);
  }

  @Override
  public Coordinate computeNextPosition(IReadOnlyReversiModel model, int curRow, int curCol,
                                        KeyCode direction) {
    int boardRows = model.getBoard().size();

    if (curRow < 0 || curCol < 0) {
      int midRow = boardRows / 2;
      return new Coordinate(midRow, model.getRow(midRow).size() / 2);
    }

    int newRow = curRow;
    int newCol = curCol;

    switch (direction) {
      case LEFT:
        newCol = Math.max(0, curCol - 1);
        break;
      case RIGHT:
        newCol = Math.min(model.getRow(curRow).size() - 1, curCol + 1);
        break;
      case UP:
        if (curRow > 0) {
          newRow = curRow - 1;
          // Map column to previous row: closest column
          newCol = Math.min(curCol, model.getRow(newRow).size() - 1);
        }
        break;
      case DOWN:
        if (curRow < boardRows - 1) {
          newRow = curRow + 1;
          newCol = Math.min(curCol, model.getRow(newRow).size() - 1);
        }
        break;
      default:
        break;
    }
    return new Coordinate(newRow, newCol);
  }

  @Override
  public double computeCanvasWidth(IReadOnlyReversiModel model) {
    int totalRows = model.getBoard().size();
    return totalRows * TRI_SIDE + 2 * PADDING;
  }

  @Override
  public double computeCanvasHeight(IReadOnlyReversiModel model) {
    int totalRows = model.getBoard().size();
    return totalRows * TRI_HEIGHT + 2 * PADDING;
  }
}
