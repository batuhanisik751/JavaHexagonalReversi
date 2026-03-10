package cs3500.reversi.view;

import java.awt.Color;

/**
 * The default dark theme — preserves the original color scheme.
 */
public class DarkTheme implements Theme {
  @Override public Color boardBackground()   { return Color.DARK_GRAY; }
  @Override public Color hexFill()           { return Color.GRAY; }
  @Override public Color hexBorder()         { return Color.BLACK; }
  @Override public Color blackPiece()        { return Color.BLACK; }
  @Override public Color whitePiece()        { return Color.WHITE; }
  @Override public Color selectedHex()       { return Color.CYAN; }
  @Override public Color placedHighlight()   { return new Color(0, 200, 0); }
  @Override public Color flippedHighlight()  { return new Color(255, 165, 0); }
  @Override public Color scorePanelBg()      { return Color.DARK_GRAY; }
  @Override public Color scoreLabelFg()      { return Color.WHITE; }
  @Override public Color turnLabelActive()   { return Color.YELLOW; }
  @Override public Color turnLabelInactive() { return Color.LIGHT_GRAY; }
  @Override public String name()             { return "Dark"; }
}
