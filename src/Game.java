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

  BufferedImage ui;

  AI currentState;

  // MULTIARRAY
  Square[][] board = new Square[8][8];

  String clickStart = null;
  String clickEnd = null;
  int turn = 0;

  // CONSTRUCTOR
  public Game() {
    currentState = AI.getInitState();
    setLayout(null);

    loadSquares();
    loadPieces();
    gameListener();
  }

  ///////////////////////////////////////////////////////////////////////////////
  // MEMBERS
  ///////////////////////////////////////////////////////////////////////////////

  public boolean validMove() {

    int x1 = Integer.parseInt(clickStart.substring(0,1));
    int y1 = Integer.parseInt(clickStart.substring(1,2));
    int x2 =   Integer.parseInt(clickEnd.substring(0,1));
    int y2 =   Integer.parseInt(clickEnd.substring(1,2));
    
    int dx = (turn==0) ?  (x2-x1) : (x2-x1);
    int dy = (turn==0) ?  (y2-y1) : (y2-y1);
    int py = (turn==0) ? -(y2-y1) : (y2-y1);

    

    Piece piece = board[y1][x1].piece;

    List<Float> slopes = new ArrayList<>();
    List<Float> distances = new ArrayList<>();
    
    // CALCULATE THE SLOPE/DISTANCE FOR THE DESIRED MOVE
    float slope = (dx==0 || dy==0) ? 0 : (float)dy / (float)dx;
    float dist  = (float) Math.sqrt(dx*dx + dy*dy);
    
    boolean pawntest = true;
    
    // IF TARGET IS ON SAME TEAM LEAVE EARLY
    if (board[y2][x2].piece != null && board[y2][x2].piece.side == board[y1][x1].piece.side)
      return false;
    
    // ASSIGN EACH OF THE PIECES A SLOPE AND DISTANCE THEY CAN MOVE
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
      slopes.add((float) 0);
      slopes.add( (float) 1);  
      distances.add((float) 1);
      distances.add( (float) Math.sqrt(2));
      if (piece.moved == 0) distances.add((float) 2);

      // prevent horizontal pawn movement
      if(dy==0) pawntest = false;
      // prevent backward movement
      if(py<0) pawntest = false;
      // if trying to go forward prevent capture
      if(Math.abs(slope) == 0 && (board[y2][x2].piece != null) ) pawntest = false;
      // if trying to go diagonal make sure it is a capture
      if(Math.abs(slope) == 1 && (board[y2][x2].piece == null || turn == board[y2][x2].piece.side)) pawntest = false;

    break;
    }
    
    // CHECK FOR COLLISION
    if(piece.type != ChessPiece.KNIGHT){
      int xp = x2;
      int yp = y2;      
      while(true)
      {
        // increment in direction of root
        if(xp>x1){xp--;}else if(xp<x1){xp++;}
        if(yp>y1){yp--;}else if(yp<y1){yp++;}
        // if at root exit while
        if(yp==y1 && xp==x1) break;
        // if piece found exit validation
        if(board[yp][xp].piece != null) return false;        
      }
    }
    
    //EVALUATE FINAL RESPONSE
    if (listContains(slopes, Math.abs(slope)) && (listContains(distances, 0) || listContains(distances, dist)) && pawntest) {
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
    currentState.setState(board);
    // currentState.print();
    if (Main.client != null && Main.client.isActive()) {
      Main.client.sendData(currentState.serialize());
    }
    if (Main.server != null && Main.server.isActive()) {
      Main.server.sendData(currentState.serialize());
    }
  }

  public void loadSquares() {
    for (int y = 0; y < 8; y++) {
      for (int x = 0; x < 8; x++) {
        // String id = String.valueOf(x) + String.valueOf(y);
        board[y][x] = currentState.state[y][x];
      }
    }
  }

  public void loadPieces() {
    for (int y = 0; y < 8; y++) {
      for (int x = 0; x < 8; x++) {
        // String id = String.valueOf(x) + String.valueOf(y);
        board[y][x].piece = currentState.state[y][x].piece;
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
              System.out.println("from " + board[y][x].id);
              clickStart = board[y][x].id;
              valid = true;
              // if a start has already been selected set the destination
            } else if (board[y][x].shape.contains(e.getPoint()) && clickStart != null) {
              System.out.println("to " + board[y][x].id);
              clickEnd = board[y][x].id;

              if (validMove()) {
                board[y1][x1].piece.moved = 1;
                turn = (turn == 0) ? 1 : 0;
                movePiece(clickStart, clickEnd);
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

  // DRAW GRAPHICS
  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;

    try {
      ui = ImageIO.read(new File("./img/bg/wood.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    // DRAW BACKGROUND
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
        if (board[y][x].id == clickStart)
          g2.setColor(Color.decode("#003366"));
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
}
