import java.util.ArrayList;
import java.util.Random;

public class AI {
    
    //what side are we trying to play for
    int side;
    
    int depth;
    int level = 0;
    
    Move best;

    public AI(int skill){
      depth = skill;
    }

    public Move getMove(Board b, int s){

      side  = s;
      best  = null;
      
      bestMove( new Board(b) );
      
      return best;
    }

    public int bestMove(Board board){

      Move move;

      level++;
      
      int score = -1000000;
      int val   = 0;

      // FIND PIECES ON MY SIDE
      for(int y1 = 0; y1 < board.len('y'); y1++){
      for(int x1 = 0; x1 < board.len('x'); x1++){

        // found one
        if( board.piece(x1,y1) != null && board.piece(x1,y1).side == side){

          //System.out.println("processing my: " + board.piece(x1,y1).type);

        //FIND SPACES IT CAN MOVE
        for(int y2 = 0; y2 < board.len('y'); y2++){
        for(int x2 = 0; x2 < board.len('x'); x2++){

          move = new Move(x1,y1,x2,y2);
          
          Board tmp = new Board(board);
          
          //IF WE HAVE A VALID MOVE GO DOWN THE RABBIT HOLE
          if( Util.movePiece(tmp,move,side) ){
            
            // GOING DEEPER OR GETTING A SCORE
            if(level<=depth){
              val = bestMove(tmp);
              level--;
            }else {
              val = heuristic(tmp);
            }
            
            // PROCESS THE SCORE AND MOVE
            if(val > score){
              score = val;
              best  = move;
            }

            print(tmp);
            System.out.println(score);

          }

        }
        }  

        }

      }
      }

    return score;
    }

    public Move maxFun(Square[][] board, int depth, int maxDepth){
      Move move = null;
      return move;
    }

    public Move minFun(Square[][] board, int depth, int maxDepth){
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

    private int heuristic(Board board) {
      
      int val = 0;
      int mod = 1;  
      
      for (int y = 0; y < board.len('y'); y++){
      for (int x = 0; x < board.len('x'); x++){
          
          if(board.piece(x,y) == null) continue;
                 
          if(x > 1 && x < 6 && y > 1 && y < 6) mod = 2;
          
          if (board.piece(x,y).side == side) {
            val += board.piece(x,y).type.value*mod;
          } else {
            val -= board.piece(x,y).type.value*mod;
          }
      }
      }
      return val;
    }

    public void print(Board board) {
      
      System.out.println("\n-----------------------------");
      
      String row;
      
      for (int y1 = 0; y1 < board.len('y'); y1++){  row = "";         
      for (int x1 = 0; x1 < board.len('x'); x1++){        
        
        if(board.piece(x1,y1) != null) {
          row = row + board.piece(x1,y1).type.name().substring(0,1) + " ";
        }else {
          row = row + "-" + " ";
        }

      } System.out.println(row);
      }

      System.out.println("-----------------------------");

    }

    public Move tmpMove(Board board){

      Random rand = new Random();
      int pc = 0;
      int mc = 0;

      Move move;
      Move fb = null;

      for (int y1 = 0; y1 < board.len('y'); y1++){
      for (int x1 = 0; x1 < board.len('x'); x1++){

        // FIND A PIECE ON MY SIDE
        if(board.piece(x1,y1) != null && board.piece(x1,y1).side == side){

          pc++;

          System.out.println("piece: " + board.piece(x1,y1).type + " " + board.piece(x1,y1).side);

          if(rand.nextInt(2) > 0 | pc>10){
            //FIND A SPACE IT CAN MOVE
            for (int y2 = 0; y2 < board.len('y'); y2++) {
            for (int x2 = 0; x2 < board.len('x'); x2++) {

              move = new Move(x1,y1,x2,y2);
              fb   = new Move(x1,y1,x2,y2);

              //IF WE HAVE A VALID MOVE BLINDLY RETURN IT
              if(Util.validMove(board.squares, move, side)){

                mc++;

                if(rand.nextInt(2) > 0 | mc>2){
                  return new Move(x1,y1,x2,y2);
                }
              }

            }
            }

          }
        }

      }
      }
      
      print(board);

      return fb;
    }
}
