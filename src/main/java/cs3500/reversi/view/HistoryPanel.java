package cs3500.reversi.view;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import java.util.List;

import cs3500.reversi.history.MoveRecord;

/**
 * A scrollable panel that displays the game's move history.
 */
public class HistoryPanel extends JPanel {
  private final DefaultListModel<String> listModel;
  private final JList<String> list;

  /**
   * Constructs a HistoryPanel with dark theme styling.
   */
  public HistoryPanel() {
    setLayout(new BorderLayout());
    setBackground(Color.DARK_GRAY);
    setPreferredSize(new Dimension(200, 0));

    JLabel title = new JLabel("  Move Log");
    title.setForeground(Color.WHITE);
    title.setFont(new Font("SansSerif", Font.BOLD, 14));
    title.setOpaque(true);
    title.setBackground(Color.DARK_GRAY);
    add(title, BorderLayout.NORTH);

    listModel = new DefaultListModel<>();
    list = new JList<>(listModel);
    list.setBackground(new Color(50, 50, 50));
    list.setForeground(Color.WHITE);
    list.setFont(new Font("Monospaced", Font.PLAIN, 12));

    JScrollPane scrollPane = new JScrollPane(list);
    scrollPane.setBorder(null);
    add(scrollPane, BorderLayout.CENTER);
  }

  /**
   * Updates the displayed history from the given records.
   * Clears and rebuilds the list, then auto-scrolls to the latest entry.
   */
  public void updateHistory(List<MoveRecord> records) {
    listModel.clear();
    for (MoveRecord record : records) {
      listModel.addElement(record.toDisplayString());
    }
    if (!listModel.isEmpty()) {
      list.ensureIndexIsVisible(listModel.size() - 1);
    }
  }
}
