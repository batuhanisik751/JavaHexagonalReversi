package cs3500.reversi.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * A modal setup dialog that lets users configure the game before starting.
 * Replaces the need for command-line arguments.
 */
public class SetupDialog extends JDialog {
  private static final Color BG = Color.DARK_GRAY;
  private static final Color FG = Color.WHITE;
  private static final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 14);
  private static final Font FIELD_FONT = new Font("SansSerif", Font.PLAIN, 14);
  private static final String[] PLAYER_OPTIONS =
          {"Human", "AI - Easy", "AI - Medium", "AI - Hard"};
  private static final String[] THEME_OPTIONS = {"Dark", "Classic Green", "High Contrast"};

  private final JSpinner boardSizeSpinner;
  private final JComboBox<String> player1Combo;
  private final JComboBox<String> player2Combo;
  private final JComboBox<String> themeCombo;
  private boolean confirmed;

  /**
   * Creates and lays out the setup dialog. Call {@code setVisible(true)} to show it.
   */
  public SetupDialog() {
    super((java.awt.Frame) null, "Reversi — New Game", true);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setResizable(false);

    JPanel content = new JPanel(new GridBagLayout());
    content.setBackground(BG);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(8, 12, 8, 12);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    // Title
    JLabel title = new JLabel("Game Setup");
    title.setForeground(FG);
    title.setFont(new Font("SansSerif", Font.BOLD, 20));
    title.setHorizontalAlignment(JLabel.CENTER);
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    content.add(title, gbc);

    gbc.gridwidth = 1;

    // Board Size
    gbc.gridx = 0;
    gbc.gridy = 1;
    content.add(styledLabel("Board Size:"), gbc);

    boardSizeSpinner = new JSpinner(new SpinnerNumberModel(4, 3, 8, 1));
    boardSizeSpinner.setFont(FIELD_FONT);
    boardSizeSpinner.setPreferredSize(new Dimension(180, 30));
    gbc.gridx = 1;
    content.add(boardSizeSpinner, gbc);

    // Player 1
    gbc.gridx = 0;
    gbc.gridy = 2;
    content.add(styledLabel("Player 1 (Black):"), gbc);

    player1Combo = new JComboBox<>(PLAYER_OPTIONS);
    player1Combo.setFont(FIELD_FONT);
    player1Combo.setPreferredSize(new Dimension(180, 30));
    gbc.gridx = 1;
    content.add(player1Combo, gbc);

    // Player 2
    gbc.gridx = 0;
    gbc.gridy = 3;
    content.add(styledLabel("Player 2 (White):"), gbc);

    player2Combo = new JComboBox<>(PLAYER_OPTIONS);
    player2Combo.setFont(FIELD_FONT);
    player2Combo.setPreferredSize(new Dimension(180, 30));
    gbc.gridx = 1;
    content.add(player2Combo, gbc);

    // Theme
    gbc.gridx = 0;
    gbc.gridy = 4;
    content.add(styledLabel("Theme:"), gbc);

    themeCombo = new JComboBox<>(THEME_OPTIONS);
    themeCombo.setFont(FIELD_FONT);
    themeCombo.setPreferredSize(new Dimension(180, 30));
    gbc.gridx = 1;
    content.add(themeCombo, gbc);

    // Start button
    JButton startButton = new JButton("Start Game");
    startButton.setFont(LABEL_FONT);
    startButton.setFocusPainted(false);
    startButton.addActionListener(e -> {
      confirmed = true;
      dispose();
    });

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.setBackground(BG);
    buttonPanel.add(startButton);
    gbc.gridx = 0;
    gbc.gridy = 5;
    gbc.gridwidth = 2;
    gbc.insets = new Insets(16, 12, 12, 12);
    content.add(buttonPanel, gbc);

    setContentPane(content);
    pack();
    setLocationRelativeTo(null);
  }

  private JLabel styledLabel(String text) {
    JLabel label = new JLabel(text);
    label.setForeground(FG);
    label.setFont(LABEL_FONT);
    return label;
  }

  /**
   * Returns whether the user clicked "Start Game" (true) or closed the dialog (false).
   */
  public boolean isConfirmed() {
    return confirmed;
  }

  /**
   * Returns the selected board size.
   */
  public int getBoardSize() {
    return (int) boardSizeSpinner.getValue();
  }

  /**
   * Returns the player 1 type as a string usable by {@code findStrategy}:
   * "human", "easy", "medium", or "hard".
   */
  public String getPlayer1Type() {
    return parsePlayerType((String) player1Combo.getSelectedItem());
  }

  /**
   * Returns the player 2 type as a string usable by {@code findStrategy}:
   * "human", "easy", "medium", or "hard".
   */
  public String getPlayer2Type() {
    return parsePlayerType((String) player2Combo.getSelectedItem());
  }

  /**
   * Returns the selected theme name: "dark", "classic", or "highcontrast".
   */
  public String getThemeName() {
    String selection = (String) themeCombo.getSelectedItem();
    switch (selection) {
      case "Classic Green":
        return "classic";
      case "High Contrast":
        return "highcontrast";
      default:
        return "dark";
    }
  }

  private String parsePlayerType(String selection) {
    switch (selection) {
      case "AI - Easy":
        return "easy";
      case "AI - Medium":
        return "medium";
      case "AI - Hard":
        return "hard";
      default:
        return "human";
    }
  }
}
