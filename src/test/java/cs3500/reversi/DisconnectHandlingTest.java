package cs3500.reversi;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cs3500.reversi.controller.NetworkController;
import cs3500.reversi.controller.ViewListener;
import cs3500.reversi.history.GameHistory;
import cs3500.reversi.history.MoveRecord;
import cs3500.reversi.model.Coordinate;
import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.model.Player;
import cs3500.reversi.model.ReversiModel;
import cs3500.reversi.network.ReversiClient;
import cs3500.reversi.network.ReversiServer;
import cs3500.reversi.view.IGraphicsView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for network disconnect handling:
 * - Server fires onClientDisconnect callback
 * - NetworkController shows disconnect dialog and runs disconnect action
 */
public class DisconnectHandlingTest {

  private ReversiServer server;
  private int port;

  @Before
  public void setUp() throws IOException {
    server = new ReversiServer(0, 3, 2000); // 2s timeout for faster tests
    server.start();
    port = server.getLocalPort();
  }

  @After
  public void tearDown() {
    if (server != null) {
      server.stop();
    }
  }

  // -------------------------------------------------------------------------
  // Server-side: onClientDisconnect callback tests
  // -------------------------------------------------------------------------

  @Test
  public void testServerFiresOnClientDisconnectOnEOF() throws Exception {
    MockServerListener listener = new MockServerListener();
    server.setListener(listener);

    Socket s1 = new Socket("localhost", port);
    BufferedReader r1 = new BufferedReader(new InputStreamReader(s1.getInputStream()));

    Socket s2 = new Socket("localhost", port);
    BufferedReader r2 = new BufferedReader(new InputStreamReader(s2.getInputStream()));

    // Read welcome + state + turn for both
    r1.readLine(); // WELCOME
    r2.readLine(); // WELCOME
    r1.readLine(); // STATE
    r2.readLine(); // STATE
    r1.readLine(); // YOUR_TURN or WAIT
    r2.readLine(); // YOUR_TURN or WAIT

    // Close s1 (BLACK) — EOF on server
    s1.close();
    Thread.sleep(500);

    // Server should have fired onClientDisconnect for BLACK
    assertTrue("onClientDisconnect should fire on EOF",
            listener.disconnectedPlayers.size() >= 1);
    assertEquals(Player.BLACK, listener.disconnectedPlayers.get(0));

    // s2 should receive OPPONENT_DISCONNECTED
    String msg = r2.readLine();
    assertEquals("OPPONENT_DISCONNECTED", msg);

    s2.close();
  }

  @Test
  public void testServerFiresOnClientDisconnectOnQuit() throws Exception {
    MockServerListener listener = new MockServerListener();
    server.setListener(listener);

    Socket s1 = new Socket("localhost", port);
    BufferedReader r1 = new BufferedReader(new InputStreamReader(s1.getInputStream()));
    PrintWriter w1 = new PrintWriter(s1.getOutputStream(), true);

    Socket s2 = new Socket("localhost", port);
    BufferedReader r2 = new BufferedReader(new InputStreamReader(s2.getInputStream()));

    // Read welcome + state + turn
    r1.readLine(); r2.readLine(); // WELCOME
    r1.readLine(); r2.readLine(); // STATE
    r1.readLine(); r2.readLine(); // TURN

    // BLACK sends QUIT
    w1.println("QUIT");
    Thread.sleep(500);

    assertTrue("onClientDisconnect should fire on QUIT",
            listener.disconnectedPlayers.size() >= 1);
    assertEquals(Player.BLACK, listener.disconnectedPlayers.get(0));

    String msg = r2.readLine();
    assertEquals("OPPONENT_DISCONNECTED", msg);

    s1.close();
    s2.close();
  }

  @Test
  public void testServerFiresOnClientDisconnectOnTimeout() throws Exception {
    // Use a server with very short timeout
    server.stop();
    server = new ReversiServer(0, 3, 500); // 500ms timeout
    server.start();
    port = server.getLocalPort();

    MockServerListener listener = new MockServerListener();
    server.setListener(listener);

    Socket s1 = new Socket("localhost", port);
    BufferedReader r1 = new BufferedReader(new InputStreamReader(s1.getInputStream()));

    Socket s2 = new Socket("localhost", port);
    BufferedReader r2 = new BufferedReader(new InputStreamReader(s2.getInputStream()));

    // Read welcome + state + turn
    r1.readLine(); r2.readLine();
    r1.readLine(); r2.readLine();
    r1.readLine(); r2.readLine();

    // Don't send anything — BLACK will time out
    Thread.sleep(1000);

    assertTrue("onClientDisconnect should fire on timeout",
            listener.disconnectedPlayers.size() >= 1);
    assertEquals(Player.BLACK, listener.disconnectedPlayers.get(0));

    s1.close();
    s2.close();
  }

  // -------------------------------------------------------------------------
  // NetworkController: disconnect dialog + action tests
  // -------------------------------------------------------------------------

  @Test
  public void testNetworkControllerShowsDisconnectDialogOnOpponentDisconnect() {
    IReversiModel model = new ReversiModel(3);
    MockDisconnectView view = new MockDisconnectView("return");
    ReversiClient client = new ReversiClient("localhost", port);
    GameHistory history = new GameHistory();

    NetworkController controller = new NetworkController(model, view, client, history);

    // Simulate opponent disconnect (call directly since we're testing the handler)
    controller.handleDisconnect();

    assertTrue("showDisconnectDialog should be called", view.disconnectDialogShown);
  }

  @Test
  public void testNetworkControllerRunsDisconnectAction() {
    IReversiModel model = new ReversiModel(3);
    MockDisconnectView view = new MockDisconnectView("return");
    ReversiClient client = new ReversiClient("localhost", port);
    GameHistory history = new GameHistory();

    NetworkController controller = new NetworkController(model, view, client, history);
    boolean[] actionRan = {false};
    controller.setDisconnectAction(() -> actionRan[0] = true);

    controller.handleDisconnect();

    assertTrue("disconnectAction should run after dialog", actionRan[0]);
  }

  @Test
  public void testNetworkControllerConnectionErrorTriggersDisconnect() {
    IReversiModel model = new ReversiModel(3);
    MockDisconnectView view = new MockDisconnectView("return");
    ReversiClient client = new ReversiClient("localhost", port);
    GameHistory history = new GameHistory();

    NetworkController controller = new NetworkController(model, view, client, history);
    boolean[] actionRan = {false};
    controller.setDisconnectAction(() -> actionRan[0] = true);

    // Call handleDisconnect directly (onConnectionError wraps in Platform.runLater)
    controller.handleDisconnect();

    assertTrue("showDisconnectDialog should be called on connection error",
            view.disconnectDialogShown);
    assertTrue("disconnectAction should run after connection error dialog", actionRan[0]);
  }

  @Test
  public void testNetworkControllerSaveLoopThenReturn() {
    IReversiModel model = new ReversiModel(3);
    // First call returns "save", second returns "return"
    MockDisconnectView view = new MockDisconnectView("save", "return");
    ReversiClient client = new ReversiClient("localhost", port);
    GameHistory history = new GameHistory();

    NetworkController controller = new NetworkController(model, view, client, history);
    boolean[] actionRan = {false};
    controller.setDisconnectAction(() -> actionRan[0] = true);

    controller.handleDisconnect();

    assertEquals("Dialog should be shown twice (save + return)", 2, view.dialogCallCount);
    assertTrue("disconnectAction should run after return", actionRan[0]);
  }

  // -------------------------------------------------------------------------
  // Mock classes
  // -------------------------------------------------------------------------

  private static class MockServerListener implements ReversiServer.ServerListener {
    final List<Player> disconnectedPlayers = new CopyOnWriteArrayList<>();

    @Override
    public void onServerLog(String message) {
      // no-op
    }

    @Override
    public void onClientDisconnect(Player disconnectedPlayer) {
      disconnectedPlayers.add(disconnectedPlayer);
    }
  }

  /**
   * Mock view that records disconnect dialog calls and returns pre-configured responses.
   */
  private static class MockDisconnectView implements IGraphicsView {
    private final String[] responses;
    volatile boolean disconnectDialogShown = false;
    volatile int dialogCallCount = 0;

    MockDisconnectView(String... responses) {
      this.responses = responses;
    }

    @Override
    public String showDisconnectDialog() {
      disconnectDialogShown = true;
      int idx = Math.min(dialogCallCount, responses.length - 1);
      dialogCallCount++;
      return responses[idx];
    }

    @Override
    public void makeVisible() { }

    @Override
    public void setViewListener(ViewListener listener) { }

    @Override
    public void refresh() { }

    @Override
    public void outOfTurnMessage() { }

    @Override
    public void invalidMoveMessage() { }

    @Override
    public void gameOver(int blackScore, int whiteScore) { }

    @Override
    public void playerTurn() { }

    @Override
    public void highlightLastMove(int placedRow, int placedCol, List<Coordinate> flipped) { }

    @Override
    public void undoNotAvailableMessage() { }

    @Override
    public void updateHistory(List<MoveRecord> records) { }

    @Override
    public void showSaveSuccess() { }

    @Override
    public void showLoadSuccess() { }

    @Override
    public void showFileError(String message) { }
  }
}
