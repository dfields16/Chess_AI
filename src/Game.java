import java.awt.*;
import java.awt.event.*;
import javax.swing.JPanel;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

class Game extends JPanel {
  private static final long serialVersionUID = 1L;

  // MEMBER VARIABLES

  BufferedImage ui, icon;

  AI currentState;

  // MULTIARRAY
  Square[][] board = new Square[8][8];

  String clickStart = null;
  String clickEnd = null;
  int turn = 0;

  // CONSTRUCTOR
  public Game() {
    currentState = new AI();
    setLayout(null);

    loadSquares();
    loadPieces();
    gameListener();
  }

  // GUI
  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;
    
    // DRAW BACKGROUND
    try {
      ui = ImageIO.read(new File("./img/bg/wood.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }

    g.drawImage(ui, 0, 0, null);
    try {
      ui = ImageIO.read(new File("./img/bg/trim.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    g.drawImage(ui, board[0][0].offx - 24, board[0][0].offy - 24, null);

    // DRAW TURN INDICATOR
    g2.setColor((turn == 0) ? Color.decode("#ffffff") : Color.decode("#000000"));
    g2.fillRect(board[0][0].offx - 25, board[0][0].offy - 25, 25, 25);

    // DRAW BOARD
    int toggle = 0;
    int rowh = 8;
    char colh;

    for (int y = 0; y < 8; y++) {
      colh = 'A';

      // HEADING Y
      g2.setColor(Color.decode("#111111"));
      g2.fillRect(board[0][0].offx - 25, board[0][0].offy + y * board[0][0].size, 25, board[0][0].size);
      g2.setColor(Color.decode("#ffffff"));
      g2.drawString(String.valueOf(rowh), board[0][0].offx - 16, board[0][0].offy + 48 + (y * board[0][0].size));

    for (int x = 0; x < 8; x++) {

      if (y == 0) {
        // HEADING X
        g2.setColor(Color.decode("#111111"));
        g2.fillRect(board[0][0].offx + x * board[0][0].size, board[0][0].offy - 25, board[0][0].size, 25);
        g2.setColor(Color.decode("#ffffff"));
        g2.drawString(String.valueOf(colh), board[0][0].offx + 34 + (x * board[0][0].size), board[0][0].offy - 8);
      }

      g2.setColor(toggle == 1 ? Color.BLACK : Color.white);
      if (board[y][x].id == clickStart) g2.setColor(Color.decode("#003366"));
      g2.fill(board[y][x].shape);

      toggle = toggle == 0 ? 1 : 0;
      colh++;
    }
    toggle = toggle == 0 ? 1 : 0;
    rowh--;
  }

    // DRAW PIECES
    String fn;
    for (int y = 0; y < 8; y++) {
      for (int x = 0; x < 8; x++) {
        if (board[y][x].piece != null) {
          fn = "./img/" + (board[y][x].piece.side == 0 ? "w" : "b") + "_" + board[y][x].piece.type.name().toLowerCase()
              + ".png";
          try {
            ui = ImageIO.read(new File(fn));
          } catch (IOException e) {
            e.printStackTrace();
          }
          g.drawImage(ui, board[y][x].shape.getBounds().x, board[y][x].shape.getBounds().y, null);
        }
      }
    }

  }

  ///////////////////////////////////////////////////////////////////////////////
  // MEMBERS
  ///////////////////////////////////////////////////////////////////////////////

  public boolean validMove() {

    int x1 = Integer.parseInt(clickStart.substring(0, 1));
    int y1 = Integer.parseInt(clickStart.substring(1, 2));
    int x2 = Integer.parseInt(clickEnd.substring(0, 1));
    int y2 = Integer.parseInt(clickEnd.substring(1, 2));

    if (board[y2][x2].piece != null && board[y2][x2].piece.side == board[y1][x1].piece.side)
      return false;

    Piece piece = board[y1][x1].piece;

    List<Float> slopes = new ArrayList<>();
    List<Float> distances = new ArrayList<>();

    switch (piece.type) {
    case KING:
      slopes.add((float) 0);
      slopes.add((float) 1);
      distances.add((float) 1);
      distances.add((float) Math.sqrt(2));
      break;
    case QUEEN:
      slopes.add((float) 0);
      slopes.add((float) 1);
      distances.add((float) 0);
      break;
    case BISCHOP:
      slopes.add((float) 1);
      distances.add((float) 0);
      break;
    case KNIGHT:
      slopes.add((float) 2);
      slopes.add((float) .5);
      distances.add((float) Math.sqrt(5));
      break;
    case ROOK:
      slopes.add((float) 0);
      distances.add((float) 0);
      break;
    case PAWN:

      // CANNOT CAPTURE FORWARD
      if ((board[y1][x1].piece.side == 0 && board[y1 - 1][x1].piece == null)
          || (board[y1][x1].piece.side == 1 && board[y1 + 1][x1].piece == null)) {
        slopes.add((float) 0);
        distances.add((float) 1);
      }
      // CAN CAPTURE SIDEWAYS
      if ((board[y1][x1].piece.side == 0 && ((x1 - 1 > 0 && board[y1 - 1][x1 - 1].piece != null)
          || (x1 + 1 < 8 && board[y1 - 1][x1 + 1].piece != null)))
          || (board[y1][x1].piece.side == 1 && ((x1 - 1 > 0 && board[y1 + 1][x1 - 1].piece != null)
              || (x1 + 1 < 8 && board[y1 + 1][x1 + 1].piece != null)))) {
        slopes.add((float) 1);
        distances.add((float) Math.sqrt(2));
      }

      if (piece.moved == 0)
        distances.add((float) 2);

      break;
      case EMPTY:
    }

    // SLOPE
    float slope = ((x2 - x1) == 0 || (y2 - y1) == 0) ? 0 : Math.abs((float) (y2 - y1) / (float) (x2 - x1));
    // DISTANCE
    float dist = (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));

    // PAWN IS THE ONLY PIECE THAT NEEDS A LITTLE HELP
    boolean pawntest = (piece.type == ChessPiece.PAWN && 
        ((y2 - y1) == 0 || (piece.side == 0 && y2 > y1) || (piece.side == 1 && y2 < y1))) ? false : true;
    
    if (piece.type == ChessPiece.PAWN && listContains(slopes, (float) 1) && 
       (board[y2][x2].piece == null || board[y2][x2].piece.side == piece.side))
      pawntest = false;

    if (listContains(slopes, slope) && (listContains(distances, 0) || listContains(distances, dist)) && pawntest) {
      return true;
    }
    return false;
  }

  boolean listContains(List<Float> list, float key) {
    for (float elem : list)
      if (elem == key)
        return true;
    return false;
  }

  public void movePiece(String start, String end) {
    int x1 = Integer.parseInt(start.substring(0, 1));
    int y1 = Integer.parseInt(start.substring(1, 2));
    int x2 = Integer.parseInt(end.substring(0, 1));
    int y2 = Integer.parseInt(end.substring(1, 2));

    board[y2][x2].piece = board[y1][x1].piece;
    board[y1][x1].piece = null;

    // Update Current State
    //currentState.setState(board);
    // currentState.print();
    //if (Main.client != null && Main.client.isActive()) {
    //  Main.client.sendData(currentState.serialize());
    //}
    //if (Main.server != null && Main.server.isActive()) {
    //  Main.server.sendData(currentState.serialize());
    //}
  }

  public void loadSquares() {
    for (int y = 0; y < 8; y++) {
      for (int x = 0; x < 8; x++) {
        String id = String.valueOf(x) + String.valueOf(y);
        board[y][x] = new Square(id, x, y);
      }
    }
  }

  public void loadPieces() {
    for (int y = 0; y < 8; y++) {
      for (int x = 0; x < 8; x++) {
        String id = String.valueOf(x) + String.valueOf(y);
        board[y][x] = currentState.state[y][x];
        board[y][x].id = id;
      }
    }
  }

  public void gameListener() {

    // BOARD/MOUSE INTERACTION
    addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {

        boolean valid = false;

        int x1 = (clickStart != null) ? Integer.parseInt(clickStart.substring(0, 1)) : 0;
        int y1 = (clickStart != null) ? Integer.parseInt(clickStart.substring(1, 2)) : 0;
        int x2 = (clickEnd != null)   ? Integer.parseInt(clickEnd.substring(0, 1)) : 0;
        int y2 = (clickEnd != null)   ? Integer.parseInt(clickEnd.substring(1, 2)) : 0;

        if (clickStart != null) {
          x1 = Integer.parseInt(clickStart.substring(0, 1));
          y1 = Integer.parseInt(clickStart.substring(1, 2));
        }
        
        // previously dealt with a final click, flush the trigger
        if (clickEnd != null) {
          clickStart = null;
          clickEnd = null;
        }

        // SEE IF A SQUARE WAS CLICKED
        for (int y = 0; y < 8; y++) {
          for (int x = 0; x < 8; x++) {
            // if an initial square is selected set to start or start over if already
            // selected
            if (board[y][x].shape.contains(e.getPoint()) && clickStart == board[y][x].id) {
              // do nothing because validator below will catch it
            } else if (board[y][x].shape.contains(e.getPoint()) && clickStart == null && board[y][x].piece != null
                && board[y][x].piece.side == turn) {
              
              clickStart = board[y][x].id;
              valid = true;
              // if a start has already been selected set the destination
            } else if (board[y][x].shape.contains(e.getPoint()) && clickStart != null) {
              
              clickEnd = board[y][x].id;

              if (validMove()) {
                board[y1][x1].piece.moved = 1;
                turn = (turn == 0) ? 1 : 0;
                movePiece(clickStart, clickEnd);
                
                System.out.println("to " + board[y2][x2].id);
                System.out.println("from " + board[y1][x1].id);
              }

              valid = false;
            }

          }
        }

        // if something other than square was clicked
        if (!valid) {
          clickStart = null;
          clickEnd = null;
        } else {
          valid = false;
        }

        // DISPLAY CHANGES
        repaint();
      }
    });
  }

}