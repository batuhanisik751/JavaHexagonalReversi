package cs3500.reversi.view;

import java.awt.Color;

/**
 * A high-contrast theme for accessibility — bright colors on a black background.
 */
public class HighContrastTheme implements Theme {
  @Override public Color boardBackground()   { return Color.BLACK; }
  @Override public Color hexFill()           { return new Color(30, 30, 30); }
  @Override public Color hexBorder()         { return Color.WHITE; }
  @Override public Color blackPiece()        { return new Color(0, 0, 200); }
  @Override public Color whitePiece()        { return new Color(255, 50, 50); }
  @Override public Color selectedHex()       { return Color.YELLOW; }
  @Override public Color placedHighlight()   { return new Color(0, 255, 0); }
  @Override public Color flippedHighlight()  { return new Color(255, 165, 0); }
  @Override public Color scorePanelBg()      { return Color.BLACK; }
  @Override public Color scoreLabelFg()      { return Color.WHITE; }
  @Override public Color turnLabelActive()   { return Color.YELLOW; }
  @Override public Color turnLabelInactive() { return Color.LIGHT_GRAY; }
  @Override public String name()             { return "High Contrast"; }
}
