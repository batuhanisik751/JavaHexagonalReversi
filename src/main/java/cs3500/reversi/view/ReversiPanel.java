package cs3500.reversi.view;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import cs3500.reversi.controller.ViewListener;
import cs3500.reversi.model.Coordinate;
import cs3500.reversi.model.IReadOnlyReversiModel;
import cs3500.reversi.model.Player;

/**
 * Represents the graphical panel for rendering a Reversi game board. ReversiPanel extends
 * {@code JPanel} and implements {@code MouseListener} and {@code KeyListener} to handle
 * user input on the game board. User actions (move, pass) are delegated to a
 * {@link ViewListener} rather than modifying the model directly.
 */
public class ReversiPanel extends JPanel implements MouseListener, KeyListener {
  static final int HEX_SIZE = 40;
  static final double HEX_WIDTH = Math.sqrt(3) * HEX_SIZE;
  static final double HEX_HEIGHT = 1.5 * HEX_SIZE;

  private final IReadOnlyReversiModel model;
  private ViewListener viewListener;
  private int selectedRow = -1;
  private int selectedCol = -1;
  private int highlightPlacedRow = -1;
  private int highlightPlacedCol = -1;
  private List<Coordinate> highlightFlipped = new ArrayList<>();

  /**
   * Constructs a new ReversiPanel with the specified Reversi model.
   * @param model The Reversi model to be rendered (read-only access).
   */
  public ReversiPanel(IReadOnlyReversiModel model) {
    this.model = model;
    this.addMouseListener(this);
    this.addKeyListener(this);
    this.setFocusable(true);
  }

  /**
   * Sets the listener that receives user actions from this panel.
   * @param listener the controller that handles moves and passes.
   */
  public void setViewListener(ViewListener listener) {
    this.viewListener = listener;
  }

  /**
   * Sets the highlight state for the last move.
   * @param placedRow row of the placed piece, or -1 to clear.
   * @param placedCol column of the placed piece, or -1 to clear.
   * @param flipped list of coordinates of flipped pieces.
   */
  void setHighlights(int placedRow, int placedCol, List<Coordinate> flipped) {
    this.highlightPlacedRow = placedRow;
    this.highlightPlacedCol = placedCol;
    this.highlightFlipped = flipped;
  }

  void clearHighlights() {
    this.highlightPlacedRow = -1;
    this.highlightPlacedCol = -1;
    this.highlightFlipped = new ArrayList<>();
  }

  /**
   * Overrides the paintComponent method to render the Reversi game board.
   * @param g The Graphics object used for rendering.
   */
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    setBackground(Color.DARK_GRAY);

    for (int r = 0; r < model.getBoard().size(); r++) {
      for (int c = 0; c < model.getRow(r).size(); c++) {
        double rowOffset = Math.abs((model.getBoardSize() - 1) - r) * (HEX_WIDTH / 2);
        double centerX = c * HEX_WIDTH + rowOffset + HEX_WIDTH;
        double centerY = r * HEX_HEIGHT + HEX_HEIGHT;

        Hexagon hex = new Hexagon(centerX, centerY, HEX_SIZE);
        g2d.setColor(Color.WHITE);
        g2d.draw(hex);

        if (!(r == selectedRow && c == selectedCol)) {
          g2d.setColor(Color.GRAY);
          g2d.fill(hex);
          g2d.setColor(Color.BLACK);
          g2d.draw(hex);
          if (model.getSpaceContent(r, c) == Player.BLACK) {
            g2d.setColor(Color.BLACK);
            g2d.fillOval((int) (centerX - HEX_SIZE / 4), (int) (centerY - HEX_SIZE / 4),
                    HEX_SIZE / 2, HEX_SIZE / 2);
          } else if (model.getSpaceContent(r, c) == Player.WHITE) {
            g2d.setColor(Color.WHITE);
            g2d.fillOval((int) (centerX - HEX_SIZE / 4), (int) (centerY - HEX_SIZE / 4),
                    HEX_SIZE / 2, HEX_SIZE / 2);
          } else {
            g2d.setColor(Color.GRAY);
            g2d.fill(hex);
            g2d.setColor(Color.BLACK);
            g2d.draw(hex);
          }
          // Draw highlight ring for last move
          if (r == highlightPlacedRow && c == highlightPlacedCol) {
            g2d.setColor(new Color(0, 200, 0)); // green for placed piece
            g2d.draw(hex);
          } else if (isHighlightedFlip(r, c)) {
            g2d.setColor(new Color(255, 165, 0)); // orange for flipped pieces
            g2d.draw(hex);
          }
        } else {
          if (model.getSpace(r, c).isEmpty()
                  && model.isValidMove(r, c, model.getCurrentTurn())) {
            g2d.setColor(Color.CYAN);
            g2d.fill(hex);
          }
        }
      }
    }
  }

  /**
   * Handles the mouseClicked event on the Reversi game board. Updates the selected row and column
   * based on the mouse click, and triggers a repaint of the view.
   * @param e The MouseEvent representing the mouse click.
   */
  public void mouseClicked(MouseEvent e) {
    int x = e.getX();
    int y = e.getY();

    int r = (int) ((y + HEX_HEIGHT / 2) / HEX_HEIGHT) - 1;
    double rowOff = Math.abs((model.getBoardSize() - 1) - r);
    int c = (int) (((x + HEX_WIDTH / 2) / HEX_WIDTH) - 1 - (rowOff / 2));

    selectedRow = r;
    selectedCol = c;

    repaint();
  }

  @Override
  public void mousePressed(MouseEvent e) {
    mouseClicked(e);
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    mouseClicked(e);
  }

  @Override
  public void mouseEntered(MouseEvent e) {
    // nothing specific happens when mouse enters
  }

  @Override
  public void mouseExited(MouseEvent e) {
    // nothing specific happens when mouse exits
  }

  /**
   * Handles the key events. If enter is pressed, a move attempt is delegated to the listener.
   * If P is pressed, a pass is delegated to the listener.
   * @param e the event to be processed
   */
  public void keyTyped(KeyEvent e) {
    if (viewListener == null) {
      return;
    }
    char keyChar = e.getKeyChar();
    switch (keyChar) {
      case 'p':
        viewListener.onPass();
        break;
      case 'u':
        viewListener.onUndo();
        break;
      case KeyEvent.VK_ENTER:
        viewListener.onMove(selectedRow, selectedCol);
        break;
      default:
        break;
    }
  }

  @Override
  public void keyPressed(KeyEvent e) {
    // nothing specific happens when key is pressed
  }

  @Override
  public void keyReleased(KeyEvent e) {
    // nothing specific happens when key is released
  }

  private boolean isHighlightedFlip(int row, int col) {
    for (Coordinate coord : highlightFlipped) {
      if (coord.getRow() == row && coord.getCol() == col) {
        return true;
      }
    }
    return false;
  }
}
