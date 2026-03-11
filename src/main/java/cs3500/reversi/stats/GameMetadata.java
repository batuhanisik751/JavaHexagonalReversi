package cs3500.reversi.stats;

/**
 * Holds game setup metadata that needs to be threaded from the setup dialog
 * to the controller for stats recording at game over.
 */
public final class GameMetadata {
  private final int boardSize;
  private final String blackPlayerType;
  private final String whitePlayerType;

  /**
   * Constructs game metadata from setup dialog values.
   * @param boardSize the board size.
   * @param blackPlayerType the black player type ("human", "easy", "medium", "hard").
   * @param whitePlayerType the white player type.
   */
  public GameMetadata(int boardSize, String blackPlayerType, String whitePlayerType) {
    this.boardSize = boardSize;
    this.blackPlayerType = blackPlayerType;
    this.whitePlayerType = whitePlayerType;
  }

  public int getBoardSize() {
    return boardSize;
  }

  public String getBlackPlayerType() {
    return blackPlayerType;
  }

  public String getWhitePlayerType() {
    return whitePlayerType;
  }
}
