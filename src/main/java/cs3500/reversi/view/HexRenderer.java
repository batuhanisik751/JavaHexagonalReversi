package cs3500.reversi.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;

import cs3500.reversi.model.Coordinate;
import cs3500.reversi.model.IReadOnlyReversiModel;

/**
 * Renderer for hexagonal board cells. Extracted from the original FxReversiCanvas hex math.
 */
class HexRenderer implements IShapeRenderer {
  static final int HEX_SIZE = 40;
  static final double HEX_WIDTH = Math.sqrt(3) * HEX_SIZE;
  static final double HEX_HEIGHT = 1.5 * HEX_SIZE;

  @Override
  public double[] cellCenter(IReadOnlyReversiModel model, int row, int col) {
    double rowOffset = Math.abs((model.getBoardSize() - 1) - row) * (HEX_WIDTH / 2);
    double centerX = col * HEX_WIDTH + rowOffset + HEX_WIDTH;
    double centerY = row * HEX_HEIGHT + HEX_HEIGHT;
    return new double[]{centerX, centerY};
  }

  @Override
  public void drawCell(GraphicsContext gc, double centerX, double centerY, FxTheme theme,
                       boolean isSelected, boolean isCursor, boolean isPlacedHighlight,
                       boolean isFlippedHighlight) {
    double[] xs = FxHexagon.xPoints(centerX, HEX_SIZE);
    double[] ys = FxHexagon.yPoints(centerY, HEX_SIZE);

    // Fill
    if (isSelected) {
      gc.setFill(theme.selectedHex());
    } else {
      gc.setFill(theme.hexFill());
    }
    gc.fillPolygon(xs, ys, 6);

    // Border
    gc.setStroke(theme.hexBorder());
    gc.strokePolygon(xs, ys, 6);

    // Highlight rings
    if (isPlacedHighlight) {
      gc.setStroke(theme.placedHighlight());
      gc.strokePolygon(xs, ys, 6);
    } else if (isFlippedHighlight) {
      gc.setStroke(theme.flippedHighlight());
      gc.strokePolygon(xs, ys, 6);
    }

    // Cursor border
    if (isCursor) {
      gc.setLineWidth(3);
      gc.setStroke(theme.focusedHex());
      gc.strokePolygon(xs, ys, 6);
      gc.setLineWidth(1);
    }
  }

  @Override
  public double pieceRadius() {
    return HEX_SIZE / 4.0;
  }

  @Override
  public Coordinate pixelToCoord(IReadOnlyReversiModel model, double x, double y) {
    int r = (int) ((y + HEX_HEIGHT / 2) / HEX_HEIGHT) - 1;
    if (r < 0 || r >= model.getBoard().size()) {
      return null;
    }
    double rowOff = Math.abs((model.getBoardSize() - 1) - r);
    int c = (int) (((x + HEX_WIDTH / 2) / HEX_WIDTH) - 1 - (rowOff / 2));
    if (c < 0 || c >= model.getRow(r).size()) {
      return null;
    }
    return new Coordinate(r, c);
  }

  @Override
  public Coordinate computeNextPosition(IReadOnlyReversiModel model, int curRow, int curCol,
                                        KeyCode direction) {
    int boardRows = model.getBoard().size();

    if (curRow < 0 || curCol < 0) {
      int midRow = model.getBoardSize() - 1;
      int midCol = model.getRow(midRow).size() / 2;
      return new Coordinate(midRow, midCol);
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
          newCol = closestColInRow(model, curRow, curCol, newRow);
        }
        break;
      case DOWN:
        if (curRow < boardRows - 1) {
          newRow = curRow + 1;
          newCol = closestColInRow(model, curRow, curCol, newRow);
        }
        break;
      default:
        break;
    }
    return new Coordinate(newRow, newCol);
  }

  private int closestColInRow(IReadOnlyReversiModel model,
                              int fromRow, int fromCol, int toRow) {
    double fromOffset = Math.abs((model.getBoardSize() - 1) - fromRow) * (HEX_WIDTH / 2);
    double toOffset = Math.abs((model.getBoardSize() - 1) - toRow) * (HEX_WIDTH / 2);
    double fromX = fromCol * HEX_WIDTH + fromOffset;
    double toCol = (fromX - toOffset) / HEX_WIDTH;
    int result = (int) Math.round(toCol);
    return Math.max(0, Math.min(result, model.getRow(toRow).size() - 1));
  }

  @Override
  public double computeCanvasWidth(IReadOnlyReversiModel model) {
    return (model.getBoardSize() * 2) * HEX_WIDTH;
  }

  @Override
  public double computeCanvasHeight(IReadOnlyReversiModel model) {
    return (model.getBoardSize() * 2) * HEX_HEIGHT;
  }
}
