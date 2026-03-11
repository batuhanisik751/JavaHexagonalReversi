package cs3500.reversi;

import org.junit.Assert;
import org.junit.Test;

import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.model.Player;
import cs3500.reversi.model.ReversiModel;
import cs3500.reversi.network.MessageParser;

/**
 * Tests for the network protocol message encoding and decoding in {@link MessageParser}.
 */
public class NetworkProtocolTest {

  @Test
  public void testEncodeDecodeInitialState() {
    IReversiModel model = new ReversiModel(3);
    String encoded = MessageParser.encodeState(model);
    MessageParser.StateData data = MessageParser.parseState(encoded);

    Assert.assertEquals(3, data.getBoardSize());
    Assert.assertEquals(Player.BLACK, data.getCurrentTurn());

    // Verify board dimensions match the hex grid (2*3 - 1 = 5 rows)
    Assert.assertEquals(5, data.getBoard().length);
    Assert.assertEquals(3, data.getBoard()[0].length);  // top row
    Assert.assertEquals(4, data.getBoard()[1].length);
    Assert.assertEquals(5, data.getBoard()[2].length);  // middle
    Assert.assertEquals(4, data.getBoard()[3].length);
    Assert.assertEquals(3, data.getBoard()[4].length);  // bottom row
  }

  @Test
  public void testEncodeDecodeRoundTrip() {
    IReversiModel model = new ReversiModel(3);
    // Make a move so the board isn't just the initial state
    model.move(1, 3, Player.BLACK);

    String encoded = MessageParser.encodeState(model);
    MessageParser.StateData data = MessageParser.parseState(encoded);

    Assert.assertEquals(Player.WHITE, data.getCurrentTurn());
    // Verify the placed piece
    Assert.assertEquals(Player.BLACK, data.getBoard()[1][3]);
  }

  @Test
  public void testEncodeDecodeLargerBoard() {
    IReversiModel model = new ReversiModel(5);
    String encoded = MessageParser.encodeState(model);
    MessageParser.StateData data = MessageParser.parseState(encoded);

    Assert.assertEquals(5, data.getBoardSize());
    Assert.assertEquals(9, data.getBoard().length); // 2*5 - 1 = 9 rows
  }

  @Test
  public void testStateMessageFormat() {
    IReversiModel model = new ReversiModel(3);
    String msg = MessageParser.stateMessage(model);
    Assert.assertTrue(msg.startsWith("STATE:"));
    // Verify we can parse the payload part
    String payload = msg.substring(6);
    MessageParser.StateData data = MessageParser.parseState(payload);
    Assert.assertEquals(3, data.getBoardSize());
  }

  @Test
  public void testGetCommandAndPayload() {
    Assert.assertEquals("MOVE", MessageParser.getCommand("MOVE:3,4"));
    Assert.assertEquals("3,4", MessageParser.getPayload("MOVE:3,4"));

    Assert.assertEquals("PASS", MessageParser.getCommand("PASS"));
    Assert.assertEquals("", MessageParser.getPayload("PASS"));

    Assert.assertEquals("WELCOME", MessageParser.getCommand("WELCOME:BLACK,3"));
    Assert.assertEquals("BLACK,3", MessageParser.getPayload("WELCOME:BLACK,3"));

    Assert.assertEquals("UNDO_DENIED", MessageParser.getCommand("UNDO_DENIED:no move to undo"));
    Assert.assertEquals("no move to undo", MessageParser.getPayload("UNDO_DENIED:no move to undo"));
  }

  @Test
  public void testWelcomeMessage() {
    Assert.assertEquals("WELCOME:BLACK,4", MessageParser.welcomeMessage(Player.BLACK, 4));
    Assert.assertEquals("WELCOME:WHITE,3", MessageParser.welcomeMessage(Player.WHITE, 3));
  }

  @Test
  public void testMoveMessage() {
    Assert.assertEquals("MOVE:3,4", MessageParser.moveMessage(3, 4));
    Assert.assertEquals("MOVE:0,0", MessageParser.moveMessage(0, 0));
  }

  @Test
  public void testMoveMadeMessage() {
    String msg = MessageParser.moveMadeMessage(Player.BLACK, 1, 2, 3);
    Assert.assertEquals("MOVE_MADE:BLACK,1,2,3", msg);
  }

  @Test
  public void testPassMadeMessage() {
    Assert.assertEquals("PASS_MADE:BLACK", MessageParser.passMadeMessage(Player.BLACK));
    Assert.assertEquals("PASS_MADE:WHITE", MessageParser.passMadeMessage(Player.WHITE));
  }

  @Test
  public void testGameOverMessage() {
    Assert.assertEquals("GAME_OVER:10,5", MessageParser.gameOverMessage(10, 5));
  }

  @Test
  public void testInvalidMessage() {
    Assert.assertEquals("INVALID:not your turn", MessageParser.invalidMessage("not your turn"));
  }

  @Test
  public void testUndoDeniedMessage() {
    Assert.assertEquals("UNDO_DENIED:no move", MessageParser.undoDeniedMessage("no move"));
  }

  @Test
  public void testErrorMessage() {
    Assert.assertEquals("ERROR:bad request", MessageParser.errorMessage("bad request"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseStateMalformedThrows() {
    MessageParser.parseState("garbage");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseStateUnknownCellThrows() {
    MessageParser.parseState("3,BLACK;_,_,_;_,X,_,_;_,_,_,_,_;_,_,_,_;_,_,_");
  }

  @Test
  public void testFullBoardRoundTrip() {
    // Create a model, play until board has a variety of states
    IReversiModel model = new ReversiModel(3);
    String encoded1 = MessageParser.encodeState(model);
    MessageParser.StateData data1 = MessageParser.parseState(encoded1);

    // Re-encode from the parsed data via a new model to test fidelity
    IReversiModel model2 = new ReversiModel(data1.getBoardSize());
    model2.loadState(data1.getCurrentTurn(), data1.getBoard());

    String encoded2 = MessageParser.encodeState(model2);
    Assert.assertEquals(encoded1, encoded2);
  }
}
