package cs3500.reversi.view;

import javafx.scene.paint.Color;

/**
 * A classic green board theme reminiscent of traditional Reversi/Othello (JavaFX version).
 */
public class FxClassicTheme implements FxTheme {
  @Override public Color boardBackground()   { return Color.rgb(0, 100, 0); }
  @Override public Color hexFill()           { return Color.rgb(34, 139, 34); }
  @Override public Color hexBorder()         { return Color.rgb(0, 60, 0); }
  @Override public Color blackPiece()        { return Color.BLACK; }
  @Override public Color whitePiece()        { return Color.WHITE; }
  @Override public Color selectedHex()       { return Color.rgb(144, 238, 144); }
  @Override public Color placedHighlight()   { return Color.YELLOW; }
  @Override public Color flippedHighlight()  { return Color.rgb(255, 165, 0); }
  @Override public Color scorePanelBg()      { return Color.rgb(0, 80, 0); }
  @Override public Color scoreLabelFg()      { return Color.WHITE; }
  @Override public Color turnLabelActive()   { return Color.YELLOW; }
  @Override public Color turnLabelInactive() { return Color.LIGHTGRAY; }
  @Override public String name()             { return "Classic Green"; }
}
