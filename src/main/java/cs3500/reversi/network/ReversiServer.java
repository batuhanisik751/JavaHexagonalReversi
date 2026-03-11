package cs3500.reversi.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.model.Player;
import cs3500.reversi.model.ReversiModel;

/**
 * A TCP game server for LAN Reversi multiplayer. The server owns the authoritative
 * game model, accepts exactly two client connections, and processes moves sequentially.
 * All model access happens on the single game-loop thread — no synchronization needed.
 */
public class ReversiServer {
  private final int port;
  private final int boardSize;
  private final int timeoutMs;

  private IReversiModel model;
  private ServerSocket serverSocket;
  private ClientConnection blackClient;
  private ClientConnection whiteClient;
  private volatile boolean running;

  // Undo support
  private IReversiModel lastSnapshot;
  private Player lastMovePlayer;

  // Listener for server events (logging, UI updates on host)
  private ServerListener listener;

  /**
   * Creates a new server that will listen on the given port.
   * @param port the TCP port to listen on (0 for OS-assigned).
   * @param boardSize the board size for new games.
   * @param timeoutMs move timeout in milliseconds (0 for no timeout).
   */
  public ReversiServer(int port, int boardSize, int timeoutMs) {
    this.port = port;
    this.boardSize = boardSize;
    this.timeoutMs = timeoutMs;
  }

  /**
   * Sets the server event listener.
   * @param listener the listener, or null to clear.
   */
  public void setListener(ServerListener listener) {
    this.listener = listener;
  }

  /**
   * Starts the server on a background thread. Returns immediately.
   * The server accepts two connections, then enters the game loop.
   */
  public void start() throws IOException {
    serverSocket = new ServerSocket(port);
    running = true;
    Thread serverThread = new Thread(this::run, "ReversiServer");
    serverThread.setDaemon(true);
    serverThread.start();
  }

  /** Returns the actual port the server is listening on. */
  public int getLocalPort() {
    return serverSocket.getLocalPort();
  }

  /** Stops the server and disconnects all clients. */
  public void stop() {
    running = false;
    closeQuietly(serverSocket);
    if (blackClient != null) {
      blackClient.close();
    }
    if (whiteClient != null) {
      whiteClient.close();
    }
  }

  private void run() {
    try {
      log("Server started on port " + serverSocket.getLocalPort());
      acceptConnections();
      if (!running) {
        return;
      }
      model = new ReversiModel(boardSize);
      broadcastState();
      sendTurnNotifications();
      gameLoop();
    } catch (IOException e) {
      if (running) {
        log("Server error: " + e.getMessage());
      }
    } finally {
      stop();
    }
  }

  private void acceptConnections() throws IOException {
    log("Waiting for Player 1 (BLACK)...");
    Socket s1 = serverSocket.accept();
    if (!running) {
      return;
    }
    blackClient = new ClientConnection(s1, Player.BLACK);
    blackClient.send(MessageParser.welcomeMessage(Player.BLACK, boardSize));
    log("Player 1 (BLACK) connected from " + s1.getRemoteSocketAddress());

    log("Waiting for Player 2 (WHITE)...");
    // Accept more connections — only the second is WHITE, extras get FULL
    Socket s2 = serverSocket.accept();
    if (!running) {
      return;
    }
    whiteClient = new ClientConnection(s2, Player.WHITE);
    whiteClient.send(MessageParser.welcomeMessage(Player.WHITE, boardSize));
    log("Player 2 (WHITE) connected from " + s2.getRemoteSocketAddress());

    // From now on, reject any extra connections in background
    startRejectThread();
  }

  private void startRejectThread() {
    Thread rejectThread = new Thread(() -> {
      while (running && !serverSocket.isClosed()) {
        try {
          Socket extra = serverSocket.accept();
          new ClientConnection(extra, Player.BLACK).send("FULL");
          extra.close();
        } catch (IOException ignored) {
          break;
        }
      }
    }, "ReversiServer-Reject");
    rejectThread.setDaemon(true);
    rejectThread.start();
  }

  private void gameLoop() throws IOException {
    while (running && !model.gameOver()) {
      ClientConnection current = currentClient();
      if (timeoutMs > 0) {
        current.setSoTimeout(timeoutMs);
      }

      String line;
      try {
        line = current.readLine();
      } catch (SocketTimeoutException e) {
        log(current.getColor() + " timed out.");
        notifyDisconnect(current);
        otherClient(current).send("OPPONENT_DISCONNECTED");
        return;
      }

      if (line == null) {
        // Client disconnected
        log(current.getColor() + " disconnected.");
        notifyDisconnect(current);
        otherClient(current).send("OPPONENT_DISCONNECTED");
        return;
      }

      String cmd = MessageParser.getCommand(line);
      String payload = MessageParser.getPayload(line);

      switch (cmd) {
        case "MOVE":
          handleMove(current, payload);
          break;
        case "PASS":
          handlePass(current);
          break;
        case "UNDO":
          handleUndo(current);
          break;
        case "PING":
          current.send("PONG");
          break;
        case "QUIT":
          log(current.getColor() + " quit.");
          notifyDisconnect(current);
          otherClient(current).send("OPPONENT_DISCONNECTED");
          return;
        default:
          current.send(MessageParser.errorMessage("Unknown command: " + cmd));
          break;
      }
    }

    // Game over
    if (model.gameOver()) {
      int blackScore = model.getScore(Player.BLACK);
      int whiteScore = model.getScore(Player.WHITE);
      String msg = MessageParser.gameOverMessage(blackScore, whiteScore);
      blackClient.send(msg);
      whiteClient.send(msg);
      log("Game over. BLACK=" + blackScore + " WHITE=" + whiteScore);
    }
  }

  private void handleMove(ClientConnection sender, String payload) {
    Player senderColor = sender.getColor();
    if (model.getCurrentTurn() != senderColor) {
      sender.send(MessageParser.invalidMessage("Not your turn"));
      return;
    }

    String[] parts = payload.split(",");
    if (parts.length != 2) {
      sender.send(MessageParser.invalidMessage("Malformed move"));
      return;
    }

    int row;
    int col;
    try {
      row = Integer.parseInt(parts[0]);
      col = Integer.parseInt(parts[1]);
    } catch (NumberFormatException e) {
      sender.send(MessageParser.invalidMessage("Invalid coordinates"));
      return;
    }

    // Snapshot for undo
    Player[][] before = snapshotBoard();
    IReversiModel undoSnapshot = model.copyModel();

    try {
      model.move(row, col, senderColor);
    } catch (IllegalStateException e) {
      sender.send(MessageParser.invalidMessage(e.getMessage()));
      return;
    }

    // Count flipped pieces
    int flippedCount = countFlipped(before, row, col);
    lastSnapshot = undoSnapshot;
    lastMovePlayer = senderColor;

    broadcastAll(MessageParser.moveMadeMessage(senderColor, row, col, flippedCount));
    broadcastState();

    if (model.gameOver()) {
      return; // gameLoop will handle game over
    }
    sendTurnNotifications();
  }

  private void handlePass(ClientConnection sender) {
    Player senderColor = sender.getColor();
    if (model.getCurrentTurn() != senderColor) {
      sender.send(MessageParser.invalidMessage("Not your turn"));
      return;
    }

    lastSnapshot = model.copyModel();
    lastMovePlayer = senderColor;
    model.passTurn();

    broadcastAll(MessageParser.passMadeMessage(senderColor));
    broadcastState();

    if (model.gameOver()) {
      return;
    }
    sendTurnNotifications();
  }

  private void handleUndo(ClientConnection sender) {
    Player senderColor = sender.getColor();
    if (lastSnapshot == null) {
      sender.send(MessageParser.undoDeniedMessage("No move to undo"));
      return;
    }
    if (lastMovePlayer != senderColor) {
      sender.send(MessageParser.undoDeniedMessage("Can only undo your own move"));
      return;
    }

    model.restoreFrom(lastSnapshot);
    lastSnapshot = null;
    lastMovePlayer = null;

    broadcastAll("UNDO_OK");
    broadcastState();
    sendTurnNotifications();
  }

  // ---------------------------------------------------------------------------
  // Helpers
  // ---------------------------------------------------------------------------

  private ClientConnection currentClient() {
    return model.getCurrentTurn() == Player.BLACK ? blackClient : whiteClient;
  }

  private ClientConnection otherClient(ClientConnection c) {
    return c == blackClient ? whiteClient : blackClient;
  }

  private void broadcastState() {
    String msg = MessageParser.stateMessage(model);
    blackClient.send(msg);
    whiteClient.send(msg);
  }

  private void broadcastAll(String message) {
    blackClient.send(message);
    whiteClient.send(message);
  }

  private void sendTurnNotifications() {
    if (model.getCurrentTurn() == Player.BLACK) {
      blackClient.send("YOUR_TURN");
      whiteClient.send("WAIT");
    } else {
      whiteClient.send("YOUR_TURN");
      blackClient.send("WAIT");
    }
  }

  private Player[][] snapshotBoard() {
    int rows = model.getBoard().size();
    Player[][] snapshot = new Player[rows][];
    for (int r = 0; r < rows; r++) {
      int cols = model.getRow(r).size();
      snapshot[r] = new Player[cols];
      for (int c = 0; c < cols; c++) {
        snapshot[r][c] = model.getSpaceContent(r, c);
      }
    }
    return snapshot;
  }

  private int countFlipped(Player[][] before, int moveRow, int moveCol) {
    int count = 0;
    for (int r = 0; r < model.getBoard().size(); r++) {
      for (int c = 0; c < model.getRow(r).size(); c++) {
        if (r == moveRow && c == moveCol) {
          continue;
        }
        Player afterPlayer = model.getSpaceContent(r, c);
        if (afterPlayer != null && afterPlayer != before[r][c]) {
          count++;
        }
      }
    }
    return count;
  }

  private void notifyDisconnect(ClientConnection client) {
    if (listener != null) {
      listener.onClientDisconnect(client.getColor());
    }
  }

  private void log(String message) {
    if (listener != null) {
      listener.onServerLog(message);
    }
  }

  private static void closeQuietly(ServerSocket ss) {
    if (ss != null) {
      try {
        ss.close();
      } catch (IOException ignored) {
        // best effort
      }
    }
  }

  /**
   * Callback interface for server-side logging and status events.
   */
  public interface ServerListener {
    void onServerLog(String message);

    /**
     * Called when a client disconnects mid-game (EOF, timeout, or quit).
     * @param disconnectedPlayer the player that disconnected.
     */
    default void onClientDisconnect(Player disconnectedPlayer) {
      // no-op by default
    }
  }
}
