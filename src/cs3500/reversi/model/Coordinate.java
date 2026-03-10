package cs3500.reversi.model;

/**
 * Represents a position on the Reversi board as a (row, col) pair.
 */
public final class Coordinate {
  private final int row;
  private final int col;

  /**
   * Creates a Coordinate with the given row and column.
   * @param row the row index.
   * @param col the column index.
   */
  public Coordinate(int row, int col) {
    this.row = row;
    this.col = col;
  }

  /**
   * Gets the row index.
   * @return the row.
   */
  public int getRow() {
    return this.row;
  }

  /**
   * Gets the column index.
   * @return the column.
   */
  public int getCol() {
    return this.col;
  }

  @Override
  public String toString() {
    return "(" + row + ", " + col + ")";
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Coordinate)) {
      return false;
    }
    Coordinate other = (Coordinate) obj;
    return this.row == other.row && this.col == other.col;
  }

  @Override
  public int hashCode() {
    return 31 * row + col;
  }
}
