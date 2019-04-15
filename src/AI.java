public class AI {

    int depth = 1;
    int level;

    Board b;
    Move  best;

    public AI(){}

    public AI(int skill){
      depth = (skill < 1 || skill > 5) ? 1 : skill;
    }

    public Move getMove(Board board){

      best  = new Move();

      level = -1;
      maxFunAB( new Board(board), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
      //if(Util.validMove(board, best))
      return best;
    //  else return null;
    }


    public double maxFunAB(Board board, double alpha, double beta){

      level++;

      double  val = Double.NEGATIVE_INFINITY;
      double tval = val;

      if(level==depth && Util.getKing(board, 0) != null && Util.getKing(board, 1)!= null) return heuristic(board);

      // FIND PIECES ON MY SIDE
      for(int y1 = 0; y1 < board.len('y'); y1++){
      for(int x1 = 0; x1 < board.len('x'); x1++){

        //FIND SPACES IT CAN MOVE
        if( board.piece(x1,y1) != null && board.piece(x1,y1).side == board.turn){
        for(int y2 = 0; y2 < board.len('y'); y2++){
        for(int x2 = 0; x2 < board.len('x'); x2++){

          Move move = new Move(x1,y1,x2,y2);
          b    = new Board(board);

          if( Util.movePiece(b,move) ){

            b.turn = (b.turn==0) ? 1 : 0;

            val = minFunAB(b, alpha, beta);

            if(val>tval) {
              tval = val;
              if(level == 0)
            	  best = move;
            }
            
            if (val > beta) {
                return val;
              }
            if (val > alpha) {
                alpha = val;
            }

          }

          


        }
        }
        }

      }
      }

      return val;
    }

    public double minFunAB(Board board, double alpha, double beta){

      double  val = Double.POSITIVE_INFINITY;
      double bval = val;

      if(level==depth) return heuristic(board);

      // FIND PIECES ON MY SIDE
      for(int y1 = 0; y1 < board.len('y'); y1++){
      for(int x1 = 0; x1 < board.len('x'); x1++){

        //FIND SPACES IT CAN MOVE
        if( board.piece(x1,y1) != null && board.piece(x1,y1).side == board.turn){
        for(int y2 = 0; y2 < board.len('y'); y2++){
        for(int x2 = 0; x2 < board.len('x'); x2++){

          Move move = new Move(x1,y1,x2,y2);
          b    = new Board(board);

          if( Util.movePiece(b,move) ){

            b.turn = (b.turn==0) ? 1 : 0;

            val = maxFunAB(b, alpha, beta);

            level --;

            if(val<bval) {
              bval = val;
            }
            if (val < alpha) {
                return val;
              }
              if (val < beta) {
                beta = val;
              }

          }
          

        }
        }
        }

      }
      }

      return val;
    }


    private int heuristic(Board board){

      int val = 0;
      int mod = 1;

      for (int y = 0; y < board.len('y'); y++){
        for (int x = 0; x < board.len('x'); x++){
          if(board.piece(x,y) == null) continue;
          Piece p = board.piece(x, y);
          if(x > 1 && x < 6 && y > 1 && y < 6) mod = 2;
          if((x == 4 || x == 5) && (y == 4 || y == 5)) mod = 3;

          if (board.piece(x,y).side == board.turn) {
            val += p.type.value + p.type.moveValue*mod;
          } else {
            val -= p.type.value + p.type.moveValue*mod;
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

    public int bestMove(Board board){

      Move move;

      level++;

      int score = -1000000;
      int val   = 0;

      // FIND PIECES ON MY SIDE
      for(int y1 = 0; y1 < board.len('y'); y1++){
      for(int x1 = 0; x1 < board.len('x'); x1++){

        //FIND SPACES IT CAN MOVE
        if( board.piece(x1,y1) != null && board.piece(x1,y1).side == board.turn){

        for(int y2 = 0; y2 < board.len('y'); y2++){
        for(int x2 = 0; x2 < board.len('x'); x2++){

          move = new Move(x1,y1,x2,y2);

          Board tmp = new Board(board);

          //IF WE HAVE A VALID MOVE GO DOWN THE RABBIT HOLE
          if( Util.movePiece(tmp,move) ){

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

            //print(tmp);
            //System.out.println(score);

          }

        }
        }

        }

      }
      }

    return score;
    }
}