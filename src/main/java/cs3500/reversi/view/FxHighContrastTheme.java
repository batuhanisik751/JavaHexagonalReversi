package cs3500.reversi.view;

import javafx.scene.paint.Color;

/**
 * A high-contrast theme for accessibility — bright colors on a black background (JavaFX version).
 */
public class FxHighContrastTheme implements FxTheme {
  @Override public Color boardBackground()   { return Color.BLACK; }
  @Override public Color hexFill()           { return Color.rgb(30, 30, 30); }
  @Override public Color hexBorder()         { return Color.WHITE; }
  @Override public Color blackPiece()        { return Color.rgb(0, 0, 200); }
  @Override public Color whitePiece()        { return Color.rgb(255, 50, 50); }
  @Override public Color selectedHex()       { return Color.YELLOW; }
  @Override public Color focusedHex()        { return Color.MAGENTA; }
  @Override public Color placedHighlight()   { return Color.rgb(0, 255, 0); }
  @Override public Color flippedHighlight()  { return Color.rgb(255, 165, 0); }
  @Override public Color scorePanelBg()      { return Color.BLACK; }
  @Override public Color scoreLabelFg()      { return Color.WHITE; }
  @Override public Color turnLabelActive()   { return Color.YELLOW; }
  @Override public Color turnLabelInactive() { return Color.LIGHTGRAY; }
  @Override public String name()             { return "High Contrast"; }
}
