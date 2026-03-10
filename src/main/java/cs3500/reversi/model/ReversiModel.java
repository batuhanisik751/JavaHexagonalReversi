package cs3500.reversi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single game of Reversi. Has a board made of a list of list of Spaces
 * and a current Player that holds the current turn of the game.
 */
public class ReversiModel implements IReversiModel {
  private final int boardSize;
  private Player currentTurn;
  private List<List<ISpace>> board;

  /**
   * Creates a Reversi Model game, initializes the board based on the given board size, sets
   * the starting turn to Player.BLACK.
   *
   * @param boardSize the size of the board, based on the size of the edges of the hexagon.
   *                  (if the size is 3, the top row of the hexagon has 3 Spaces.)
   */
  public ReversiModel(int boardSize) {
    if (boardSize <= 2) {
      throw new IllegalArgumentException("Board size must be greater than 2.");
    }
    this.boardSize = boardSize;
    this.board = new ArrayList<>();
    initBoard(boardSize);
    initSpacesInBoard(boardSize);
    this.currentTurn = Player.BLACK;
  }

  /**
   * Private copy constructor used by {@link #copyModel()}.
   */
  private ReversiModel(int boardSize, List<List<ISpace>> board, Player currentTurn) {
    this.boardSize = boardSize;
    this.board = board;
    this.currentTurn = currentTurn;
  }

  @Override
  public int getBoardSize() {
    return this.boardSize;
  }

  @Override
  public Player getCurrentTurn() {
    return this.currentTurn;
  }

  @Override
  public List<List<ISpace>> getBoard() {
    List<List<ISpace>> boardCopy = new ArrayList<>(board.size());
    for (int row = 0; row < board.size(); row++) {
      List<ISpace> rowCopy = new ArrayList<>(getRow(row));
      boardCopy.add(rowCopy);
    }
    return boardCopy;
  }

  @Override
  public List<ISpace> getRow(int numRow) {
    if (numRow >= board.size() || numRow < 0) {
      throw new IllegalArgumentException("Invalid row.");
    }
    return this.board.get(numRow);
  }

  @Override
  public Player getSpaceContent(int numRow, int numCol) {
    if (numRow >= board.size() || numRow < 0) {
      throw new IllegalArgumentException("Invalid row.");
    }
    if (numCol >= board.get(numRow).size() || numCol < 0) {
      throw new IllegalArgumentException("Invalid column.");
    }
    if (getSpace(numRow, numCol).isEmpty()) {
      return null;
    } else {
      return getSpace(numRow, numCol).getPlayer();
    }
  }

  @Override
  public ISpace getSpace(int numRow, int numCol) {
    if (numRow >= board.size() || numRow < 0) {
      throw new IllegalArgumentException("Invalid row.");
    }
    if (numCol >= board.get(numRow).size() || numCol < 0) {
      throw new IllegalArgumentException("Invalid column.");
    }
    return this.board.get(numRow).get(numCol);
  }

  /**
   * Fills the ISpace at the given coordinates with the current turn's player.
   * Unlike {@link #move}, this does not flip opponent pieces.
   *
   * @param numRow the row of the ISpace.
   * @param numCol the column of the ISpace.
   * @throws IllegalArgumentException if coordinates are out of bounds.
   * @throws IllegalStateException if the space is already filled or the move is invalid.
   */
  public void fillSpace(int numRow, int numCol) {
    if (numRow >= board.size() || numRow < 0) {
      throw new IllegalArgumentException("Invalid row.");
    }
    if (numCol >= board.get(numRow).size() || numCol < 0) {
      throw new IllegalArgumentException("Invalid column.");
    }
    if (!getSpace(numRow, numCol).isEmpty()) {
      throw new IllegalStateException("Space already filled.");
    }
    if (isValidMove(numRow, numCol, this.currentTurn)) {
      getSpace(numRow, numCol).setFilled(this.currentTurn);
      changeTurn();
    } else {
      throw new IllegalStateException("Invalid move.");
    }
  }

  @Override
  public void passTurn() {
    changeTurn();
  }

  @Override
  public boolean move(int row, int col, Player player) {
    if (!player.equals(this.currentTurn)) {
      throw new IllegalStateException("Out of turn move.");
    }
    if (noValidMoves(player)) {
      changeTurn();
    }
    if (!isValidMove(row, col, player)) {
      throw new IllegalStateException("Invalid move.");
    }
    ISpace space = getSpace(row, col);
    space.setFilled(player);
    flipPieces(row, col);
    changeTurn();
    return true;
  }

  @Override
  public boolean isValidMove(int row, int col, Player player) {
    if (row < 0 || row >= getBoard().size() || col < 0 || col >= getRow(row).size()
            || !getSpace(row, col).isEmpty()) {
      return false;
    }
    for (int dr = -1; dr <= 1; dr++) {
      for (int dc = -1; dc <= 1; dc++) {
        if (dr == 0 && dc == 0) {
          continue;
        }
        int r = row + dr;
        int c = col + dc;
        boolean foundSameColor = false;
        while (r >= 0 && r < getBoard().size() && c >= 0 && c < getRow(r).size()) {
          ISpace space = getSpace(r, c);
          if (space.isEmpty()) {
            break;
          }
          if (space.getPlayer() == currentTurn) {
            if (foundSameColor) {
              return true;
            } else {
              break;
            }
          }
          foundSameColor = true;
          r += dr;
          c += dc;
        }
      }
    }
    return false;
  }

  @Override
  public int getScore(Player player) {
    int score = 0;
    for (List<ISpace> spaces : board) {
      for (ISpace space : spaces) {
        if (!space.isEmpty() && space.getPlayer().equals(player)) {
          score += 1;
        }
      }
    }
    return score;
  }

  @Override
  public boolean gameOver() {
    return gameOverByNoValidMoves() || gameOverByFullBoard()
            || getScore(Player.BLACK) == 0 || getScore(Player.WHITE) == 0;
  }

  @Override
  public boolean noValidMoves(Player player) {
    for (int row = 0; row < getBoard().size(); row++) {
      for (int col = 0; col < getRow(row).size(); col++) {
        if (getSpace(row, col).isEmpty()) {
          if (isValidMove(row, col, player)) {
            return false;
          }
        }
      }
    }
    return true;
  }

  @Override
  public int getOpponentScore(Player player) {
    if (player.equals(Player.BLACK)) {
      return getScore(Player.WHITE);
    } else {
      return getScore(Player.BLACK);
    }
  }

  @Override
  public IReversiModel copyModel() {
    return new ReversiModel(this.boardSize, this.copyBoard(), this.currentTurn);
  }

  @Override
  public void restoreFrom(IReversiModel snapshot) {
    this.currentTurn = snapshot.getCurrentTurn();
    for (int r = 0; r < this.board.size(); r++) {
      for (int c = 0; c < this.board.get(r).size(); c++) {
        this.board.get(r).get(c).setFilled(snapshot.getSpaceContent(r, c));
      }
    }
  }

  @Override
  public void loadState(Player currentTurn, Player[][] boardState) {
    this.currentTurn = currentTurn;
    for (int r = 0; r < this.board.size(); r++) {
      for (int c = 0; c < this.board.get(r).size(); c++) {
        this.board.get(r).get(c).setFilled(boardState[r][c]);
      }
    }
  }

  private boolean gameOverByFullBoard() {
    for (int row = 0; row < getBoard().size(); row++) {
      for (int col = 0; col < getRow(row).size(); col++) {
        if (getSpace(row, col).isEmpty()) {
          return false;
        }
      }
    }
    return true;
  }

  private boolean gameOverByNoValidMoves() {
    if (noValidMoves(currentTurn)) {
      changeTurn();
      return noValidMoves(currentTurn);
    }
    return false;
  }

  private void initBoard(int boardSize) {
    for (int i = 0; i < (boardSize * 2) - 1; i++) {
      board.add(new ArrayList<>());
    }
  }

  private void initSpacesInBoard(int boardSize) {
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
    this.getSpace(boardSize - 2, boardSize - 2).setFilled(Player.BLACK);
    this.getSpace(boardSize - 1, boardSize).setFilled(Player.BLACK);
    this.getSpace(boardSize, boardSize - 2).setFilled(Player.BLACK);
    this.getSpace(boardSize - 2, boardSize - 1).setFilled(Player.WHITE);
    this.getSpace(boardSize - 1, boardSize - 2).setFilled(Player.WHITE);
    this.getSpace(boardSize, boardSize - 1).setFilled(Player.WHITE);
  }

  private void changeTurn() {
    if (currentTurn == Player.BLACK) {
      currentTurn = Player.WHITE;
    } else if (currentTurn == Player.WHITE) {
      currentTurn = Player.BLACK;
    }
  }

  private void flipPieces(int row, int col) {
    for (int dr = -1; dr <= 1; dr++) {
      for (int dc = -1; dc <= 1; dc++) {
        if (dr == 0 && dc == 0) {
          continue;
        }
        int r = row + dr;
        int c = col + dc;
        boolean foundOppPlayer = false;
        List<ISpace> spacesToFlip = new ArrayList<>();
        while (isWithinBounds(r, c)) {
          ISpace adjacentSpace = getSpace(r, c);
          if (adjacentSpace.isEmpty()) {
            break;
          }
          if (adjacentSpace.getPlayer() == currentTurn) {
            if (foundOppPlayer) {
              for (ISpace spaceToFlip : spacesToFlip) {
                spaceToFlip.setFilled(currentTurn);
              }
            }
            break;
          }
          foundOppPlayer = true;
          spacesToFlip.add(adjacentSpace);
          r += dr;
          c += dc;
        }
      }
    }
  }

  private boolean isWithinBounds(int row, int col) {
    return row >= 0 && row < this.getBoard().size() && col >= 0 && col < this.getRow(row).size();
  }

  private List<List<ISpace>> copyBoard() {
    List<List<ISpace>> boardCopy = new ArrayList<>();
    for (List<ISpace> row : board) {
      List<ISpace> rowCopy = new ArrayList<>();
      for (ISpace space : row) {
        ISpace spaceCopy = new Space();
        if (!space.isEmpty()) {
          spaceCopy.setFilled(space.getPlayer());
        }
        rowCopy.add(spaceCopy);
      }
      boardCopy.add(rowCopy);
    }
    return boardCopy;
  }
}
