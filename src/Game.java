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
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

class Game extends JPanel {
  private static final long serialVersionUID = 1L;

  Client client;

  Board board;
  Point cursor;
  Move click;
  long time = 0;
  long timeLimit = 0;
  BufferedImage ui;

  public Game() {
    try {
      client = new Client(this, InetAddress.getByName("localhost"), 1200);
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
    setLayout(null);
    startGame();
    gameListener();
  }

  public void startGame(){
    click   = new Move();
    board = new Board(8, 8);
    Util.resetPieces(board);
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
            if( board.shape(x,y).contains(e.getPoint()) && click.start == board.coord(x,y)){
              // do nothing because validator below will catch it
            }else if (board.in(x,y,e.getPoint()) && click.start == null && board.piece(x,y) != null &&
                      board.piece(x,y).side == board.turn && board.piece(x,y).side == client.team)
            {
              click.start = new Point(x, y);
              valid = true;
              // if a start has already been selected set the destination
            }else if( board.in(x,y,e.getPoint()) && click.start != null){
              click.end = new Point(x, y);

              client.sendMove(click);
              // System.out.println(board.turn);

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
    g.drawImage(ui, board.offx(0,0) - 24, board.offy(0,0) - 24, null);

    // DRAW TURN INDICATOR
    g2.setColor((board.turn == 0) ? Color.decode("#ffffff") : Color.decode("#000000"));
    g2.fillRect(board.offx(0,0) - 25, board.offy(0,0) - 25, 25, 25);

    // DRAW TIMER
    g2.setColor((board.turn == 0) ? Color.decode("#ffffff") : Color.decode("#000000"));
    g2.fillRect(board.offx(0,0) + board.size(0,0) * 6, board.offy(0,0) - 60, board.size(0,0) * 2, 25);
    g2.setColor((board.turn == 0) ? Color.decode("#000000") : Color.decode("#ffffff"));
    String timedis = String.valueOf("Time: ") + String.format("%03d", (time / 1000)) + " of " + String.format("%03d", timeLimit / 1000);
    g.setFont(new Font("default", Font.BOLD, 16));
    g2.drawString(timedis, board.offx(0,0) + 15 + board.size(0,0) * 6, board.offy(0,0) - 42);

    // DRAW BOARD
    int toggle = 0;
    int rowh = 8;
    char colh;

    for (int y = 0; y < 8; y++) {
      colh = 'A';

      // HEADING Y
      g2.setColor(Color.decode("#111111"));
      g2.fillRect(board.offx(0,0) - 25, board.offy(0,0) + y * board.size(0,0), 25, board.size(0,0));
      g2.setColor(Color.decode("#ffffff"));
      g2.drawString(String.valueOf(rowh), board.offx(0,0) - 16, board.offy(0,0) + 48 + (y * board.size(0,0)));

      for (int x = 0; x < 8; x++) {

        if (y == 0) {
          // HEADING X
          g2.setColor(Color.decode("#111111"));
          g2.fillRect(board.offx(0,0) + x * board.size(0,0), board.offy(0,0) - 25, board.size(0,0), 25);
          g2.setColor(Color.decode("#ffffff"));
          g2.drawString(String.valueOf(colh), board.offx(0,0) + 34 + (x * board.size(0,0)), board.offy(0,0) - 8);
        }

        g2.setColor(toggle == 1 ? Color.BLACK : Color.white);
        // g2.setColor(toggle == 1 ? Color.decode("#603f2f") : Color.decode("#dfa070")
        // );

        if (board.coord(x,y).equals(click.start))
          g2.setColor(Color.decode("#003366"));
        g2.fill(board.shape(x,y));

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
    g.drawImage(ui, board.offx(0,0), board.offy(0,0), null);

    // DRAW PIECES
    String fn;
    for (int y = 0; y < 8; y++) {
      for (int x = 0; x < 8; x++) {
        if (board.piece(x,y) != null) {

          fn = "./img/" + pack + "/" + board.size(x,y) + "/" + (board.piece(x,y).side == 0 ? "W" : "B") + "_"
              + board.piece(x,y).type.name() + ".png";

          try {
            ui = ImageIO.read(new File(fn));
          } catch (IOException e) {
            e.printStackTrace();
          }

          if (board.coord(x,y).equals(click.start)) {
            g.drawImage(ui, (int) cursor.getX() - board.size(0,0) / 2, (int) cursor.getY() - (board.size(0,0) / 2),
                null);
          } else {
            g.drawImage(ui, board.shape(x,y).getBounds().x, board.shape(x,y).getBounds().y, null);
          }

        }
      }
    }

    // DRAW THE JAILYARD
    int jxw = board.offx(0,0) - board.size(0,0) * 2;
    int jyw = board.offy(0,0);

    int jxb = board.offx(0,0) + (board.size(0,0) * 9 - 19);
    int jyb = board.offy(0,0);

    int cntw = 0, cntb = 0;

    for (Move move : board.history) {

      if (move.captured != null) {

        fn = "./img/" + pack + "/" + board.size(move.x1(),move.y1()) + "/" + (move.captured.side == 0 ? "W" : "B")
            + "_" + move.captured.type.name() + ".png";

        try {
          ui = ImageIO.read(new File(fn));
        } catch (IOException e) {
          e.printStackTrace();
        }

        if (move.captured.side == 0) {
          g.drawImage(ui, jxw, jyw, null);
          jyw += board.size(0,0);
          cntw++;

          if (cntw == 8) {
            jxw = board.offx(0,0) - (board.size(0,0) * 2) - 50;
            jyw = board.offy(0,0);
          }

        } else {
          g.drawImage(ui, jxb, jyb, null);
          jyb += board.size(0,0);
          cntb++;

          if (cntb == 8) {
            jxb = board.offx(0,0) + (board.size(0,0) * 9 - 19) + 50;
            jyb = board.offy(0,0);
          }

        }

      }
    }

  }
}
