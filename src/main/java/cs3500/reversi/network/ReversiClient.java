package cs3500.reversi.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import cs3500.reversi.model.Player;

/**
 * A TCP client that connects to a {@link ReversiServer} for LAN multiplayer.
 * Sends user actions to the server and dispatches received events to a
 * {@link ClientListener}.
 */
public class ReversiClient {
  private final String host;
  private final int port;
  private Socket socket;
  private BufferedReader in;
  private PrintWriter out;
  private Player assignedColor;
  private int serverBoardSize;
  private ClientListener listener;
  private volatile boolean connected;
  private Thread readerThread;

  /**
   * Creates a client that will connect to the given host and port.
   * @param host the server hostname or IP address.
   * @param port the server TCP port.
   */
  public ReversiClient(String host, int port) {
    this.host = host;
    this.port = port;
  }

  /**
   * Sets the listener that receives server events.
   * @param listener the listener, or null to clear.
   */
  public void setListener(ClientListener listener) {
    this.listener = listener;
  }

  /**
   * Connects to the server and waits for a WELCOME message.
   * @return the player color assigned by the server.
   * @throws IOException if connection or handshake fails.
   */
  public Player connect() throws IOException {
    socket = new Socket(host, port);
    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    out = new PrintWriter(socket.getOutputStream(), true);

    String welcome = in.readLine();
    if (welcome == null) {
      throw new IOException("Server closed connection before welcome");
    }
    String cmd = MessageParser.getCommand(welcome);
    if ("FULL".equals(cmd)) {
      socket.close();
      throw new IOException("Server is full — game already has two players");
    }
    if (!"WELCOME".equals(cmd)) {
      throw new IOException("Unexpected server message: " + welcome);
    }
    String payload = MessageParser.getPayload(welcome);
    String[] parts = payload.split(",", 2);
    assignedColor = Player.valueOf(parts[0]);
    serverBoardSize = parts.length > 1 ? Integer.parseInt(parts[1]) : 3;
    connected = true;
    return assignedColor;
  }

  /**
   * Starts a background thread that reads server messages and dispatches to the listener.
   * Must be called after {@link #connect()} and {@link #setListener(ClientListener)}.
   */
  public void startListening() {
    readerThread = new Thread(this::readLoop, "ReversiClient-Reader");
    readerThread.setDaemon(true);
    readerThread.start();
  }

  private void readLoop() {
    try {
      while (connected) {
        String line = in.readLine();
        if (line == null) {
          connected = false;
          if (listener != null) {
            listener.onConnectionError("Lost connection to server");
          }
          return;
        }
        dispatch(line);
      }
    } catch (IOException e) {
      if (connected) {
        connected = false;
        if (listener != null) {
          listener.onConnectionError("Connection error: " + e.getMessage());
        }
      }
    }
  }

  private void dispatch(String line) {
    if (listener == null) {
      return;
    }
    String cmd = MessageParser.getCommand(line);
    String payload = MessageParser.getPayload(line);

    switch (cmd) {
      case "STATE":
        MessageParser.StateData data = MessageParser.parseState(payload);
        listener.onStateUpdate(data.getBoardSize(), data.getCurrentTurn(), data.getBoard());
        break;
      case "YOUR_TURN":
        listener.onYourTurn();
        break;
      case "WAIT":
        listener.onWait();
        break;
      case "MOVE_MADE":
        parseMoveMade(payload);
        break;
      case "PASS_MADE":
        listener.onPassMade(Player.valueOf(payload));
        break;
      case "UNDO_OK":
        listener.onUndoOk();
        break;
      case "UNDO_DENIED":
        listener.onUndoDenied(payload);
        break;
      case "INVALID":
        listener.onInvalidMove(payload);
        break;
      case "GAME_OVER":
        parseGameOver(payload);
        break;
      case "OPPONENT_DISCONNECTED":
        listener.onOpponentDisconnected();
        break;
      case "PONG":
        // ignore keepalive response
        break;
      case "ERROR":
        listener.onConnectionError("Server error: " + payload);
        break;
      default:
        // unknown message — ignore
        break;
    }
  }

  private void parseMoveMade(String payload) {
    String[] parts = payload.split(",");
    Player player = Player.valueOf(parts[0]);
    int row = Integer.parseInt(parts[1]);
    int col = Integer.parseInt(parts[2]);
    int flipped = Integer.parseInt(parts[3]);
    listener.onMoveMade(player, row, col, flipped);
  }

  private void parseGameOver(String payload) {
    String[] parts = payload.split(",");
    int blackScore = Integer.parseInt(parts[0]);
    int whiteScore = Integer.parseInt(parts[1]);
    listener.onGameOver(blackScore, whiteScore);
  }

  // ---------------------------------------------------------------------------
  // Send commands to server
  // ---------------------------------------------------------------------------

  public void sendMove(int row, int col) {
    out.println(MessageParser.moveMessage(row, col));
  }

  public void sendPass() {
    out.println("PASS");
  }

  public void sendUndo() {
    out.println("UNDO");
  }

  public void sendQuit() {
    out.println("QUIT");
  }

  public void sendPing() {
    out.println("PING");
  }

  /** Disconnects from the server cleanly. */
  public void disconnect() {
    connected = false;
    try {
      if (out != null) {
        sendQuit();
      }
      if (socket != null) {
        socket.close();
      }
    } catch (IOException ignored) {
      // best-effort
    }
  }

  public boolean isConnected() {
    return connected;
  }

  public Player getAssignedColor() {
    return assignedColor;
  }

  /** Returns the board size received from the server during the WELCOME handshake. */
  public int getServerBoardSize() {
    return serverBoardSize;
  }
}
