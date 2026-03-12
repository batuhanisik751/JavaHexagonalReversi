package cs3500.reversi.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Square board shape for Reversi (standard Othello rules).
 * Creates a uniform size x size grid with 8-directional adjacency.
 */
public class SquareBoardShape implements IBoardShape {

  private static final List<int[]> DIRECTIONS = Collections.unmodifiableList(Arrays.asList(
          new int[]{-1, -1}, new int[]{-1, 0}, new int[]{-1, 1},
          new int[]{0, -1}, new int[]{0, 1},
          new int[]{1, -1}, new int[]{1, 0}, new int[]{1, 1}
  ));

  @Override
  public String getShapeName() {
    return "square";
  }

  @Override
  public List<List<ISpace>> createBoard(int boardSize) {
    List<List<ISpace>> board = new ArrayList<>();
    for (int r = 0; r < boardSize; r++) {
      List<ISpace> row = new ArrayList<>();
      for (int c = 0; c < boardSize; c++) {
        row.add(new Space());
      }
      board.add(row);
    }
    return board;
  }

  @Override
  public void placeInitialPieces(List<List<ISpace>> board, int boardSize) {
    int mid = boardSize / 2;
    board.get(mid - 1).get(mid - 1).setFilled(Player.WHITE);
    board.get(mid - 1).get(mid).setFilled(Player.BLACK);
    board.get(mid).get(mid - 1).setFilled(Player.BLACK);
    board.get(mid).get(mid).setFilled(Player.WHITE);
  }

  @Override
  public List<int[]> getDirections(int row, int col) {
    return DIRECTIONS;
  }

  @Override
  public boolean isWithinBounds(List<List<ISpace>> board, int row, int col) {
    return row >= 0 && row < board.size() && col >= 0 && col < board.get(row).size();
  }

  @Override
  public List<Coordinate> getCorners(int boardSize, List<List<ISpace>> board) {
    List<Coordinate> corners = new ArrayList<>();
    corners.add(new Coordinate(0, 0));
    corners.add(new Coordinate(0, boardSize - 1));
    corners.add(new Coordinate(boardSize - 1, 0));
    corners.add(new Coordinate(boardSize - 1, boardSize - 1));
    return corners;
  }

  @Override
  public int totalRows(int boardSize) {
    return boardSize;
  }

  @Override
  public void validateBoardSize(int boardSize) {
    if (boardSize < 4 || boardSize % 2 != 0) {
      throw new IllegalArgumentException("Square board size must be even and at least 4.");
    }
  }
}
