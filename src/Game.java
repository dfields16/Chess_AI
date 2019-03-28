import java.awt.*;
import java.awt.event.*;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

class Game extends JPanel {
  private static final long serialVersionUID = 1L;

  // MEMBER VARIABLES
  AI ai;

  Square[][] board = new Square[8][8];  
  ArrayList<Move> history;

  Point cursor;
  Move  click;

  int turn    = 0;
  int turnlen = 10;
  int timer   = 0;  
  
  BufferedImage ui;  
  
  // CONSTRUCTOR
  public Game(){
    setLayout(null);
    startGame();
    gameListener();
  }

  public void startGame(){
    
    history = new ArrayList<Move>();
    click   = new Move();
    
    loadSquares();
    loadPieces();     
    
    Timer clock    = new Timer();    
    TimerTask task = new TimerTask(){
      @Override
      public void run() {        
        timer++;
        if(timer>turnlen){
          click.clear();
          turn = (turn==0) ? 1 : 0;
          timer = 1;
        }
        repaint();
      }
    };
    clock.scheduleAtFixedRate(task,1000,1000);
    
  }

  ///////////////////////////////////////////////////////////////////////////////
  // MEMBERS
  ///////////////////////////////////////////////////////////////////////////////

  public boolean validMove(Move move) {
    
    Piece start = board[move.y1()][move.x1()].piece;
    Piece end   = board[move.y2()][move.x2()].piece;
    
    boolean valid    = true;
    boolean pawntest = true;
    
    int dx = move.x2()-move.x1();
    int dy = move.y2()-move.y1();
    int py = (turn==0) ? (-1)*dy : dy;

    List<Float> slopes    = new ArrayList<>();
    List<Float> distances = new ArrayList<>();
    
    // CALCULATE THE SLOPE/DISTANCE FOR THE DESIRED MOVE
    float slope = (dx==0 || dy==0) ? 0 : (float)dy / (float)dx;
    float dist  = (float) Math.sqrt(dx*dx + dy*dy);
    
    // IF TARGET IS ON SAME TEAM LEAVE EARLY
    if (end != null && end.side == start.side) valid = false;
    
    // ASSIGN EACH OF THE PIECES A SLOPE AND DISTANCE THEY CAN MOVE
    switch (start.type) {
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
      if (start.moved == 0) distances.add((float) 2);

      // prevent horizontal pawn movement
      if(dy==0) pawntest = false;
      // prevent backward movement
      if(py<0) pawntest = false;
      // if trying to go forward prevent capture
      if(Math.abs(slope) == 0 && (end != null) ) pawntest = false;
      // if trying to go diagonal make sure it is a capture
      if(Math.abs(slope) == 1 && (end == null || turn == end.side)) pawntest = false;
    break;
    case EMPTY:
    break;
    }
    
    // DID PAWN PASS
    if(!pawntest) valid = false;
    
    // CHECK FOR COLLISION
    if(checkCollision(click)) valid = false;

    // EVALUATE FINAL RESPONSE
    if(!listContains(slopes, Math.abs(slope))) valid = false;
    if(!listContains(distances, 0) && !listContains(distances, dist)) valid = false;
    
    // CAN CASTLE?
    //If king is moving, and not in check and has not moved yet
    if(start.type == ChessPiece.KING && start.checked==0 && start.moved==0){  
      
      Square corner;
      Move   castle;
      // DETECT DIRECTION/AVAILABILITY
      // trying to castle small side
      if(dy==0 && dx>0 && dist==2){
        corner = board[move.y2()][move.x2()+1];
        castle = new Move(corner.coord,board[move.y2()][move.x2()-1].coord,null);
        if( corner.piece.type == ChessPiece.ROOK && corner.piece.moved==0 && !checkCollision(click) ){          
          movePiece(castle);
          valid = true;
        }        
      // trying to castle big side
      }else if(dy==0 && dx<0 && dist==3){
        corner = board[move.y2()][move.x2()-1];
        castle = new Move(corner.coord,board[move.y2()][move.x2()+1].coord,null);
        if( corner.piece.type == ChessPiece.ROOK && corner.piece.moved==0){
          movePiece(castle);
          valid = true;
        }
      }
    }

    return valid;
  }

  boolean listContains(List<Float> list, float key) {
    for (float elem : list)
      if (elem == key)
        return true;
    return false;
  }

  public boolean checkCollision(Move move){
    if(board[move.y1()][move.x1()].piece.type != ChessPiece.KNIGHT){
      int xp = move.x2();
      int yp = move.y2();
      while(true)
      {
        // increment in direction of root
        if(xp>move.x1()){xp--;}else if(xp<move.x1()){xp++;}
        if(yp>move.y1()){yp--;}else if(yp<move.y1()){yp++;}
        // if at root exit while
        if(yp==move.y1() && xp==move.x1()) break;
        // if piece found exit validation
        if(board[yp][xp].piece != null) return true;        
      }      
    }
    return false;
  }

  public void movePiece(Move move){
    
    board[move.y1()][move.x1()].piece.moved = 1;

    checkCapture(move);

    board[move.y2()][move.x2()].piece = board[move.y1()][move.x1()].piece;
    board[move.y1()][move.x1()].piece = null;
    
    timer = 0;

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
    int        side = 1;

    for(int y=0;y<8;y++)
    {      
      if(y==4) side--;
      for(int x=0;x<8;x++)
      {
        type = null;        
        if( (y==0 || y==7) && (x==0 || x==7)) type = ChessPiece.ROOK;
        if( (y==0 || y==7) && (x==1 || x==6)) type = ChessPiece.KNIGHT;
        if( (y==0 || y==7) && (x==2 || x==5)) type = ChessPiece.BISCHOP;
        if( (y==0 || y==7) && (x==3))         type = ChessPiece.QUEEN;
        if( (y==0 || y==7) && (x==4))         type = ChessPiece.KING;
        if  (y==1 || y==6)                    type = ChessPiece.PAWN;
        if(type != null) board[y][x].piece = new Piece(type,side);
      }
    }
  }

  public boolean checkCapture(Move move){    
    if( board[move.y2()][move.x2()].piece != null ){
      history.add( new Move(move.start,move.end,board[move.y2()][move.x2()].piece) );
      return true;
    }else{
      history.add( new Move(move.start,move.end,null) );
      return false;
    }
  }

  public void gameListener() {
    
    addMouseMotionListener(new MouseAdapter() {

      // BOARD/MOUSE INTERACTION
      public void mouseMoved(MouseEvent e){
        cursor = e.getPoint();
        if(click.start != null) repaint();
      }

    });

    addMouseListener(new MouseAdapter() {    

      // BOARD/MOUSE INTERACTION
      public void mousePressed(MouseEvent e) {

        boolean valid = false;
        
        // previously dealt with a final click, flush the trigger
        if (click.end != null) click.clear();

        // SEE IF A SQUARE WAS CLICKED
        for (int y = 0; y < 8; y++) {
          for (int x = 0; x < 8; x++) {
            // if an initial square is selected set to start or start over if already selected
            if (board[y][x].shape.contains(e.getPoint()) && click.start == board[y][x].coord) {
              // do nothing because validator below will catch it
            } else if (board[y][x].shape.contains(e.getPoint()) && click.start == null && board[y][x].piece != null
                && board[y][x].piece.side == turn) {
              System.out.println("from " + board[y][x].coord);
              click.start = new Point(x,y);
              valid = true;
              // if a start has already been selected set the destination
            } else if (board[y][x].shape.contains(e.getPoint()) && click.start != null) {
              System.out.println("to " + board[y][x].coord);
              click.end = new Point(x,y);
              
              if (validMove(click)) {
                movePiece(click);
                turn = (turn == 0) ? 1 : 0;
              }

              valid = false;
            }

          }
        }

        // if something other than square was clicked
        if (!valid) {
          click.clear();
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
    
    String pack = "marble";
    
    // DRAW BACKGROUND
    try {
      ui = ImageIO.read(new File("./img/"+pack+"/bg.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }   
    g.drawImage(ui, 0, 0, null);
    // DRAW TRIM
    try {
      ui = ImageIO.read(new File("./img/"+pack+"/trim.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    g.drawImage(ui, board[0][0].offx - 24, board[0][0].offy - 24, null);

    // DRAW TURN INDICATOR
    g2.setColor((turn == 0) ? Color.decode("#ffffff") : Color.decode("#000000"));
    g2.fillRect(board[0][0].offx - 25, board[0][0].offy - 25, 25, 25);
    
    // DRAW TIMER
    g2.setColor((turn==0) ? Color.decode("#ffffff") : Color.decode("#000000"));
    g2.fillRect(board[0][0].offx+board[0][0].size*6,board[0][0].offy-60,board[0][0].size*2,25);
    g2.setColor((turn==0) ? Color.decode("#000000") : Color.decode("#ffffff"));
    String timedis = String.valueOf("Time: ")+String.format("%02d",timer)+" of "+String.format("%02d",turnlen);
    g.setFont(new Font("default", Font.BOLD, 16));
    g2.drawString(timedis,board[0][0].offx+15+board[0][0].size*6,board[0][0].offy-42);

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

        //g2.setColor(toggle == 1 ? Color.BLACK : Color.white);
        g2.setColor(toggle == 1 ? Color.decode("#603f2f") : Color.decode("#dfa070") );
        
        if( board[y][x].coord.equals(click.start) )
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
      ui = ImageIO.read(new File("./img/"+pack+"/board.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }    
    g.drawImage(ui,board[0][0].offx,board[0][0].offy, null);

    // DRAW PIECES
    String fn;
    for (int y = 0; y < 8; y++) {
      for (int x = 0; x < 8; x++) {
        if (board[y][x].piece != null) {
          
          fn = "./img/" + pack + "/" + (board[y][x].piece.side == 0 ? "W" : "B") + "_" + board[y][x].piece.type.name() + ".png";
          
          try {
            ui = ImageIO.read(new File(fn));
          } catch (IOException e) {
            e.printStackTrace();
          }
           
          if( board[y][x].coord.equals(click.start) ){
            g.drawImage(ui, (int)cursor.getX()-board[0][0].size/2, (int)cursor.getY()-(board[0][0].size/2), null);
          }else{
            g.drawImage(ui, board[y][x].shape.getBounds().x, board[y][x].shape.getBounds().y, null);
          }          
          
        }
      }
    }
    
    //DRAW THE JAILYARD
    int jxw = board[0][0].offx-board[0][0].size*2;
    int jyw = board[0][0].offy;
    
    int jxb = board[0][0].offx+(board[0][0].size*9-19);
    int jyb = board[0][0].offy;
    
    int cntw=0,cntb=0;
    
    for(Move move : history){
      
      if(move.captured != null){
        
        fn = "./img/" + pack + "/" + (move.captured.side == 0 ? "W" : "B") + "_" + move.captured.type.name() + ".png";
        
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
