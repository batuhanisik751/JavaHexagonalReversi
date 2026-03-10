package cs3500.reversi.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import cs3500.reversi.controller.ViewListener;
import cs3500.reversi.model.IReadOnlyReversiModel;

/**
 * Represents the graphical view of the Reversi game.
 */
public class ReversiGraphicsView extends JFrame implements IGraphicsView {
  private final IReadOnlyReversiModel model;
  private final ReversiPanel reversiPanel;

  /**
   * Constructs a new ReversiGraphicsView with the given Reversi model.
   * @param model The Reversi model to be displayed.
   */
  public ReversiGraphicsView(IReadOnlyReversiModel model) {
    super();
    this.model = model;
    setTitle("Reversi");
    int windowSizeX = (int) ((model.getBoardSize() * 2) * ReversiPanel.HEX_WIDTH);
    int windowSizeY = (int) ((model.getBoardSize() * 2) * ReversiPanel.HEX_HEIGHT);
    setSize(windowSizeX, windowSizeY);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    reversiPanel = new ReversiPanel(model);
    reversiPanel.setBackground(Color.BLACK);
    reversiPanel.setPreferredSize(new Dimension(windowSizeX, windowSizeY));
    this.add(reversiPanel, BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout());
    this.add(buttonPanel, BorderLayout.SOUTH);
    JButton quitButton = new JButton("Quit");
    quitButton.addActionListener((ActionEvent e) -> {
      System.exit(0);
    });
    buttonPanel.add(quitButton);

    setResizable(true);
    pack();
    setLocationRelativeTo(null);
    reversiPanel.requestFocus();
  }

  /**
   * Sets the listener that receives user actions from this view.
   * Delegates to the panel where input events originate.
   * @param listener the controller that handles user actions.
   */
  @Override
  public void setViewListener(ViewListener listener) {
    this.reversiPanel.setViewListener(listener);
  }

  /**
   * Updates the view with the current state of the game.
   */
  @Override
  public void refresh() {
    this.reversiPanel.repaint();
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
    String message = "Game Over!\nBlack Score: " + blackScore + "\nWhite Score: " + whiteScore;
    JOptionPane.showMessageDialog(this, message,
            "Game Over", JOptionPane.INFORMATION_MESSAGE);
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
}
