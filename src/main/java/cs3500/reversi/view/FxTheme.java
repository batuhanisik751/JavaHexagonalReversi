package cs3500.reversi.view;

import javafx.scene.paint.Color;

/**
 * Defines the color palette for rendering the Reversi board and UI using JavaFX colors.
 */
public interface FxTheme {
  Color boardBackground();
  Color hexFill();
  Color hexBorder();
  Color blackPiece();
  Color whitePiece();
  Color selectedHex();
  Color focusedHex();
  Color placedHighlight();
  Color flippedHighlight();
  Color scorePanelBg();
  Color scoreLabelFg();
  Color turnLabelActive();
  Color turnLabelInactive();
  String name();
}
