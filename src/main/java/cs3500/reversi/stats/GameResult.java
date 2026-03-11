package cs3500.reversi.stats;

/**
 * Immutable record of a completed Reversi game, used for win/loss statistics tracking.
 */
public final class GameResult {
  private final String date;
  private final int boardSize;
  private final String player1Type;
  private final String player2Type;
  private final String winner;
  private final int blackScore;
  private final int whiteScore;
  private final int moveCount;

  /**
   * Constructs a new GameResult with the given fields.
   * @param date the date the game was played (ISO format, e.g. "2026-03-11").
   * @param boardSize the board size used.
   * @param player1Type the black player type ("human", "easy", "medium", "hard").
   * @param player2Type the white player type.
   * @param winner the winner ("black", "white", or "draw").
   * @param blackScore the final black score.
   * @param whiteScore the final white score.
   * @param moveCount the total number of moves (including passes).
   */
  public GameResult(String date, int boardSize, String player1Type, String player2Type,
                    String winner, int blackScore, int whiteScore, int moveCount) {
    this.date = date;
    this.boardSize = boardSize;
    this.player1Type = player1Type;
    this.player2Type = player2Type;
    this.winner = winner;
    this.blackScore = blackScore;
    this.whiteScore = whiteScore;
    this.moveCount = moveCount;
  }

  public String getDate() {
    return date;
  }

  public int getBoardSize() {
    return boardSize;
  }

  public String getPlayer1Type() {
    return player1Type;
  }

  public String getPlayer2Type() {
    return player2Type;
  }

  public String getWinner() {
    return winner;
  }

  public int getBlackScore() {
    return blackScore;
  }

  public int getWhiteScore() {
    return whiteScore;
  }

  public int getMoveCount() {
    return moveCount;
  }
}
