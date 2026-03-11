package cs3500.reversi.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import cs3500.reversi.model.Player;

/**
 * Wraps a TCP socket for one connected client, providing line-oriented I/O.
 */
class ClientConnection {
  private final Socket socket;
  private final BufferedReader in;
  private final PrintWriter out;
  private final Player assignedColor;

  /**
   * Creates a connection wrapper for the given socket, assigning the specified color.
   * @param socket the client's TCP socket.
   * @param color the player color assigned to this client.
   * @throws IOException if stream creation fails.
   */
  ClientConnection(Socket socket, Player color) throws IOException {
    this.socket = socket;
    this.assignedColor = color;
    this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    this.out = new PrintWriter(socket.getOutputStream(), true);
  }

  /** Sends a single line message to this client (auto-flushes). */
  void send(String message) {
    out.println(message);
  }

  /** Blocking read of one line from this client. Returns null on EOF/disconnect. */
  String readLine() throws IOException {
    return in.readLine();
  }

  /** Closes the underlying socket and streams. */
  void close() {
    try {
      socket.close();
    } catch (IOException ignored) {
      // best-effort close
    }
  }

  Player getColor() {
    return assignedColor;
  }

  boolean isClosed() {
    return socket.isClosed();
  }

  /**
   * Sets the read timeout on this connection's socket.
   * @param millis timeout in milliseconds; 0 means infinite.
   * @throws IOException if setting the timeout fails.
   */
  void setSoTimeout(int millis) throws IOException {
    socket.setSoTimeout(millis);
  }
}
