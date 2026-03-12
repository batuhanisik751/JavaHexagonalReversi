package cs3500.reversi.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Hexagonal board shape for Reversi. This is the default/original geometry.
 * The board is a jagged array forming a hexagon with (boardSize * 2) - 1 rows.
 */
public class HexBoardShape implements IBoardShape {

  private static final List<int[]> DIRECTIONS = Collections.unmodifiableList(Arrays.asList(
          new int[]{-1, -1}, new int[]{-1, 0}, new int[]{-1, 1},
          new int[]{0, -1}, new int[]{0, 1},
          new int[]{1, -1}, new int[]{1, 0}, new int[]{1, 1}
  ));

  @Override
  public String getShapeName() {
    return "hexagonal";
  }

  @Override
  public List<List<ISpace>> createBoard(int boardSize) {
    List<List<ISpace>> board = new ArrayList<>();
    for (int i = 0; i < (boardSize * 2) - 1; i++) {
      board.add(new ArrayList<>());
    }
    int spaces = 0;
    for (int row = 0; row < board.size(); row++) {
      if (row < boardSize) {
        for (int j = 0; j < boardSize + row; j++) {
          board.get(row).add(new Space());
        }
      } else {
        for (int j = 0; j < (boardSize * 2) - 2 - spaces; j++) {
          board.get(row).add(new Space());
        }
        spaces++;
      }
    }
    return board;
  }

  @Override
  public void placeInitialPieces(List<List<ISpace>> board, int boardSize) {
    board.get(boardSize - 2).get(boardSize - 2).setFilled(Player.BLACK);
    board.get(boardSize - 1).get(boardSize).setFilled(Player.BLACK);
    board.get(boardSize).get(boardSize - 2).setFilled(Player.BLACK);
    board.get(boardSize - 2).get(boardSize - 1).setFilled(Player.WHITE);
    board.get(boardSize - 1).get(boardSize - 2).setFilled(Player.WHITE);
    board.get(boardSize).get(boardSize - 1).setFilled(Player.WHITE);
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
    int totalRows = totalRows(boardSize);
    // Top-left and top-right
    corners.add(new Coordinate(0, 0));
    corners.add(new Coordinate(0, board.get(0).size() - 1));
    // Middle-left and middle-right
    int midRow = boardSize - 1;
    corners.add(new Coordinate(midRow, 0));
    corners.add(new Coordinate(midRow, board.get(midRow).size() - 1));
    // Bottom-left and bottom-right
    corners.add(new Coordinate(totalRows - 1, 0));
    corners.add(new Coordinate(totalRows - 1, board.get(totalRows - 1).size() - 1));
    return corners;
  }

  @Override
  public int totalRows(int boardSize) {
    return (boardSize * 2) - 1;
  }

  @Override
  public void validateBoardSize(int boardSize) {
    if (boardSize <= 2) {
      throw new IllegalArgumentException("Board size must be greater than 2.");
    }
  }
}
