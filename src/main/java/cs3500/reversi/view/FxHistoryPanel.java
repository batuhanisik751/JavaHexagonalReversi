package cs3500.reversi.view;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import cs3500.reversi.history.MoveRecord;

/**
 * A JavaFX panel that displays the game's move history in a scrollable list.
 */
class FxHistoryPanel extends VBox {
  private final ObservableList<String> items;
  private final ListView<String> listView;

  /**
   * Constructs an FxHistoryPanel with dark theme styling.
   */
  FxHistoryPanel() {
    setStyle("-fx-background-color: #555555;");
    setPrefWidth(200);

    Label title = new Label("  Move Log");
    title.setStyle("-fx-text-fill: white; -fx-font-family: 'SansSerif'; "
            + "-fx-font-weight: bold; -fx-font-size: 14px; "
            + "-fx-background-color: #555555; -fx-padding: 4px;");
    title.setMaxWidth(Double.MAX_VALUE);

    items = FXCollections.observableArrayList();
    listView = new ListView<>(items);
    listView.setStyle("-fx-control-inner-background: #323232; "
            + "-fx-text-fill: white; -fx-font-family: 'Monospaced'; -fx-font-size: 12px;");
    VBox.setVgrow(listView, Priority.ALWAYS);

    getChildren().addAll(title, listView);
  }

  /**
   * Updates the displayed history from the given records.
   * Clears and rebuilds the list, then auto-scrolls to the latest entry.
   */
  void updateHistory(List<MoveRecord> records) {
    items.clear();
    for (MoveRecord record : records) {
      items.add(record.toDisplayString());
    }
    if (!items.isEmpty()) {
      listView.scrollTo(items.size() - 1);
    }
  }
}
