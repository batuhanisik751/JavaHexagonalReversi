package cs3500.reversi.view.legacy;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import java.io.File;
import java.util.List;

import cs3500.reversi.audio.SoundManager;
import cs3500.reversi.controller.ViewListener;
import cs3500.reversi.history.MoveRecord;
import cs3500.reversi.model.Coordinate;
import cs3500.reversi.model.IReadOnlyReversiModel;
import cs3500.reversi.model.Player;
import cs3500.reversi.view.IGraphicsView;

/**
 * Represents the graphical view of the Reversi game.
 */
public class ReversiGraphicsView extends JFrame implements IGraphicsView {
  private final IReadOnlyReversiModel model;
  private final ReversiPanel reversiPanel;
  private final JLabel blackScoreLabel;
  private final JLabel whiteScoreLabel;
  private final Player player;
  private final JLabel turnLabel;
  private final Theme theme;
  private final HistoryPanel historyPanel;
  private Runnable restartAction;
  private ViewListener viewListener;

  /**
   * Constructs a new ReversiGraphicsView with the given Reversi model and theme.
   * @param model The Reversi model to be displayed.
   * @param player The player this view belongs to.
   * @param theme The color theme to apply.
   */
  public ReversiGraphicsView(IReadOnlyReversiModel model, Player player, Theme theme) {
    super();
    this.model = model;
    this.player = player;
    this.theme = theme;
    setTitle("Reversi - " + (player == Player.BLACK ? "Black (X)" : "White (O)"));
    int boardWidth = (int) ((model.getBoardSize() * 2.5) * ReversiPanel.HEX_WIDTH);
    int windowSizeY = (int) ((model.getBoardSize() * 2.5) * ReversiPanel.HEX_HEIGHT);
    int historyWidth = 200;
    setSize(boardWidth + historyWidth, windowSizeY);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    reversiPanel = new ReversiPanel(model, theme);
    reversiPanel.setBackground(theme.boardBackground());
    reversiPanel.setPreferredSize(new Dimension(boardWidth, windowSizeY));
    reversiPanel.setMinimumSize(new Dimension(boardWidth, windowSizeY));
    this.add(reversiPanel, BorderLayout.CENTER);

    JPanel scorePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 5));
    scorePanel.setBackground(theme.scorePanelBg());

    blackScoreLabel = new JLabel();
    blackScoreLabel.setForeground(theme.scoreLabelFg());
    blackScoreLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

    whiteScoreLabel = new JLabel();
    whiteScoreLabel.setForeground(theme.scoreLabelFg());
    whiteScoreLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

    turnLabel = new JLabel();
    turnLabel.setForeground(theme.turnLabelActive());
    turnLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

    scorePanel.add(blackScoreLabel);
    scorePanel.add(whiteScoreLabel);
    scorePanel.add(turnLabel);
    this.add(scorePanel, BorderLayout.NORTH);

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout());
    this.add(buttonPanel, BorderLayout.SOUTH);
    JButton saveButton = new JButton("Save");
    saveButton.addActionListener((ActionEvent e) -> {
      if (viewListener != null) {
        viewListener.onSave();
      }
    });
    buttonPanel.add(saveButton);
    JButton loadButton = new JButton("Load");
    loadButton.addActionListener((ActionEvent e) -> {
      if (viewListener != null) {
        viewListener.onLoad();
      }
    });
    buttonPanel.add(loadButton);
    JButton undoButton = new JButton("Undo");
    undoButton.addActionListener((ActionEvent e) -> {
      if (viewListener != null) {
        viewListener.onUndo();
      }
    });
    buttonPanel.add(undoButton);
    JButton quitButton = new JButton("Quit");
    quitButton.addActionListener((ActionEvent e) -> {
      System.exit(0);
    });
    buttonPanel.add(quitButton);
    JToggleButton muteButton = new JToggleButton("Mute");
    muteButton.setSelected(SoundManager.isMuted());
    muteButton.addActionListener((ActionEvent e) -> {
      SoundManager.setMuted(muteButton.isSelected());
    });
    buttonPanel.add(muteButton);

    historyPanel = new HistoryPanel();
    this.add(historyPanel, BorderLayout.EAST);

    setResizable(true);
    pack();
    setMinimumSize(getSize());
    setLocationRelativeTo(null);
    updateStatusLabels();
    reversiPanel.requestFocus();
  }

  /**
   * Sets the listener that receives user actions from this view.
   * Delegates to the panel where input events originate.
   * @param listener the controller that handles user actions.
   */
  @Override
  public void setViewListener(ViewListener listener) {
    this.viewListener = listener;
    this.reversiPanel.setViewListener(listener);
  }

  /**
   * Sets the action to run when the user chooses "Play Again" at game over.
   * @param restartAction the action that restarts the game.
   */
  public void setRestartAction(Runnable restartAction) {
    this.restartAction = restartAction;
  }

  /**
   * Updates the view with the current state of the game.
   */
  @Override
  public void refresh() {
    updateStatusLabels();
    this.reversiPanel.repaint();
  }

  /**
   * Updates the score labels and turn indicator with the current game state.
   */
  private void updateStatusLabels() {
    blackScoreLabel.setText("Black (X): " + model.getScore(Player.BLACK));
    whiteScoreLabel.setText("White (O): " + model.getScore(Player.WHITE));
    if (model.gameOver()) {
      turnLabel.setText("Game Over");
      turnLabel.setForeground(theme.turnLabelInactive());
    } else if (model.getCurrentTurn() == player) {
      turnLabel.setText("Your Turn");
      turnLabel.setForeground(theme.turnLabelActive());
    } else {
      turnLabel.setText("Waiting...");
      turnLabel.setForeground(theme.turnLabelInactive());
    }
  }

  /**
   * Makes the view visible.
   */
  @Override
  public void makeVisible() {
    this.setVisible(true);
  }

  /**
   * Shows a message indicating a player is out-of-turn.
   */
  @Override
  public void outOfTurnMessage() {
    JOptionPane.showMessageDialog(this, "It's not your turn. "
                    + "Please wait for your turn.",
            "Out of Turn", JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Shows a message indicating the attempted move is invalid.
   */
  @Override
  public void invalidMoveMessage() {
    JOptionPane.showMessageDialog(this, "Invalid move. "
                    + "Please try again.",
            "Invalid Move", JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Sends a message displaying that the game is over, along with corresponding player scores.
   * @param blackScore score of Player.BLACK
   * @param whiteScore score of Player.WHITE
   */
  @Override
  public void gameOver(int blackScore, int whiteScore) {
    String winner;
    if (blackScore > whiteScore) {
      winner = "Black (X) wins!";
    } else if (whiteScore > blackScore) {
      winner = "White (O) wins!";
    } else {
      winner = "It's a tie!";
    }
    String message = winner + "\n\nBlack Score: " + blackScore + "\nWhite Score: " + whiteScore;

    String[] options = {"Play Again", "Quit"};
    int choice = JOptionPane.showOptionDialog(
            this, message, "Game Over",
            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
            null, options, options[0]);

    if (choice == 0 && restartAction != null) {
      restartAction.run();
    } else {
      System.exit(0);
    }
  }

  /**
   * Sends a message displaying who holds the current turn.
   */
  @Override
  public void playerTurn() {
    String message = "Player " + model.getCurrentTurn() + "'s turn.";
    JOptionPane.showMessageDialog(this, message,
            "Player Turn", JOptionPane.INFORMATION_MESSAGE);
  }

  @Override
  public void highlightLastMove(int placedRow, int placedCol, List<Coordinate> flipped) {
    this.reversiPanel.setHighlights(placedRow, placedCol, flipped);
  }

  @Override
  public void undoNotAvailableMessage() {
    JOptionPane.showMessageDialog(this, "No move to undo.",
            "Undo Not Available", JOptionPane.INFORMATION_MESSAGE);
  }

  @Override
  public void updateHistory(List<MoveRecord> records) {
    this.historyPanel.updateHistory(records);
  }

  @Override
  public void showSaveSuccess() {
    JOptionPane.showMessageDialog(this, "Game saved successfully.",
            "Save", JOptionPane.INFORMATION_MESSAGE);
  }

  @Override
  public void showLoadSuccess() {
    JOptionPane.showMessageDialog(this, "Game loaded successfully.",
            "Load", JOptionPane.INFORMATION_MESSAGE);
  }

  @Override
  public void showFileError(String message) {
    JOptionPane.showMessageDialog(this, message,
            "File Error", JOptionPane.ERROR_MESSAGE);
  }

  @Override
  public void scheduleDelayed(Runnable action, int delayMs) {
    Timer timer = new Timer(delayMs, e -> action.run());
    timer.setRepeats(false);
    timer.start();
  }

  @Override
  public void runOnUIThread(Runnable action) {
    SwingUtilities.invokeLater(action);
  }

  @Override
  public File showSaveFileChooser() {
    JFileChooser chooser = new JFileChooser();
    chooser.setFileFilter(new FileNameExtensionFilter("Reversi Save (.reversi)", "reversi"));
    if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      if (!file.getName().endsWith(".reversi")) {
        file = new File(file.getAbsolutePath() + ".reversi");
      }
      return file;
    }
    return null;
  }

  @Override
  public File showLoadFileChooser() {
    JFileChooser chooser = new JFileChooser();
    chooser.setFileFilter(new FileNameExtensionFilter("Reversi Save (.reversi)", "reversi"));
    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      return chooser.getSelectedFile();
    }
    return null;
  }
}
