
public class Response{

  Square[][] board;
  Move move;
  boolean valid;
  int turn;

  Response(){}
  
  Response(Square[][] b, Move m, boolean v,int t){    
    board = b;
    move  = m;
    valid = v;   
    turn  = t;
  }

}