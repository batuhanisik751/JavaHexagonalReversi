package cs3500.reversi.view;

import java.awt.Color;

/**
 * Defines the color palette for rendering the Reversi board and UI.
 */
public interface Theme {
  Color boardBackground();
  Color hexFill();
  Color hexBorder();
  Color blackPiece();
  Color whitePiece();
  Color selectedHex();
  Color placedHighlight();
  Color flippedHighlight();
  Color scorePanelBg();
  Color scoreLabelFg();
  Color turnLabelActive();
  Color turnLabelInactive();
  String name();
}
