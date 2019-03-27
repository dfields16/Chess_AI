import java.awt.*;
import java.awt.event.*;

import javax.swing.JPanel;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

class Move{
  
  Point from,to;
  Piece captured = null;
  
  Move(int x1, int y1,int x2, int y2){
    from.setLocation(x1,y1);
    to.setLocation(x2,y2);
  }  
  Move(Point f, Point t,Piece c){
    from = f;
    to   = t;
    captured = c;
  }  
}

class Game extends JPanel {
  private static final long serialVersionUID = 1L;

  // MEMBER VARIABLES

  BufferedImage ui;

  AI ai;

  // MULTIARRAY
  Square[][] board = new Square[8][8];
  
  ArrayList<Move> history = new ArrayList<Move>();
  
  Point clickStart = null;
  Point clickEnd   = null;
  
  int turn = 0;

  Point cursor;

  // CONSTRUCTOR
  public Game() {
    
    setLayout(null);

    loadSquares();
    loadPieces();
    gameListener();
  }

  ///////////////////////////////////////////////////////////////////////////////
  // MEMBERS
  ///////////////////////////////////////////////////////////////////////////////

  public boolean validMove() {

    int x1 = (int)clickStart.getX();
    int y1 = (int)clickStart.getY();
    int x2 = (int)clickEnd.getX();
    int y2 = (int)clickEnd.getY();
    
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
    case EMPTY:
    break;
    }

    // CHECK FOR COLLISION
    if( checkCollision(clickStart,clickEnd)) return false;

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

  public boolean checkCollision(Point a, Point b){
    if(board[(int)a.getY()][(int)a.getX()].piece.type != ChessPiece.KNIGHT){
      int xp = (int)b.getX();
      int yp = (int)b.getY();
      while(true)
      {
        // increment in direction of root
        if(xp>(int)a.getX()){xp--;}else if(xp<(int)a.getX()){xp++;}
        if(yp>(int)a.getY()){yp--;}else if(yp<(int)a.getY()){yp++;}
        // if at root exit while
        if(yp==(int)a.getY() && xp==(int)a.getX()) break;
        // if piece found exit validation
        if(board[yp][xp].piece != null) return true;        
      }      
    }
    return false;
  }

  public void movePiece() {

    board[(int)clickEnd.getY()][(int)clickEnd.getX()].piece = board[(int)clickStart.getY()][(int)clickStart.getX()].piece;
    board[(int)clickStart.getY()][(int)clickStart.getX()].piece = null;

    // Update Current State
    //ai.setState(board);
    //currentState.print();
    if (Main.client != null && Main.client.isActive()) {
      Main.client.sendData(ai.serialize());
    }
    if (Main.server != null && Main.server.isActive()) {
      Main.server.sendData(ai.serialize());
    }
  }

  public void loadSquares() {
    for (int y = 0; y < 8; y++) {
      for (int x = 0; x < 8; x++) {
        board[y][x] = new Square(x,y);
      }
    }
  }

  public void loadPieces()
  {
    ChessPiece type = ChessPiece.PAWN;
    int       side   = 1;

    int w = 0;
    for(int y=0;y<8;y++)
    {      
      if(y==4) side--;
      
      for(int x=0;x<8;x++)
      {

        if( (y==0 || y==7) && (x==0 || x==7)) {
          type = ChessPiece.ROOK;
          w=1;
        }
        if( (y==0 || y==7) && (x==1 || x==6)) {
          type = ChessPiece.KNIGHT;
          w=1;
        }
        if( (y==0 || y==7) && (x==2 || x==5)) {
          type = ChessPiece.BISCHOP;
          w=1;
        }
        if( (y==0 || y==7) && (x==3)) {
          type = ChessPiece.QUEEN;
          w=1;
        }
        if( (y==0 || y==7) && (x==4)) {
          type = ChessPiece.KING;
          w=1;
        }
        if(y==1 || y==6) {
          type = ChessPiece.PAWN;
          w=1;
        }        

        if(w==1) {
          board[y][x].piece = new Piece(type,side);
        }        
        w=0;
      }      
    }
  }
  
  public void checkCapture(){    
    if( board[(int)clickEnd.getY()][(int)clickEnd.getX()].piece != null ){
      System.out.println("captured: " + board[(int)clickEnd.getY()][(int)clickEnd.getX()].piece.type);
      history.add( new Move(clickStart,clickEnd,board[(int)clickEnd.getY()][(int)clickEnd.getX()].piece) );
    }else{
      history.add( new Move(clickStart,clickEnd,null) );
    }
  }

  public void gameListener() {
    
    addMouseMotionListener(new MouseAdapter() {

      // BOARD/MOUSE INTERACTION
      public void mouseMoved(MouseEvent e){
        cursor = e.getPoint();
        if(clickStart != null) repaint();
      }

    });

    addMouseListener(new MouseAdapter() {    

      // BOARD/MOUSE INTERACTION
      public void mousePressed(MouseEvent e) {

        boolean valid = false;
        
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
            if (board[y][x].shape.contains(e.getPoint()) && clickStart == board[y][x].coord) {
              // do nothing because validator below will catch it
            } else if (board[y][x].shape.contains(e.getPoint()) && clickStart == null && board[y][x].piece != null
                && board[y][x].piece.side == turn) {
              System.out.println("from " + board[y][x].coord);
              clickStart = new Point(x,y);
              valid = true;
              // if a start has already been selected set the destination
            } else if (board[y][x].shape.contains(e.getPoint()) && clickStart != null) {
              System.out.println("to " + board[y][x].coord);
              clickEnd = new Point(x,y);
              
              if (validMove()) {
                board[(int)clickStart.getY()][(int)clickStart.getX()].piece.moved = 1;
                turn = (turn == 0) ? 1 : 0;
                checkCapture();
                movePiece();
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
        if( board[y][x].coord.equals(clickStart) )
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
          
          fn = "./img/" + (board[y][x].piece.side == 0 ? "w" : "b") + "_" + board[y][x].piece.type.name().toLowerCase() + ".png";
          
          try {
            ui = ImageIO.read(new File(fn));
          } catch (IOException e) {
            e.printStackTrace();
          }
           
          if( board[y][x].coord.equals(clickStart) ){
            g.drawImage(ui, (int)cursor.getX()-board[0][0].size/2, (int)cursor.getY()-(board[0][0].size/2)-25, null);
          }else{
            g.drawImage(ui, board[y][x].shape.getBounds().x, board[y][x].shape.getBounds().y, null);
          }          
        }
      }
    }

    int jxw = board[0][0].offx-board[0][0].size*2;
    int jyw = board[0][0].offy;
    
    int jxb = board[0][0].offx+(board[0][0].size*9-19);
    int jyb = board[0][0].offy;
    
    int cntw=0,cntb=0;
    
    for(Move move : history){
      
      if(move.captured != null){
        
        fn = "./img/" + (move.captured.side == 0 ? "w" : "b") + "_" + move.captured.type.name().toLowerCase() + ".png";
        
        try {
          ui = ImageIO.read(new File(fn));
        }catch(IOException e){
          e.printStackTrace();
        }
        
        if(move.captured.side==0){
          g.drawImage(ui,jxw,jyw, null);
          jyw+=board[0][0].size;
          cntw++;
          
          if(cntw==8){
            jxw = board[0][0].offx-(board[0][0].size*2)-50;
            jyw = board[0][0].offy;            
          }
          
        }else{
          g.drawImage(ui,jxb,jyb, null);
          jyb+=board[0][0].size; 
          cntb++;
          
          if(cntb==8){
            jxb = board[0][0].offx+(board[0][0].size*9-19)+50;
            jyb = board[0][0].offy;
          }
          
        }

      }
    }

  }
}
