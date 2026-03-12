package cs3500.reversi.model;

import java.util.List;

/**
 * Encapsulates the geometry of a Reversi board shape.
 * Implementations define board structure, initial placement,
 * move directions, corner detection, and bounds checking.
 */
public interface IBoardShape {

  /**
   * Returns the name of this shape (e.g., "hexagonal", "square", "triangular").
   */
  String getShapeName();

  /**
   * Creates and returns the initial empty board structure with pieces placed.
   * @param boardSize the size parameter for this shape.
   * @return the initialized board.
   */
  List<List<ISpace>> createBoard(int boardSize);

  /**
   * Places the initial pieces on the given board.
   * @param board the board to place pieces on.
   * @param boardSize the size parameter for this shape.
   */
  void placeInitialPieces(List<List<ISpace>> board, int boardSize);

  /**
   * Returns the direction vectors (dr, dc) for move validation from the given position.
   * For hex and square this is constant; for triangular it depends on cell orientation.
   * @param row the row of the cell.
   * @param col the column of the cell.
   * @return list of direction vectors as int[2] arrays {dr, dc}.
   */
  List<int[]> getDirections(int row, int col);

  /**
   * Checks if (row, col) is within the board bounds.
   * @param board the board to check bounds against.
   * @param row the row index.
   * @param col the column index.
   * @return true if within bounds.
   */
  boolean isWithinBounds(List<List<ISpace>> board, int row, int col);

  /**
   * Returns all corner coordinates for this board shape and size.
   * @param boardSize the size parameter.
   * @param board the board (needed for shapes with variable row lengths).
   * @return list of corner coordinates.
   */
  List<Coordinate> getCorners(int boardSize, List<List<ISpace>> board);

  /**
   * Returns the total number of rows for a given board size.
   * @param boardSize the size parameter.
   * @return total row count.
   */
  int totalRows(int boardSize);

  /**
   * Validates the board size for this shape.
   * @param boardSize the size to validate.
   * @throws IllegalArgumentException if the size is invalid.
   */
  void validateBoardSize(int boardSize);
}
