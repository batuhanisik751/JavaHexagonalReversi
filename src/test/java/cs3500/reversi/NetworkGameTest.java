package cs3500.reversi;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cs3500.reversi.model.Player;
import cs3500.reversi.network.ClientListener;
import cs3500.reversi.network.ReversiClient;
import cs3500.reversi.network.ReversiServer;

/**
 * Integration tests for the network multiplayer system using loopback sockets.
 */
public class NetworkGameTest {

  private ReversiServer server;
  private int port;

  @Before
  public void setUp() throws IOException {
    server = new ReversiServer(0, 3, 5000); // port 0 = OS-assigned, 5s timeout
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
  // Raw socket tests (test protocol without ReversiClient)
  // -------------------------------------------------------------------------

  @Test
  public void testTwoClientsConnect() throws Exception {
    Socket s1 = new Socket("localhost", port);
    BufferedReader r1 = new BufferedReader(new InputStreamReader(s1.getInputStream()));

    String welcome1 = r1.readLine();
    Assert.assertEquals("WELCOME:BLACK,3", welcome1);

    Socket s2 = new Socket("localhost", port);
    BufferedReader r2 = new BufferedReader(new InputStreamReader(s2.getInputStream()));

    String welcome2 = r2.readLine();
    Assert.assertEquals("WELCOME:WHITE,3", welcome2);

    // Both should receive initial STATE
    String state1 = r1.readLine();
    Assert.assertTrue(state1.startsWith("STATE:"));

    String state2 = r2.readLine();
    Assert.assertTrue(state2.startsWith("STATE:"));
    Assert.assertEquals(state1, state2);

    // BLACK gets YOUR_TURN, WHITE gets WAIT
    String turn1 = r1.readLine();
    String turn2 = r2.readLine();
    Assert.assertEquals("YOUR_TURN", turn1);
    Assert.assertEquals("WAIT", turn2);

    s1.close();
    s2.close();
  }

  @Test
  public void testServerRejectsThirdClient() throws Exception {
    Socket s1 = new Socket("localhost", port);
    Socket s2 = new Socket("localhost", port);

    // Give server time to accept both and start reject thread
    Thread.sleep(500);

    Socket s3 = new Socket("localhost", port);
    BufferedReader r3 = new BufferedReader(new InputStreamReader(s3.getInputStream()));
    String response = r3.readLine();
    Assert.assertEquals("FULL", response);

    s1.close();
    s2.close();
    s3.close();
  }

  @Test
  public void testMoveAndStateUpdate() throws Exception {
    Socket s1 = new Socket("localhost", port);
    BufferedReader r1 = new BufferedReader(new InputStreamReader(s1.getInputStream()));
    PrintWriter w1 = new PrintWriter(s1.getOutputStream(), true);

    Socket s2 = new Socket("localhost", port);
    BufferedReader r2 = new BufferedReader(new InputStreamReader(s2.getInputStream()));

    // Drain welcome + state + turn messages
    r1.readLine(); // WELCOME
    r2.readLine(); // WELCOME
    r1.readLine(); // STATE
    r2.readLine(); // STATE
    r1.readLine(); // YOUR_TURN
    r2.readLine(); // WAIT

    // BLACK makes a valid move (0,0 on a size-3 board)
    w1.println("MOVE:1,3");

    // Both should get MOVE_MADE
    String moveMade1 = r1.readLine();
    Assert.assertTrue(moveMade1.startsWith("MOVE_MADE:BLACK"));

    String moveMade2 = r2.readLine();
    Assert.assertEquals(moveMade1, moveMade2);

    // Both get updated STATE
    String newState1 = r1.readLine();
    Assert.assertTrue(newState1.startsWith("STATE:"));
    String newState2 = r2.readLine();
    Assert.assertEquals(newState1, newState2);

    // Now WHITE should get YOUR_TURN, BLACK should get WAIT
    String wait1 = r1.readLine();
    String turn2 = r2.readLine();
    Assert.assertEquals("WAIT", wait1);
    Assert.assertEquals("YOUR_TURN", turn2);

    s1.close();
    s2.close();
  }

  @Test
  public void testInvalidMoveRejected() throws Exception {
    Socket s1 = new Socket("localhost", port);
    BufferedReader r1 = new BufferedReader(new InputStreamReader(s1.getInputStream()));
    PrintWriter w1 = new PrintWriter(s1.getOutputStream(), true);

    Socket s2 = new Socket("localhost", port);
    BufferedReader r2 = new BufferedReader(new InputStreamReader(s2.getInputStream()));

    // Drain initial messages
    r1.readLine(); r2.readLine(); // WELCOME
    r1.readLine(); r2.readLine(); // STATE
    r1.readLine(); r2.readLine(); // TURN

    // Try to move to an occupied space (center of initial setup)
    w1.println("MOVE:1,1");

    String response = r1.readLine();
    Assert.assertTrue(response.startsWith("INVALID:"));

    s1.close();
    s2.close();
  }

  @Test
  public void testPassTurn() throws Exception {
    Socket s1 = new Socket("localhost", port);
    BufferedReader r1 = new BufferedReader(new InputStreamReader(s1.getInputStream()));
    PrintWriter w1 = new PrintWriter(s1.getOutputStream(), true);

    Socket s2 = new Socket("localhost", port);
    BufferedReader r2 = new BufferedReader(new InputStreamReader(s2.getInputStream()));

    // Drain initial messages
    r1.readLine(); r2.readLine(); // WELCOME
    r1.readLine(); r2.readLine(); // STATE
    r1.readLine(); r2.readLine(); // TURN

    // BLACK passes
    w1.println("PASS");

    // Both get PASS_MADE
    String pass1 = r1.readLine();
    Assert.assertEquals("PASS_MADE:BLACK", pass1);
    String pass2 = r2.readLine();
    Assert.assertEquals("PASS_MADE:BLACK", pass2);

    s1.close();
    s2.close();
  }

  @Test
  public void testTurnAlternatesAfterMove() throws Exception {
    Socket s1 = new Socket("localhost", port);
    BufferedReader r1 = new BufferedReader(new InputStreamReader(s1.getInputStream()));
    PrintWriter w1 = new PrintWriter(s1.getOutputStream(), true);

    Socket s2 = new Socket("localhost", port);
    BufferedReader r2 = new BufferedReader(new InputStreamReader(s2.getInputStream()));
    PrintWriter w2 = new PrintWriter(s2.getOutputStream(), true);

    // Drain initial messages
    r1.readLine(); r2.readLine(); // WELCOME
    r1.readLine(); r2.readLine(); // STATE
    r1.readLine(); r2.readLine(); // TURN

    // BLACK moves
    w1.println("MOVE:1,3");
    r1.readLine(); r2.readLine(); // MOVE_MADE
    r1.readLine(); r2.readLine(); // STATE
    r1.readLine(); r2.readLine(); // WAIT/YOUR_TURN

    // Now WHITE should be able to move (it's WHITE's turn)
    w2.println("MOVE:3,0");
    String moveMade = r2.readLine();
    Assert.assertTrue("WHITE should be able to move after BLACK",
            moveMade.startsWith("MOVE_MADE:WHITE"));

    s1.close();
    s2.close();
  }

  @Test
  public void testQuitNotifiesOpponent() throws Exception {
    Socket s1 = new Socket("localhost", port);
    BufferedReader r1 = new BufferedReader(new InputStreamReader(s1.getInputStream()));
    PrintWriter w1 = new PrintWriter(s1.getOutputStream(), true);

    Socket s2 = new Socket("localhost", port);
    BufferedReader r2 = new BufferedReader(new InputStreamReader(s2.getInputStream()));

    // Drain initial messages
    r1.readLine(); r2.readLine(); // WELCOME
    r1.readLine(); r2.readLine(); // STATE
    r1.readLine(); r2.readLine(); // TURN

    // BLACK quits
    w1.println("QUIT");

    String response = r2.readLine();
    Assert.assertEquals("OPPONENT_DISCONNECTED", response);

    s1.close();
    s2.close();
  }

  // -------------------------------------------------------------------------
  // ReversiClient tests with MockClientListener
  // -------------------------------------------------------------------------

  @Test
  public void testClientReceivesBoardSizeFromServer() throws Exception {
    ReversiClient client1 = new ReversiClient("localhost", port);
    client1.connect();
    Assert.assertEquals(3, client1.getServerBoardSize());

    ReversiClient client2 = new ReversiClient("localhost", port);
    client2.connect();
    Assert.assertEquals(3, client2.getServerBoardSize());

    client1.disconnect();
    client2.disconnect();
  }

  @Test
  public void testDifferentBoardSizeServer() throws Exception {
    // Start a separate server with boardSize=5
    server.stop();
    server = new ReversiServer(0, 5, 5000);
    server.start();
    port = server.getLocalPort();

    ReversiClient client1 = new ReversiClient("localhost", port);
    client1.connect();
    Assert.assertEquals(5, client1.getServerBoardSize());

    ReversiClient client2 = new ReversiClient("localhost", port);
    client2.connect();
    Assert.assertEquals(5, client2.getServerBoardSize());

    MockClientListener listener1 = new MockClientListener();
    client1.setListener(listener1);
    client1.startListening();

    MockClientListener listener2 = new MockClientListener();
    client2.setListener(listener2);
    client2.startListening();

    Thread.sleep(500);

    // State update should have boardSize=5 with 9 rows (2*5-1)
    Assert.assertFalse(listener1.stateUpdates.isEmpty());
    Assert.assertEquals(5, listener1.stateUpdates.get(0).boardSize);

    client1.disconnect();
    client2.disconnect();
  }

  @Test
  public void testStateSyncWithCorrectBoardSize() throws Exception {
    // Verify that creating a model with the server's board size allows loadState to work
    ReversiClient client1 = new ReversiClient("localhost", port);
    client1.connect();
    ReversiClient client2 = new ReversiClient("localhost", port);
    client2.connect();

    // Create local model with server's board size (should be 3)
    cs3500.reversi.model.IReversiModel localModel =
            new cs3500.reversi.model.ReversiModel(client2.getServerBoardSize());

    MockClientListener listener2 = new MockClientListener();
    client2.setListener(listener2);
    client1.setListener(new MockClientListener());
    client1.startListening();
    client2.startListening();

    Thread.sleep(300);

    // Apply state update to local model — should not throw
    MockClientListener.StateUpdate state = listener2.stateUpdates.get(0);
    localModel.loadState(state.currentTurn, state.board);

    // Verify model has correct dimensions
    Assert.assertEquals(3, localModel.getBoardSize());
    Assert.assertEquals(5, localModel.getBoard().size()); // 2*3-1 rows

    // Make a move and verify state syncs
    client1.sendMove(1, 3);
    Thread.sleep(500);

    // Get the updated state
    Assert.assertTrue(listener2.stateUpdates.size() >= 2);
    MockClientListener.StateUpdate updatedState =
            listener2.stateUpdates.get(listener2.stateUpdates.size() - 1);
    localModel.loadState(updatedState.currentTurn, updatedState.board);

    // Verify the move is reflected
    Assert.assertEquals(cs3500.reversi.model.Player.BLACK,
            localModel.getSpaceContent(1, 3));

    client1.disconnect();
    client2.disconnect();
  }

  @Test
  public void testReversiClientConnectsAndReceivesState() throws Exception {
    ReversiClient client1 = new ReversiClient("localhost", port);
    Player color1 = client1.connect();
    Assert.assertEquals(Player.BLACK, color1);

    ReversiClient client2 = new ReversiClient("localhost", port);
    Player color2 = client2.connect();
    Assert.assertEquals(Player.WHITE, color2);

    MockClientListener listener1 = new MockClientListener();
    MockClientListener listener2 = new MockClientListener();
    client1.setListener(listener1);
    client2.setListener(listener2);
    client1.startListening();
    client2.startListening();

    // Wait for initial state + turn messages
    Thread.sleep(500);

    Assert.assertTrue(listener1.stateUpdates.size() >= 1);
    Assert.assertTrue(listener2.stateUpdates.size() >= 1);
    Assert.assertTrue(listener1.gotYourTurn);
    Assert.assertTrue(listener2.gotWait);

    client1.disconnect();
    client2.disconnect();
  }

  @Test
  public void testReversiClientSendsMove() throws Exception {
    ReversiClient client1 = new ReversiClient("localhost", port);
    client1.connect();
    ReversiClient client2 = new ReversiClient("localhost", port);
    client2.connect();

    MockClientListener listener1 = new MockClientListener();
    MockClientListener listener2 = new MockClientListener();
    client1.setListener(listener1);
    client2.setListener(listener2);
    client1.startListening();
    client2.startListening();

    Thread.sleep(300); // wait for initial messages

    // BLACK makes a move
    client1.sendMove(1, 3);
    Thread.sleep(500);

    // Both should have received a MOVE_MADE event
    Assert.assertFalse(listener1.movesMade.isEmpty());
    Assert.assertFalse(listener2.movesMade.isEmpty());
    Assert.assertEquals(Player.BLACK, listener1.movesMade.get(0).player);

    client1.disconnect();
    client2.disconnect();
  }

  @Test
  public void testReversiClientSendsPass() throws Exception {
    ReversiClient client1 = new ReversiClient("localhost", port);
    client1.connect();
    ReversiClient client2 = new ReversiClient("localhost", port);
    client2.connect();

    MockClientListener listener1 = new MockClientListener();
    MockClientListener listener2 = new MockClientListener();
    client1.setListener(listener1);
    client2.setListener(listener2);
    client1.startListening();
    client2.startListening();

    Thread.sleep(300);

    client1.sendPass();
    Thread.sleep(500);

    Assert.assertFalse(listener1.passesMade.isEmpty());
    Assert.assertEquals(Player.BLACK, listener1.passesMade.get(0));

    client1.disconnect();
    client2.disconnect();
  }

  @Test
  public void testDisconnectNotifiesOpponent() throws Exception {
    ReversiClient client1 = new ReversiClient("localhost", port);
    client1.connect();
    ReversiClient client2 = new ReversiClient("localhost", port);
    client2.connect();

    MockClientListener listener2 = new MockClientListener();
    client2.setListener(listener2);
    client1.setListener(new MockClientListener());
    client1.startListening();
    client2.startListening();

    Thread.sleep(300);

    // BLACK quits
    client1.sendQuit();
    Thread.sleep(500);

    Assert.assertTrue(listener2.opponentDisconnected);

    client2.disconnect();
  }

  @Test
  public void testPingPong() throws Exception {
    Socket s1 = new Socket("localhost", port);
    BufferedReader r1 = new BufferedReader(new InputStreamReader(s1.getInputStream()));
    PrintWriter w1 = new PrintWriter(s1.getOutputStream(), true);

    Socket s2 = new Socket("localhost", port);

    // Drain initial messages
    r1.readLine(); // WELCOME
    Thread.sleep(300); // wait for second player assignment
    r1.readLine(); // STATE
    r1.readLine(); // YOUR_TURN

    w1.println("PING");
    String pong = r1.readLine();
    Assert.assertEquals("PONG", pong);

    s1.close();
    s2.close();
  }

  @Test
  public void testUndoDeniedWhenNoMove() throws Exception {
    Socket s1 = new Socket("localhost", port);
    BufferedReader r1 = new BufferedReader(new InputStreamReader(s1.getInputStream()));
    PrintWriter w1 = new PrintWriter(s1.getOutputStream(), true);

    Socket s2 = new Socket("localhost", port);
    BufferedReader r2 = new BufferedReader(new InputStreamReader(s2.getInputStream()));

    // Drain initial messages
    r1.readLine(); r2.readLine(); // WELCOME
    r1.readLine(); r2.readLine(); // STATE
    r1.readLine(); r2.readLine(); // TURN

    // Try undo with no previous move
    w1.println("UNDO");
    String response = r1.readLine();
    Assert.assertTrue(response.startsWith("UNDO_DENIED:"));

    s1.close();
    s2.close();
  }

  // -------------------------------------------------------------------------
  // Mock listener for assertions
  // -------------------------------------------------------------------------

  static class MockClientListener implements ClientListener {
    final List<StateUpdate> stateUpdates = new CopyOnWriteArrayList<>();
    final List<MoveMade> movesMade = new CopyOnWriteArrayList<>();
    final List<Player> passesMade = new CopyOnWriteArrayList<>();
    volatile boolean gotYourTurn = false;
    volatile boolean gotWait = false;
    volatile boolean opponentDisconnected = false;
    volatile boolean undoOk = false;
    volatile String undoDeniedReason = null;
    volatile String invalidMoveReason = null;
    volatile int gameOverBlack = -1;
    volatile int gameOverWhite = -1;
    volatile String connectionError = null;

    @Override
    public void onStateUpdate(int boardSize, Player currentTurn, Player[][] boardState) {
      stateUpdates.add(new StateUpdate(boardSize, currentTurn, boardState));
    }

    @Override
    public void onYourTurn() {
      gotYourTurn = true;
    }

    @Override
    public void onWait() {
      gotWait = true;
    }

    @Override
    public void onMoveMade(Player player, int row, int col, int flippedCount) {
      movesMade.add(new MoveMade(player, row, col, flippedCount));
    }

    @Override
    public void onPassMade(Player player) {
      passesMade.add(player);
    }

    @Override
    public void onUndoOk() {
      undoOk = true;
    }

    @Override
    public void onUndoDenied(String reason) {
      undoDeniedReason = reason;
    }

    @Override
    public void onInvalidMove(String reason) {
      invalidMoveReason = reason;
    }

    @Override
    public void onGameOver(int blackScore, int whiteScore) {
      gameOverBlack = blackScore;
      gameOverWhite = whiteScore;
    }

    @Override
    public void onOpponentDisconnected() {
      opponentDisconnected = true;
    }

    @Override
    public void onConnectionError(String message) {
      connectionError = message;
    }

    static class StateUpdate {
      final int boardSize;
      final Player currentTurn;
      final Player[][] board;
      StateUpdate(int boardSize, Player currentTurn, Player[][] board) {
        this.boardSize = boardSize;
        this.currentTurn = currentTurn;
        this.board = board;
      }
    }

    static class MoveMade {
      final Player player;
      final int row;
      final int col;
      final int flippedCount;
      MoveMade(Player player, int row, int col, int flippedCount) {
        this.player = player;
        this.row = row;
        this.col = col;
        this.flippedCount = flippedCount;
      }
    }
  }
}
