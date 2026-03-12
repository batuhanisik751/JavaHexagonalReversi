package cs3500.reversi.model;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Tests for IBoardShape implementations: HexBoardShape, SquareBoardShape, TriangularBoardShape.
 */
public class BoardShapeTests {

  // ==================== HexBoardShape Tests ====================

  @Test
  public void testHexShapeName() {
    Assert.assertEquals("hexagonal", new HexBoardShape().getShapeName());
  }

  @Test
  public void testHexBoardCreation3() {
    HexBoardShape shape = new HexBoardShape();
    List<List<ISpace>> board = shape.createBoard(3);
    Assert.assertEquals(5, board.size());
    Assert.assertEquals(3, board.get(0).size());
    Assert.assertEquals(4, board.get(1).size());
    Assert.assertEquals(5, board.get(2).size());
    Assert.assertEquals(4, board.get(3).size());
    Assert.assertEquals(3, board.get(4).size());
  }

  @Test
  public void testHexBoardCreation4() {
    HexBoardShape shape = new HexBoardShape();
    List<List<ISpace>> board = shape.createBoard(4);
    Assert.assertEquals(7, board.size());
    Assert.assertEquals(4, board.get(0).size());
    Assert.assertEquals(5, board.get(1).size());
    Assert.assertEquals(6, board.get(2).size());
    Assert.assertEquals(7, board.get(3).size());
    Assert.assertEquals(6, board.get(4).size());
    Assert.assertEquals(5, board.get(5).size());
    Assert.assertEquals(4, board.get(6).size());
  }

  @Test
  public void testHexInitialPieces() {
    HexBoardShape shape = new HexBoardShape();
    List<List<ISpace>> board = shape.createBoard(3);
    shape.placeInitialPieces(board, 3);
    Assert.assertEquals(Player.BLACK, board.get(1).get(1).getPlayer());
    Assert.assertEquals(Player.BLACK, board.get(2).get(3).getPlayer());
    Assert.assertEquals(Player.BLACK, board.get(3).get(1).getPlayer());
    Assert.assertEquals(Player.WHITE, board.get(1).get(2).getPlayer());
    Assert.assertEquals(Player.WHITE, board.get(2).get(1).getPlayer());
    Assert.assertEquals(Player.WHITE, board.get(3).get(2).getPlayer());
  }

  @Test
  public void testHexDirections() {
    HexBoardShape shape = new HexBoardShape();
    List<int[]> dirs = shape.getDirections(0, 0);
    Assert.assertEquals(8, dirs.size());
  }

  @Test
  public void testHexTotalRows() {
    HexBoardShape shape = new HexBoardShape();
    Assert.assertEquals(5, shape.totalRows(3));
    Assert.assertEquals(7, shape.totalRows(4));
    Assert.assertEquals(11, shape.totalRows(6));
  }

  @Test
  public void testHexCorners() {
    HexBoardShape shape = new HexBoardShape();
    List<List<ISpace>> board = shape.createBoard(3);
    List<Coordinate> corners = shape.getCorners(3, board);
    Assert.assertEquals(6, corners.size());
    Assert.assertTrue(corners.contains(new Coordinate(0, 0)));
    Assert.assertTrue(corners.contains(new Coordinate(0, 2)));
    Assert.assertTrue(corners.contains(new Coordinate(2, 0)));
    Assert.assertTrue(corners.contains(new Coordinate(2, 4)));
    Assert.assertTrue(corners.contains(new Coordinate(4, 0)));
    Assert.assertTrue(corners.contains(new Coordinate(4, 2)));
  }

  @Test
  public void testHexBoundsCheck() {
    HexBoardShape shape = new HexBoardShape();
    List<List<ISpace>> board = shape.createBoard(3);
    Assert.assertTrue(shape.isWithinBounds(board, 0, 0));
    Assert.assertTrue(shape.isWithinBounds(board, 2, 4));
    Assert.assertFalse(shape.isWithinBounds(board, -1, 0));
    Assert.assertFalse(shape.isWithinBounds(board, 5, 0));
    Assert.assertFalse(shape.isWithinBounds(board, 0, 3));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testHexInvalidSize() {
    new HexBoardShape().validateBoardSize(2);
  }

  @Test
  public void testHexBackwardCompatibility() {
    // Verify that ReversiModel(3) produces identical board to HexBoardShape
    IReversiModel modelOld = new ReversiModel(3);
    IReversiModel modelNew = new ReversiModel(3, new HexBoardShape());
    Assert.assertEquals(modelOld.getBoard().size(), modelNew.getBoard().size());
    for (int r = 0; r < modelOld.getBoard().size(); r++) {
      Assert.assertEquals(modelOld.getRow(r).size(), modelNew.getRow(r).size());
      for (int c = 0; c < modelOld.getRow(r).size(); c++) {
        Assert.assertEquals(modelOld.getSpaceContent(r, c), modelNew.getSpaceContent(r, c));
      }
    }
  }

  // ==================== SquareBoardShape Tests ====================

  @Test
  public void testSquareShapeName() {
    Assert.assertEquals("square", new SquareBoardShape().getShapeName());
  }

  @Test
  public void testSquareBoardCreation8() {
    SquareBoardShape shape = new SquareBoardShape();
    List<List<ISpace>> board = shape.createBoard(8);
    Assert.assertEquals(8, board.size());
    for (int r = 0; r < 8; r++) {
      Assert.assertEquals(8, board.get(r).size());
    }
  }

  @Test
  public void testSquareBoardCreation4() {
    SquareBoardShape shape = new SquareBoardShape();
    List<List<ISpace>> board = shape.createBoard(4);
    Assert.assertEquals(4, board.size());
    for (int r = 0; r < 4; r++) {
      Assert.assertEquals(4, board.get(r).size());
    }
  }

  @Test
  public void testSquareInitialPieces() {
    SquareBoardShape shape = new SquareBoardShape();
    List<List<ISpace>> board = shape.createBoard(8);
    shape.placeInitialPieces(board, 8);
    // Standard Othello center: W at (3,3), B at (3,4), B at (4,3), W at (4,4)
    Assert.assertEquals(Player.WHITE, board.get(3).get(3).getPlayer());
    Assert.assertEquals(Player.BLACK, board.get(3).get(4).getPlayer());
    Assert.assertEquals(Player.BLACK, board.get(4).get(3).getPlayer());
    Assert.assertEquals(Player.WHITE, board.get(4).get(4).getPlayer());
  }

  @Test
  public void testSquareDirections() {
    SquareBoardShape shape = new SquareBoardShape();
    List<int[]> dirs = shape.getDirections(0, 0);
    Assert.assertEquals(8, dirs.size());
  }

  @Test
  public void testSquareCorners() {
    SquareBoardShape shape = new SquareBoardShape();
    List<List<ISpace>> board = shape.createBoard(8);
    List<Coordinate> corners = shape.getCorners(8, board);
    Assert.assertEquals(4, corners.size());
    Assert.assertTrue(corners.contains(new Coordinate(0, 0)));
    Assert.assertTrue(corners.contains(new Coordinate(0, 7)));
    Assert.assertTrue(corners.contains(new Coordinate(7, 0)));
    Assert.assertTrue(corners.contains(new Coordinate(7, 7)));
  }

  @Test
  public void testSquareTotalRows() {
    Assert.assertEquals(8, new SquareBoardShape().totalRows(8));
    Assert.assertEquals(4, new SquareBoardShape().totalRows(4));
  }

  @Test
  public void testSquareBoundsCheck() {
    SquareBoardShape shape = new SquareBoardShape();
    List<List<ISpace>> board = shape.createBoard(8);
    Assert.assertTrue(shape.isWithinBounds(board, 0, 0));
    Assert.assertTrue(shape.isWithinBounds(board, 7, 7));
    Assert.assertFalse(shape.isWithinBounds(board, -1, 0));
    Assert.assertFalse(shape.isWithinBounds(board, 8, 0));
    Assert.assertFalse(shape.isWithinBounds(board, 0, 8));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSquareInvalidSizeOdd() {
    new SquareBoardShape().validateBoardSize(5);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSquareInvalidSizeTooSmall() {
    new SquareBoardShape().validateBoardSize(2);
  }

  // ==================== TriangularBoardShape Tests ====================

  @Test
  public void testTriangularShapeName() {
    Assert.assertEquals("triangular", new TriangularBoardShape().getShapeName());
  }

  @Test
  public void testTriangularBoardCreation5() {
    TriangularBoardShape shape = new TriangularBoardShape();
    List<List<ISpace>> board = shape.createBoard(5);
    Assert.assertEquals(5, board.size());
    Assert.assertEquals(1, board.get(0).size());
    Assert.assertEquals(3, board.get(1).size());
    Assert.assertEquals(5, board.get(2).size());
    Assert.assertEquals(7, board.get(3).size());
    Assert.assertEquals(9, board.get(4).size());
  }

  @Test
  public void testTriangularBoardCreation4() {
    TriangularBoardShape shape = new TriangularBoardShape();
    List<List<ISpace>> board = shape.createBoard(4);
    Assert.assertEquals(4, board.size());
    Assert.assertEquals(1, board.get(0).size());
    Assert.assertEquals(3, board.get(1).size());
    Assert.assertEquals(5, board.get(2).size());
    Assert.assertEquals(7, board.get(3).size());
  }

  @Test
  public void testTriangularDirectionsUpPointing() {
    TriangularBoardShape shape = new TriangularBoardShape();
    // Even col index = up-pointing
    List<int[]> dirs = shape.getDirections(2, 0);
    Assert.assertEquals(3, dirs.size());
  }

  @Test
  public void testTriangularDirectionsDownPointing() {
    TriangularBoardShape shape = new TriangularBoardShape();
    // Odd col index = down-pointing
    List<int[]> dirs = shape.getDirections(2, 1);
    Assert.assertEquals(3, dirs.size());
  }

  @Test
  public void testTriangularCorners() {
    TriangularBoardShape shape = new TriangularBoardShape();
    List<List<ISpace>> board = shape.createBoard(5);
    List<Coordinate> corners = shape.getCorners(5, board);
    Assert.assertEquals(3, corners.size());
    Assert.assertTrue(corners.contains(new Coordinate(0, 0)));
    Assert.assertTrue(corners.contains(new Coordinate(4, 0)));
    Assert.assertTrue(corners.contains(new Coordinate(4, 8)));
  }

  @Test
  public void testTriangularTotalRows() {
    Assert.assertEquals(5, new TriangularBoardShape().totalRows(5));
    Assert.assertEquals(4, new TriangularBoardShape().totalRows(4));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testTriangularInvalidSize() {
    new TriangularBoardShape().validateBoardSize(3);
  }

  @Test
  public void testTriangularBoundsCheck() {
    TriangularBoardShape shape = new TriangularBoardShape();
    List<List<ISpace>> board = shape.createBoard(5);
    Assert.assertTrue(shape.isWithinBounds(board, 0, 0));
    Assert.assertTrue(shape.isWithinBounds(board, 4, 8));
    Assert.assertFalse(shape.isWithinBounds(board, 0, 1));
    Assert.assertFalse(shape.isWithinBounds(board, -1, 0));
    Assert.assertFalse(shape.isWithinBounds(board, 5, 0));
  }

  // ==================== Cross-Shape Tests ====================

  @Test
  public void testGetBoardShapeFromModel() {
    IReversiModel hexModel = new ReversiModel(3);
    Assert.assertEquals("hexagonal", hexModel.getBoardShape().getShapeName());

    IReversiModel sqModel = new ReversiModel(8, new SquareBoardShape());
    Assert.assertEquals("square", sqModel.getBoardShape().getShapeName());

    IReversiModel triModel = new ReversiModel(5, new TriangularBoardShape());
    Assert.assertEquals("triangular", triModel.getBoardShape().getShapeName());
  }

  @Test
  public void testCopyModelPreservesShape() {
    IReversiModel sqModel = new ReversiModel(8, new SquareBoardShape());
    IReversiModel copy = sqModel.copyModel();
    Assert.assertEquals("square", copy.getBoardShape().getShapeName());
    Assert.assertEquals(8, copy.getBoardSize());
  }
}
