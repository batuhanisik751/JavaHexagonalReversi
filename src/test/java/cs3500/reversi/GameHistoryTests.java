package cs3500.reversi;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cs3500.reversi.history.GameHistory;
import cs3500.reversi.history.MoveRecord;
import cs3500.reversi.model.Coordinate;
import cs3500.reversi.model.Player;

/**
 * Tests for GameHistory and MoveRecord classes.
 */
public class GameHistoryTests {

  private GameHistory history;

  @Before
  public void setUp() {
    history = new GameHistory();
  }

  // ---- GameHistory Tests ----

  @Test
  public void testNewHistoryIsEmpty() {
    Assert.assertTrue(history.getRecords().isEmpty());
  }

  @Test
  public void testRecordMoveAddsRecord() {
    List<Coordinate> flipped = Arrays.asList(new Coordinate(1, 2));
    history.recordMove(Player.BLACK, 2, 3, flipped);
    Assert.assertEquals(1, history.getRecords().size());
    Assert.assertFalse(history.getRecords().get(0).isPass());
  }

  @Test
  public void testRecordPassAddsRecord() {
    history.recordPass(Player.WHITE);
    Assert.assertEquals(1, history.getRecords().size());
    Assert.assertTrue(history.getRecords().get(0).isPass());
  }

  @Test
  public void testMoveNumbersAutoIncrement() {
    history.recordMove(Player.BLACK, 1, 2, new ArrayList<>());
    history.recordPass(Player.WHITE);
    history.recordMove(Player.BLACK, 3, 4, new ArrayList<>());
    List<MoveRecord> records = history.getRecords();
    Assert.assertEquals(1, records.get(0).getMoveNumber());
    Assert.assertEquals(2, records.get(1).getMoveNumber());
    Assert.assertEquals(3, records.get(2).getMoveNumber());
  }

  @Test
  public void testUndoLastRemovesLastRecord() {
    history.recordMove(Player.BLACK, 1, 2, new ArrayList<>());
    history.recordPass(Player.WHITE);
    Assert.assertEquals(2, history.getRecords().size());
    history.undoLast();
    Assert.assertEquals(1, history.getRecords().size());
    Assert.assertFalse(history.getRecords().get(0).isPass());
  }

  @Test
  public void testUndoLastOnEmptyHistoryIsNoOp() {
    history.undoLast();
    Assert.assertTrue(history.getRecords().isEmpty());
  }

  @Test
  public void testUndoAllRecords() {
    history.recordMove(Player.BLACK, 0, 0, new ArrayList<>());
    history.recordPass(Player.WHITE);
    history.undoLast();
    history.undoLast();
    Assert.assertTrue(history.getRecords().isEmpty());
    // One more undo on empty should be safe
    history.undoLast();
    Assert.assertTrue(history.getRecords().isEmpty());
  }

  @Test
  public void testClearRemovesAllRecords() {
    history.recordMove(Player.BLACK, 0, 0, new ArrayList<>());
    history.recordPass(Player.WHITE);
    history.recordMove(Player.BLACK, 1, 1, new ArrayList<>());
    history.clear();
    Assert.assertTrue(history.getRecords().isEmpty());
  }

  @Test
  public void testLoadRecordsReplacesExisting() {
    history.recordMove(Player.BLACK, 0, 0, new ArrayList<>());
    List<MoveRecord> loaded = new ArrayList<>();
    loaded.add(MoveRecord.pass(1, Player.WHITE));
    loaded.add(MoveRecord.move(2, Player.BLACK, 3, 4, new ArrayList<>()));
    history.loadRecords(loaded);
    Assert.assertEquals(2, history.getRecords().size());
    Assert.assertTrue(history.getRecords().get(0).isPass());
    Assert.assertEquals(Player.WHITE, history.getRecords().get(0).getPlayer());
  }

  @Test
  public void testLoadRecordsOnEmptyHistory() {
    List<MoveRecord> loaded = new ArrayList<>();
    loaded.add(MoveRecord.move(1, Player.BLACK, 1, 2, new ArrayList<>()));
    history.loadRecords(loaded);
    Assert.assertEquals(1, history.getRecords().size());
  }

  @Test
  public void testLoadEmptyRecordsClearsHistory() {
    history.recordMove(Player.BLACK, 0, 0, new ArrayList<>());
    history.loadRecords(new ArrayList<>());
    Assert.assertTrue(history.getRecords().isEmpty());
  }

  @Test
  public void testGetRecordsReturnsUnmodifiableCopy() {
    history.recordMove(Player.BLACK, 0, 0, new ArrayList<>());
    List<MoveRecord> records = history.getRecords();
    try {
      records.add(MoveRecord.pass(2, Player.WHITE));
      Assert.fail("Expected UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      // expected
    }
    // Original history unchanged
    Assert.assertEquals(1, history.getRecords().size());
  }

  @Test
  public void testRecordAfterUndoGetsCorrectMoveNumber() {
    history.recordMove(Player.BLACK, 0, 0, new ArrayList<>());
    history.recordPass(Player.WHITE);
    history.undoLast();
    // Size is now 1, so next record should be moveNumber 2
    history.recordMove(Player.BLACK, 1, 1, new ArrayList<>());
    Assert.assertEquals(2, history.getRecords().get(1).getMoveNumber());
  }

  // ---- MoveRecord Tests ----

  @Test
  public void testMoveRecordFactoryMethod() {
    List<Coordinate> flipped = Arrays.asList(
            new Coordinate(1, 2), new Coordinate(3, 4));
    MoveRecord record = MoveRecord.move(1, Player.BLACK, 2, 3, flipped);
    Assert.assertEquals(1, record.getMoveNumber());
    Assert.assertEquals(Player.BLACK, record.getPlayer());
    Assert.assertEquals(2, record.getRow());
    Assert.assertEquals(3, record.getCol());
    Assert.assertEquals(2, record.getFlipped().size());
    Assert.assertFalse(record.isPass());
  }

  @Test
  public void testPassRecordFactoryMethod() {
    MoveRecord record = MoveRecord.pass(5, Player.WHITE);
    Assert.assertEquals(5, record.getMoveNumber());
    Assert.assertEquals(Player.WHITE, record.getPlayer());
    Assert.assertEquals(-1, record.getRow());
    Assert.assertEquals(-1, record.getCol());
    Assert.assertTrue(record.getFlipped().isEmpty());
    Assert.assertTrue(record.isPass());
  }

  @Test
  public void testMoveRecordFlippedListIsUnmodifiable() {
    List<Coordinate> flipped = new ArrayList<>();
    flipped.add(new Coordinate(1, 2));
    MoveRecord record = MoveRecord.move(1, Player.BLACK, 0, 0, flipped);
    try {
      record.getFlipped().add(new Coordinate(3, 4));
      Assert.fail("Expected UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      // expected
    }
    Assert.assertEquals(1, record.getFlipped().size());
  }

  @Test
  public void testMoveRecordFlippedListIsDefensiveCopy() {
    List<Coordinate> flipped = new ArrayList<>();
    flipped.add(new Coordinate(1, 2));
    MoveRecord record = MoveRecord.move(1, Player.BLACK, 0, 0, flipped);
    // Modify the original list
    flipped.add(new Coordinate(3, 4));
    // Record should not be affected
    Assert.assertEquals(1, record.getFlipped().size());
  }

  @Test
  public void testMoveDisplayString() {
    List<Coordinate> flipped = Arrays.asList(
            new Coordinate(1, 2), new Coordinate(3, 4));
    MoveRecord record = MoveRecord.move(1, Player.BLACK, 2, 3, flipped);
    Assert.assertEquals("1. X -> (2, 3) flipped 2", record.toDisplayString());
  }

  @Test
  public void testPassDisplayString() {
    MoveRecord record = MoveRecord.pass(2, Player.WHITE);
    Assert.assertEquals("2. O passed", record.toDisplayString());
  }

  @Test
  public void testMoveRecordWithEmptyFlippedList() {
    MoveRecord record = MoveRecord.move(1, Player.BLACK, 0, 0, new ArrayList<>());
    Assert.assertTrue(record.getFlipped().isEmpty());
    Assert.assertEquals("1. X -> (0, 0) flipped 0", record.toDisplayString());
  }
}
