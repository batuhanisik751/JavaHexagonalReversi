package cs3500.reversi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single game of Reversi. Has a board made of a list of list of Spaces
 * and a current Player that holds the current turn of the game.
 */
public class ReversiModel implements IReversiModel {
  private final int boardSize;
  private final IBoardShape boardShape;
  private Player currentTurn;
  private List<List<ISpace>> board;

  /**
   * Creates a Reversi Model game with hexagonal board (backward compatible).
   *
   * @param boardSize the size of the board, based on the size of the edges of the hexagon.
   */
  public ReversiModel(int boardSize) {
    this(boardSize, new HexBoardShape());
  }

  /**
   * Creates a Reversi Model game with the given board shape.
   *
   * @param boardSize the size of the board (interpretation depends on shape).
   * @param boardShape the board shape to use.
   */
  public ReversiModel(int boardSize, IBoardShape boardShape) {
    boardShape.validateBoardSize(boardSize);
    this.boardSize = boardSize;
    this.boardShape = boardShape;
    this.board = boardShape.createBoard(boardSize);
    boardShape.placeInitialPieces(this.board, boardSize);
    this.currentTurn = Player.BLACK;
  }

  /**
   * Private copy constructor used by {@link #copyModel()}.
   */
  private ReversiModel(int boardSize, IBoardShape boardShape,
                       List<List<ISpace>> board, Player currentTurn) {
    this.boardSize = boardSize;
    this.boardShape = boardShape;
    this.board = board;
    this.currentTurn = currentTurn;
  }

  @Override
  public int getBoardSize() {
    return this.boardSize;
  }

  @Override
  public IBoardShape getBoardShape() {
    return this.boardShape;
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
    if (!boardShape.isWithinBounds(board, row, col) || !getSpace(row, col).isEmpty()) {
      return false;
    }
    for (int[] dir : boardShape.getDirections(row, col)) {
      int dr = dir[0];
      int dc = dir[1];
      int r = row + dr;
      int c = col + dc;
      boolean foundOpponent = false;
      while (boardShape.isWithinBounds(board, r, c)) {
        ISpace space = getSpace(r, c);
        if (space.isEmpty()) {
          break;
        }
        if (space.getPlayer() == currentTurn) {
          if (foundOpponent) {
            return true;
          } else {
            break;
          }
        }
        foundOpponent = true;
        r += dr;
        c += dc;
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
    for (int row = 0; row < board.size(); row++) {
      for (int col = 0; col < board.get(row).size(); col++) {
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
    return new ReversiModel(this.boardSize, this.boardShape, this.copyBoard(), this.currentTurn);
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
    for (int row = 0; row < board.size(); row++) {
      for (int col = 0; col < board.get(row).size(); col++) {
        if (getSpace(row, col).isEmpty()) {
          return false;
        }
      }
    }
    return true;
  }

  private boolean gameOverByNoValidMoves() {
    return noValidMoves(Player.BLACK) && noValidMoves(Player.WHITE);
  }

  private void changeTurn() {
    if (currentTurn == Player.BLACK) {
      currentTurn = Player.WHITE;
    } else if (currentTurn == Player.WHITE) {
      currentTurn = Player.BLACK;
    }
  }

  private void flipPieces(int row, int col) {
    for (int[] dir : boardShape.getDirections(row, col)) {
      int dr = dir[0];
      int dc = dir[1];
      int r = row + dr;
      int c = col + dc;
      boolean foundOppPlayer = false;
      List<ISpace> spacesToFlip = new ArrayList<>();
      while (boardShape.isWithinBounds(board, r, c)) {
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
