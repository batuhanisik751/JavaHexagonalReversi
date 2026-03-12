package cs3500.reversi.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;

import cs3500.reversi.model.Coordinate;
import cs3500.reversi.model.IReadOnlyReversiModel;

/**
 * Renderer for square board cells (standard Othello grid).
 */
class SquareRenderer implements IShapeRenderer {
  static final double CELL_SIZE = 60;
  static final double PADDING = 30;

  @Override
  public double[] cellCenter(IReadOnlyReversiModel model, int row, int col) {
    double centerX = col * CELL_SIZE + PADDING + CELL_SIZE / 2;
    double centerY = row * CELL_SIZE + PADDING + CELL_SIZE / 2;
    return new double[]{centerX, centerY};
  }

  @Override
  public void drawCell(GraphicsContext gc, double centerX, double centerY, FxTheme theme,
                       boolean isSelected, boolean isCursor, boolean isPlacedHighlight,
                       boolean isFlippedHighlight) {
    double x = centerX - CELL_SIZE / 2;
    double y = centerY - CELL_SIZE / 2;

    // Fill
    if (isSelected) {
      gc.setFill(theme.selectedHex());
    } else {
      gc.setFill(theme.hexFill());
    }
    gc.fillRect(x, y, CELL_SIZE, CELL_SIZE);

    // Border
    gc.setStroke(theme.hexBorder());
    gc.strokeRect(x, y, CELL_SIZE, CELL_SIZE);

    // Highlight rings
    if (isPlacedHighlight) {
      gc.setStroke(theme.placedHighlight());
      gc.setLineWidth(2);
      gc.strokeRect(x, y, CELL_SIZE, CELL_SIZE);
      gc.setLineWidth(1);
    } else if (isFlippedHighlight) {
      gc.setStroke(theme.flippedHighlight());
      gc.setLineWidth(2);
      gc.strokeRect(x, y, CELL_SIZE, CELL_SIZE);
      gc.setLineWidth(1);
    }

    // Cursor border
    if (isCursor) {
      gc.setLineWidth(3);
      gc.setStroke(theme.focusedHex());
      gc.strokeRect(x, y, CELL_SIZE, CELL_SIZE);
      gc.setLineWidth(1);
    }
  }

  @Override
  public double pieceRadius() {
    return CELL_SIZE / 4.0;
  }

  @Override
  public Coordinate pixelToCoord(IReadOnlyReversiModel model, double x, double y) {
    int col = (int) ((x - PADDING) / CELL_SIZE);
    int row = (int) ((y - PADDING) / CELL_SIZE);
    if (row < 0 || row >= model.getBoard().size()
            || col < 0 || col >= model.getRow(row).size()) {
      return null;
    }
    return new Coordinate(row, col);
  }

  @Override
  public Coordinate computeNextPosition(IReadOnlyReversiModel model, int curRow, int curCol,
                                        KeyCode direction) {
    int boardSize = model.getBoard().size();

    if (curRow < 0 || curCol < 0) {
      return new Coordinate(boardSize / 2, boardSize / 2);
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
        newRow = Math.max(0, curRow - 1);
        break;
      case DOWN:
        newRow = Math.min(boardSize - 1, curRow + 1);
        break;
      default:
        break;
    }
    return new Coordinate(newRow, newCol);
  }

  @Override
  public double computeCanvasWidth(IReadOnlyReversiModel model) {
    return model.getBoardSize() * CELL_SIZE + 2 * PADDING;
  }

  @Override
  public double computeCanvasHeight(IReadOnlyReversiModel model) {
    return model.getBoardSize() * CELL_SIZE + 2 * PADDING;
  }
}
