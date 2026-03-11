package cs3500.reversi.view;

import javafx.scene.paint.Color;

/**
 * The default dark theme — preserves the original color scheme (JavaFX version).
 */
public class FxDarkTheme implements FxTheme {
  @Override public Color boardBackground()   { return Color.DARKGRAY; }
  @Override public Color hexFill()           { return Color.GRAY; }
  @Override public Color hexBorder()         { return Color.BLACK; }
  @Override public Color blackPiece()        { return Color.BLACK; }
  @Override public Color whitePiece()        { return Color.WHITE; }
  @Override public Color selectedHex()       { return Color.CYAN; }
  @Override public Color placedHighlight()   { return Color.rgb(0, 200, 0); }
  @Override public Color flippedHighlight()  { return Color.rgb(255, 165, 0); }
  @Override public Color scorePanelBg()      { return Color.DARKGRAY; }
  @Override public Color scoreLabelFg()      { return Color.WHITE; }
  @Override public Color turnLabelActive()   { return Color.YELLOW; }
  @Override public Color turnLabelInactive() { return Color.LIGHTGRAY; }
  @Override public String name()             { return "Dark"; }
}
