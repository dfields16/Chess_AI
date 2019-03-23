import java.awt.*;
import java.awt.event.*;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

enum ChessPiece{KING,QUEEN,BISCHOP,KNIGHT,ROOK,PAWN}

class Board extends JPanel{
  private static final long serialVersionUID = 1L;

  // MEMBER VARIABLES
  BufferedImage base,trim;
  Square[] squares = new Square[64];
   Piece[] pieces  = new Piece[32];

  int clickStart   = -1;
  int clickEnd     = -1;
  int chosen       = -1;  
  boolean valid    = false;

  // CONSTRUCTOR
  public Board()
  {    
    setLayout(null);

    loadBoard();
    loadSquares();
    loadPieces();
    gameListener();
  }

  // DRAW
  @Override
  protected void paintComponent(Graphics g) 
  {    
    // DRAW BACKGROUND
    g.drawImage(base,0,0, null);
    g.drawImage(trim,75,65, null);

    Graphics2D g2 = (Graphics2D) g;
    Color color;

    // DRAW BOARD
    for(Square square : squares){      

      if(clickStart == square.id){
        color = Color.decode("#6699cc");
        color = Color.decode("#99ff99");        
      }else if(clickEnd == square.id){
        color = Color.decode("#ff6666");
      }else{
        color = square.color;   
      }      

      g2.setColor(color);
      g2.fill(square.shape);
    }  

    // DRAW PIECES
    for(Piece piece : pieces) g.drawImage(piece.icon,squares[piece.square].x,squares[piece.square].y, null);
  }

  ///////////////////////////////////////////////////////////////////////////////
  // MEMBERS 
  ///////////////////////////////////////////////////////////////////////////////

  public void loadBoard() {

    try {
      base = ImageIO.read(new File("./img/bg/wood.png"));
      trim = ImageIO.read(new File("./img/bg/trim.png"));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  // LOAD CHESS BOARD
  public void loadSquares()
  {
    int sz      = 75;
    int offx    = 75;
    int offy    = 75;
    int toggle  = 0;
    int at      = 0;
    int row     = 8;

    char col;

    //DRAW THE GAME BOARD
    for(int y=offy;y<(offy+8*sz);y+=sz){
      col = 'A';
      for(int x=offx;x<(offx+8*sz);x+=sz){
        squares[at] = new Square(at,String.valueOf(col)+String.valueOf(row),x,y,sz,(toggle==0 ? Color.decode("#ffffff") : Color.decode("#000000")) );
        toggle = (toggle==0?1:0);
        at++;
        col++;
      }
      toggle = (toggle==0?1:0);
      row--;
    }
  }

  public void movePiece(int clickStart,int clickEnd) 
  {
    pieces[squares[clickStart].piece].square = clickEnd;            
    squares[clickEnd].piece = squares[clickStart].piece;
    squares[clickStart].piece = -1;
  }

  //LOAD CHESS PIECES
  public void loadPieces()
  { 
    int square = 0;

    String      icon = "./img/b_green.png";
    ChessPiece piece = ChessPiece.PAWN;
    Color      color = Color.BLACK;
    
    for(int i = 0;i<32;i++)
    {      
      if(i==16) square = 48;
      
      // SET THE PIECE
      switch(i) 
      {
      case 0:case 7:
        icon  = "./img/b_rook.png";
        piece = ChessPiece.ROOK;
        color = Color.BLACK;
      break;
      case 24:case 31:
        icon  = "./img/w_rook.png";
        piece = ChessPiece.ROOK;
        color = Color.WHITE;
      break;
      case 1:case 6:
        icon  = "./img/b_knight.png";
        piece = ChessPiece.KNIGHT;
        color = Color.BLACK;
      break;
      case 25:case 30:
        icon  = "./img/w_knight.png";
        piece = ChessPiece.KNIGHT;
        color = Color.WHITE;
      break;
      case 2:case 5:
        icon  = "./img/b_bischop.png";
        piece = ChessPiece.BISCHOP;
        color = Color.BLACK;
      break;
      case 26:case 29:
        icon  = "./img/w_bischop.png";
        piece = ChessPiece.BISCHOP;
        color = Color.WHITE;
      break;
      case 3:
        icon  = "./img/b_queen.png";
        piece = ChessPiece.QUEEN;
        color = Color.BLACK;        
      break;
      case 4:
        icon  = "./img/b_king.png";
        piece = ChessPiece.KING;
        color = Color.BLACK;
      break;
      case 27:
        icon  = "./img/W_queen.png";
        piece = ChessPiece.QUEEN;
        color = Color.BLACK;        
      break;
      case 28:
        icon  = "./img/W_king.png";
        piece = ChessPiece.KING;
        color = Color.BLACK;
      break;
      case 8:case 9:case 10:case 11:case 12:case 13:case 14:case 15:
        icon  = "./img/b_pawn.png";
        piece = ChessPiece.PAWN;
        color = Color.BLACK;
      break;
      case 16:case 17:case 18:case 19:case 20:case 21:case 22:case 23:
        icon  = "./img/w_pawn.png";
        piece = ChessPiece.PAWN;
        color = Color.WHITE;
      break;
      }     

      pieces[i]             = new Piece(piece,icon,color,square);
      squares[square].piece = i;

      square++;      
    }
  }

  public void gameListener() 
  {    
    // BOARD/MOUSE INTERACTION
    addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {

        // previously dealt with a final click, flush the trigger
        if(clickEnd>-1){
          clickStart = -1;
          clickEnd   = -1;
        }

        // SEE IF A SQUARE WAS CLICKED
        for(Square square : squares)
        {          
          // if an initial square is selected set to start or start over if already selected
          if( square.shape.contains(e.getPoint()) && clickStart==square.id ){

            // do nothing because validator below will catch it

          // if a start has already been selected set the destination
          }else if( square.shape.contains(e.getPoint()) && clickStart<0 && square.piece>-1  ){
            System.out.println("from " + square.coord);
            clickStart = square.id;
            valid = true;
          // if a start has already been selected set the destination
          }else if( square.shape.contains(e.getPoint()) && clickStart>-1 && square.piece<0 ){
            System.out.println("to " + square.coord);
            clickEnd   = square.id;

            movePiece(clickStart,clickEnd);

            valid = true;
          }
        }

        // if something other than square was clicked
        if(!valid){
          clickStart = -1;
          clickEnd   = -1;
        }else{
          valid = false;
        }

        // DISPLAY CHANGES
        repaint();

      }
    });
  }

}