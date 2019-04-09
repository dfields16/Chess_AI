import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

class Game extends JPanel {
  private static final long serialVersionUID = 1L;
  
  Client client = new Client();

  Square[][] board;
  ArrayList<Move> history;

  Point cursor;
  Move click;
  
  Response response;

  BufferedImage ui;

  public Game() {
    setLayout(null);
    startGame();
    gameListener();
  }

  ///////////////////////////////////////////////////////////////////////////////
  // MEMBERS
  ///////////////////////////////////////////////////////////////////////////////

  public void startGame(){
    click   = new Move();
    board   = client.getBoard();
    history = new ArrayList<Move>();
  }

  ///////////////////////////////////////////////////////////////////////////////
  // LISTENER
  ///////////////////////////////////////////////////////////////////////////////
  
  public void gameListener() {

    addMouseMotionListener(new MouseAdapter() {

      // BOARD/MOUSE INTERACTION
      public void mouseMoved(MouseEvent e) {
        cursor = e.getPoint();
        if(click.start != null) repaint();
      }

    });

    addMouseListener(new MouseAdapter() {

      // BOARD/MOUSE INTERACTION
      public void mousePressed(MouseEvent e){

        boolean valid = false;

        // previously dealt with a final click, flush the trigger
        if (click.end != null) click.clear();
        
        // SEE IF A SQUARE WAS CLICKED
        for (int y = 0; y < 8; y++){
          for (int x = 0; x < 8; x++){
            // if an initial square is selected set to start or start over if already
            // selected
            if (board[y][x].shape.contains(e.getPoint()) && click.start == board[y][x].coord){
              // do nothing because validator below will catch it
            } else if (board[y][x].shape.contains(e.getPoint()) && click.start == null && board[y][x].piece != null && board[y][x].piece.side == client.turn && board[y][x].piece.side == client.side){
              click.start = new Point(x, y);
              valid = true;
              // if a start has already been selected set the destination
            } else if (board[y][x].shape.contains(e.getPoint()) && click.start != null){
              click.end = new Point(x, y);
              
              response  = client.sendMove(board,click);

              if ( response.valid ) {
                board = response.board;
                client.turn  = response.turn;
                System.out.println(client.turn);
              }

              valid = false;
            }

          }
        }

        // if something other than square was clicked
        if(!valid){
          click.clear();
        }else{
          valid = false;
        }

        // DISPLAY CHANGES
        repaint();
      }
    });
  }

  ///////////////////////////////////////////////////////////////////////////////
  // GUI
  ///////////////////////////////////////////////////////////////////////////////

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;

    String pack = "v2";

    // DRAW BACKGROUND
    try {
      ui = ImageIO.read(new File("./img/" + pack + "/bg.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    g.drawImage(ui, 0, 0, null);
    // DRAW TRIM
    try {
      ui = ImageIO.read(new File("./img/" + pack + "/trim.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    g.drawImage(ui, board[0][0].offx - 24, board[0][0].offy - 24, null);

    // DRAW TURN INDICATOR
    g2.setColor((client.turn == 0) ? Color.decode("#ffffff") : Color.decode("#000000"));
    g2.fillRect(board[0][0].offx - 25, board[0][0].offy - 25, 25, 25);

    // DRAW TIMER
    g2.setColor((client.turn == 0) ? Color.decode("#ffffff") : Color.decode("#000000"));
    g2.fillRect(board[0][0].offx + board[0][0].size * 6, board[0][0].offy - 60, board[0][0].size * 2, 25);
    g2.setColor((client.turn == 0) ? Color.decode("#000000") : Color.decode("#ffffff"));
    String timedis = String.valueOf("Time: ") + String.format("%02d", 0) + " of " + String.format("%02d", 60);
    g.setFont(new Font("default", Font.BOLD, 16));
    g2.drawString(timedis, board[0][0].offx + 15 + board[0][0].size * 6, board[0][0].offy - 42);

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
        // g2.setColor(toggle == 1 ? Color.decode("#603f2f") : Color.decode("#dfa070")
        // );

        if (board[y][x].coord.equals(click.start))
          g2.setColor(Color.decode("#003366"));
        g2.fill(board[y][x].shape);

        toggle = toggle == 0 ? 1 : 0;
        colh++;
      }
      toggle = toggle == 0 ? 1 : 0;
      rowh--;
    }

    // DRAW TEXTURE
    try {
      ui = ImageIO.read(new File("./img/" + pack + "/board.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    g.drawImage(ui, board[0][0].offx, board[0][0].offy, null);

    // DRAW PIECES
    String fn;
    for (int y = 0; y < 8; y++) {
      for (int x = 0; x < 8; x++) {
        if (board[y][x].piece != null) {

          fn = "./img/" + pack + "/" + board[y][x].size + "/" + (board[y][x].piece.side == 0 ? "W" : "B") + "_"
              + board[y][x].piece.type.name() + ".png";

          try {
            ui = ImageIO.read(new File(fn));
          } catch (IOException e) {
            e.printStackTrace();
          }

          if (board[y][x].coord.equals(click.start)) {
            g.drawImage(ui, (int) cursor.getX() - board[0][0].size / 2, (int) cursor.getY() - (board[0][0].size / 2),
                null);
          } else {
            g.drawImage(ui, board[y][x].shape.getBounds().x, board[y][x].shape.getBounds().y, null);
          }

        }
      }
    }

    // DRAW THE JAILYARD
    int jxw = board[0][0].offx - board[0][0].size * 2;
    int jyw = board[0][0].offy;

    int jxb = board[0][0].offx + (board[0][0].size * 9 - 19);
    int jyb = board[0][0].offy;

    int cntw = 0, cntb = 0;

    for (Move move : history) {

      if (move.captured != null) {

        fn = "./img/" + pack + "/" + board[move.y1()][move.x1()].size + "/" + (move.captured.side == 0 ? "W" : "B")
            + "_" + move.captured.type.name() + ".png";

        try {
          ui = ImageIO.read(new File(fn));
        } catch (IOException e) {
          e.printStackTrace();
        }

        if (move.captured.side == 0) {
          g.drawImage(ui, jxw, jyw, null);
          jyw += board[0][0].size;
          cntw++;

          if (cntw == 8) {
            jxw = board[0][0].offx - (board[0][0].size * 2) - 50;
            jyw = board[0][0].offy;
          }

        } else {
          g.drawImage(ui, jxb, jyb, null);
          jyb += board[0][0].size;
          cntb++;

          if (cntb == 8) {
            jxb = board[0][0].offx + (board[0][0].size * 9 - 19) + 50;
            jyb = board[0][0].offy;
          }

        }

      }
    }

  }
}
