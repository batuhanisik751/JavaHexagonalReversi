package cs3500.reversi.view.legacy;

import java.awt.Color;

/**
 * A classic green board theme reminiscent of traditional Reversi/Othello.
 */
public class ClassicTheme implements Theme {
  @Override public Color boardBackground()   { return new Color(0, 100, 0); }
  @Override public Color hexFill()           { return new Color(34, 139, 34); }
  @Override public Color hexBorder()         { return new Color(0, 60, 0); }
  @Override public Color blackPiece()        { return Color.BLACK; }
  @Override public Color whitePiece()        { return Color.WHITE; }
  @Override public Color selectedHex()       { return new Color(144, 238, 144); }
  @Override public Color placedHighlight()   { return Color.YELLOW; }
  @Override public Color flippedHighlight()  { return new Color(255, 165, 0); }
  @Override public Color scorePanelBg()      { return new Color(0, 80, 0); }
  @Override public Color scoreLabelFg()      { return Color.WHITE; }
  @Override public Color turnLabelActive()   { return Color.YELLOW; }
  @Override public Color turnLabelInactive() { return Color.LIGHT_GRAY; }
  @Override public String name()             { return "Classic Green"; }
}
