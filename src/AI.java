import java.util.ArrayList;
import java.util.Random;

public class AI {

    Square[][] state = new Square[8][8];
    int side;

    public AI(){}

    public Move getMove(Square[][] b, int d, int s) {
      state = b;
      side  = s;
      //return maxFun(state,0,d);
      return tmpMove();
    }
    
    public Move tmpMove(){
      
      Random rand = new Random();
      int pc = 0;
      int mc = 0;
      
      Move move;
      Move fb = null;
      
      for (int y1 = 0; y1 < state.length; y1++) {
        for (int x1 = 0; x1 < state[y1].length; x1++) {
          
          // FIND A PIECE ON MY SIDE
          if(state[y1][x1].piece != null && state[y1][x1].piece.side == side) {
            
            pc++;
            
            System.out.println("piece: " + state[y1][x1].piece.type + " " + state[y1][x1].piece.side);
            
            if(rand.nextInt(2) > 0 | pc>10){
              //FIND A SPACE IT CAN MOVE
              for (int y2 = 0; y2 < state.length; y2++) {
                for (int x2 = 0; x2 < state[y2].length; x2++) {
                  
                  move = new Move(x1,y1,x2,y2);
                  fb   = new Move(x1,y1,x2,y2);
                  
                  //IF WE HAVE A VALID MOVE BLINDLY RETURN IT
                  if(Util.validMove(state, move, side)) {
                    
                    mc++;
                    
                    if(rand.nextInt(2) > 0 | mc>2) {
                      return new Move(x1,y1,x2,y2);
                    }
                  }
                  
                }
              }
              
            }
          }
          
        }
      }
      
      return fb;
        
    }

    public Move maxFun(Square[][] board, int depth, int maxDepth) {      
      Move move = null;
      return move;
    }

    public Move minFun(Square[][] board, int depth, int maxDepth) {      
      Move move = null;
      return move;
    }

    public void setState(Square[][] squares) {
        // clear();
        // state = squares;
    }

    public Move bestMove(ArrayList<Move> moves, boolean bestWorst) {
      Move move = null;
      return move;
    }

    private int heuristic(Square[][] board) {
        int val = 0;
        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[y].length; x++) {
                if (board[y][x].piece == null)
                    continue;
                
                int mod = 1;
                if(x > 1 && x < 6 && y > 1 && y < 6)mod = 2;
                if (board[y][x].piece.side == side) {
                    val += board[y][x].piece.type.value*2;
                } else {
                    val -= board[y][x].piece.type.value*2;
                }
            }
        }
        return val;
    }

    public String serialize(Square[][] state) {
        String dat = "";
        for (int y = 0; y < state.length; y++) {
            for (int x = 0; x < state.length; x++) {
                Piece p = state[y][x].piece;
                if (p == null)
                    p = new Piece(ChessPiece.EMPTY, -1);
                dat += p.type.toString() + ":" + ((p.side == 0) ? "WHITE" : "BLACK") + ":" + toPoint(x, y) + ":"
                        + p.moved + ",";
            }
        }
        return dat.substring(0, dat.length() - 2);
    }

    private String toPoint(int x, int y) {
        String tmp = "";
        tmp += (char) ('A' + y);
        tmp += x;
        return tmp;
    }

    public static Square[][] deserialize(String str) {
        Square[][] dState = new Square[8][8];
        for (String s : str.split(",")) {
            String[] dat = s.split(":");
            Piece p = new Piece(ChessPiece.valueOf(dat[0]), ((dat[1].equals("WHITE")) ? 0 : 1));
            // p.hasMoved = dat[3].from;
            int x = (int) dat[2].charAt(1) - '0';
            int y = (int) dat[2].charAt(0) - 'A';
            if (p.type == ChessPiece.EMPTY)
                p = null;
            Square sqr = new Square(x, y);
            sqr.piece = p;
            dState[y][x] = sqr;
        }
        return dState;
    }

    public boolean equals(Square[][] s1, Square[][] s2) {
        return this.serialize(s1).equals(serialize(s2));
    }
}

class Pair {
    Square[][] first;
    int second;

    public Pair() {

    }

    public Pair(Square[][] f, int s) {
        first = f;
        second = s;
    }
}