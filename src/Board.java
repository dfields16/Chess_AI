import java.awt.*;
import java.awt.event.*;

import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

class Board extends JPanel {
  private static final long serialVersionUID = 1L;

  // MEMBER VARIABLES
  BufferedImage base, trim;

  GameState currentState;

  Square[] squares = new Square[64];
  Piece[] pieces = new Piece[64];

  int clickStart = -1;
  int clickEnd = -1;
  int chosen = -1;
  boolean valid = false;

  // CONSTRUCTOR
  public Board() {
    currentState = new GameState();
    setLayout(null);

    loadBoard();
    loadSquares();
    loadPieces();
    gameListener();

  }

  // DRAW
  @Override
  protected void paintComponent(Graphics g) {
    // DRAW BACKGROUND
    g.drawImage(base, 0, 0, null);
    g.drawImage(trim, 75, 65, null);

    Graphics2D g2 = (Graphics2D) g;
    Color color;

    // DRAW BOARD
    for (Square square : squares) {

      if (clickStart == square.id) {
        color = Color.decode("#6699cc");
        color = Color.decode("#99ff99");
      } else if (clickEnd == square.id) {
        color = Color.decode("#ff6666");
      } else {
        color = square.color;
      }

      g2.setColor(color);
      g2.fill(square.shape);
    }

    // DRAW PIECES
    for (Piece piece : pieces)
      g.drawImage(piece.icon, squares[piece.square].x, squares[piece.square].y, null);
  }

  ///////////////////////////////////////////////////////////////////////////////
  // MEMBERS
  ///////////////////////////////////////////////////////////////////////////////

  public void loadBoard() {

    try {
      base = ImageIO.read(new File("./img/bg/wood.png"));
      trim = ImageIO.read(new File("./img/bg/trim.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // LOAD CHESS BOARD
  public void loadSquares() {
    int sz = 75;
    int offx = 75;
    int offy = 75;
    int toggle = 0;
    int at = 0;
    int row = 8;

    char col;

    // DRAW THE GAME BOARD
    for (int y = offy; y < (offy + 8 * sz); y += sz) {
      col = 'A';
      for (int x = offx; x < (offx + 8 * sz); x += sz) {
        squares[at] = new Square(at, String.valueOf(col) + String.valueOf(row), x, y, sz,
            (toggle == 0 ? Color.decode("#ffffff") : Color.decode("#000000")));
        toggle = (toggle == 0 ? 1 : 0);
        at++;
        col++;
      }
      toggle = (toggle == 0 ? 1 : 0);
      row--;
    }
  }

  public void movePiece(int clickStart, int clickEnd) {
    pieces[squares[clickStart].piece].square = clickEnd;
    squares[clickEnd].piece = squares[clickStart].piece;
    squares[clickStart].piece = -1;
    // Update Current State
    currentState.update(pieces);
    // currentState.print();
    if (Main.client != null && Main.client.isActive()) {
      Main.client.sendData(currentState.serialize());
    }
    if (Main.server != null && Main.server.isActive()) {
      Main.server.sendData(currentState.serialize());
    }

  }

  // LOAD CHESS PIECES
  public void loadPieces() {
    Piece[] pcs = currentState.toGUI();
    for (int i = 0; i < pcs.length; i++) {
      pieces[i] = pcs[i];
      pieces[i].square = i;
      if (pcs[i].type != ChessPiece.EMPTY)
        squares[i].piece = i;
      else
        squares[i].piece = -1;
    }

  }

  public void gameListener() {
    // BOARD/MOUSE INTERACTION
    addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {

        // previously dealt with a final click, flush the trigger
        if (clickEnd > -1) {
          clickStart = -1;
          clickEnd = -1;
        }

        // SEE IF A SQUARE WAS CLICKED
        for (Square square : squares) {
          // if an initial square is selected set to start or start over if already
          // selected
          if (square.shape.contains(e.getPoint()) && clickStart == square.id) {

            // do nothing because validator below will catch it

            // if a start has already been selected set the destination
          } else if (square.shape.contains(e.getPoint()) && clickStart < 0 && square.piece > -1) {
            System.out.println("from " + square.coord);
            clickStart = square.id;
            valid = true;
            // if a start has already been selected set the destination
          } else if (square.shape.contains(e.getPoint()) && clickStart > -1 && square.piece < 0) {
            System.out.println("to " + square.coord);
            clickEnd = square.id;

            movePiece(clickStart, clickEnd);

            valid = true;
          }
        }

        // if something other than square was clicked
        if (!valid) {
          clickStart = -1;
          clickEnd = -1;
        } else {
          valid = false;
        }

        // DISPLAY CHANGES
        repaint();

      }
    });
  }

}