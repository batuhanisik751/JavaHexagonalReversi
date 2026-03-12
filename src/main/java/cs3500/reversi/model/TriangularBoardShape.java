package cs3500.reversi.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Triangular board shape for Reversi (experimental).
 * The board is a triangle where row r has (2 * r + 1) cells.
 * Cells alternate between up-pointing (even col index) and down-pointing (odd col index).
 * Each triangle shares 3 edges with neighbors, giving 3 directions per cell.
 */
public class TriangularBoardShape implements IBoardShape {

  // Up-pointing triangle (even col): neighbors are left, right, and below
  private static final List<int[]> UP_DIRECTIONS = Collections.unmodifiableList(Arrays.asList(
          new int[]{0, -1},   // left neighbor (same row)
          new int[]{0, 1},    // right neighbor (same row)
          new int[]{1, 1}     // below neighbor (next row, col+1)
  ));

  // Down-pointing triangle (odd col): neighbors are left, right, and above
  private static final List<int[]> DOWN_DIRECTIONS = Collections.unmodifiableList(Arrays.asList(
          new int[]{0, -1},   // left neighbor (same row)
          new int[]{0, 1},    // right neighbor (same row)
          new int[]{-1, -1}   // above neighbor (prev row, col-1)
  ));

  @Override
  public String getShapeName() {
    return "triangular";
  }

  @Override
  public List<List<ISpace>> createBoard(int boardSize) {
    List<List<ISpace>> board = new ArrayList<>();
    for (int r = 0; r < boardSize; r++) {
      List<ISpace> row = new ArrayList<>();
      int cellsInRow = 2 * r + 1;
      for (int c = 0; c < cellsInRow; c++) {
        row.add(new Space());
      }
      board.add(row);
    }
    return board;
  }

  @Override
  public void placeInitialPieces(List<List<ISpace>> board, int boardSize) {
    // Place initial pieces in the center area of the triangle.
    // Use the middle row and place alternating pieces around the center.
    int midRow = boardSize / 2;
    int midCol = board.get(midRow).size() / 2;

    // Place 4 pieces in a cluster: 2 black, 2 white
    board.get(midRow).get(midCol).setFilled(Player.BLACK);
    if (midCol + 1 < board.get(midRow).size()) {
      board.get(midRow).get(midCol + 1).setFilled(Player.WHITE);
    }
    if (midCol - 1 >= 0) {
      board.get(midRow).get(midCol - 1).setFilled(Player.WHITE);
    }
    // Place one more in the row below if possible
    if (midRow + 1 < boardSize) {
      int belowCol = midCol + 1;
      if (belowCol < board.get(midRow + 1).size()) {
        board.get(midRow + 1).get(belowCol).setFilled(Player.BLACK);
      }
    }
  }

  @Override
  public List<int[]> getDirections(int row, int col) {
    // Even col index = up-pointing, odd col index = down-pointing
    if (col % 2 == 0) {
      return UP_DIRECTIONS;
    } else {
      return DOWN_DIRECTIONS;
    }
  }

  @Override
  public boolean isWithinBounds(List<List<ISpace>> board, int row, int col) {
    return row >= 0 && row < board.size() && col >= 0 && col < board.get(row).size();
  }

  @Override
  public List<Coordinate> getCorners(int boardSize, List<List<ISpace>> board) {
    List<Coordinate> corners = new ArrayList<>();
    // Top corner (apex)
    corners.add(new Coordinate(0, 0));
    // Bottom-left corner
    corners.add(new Coordinate(boardSize - 1, 0));
    // Bottom-right corner
    corners.add(new Coordinate(boardSize - 1, board.get(boardSize - 1).size() - 1));
    return corners;
  }

  @Override
  public int totalRows(int boardSize) {
    return boardSize;
  }

  @Override
  public void validateBoardSize(int boardSize) {
    if (boardSize < 4) {
      throw new IllegalArgumentException("Triangular board size must be at least 4.");
    }
  }
}
