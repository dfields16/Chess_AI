import java.awt.Point;
import java.awt.Shape;
import java.util.ArrayList;

class Board{

  Square[][]      squares;
  ArrayList<Move> history;

  boolean valid;

  int turn;
  int turnlen = 500;
  int timer   = 0;

  int checked = 0;
  int mated   = 0;
  
  int active  = 0;

  Board(Board board){

    active  = 0;
    valid   = board.valid;
    checked = board.checked;
    mated   = board.mated;
    turn    = board.turn;
    turnlen = board.turnlen;
    timer   = board.timer;
    squares = new Square[board.len('y')][board.len('x')];
    history = new ArrayList<Move>(board.history);

    for(int y = 0; y < board.len('y'); y++) {
    for(int x = 0; x < board.len('x'); x++) {

      squares[y][x] = new Square(board.squares[y][x]);

    }
    }

  }

  Board(Square[][] sqs){

    squares = new Square[sqs.length][sqs[0].length];
    history = new ArrayList<Move>();

    for(int y = 0; y < sqs.length; y++){
    for(int x = 0; x < sqs[0].length; x++){
      squares[y][x] = new Square(sqs[y][x]);
    }
    }

  }

  Board(int r,int c){
    squares = new Square[c][r];
    history = new ArrayList<Move>();

    for (int y = 0; y < c; y++) {
      for (int x = 0; x < r; x++) {
        squares[y][x] = new Square(x, y);
      }
    }

  }

  public int len(char a) {

    int s = 0;

    switch(a) {
    case 'x':case 'X':
      s = squares[0].length;
    break;
    case 'y':case 'Y':
      s = squares.length;
    break;
    }

    return s;
  }

  public Square getSquare(int x,int y){
    return squares[y][x];
  }

  public Piece piece(int x,int y){
    return squares[y][x].piece;
  }

  public Shape shape(int x,int y){
    return squares[y][x].shape;
  }

  public Point coord(int x,int y){
    return squares[y][x].coord;
  }

  public boolean in(int x,int y,Point p){
    return squares[y][x].shape.contains(p);
  }

  public int offx(int x,int y){
    return squares[y][x].offx;
  }

  public int offy(int x,int y){
    return squares[y][x].offy;
  }

  public int size(int x,int y){
    return squares[y][x].size;
  }

}