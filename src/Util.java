import java.util.List;
import java.awt.Point;
import java.util.ArrayList;

public class Util {

  public static void resetPieces(Board board) {
    ChessPiece type = ChessPiece.PAWN;
    int side = 1;

    for (int y = 0; y < 8; y++) {
      if (y == 4)
        side--;
      for (int x = 0; x < 8; x++) {
        type = null;
        if ((y == 0 || y == 7) && (x == 0 || x == 7))
          type = ChessPiece.ROOK;
        if ((y == 0 || y == 7) && (x == 1 || x == 6))
          type = ChessPiece.KNIGHT;
        if ((y == 0 || y == 7) && (x == 2 || x == 5))
          type = ChessPiece.BISCHOP;
        if ((y == 0 || y == 7) && (x == 3))
          type = ChessPiece.QUEEN;
        if ((y == 0 || y == 7) && (x == 4))
          type = ChessPiece.KING;
        if (y == 1 || y == 6)
          type = ChessPiece.PAWN;
        if (type != null)
          board.squares[y][x].piece = new Piece(type, side);
      }
    }
  }
  
  public Board makeMove(Board board, Move move){

    if( Util.movePiece(board,move) ){

      board.turn = (board.turn == 0) ? 1 : 0;
      board.timer = 1;

    }

    return board;
  }

  public static boolean inCheck(Board board, Move move, int side) {

    Util.forcePiece(board,move);
    board.turn = side;
    
    Square king = getKing(board,side);
    Move m;
     
    if(king.piece != null) {
      
      System.out.println(king.coord.getX() + " " + king.coord.getY());
    
      for (int y = 0; y < 8; y++) {
      for (int x = 0; x < 8; x++) {
      if (board.piece(x,y) != null && board.piece(x,y).side != side) {
          m = new Move(board.coord(x,y),king.coord);
          if(validMove(board,m)) return true;    
      }
      }
      }

    }

    return false;
  }

//  public static boolean inCheckmate(Square[][] board, int side) {
//    Square king = getKing(board,side);
//    int kx = (int) king.coord.getX();
//  int ky = (int) king.coord.getY();
//  int[] opponentCapture = new int[8];
//    boolean checkmate = false;
//    if(board[ky][kx].piece.checked == 0) {
//      checkmate = false;
//    }
//    else {
//
//      Point tempKing;
//      Point newMove;
//      Move tempMove;
//
//      for(int x = 0; x < 8; x++) {
//        for(int y = 0; y < 8; y++) {
//          newMove = new Point(x,y);
//          if(ky+1 < 8 && board[y][x].piece != null && board[y][x].piece.side != side) {
//            // NEed to change move to Move(point, point);
//            tempKing = new Point(kx, ky+1);
//            tempMove = new Move(newMove, tempKing);
//            if(validMove(board, tempMove, board[y][x].piece.side)) {
//              opponentCapture[0]++;
//            }
//          }
//          if(ky+1 < 8 && kx+1 < 8 && board[y][x].piece != null && board[y][x].piece.side != side) {
//            tempKing = new Point(kx+1, ky+1);
//            tempMove = new Move(newMove, tempKing);
//            if(validMove(board, tempMove, board[y][x].piece.side)) {
//              opponentCapture[1]++;
//            }
//          }
//          if(ky-1 >0 && kx+1 < 8 && board[y][x].piece != null && board[y][x].piece.side != side) {
//            tempKing = new Point(kx+1, ky-1);
//            tempMove = new Move(newMove, tempKing);
//            if(validMove(board, tempMove, board[y][x].piece.side)) {
//              opponentCapture[2]++;
//            }
//          }
//          if(ky-1 >0 && kx-1 >0 && board[y][x].piece != null && board[y][x].piece.side != side) {
//            tempKing = new Point(kx-1, ky-1);
//            tempMove = new Move(newMove, tempKing);
//            if(validMove(board, tempMove, board[y][x].piece.side)) {
//              opponentCapture[3]++;
//            }
//          }
//          if(kx-1 >0 && ky+1 < 8 && board[y][x].piece != null && board[y][x].piece.side != side) {
//            tempKing = new Point(kx-1, ky);
//            tempMove = new Move(newMove, tempKing);
//            if(validMove(board, tempMove, board[y][x].piece.side)) {
//              opponentCapture[4]++;
//            }
//          }
//          if(kx+1 < 8 && board[y][x].piece != null && board[y][x].piece.side != side) {
//            tempKing = new Point(kx+1, ky);
//            tempMove = new Move(newMove, tempKing);
//            if(validMove(board, tempMove, board[y][x].piece.side)) {
//              opponentCapture[5]++;
//            }
//          }
//          if(ky-1 >0 && board[y][x].piece != null && board[y][x].piece.side != side) {
//            tempKing = new Point(kx, ky-1);
//            tempMove = new Move(newMove, tempKing);
//            if(validMove(board, tempMove, board[y][x].piece.side)) {
//              opponentCapture[6]++;
//            }
//          }
//          if(kx-1 < 8 && board[y][x].piece != null && board[y][x].piece.side != side) {
//            tempKing = new Point(kx-1, ky);
//            tempMove = new Move(newMove, tempKing);
//            if(validMove(board, tempMove, board[y][x].piece.side)) {
//              opponentCapture[7]++;
//            }
//          }
//        }
//      }
//
//      if(kx+1 < 8 && ky+1<8 ) {
//          if(opponentCapture[0] != 0 && opponentCapture[1] != 0 && opponentCapture[5] != 0) {
//            checkmate = true;
//          }
//        }
//      else if(kx-1>0  && ky+1<8 ) {
//          if(opponentCapture[4] != 0 && opponentCapture[7] != 0 && opponentCapture[0] != 0) {
//            checkmate = true;
//          }
//        }
//      else if(kx-1>0  && ky-1>0 ) {
//          if(opponentCapture[6] != 0 && opponentCapture[7] != 0 && opponentCapture[3] != 0) {
//            checkmate = true;
//          }
//        }
//      else if(kx+1<8  && ky-1>0 ) {
//          if(opponentCapture[6] != 0 && opponentCapture[5] != 0 && opponentCapture[2] != 0) {
//            checkmate = true;
//          }
//        }
//      else {
//        int sum = 0;
//        for(int i = 0; i < 8; i++) {
//          if(opponentCapture[i] == 0) { // means king can go there
//            sum = 0;
//            break;
//          }
//          sum += opponentCapture[i];
//        }
//        if(sum != 0)
//          checkmate = true;
//      }
//    }
//
//    return checkmate;
//
//  }

  public static void addHistory(Board board,Move move) {

    if (checkCapture(board, move)) {
      board.history.add(new Move(move.start, move.end,board.piece(move.x2(),move.y2())));
    } else {
      board.history.add(new Move(move.start, move.end,null));
    }

  }
  
  public static boolean forcePiece(Board board, Move move) {

      board.valid = true;
      board.squares[move.y1()][move.x1()].piece.moved = 1;
      board.squares[move.y2()][move.x2()].piece = board.squares[move.y1()][move.x1()].piece;
      board.squares[move.y1()][move.x1()].piece = null;
      return true;
      
  }

  public static boolean movePiece(Board board, Move move) {

    if( !validMove(board,move) ) {
      board.valid = false;
      return false;
    }else {

      if(board.active > 0) addHistory(board,move);

      board.valid = true;
      board.squares[move.y1()][move.x1()].piece.moved = 1;
      board.squares[move.y2()][move.x2()].piece = board.squares[move.y1()][move.x1()].piece;
      board.squares[move.y1()][move.x1()].piece = null;
      return true;
    }

  }

//  public static ArrayList<Move> potentialMoves(Square[][] presentState, int side) {
//      ArrayList<Move> foundMoves = new ArrayList<Move>();
//
//      for (int y = 0; y < 8; y++) {
//          for (int x = 0; x < 8; x++) {
//              Piece p = presentState[y][x].piece;
//              if (p != null && p.side == side) {
//                  for (int y2 = 0; y2 < 8; y2++) {
//                      for (int x2 = 0; x2 < 8; x2++) {
//                          if (x == x2 && y == y2)
//                              continue;
//                          if (presentState[y2][x2].piece == null || presentState[y2][x2].piece.side != side) {
//                              Move testMove = new Move(new Point(x, y), new Point(x2, y2));
//
//                              if (Util.validMove(presentState, testMove, side)) {
//                                  foundMoves.add(testMove);
//                              }
//                          }
//                      }
//                  }
//              }
//          }
//      }
//
//      return foundMoves;
//  }

  public static boolean checkCapture(Board board, Move move){
      if (board.piece(move.x2(),move.y2()) != null) {
          return true;
      } else {
          return false;
      }
  }

  public static boolean validMove(Board board, Move move){

      Piece start = board.piece(move.x1(),move.y1());
      Piece end   = board.piece(move.x2(),move.y2());

      boolean valid    = true;
      boolean pawntest = true;

      int dx = move.x2() - move.x1();
      int dy = move.y2() - move.y1();
      int py = (start.side == 0) ? (-1) * dy : dy;

      List<Float> slopes    = new ArrayList<>();
      List<Float> distances = new ArrayList<>();

      // CALCULATE THE SLOPE/DISTANCE FOR THE DESIRED MOVE
      float slope = (dx == 0 || dy == 0) ? 0 : (float) dy / (float) dx;
      float dist  = (float) Math.sqrt(dx * dx + dy * dy);

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
          slopes.add((float) 1);
          distances.add((float) 1);
          distances.add((float) Math.sqrt(2));
          if (start.moved == 0) distances.add((float) 2);

          // prevent horizontal pawn movement
          if (dy == 0) pawntest = false;
          // prevent backward movement
          if (py < 0) pawntest = false;
          // if trying to go forward prevent capture
          if (Math.abs(slope) == 0 && (end != null)) pawntest = false;
          // if trying to go diagonal make sure it is a capture
          if (Math.abs(slope) == 1 && (end == null || start.side == end.side)) pawntest = false;
          break;
      case EMPTY:
          break;
      }

      // DID PAWN PASS
      if (!pawntest) valid = false;

      // CHECK FOR COLLISION
      if (checkCollision(board, move)) valid = false;

      // EVALUATE FINAL RESPONSE
      if (!listContains(slopes, Math.abs(slope))) valid = false;
      if (!listContains(distances, 0) && !listContains(distances, dist)) valid = false;

      // MOVING INTO CHECK?
      
      if(start.side == board.turn && board.active == 1){
        if( inCheck(new Board(board),move,board.turn) ) valid = false;
      }

      // // CAN CASTLE?
      // // If king is moving, and not in check and has not moved yet
      // if (start.type == ChessPiece.KING && start.side == turn && start.checked == 0
      // && start.moved == 0) {

      // Square corner;
      // Move castle;
      // // DETECT DIRECTION/AVAILABILITY
      // // trying to castle small side
      // if (dy == 0 && dx > 0 && dist == 2) {
      // corner = board[move.y2()][move.x2() + 1];
      // castle = new Move(corner.coord, board[move.y2()][move.x2() - 1].coord, null);
      // if (corner.piece.type == ChessPiece.ROOK && corner.piece.moved == 0 &&
      // !checkCollision(board, move)) {
      // movePiece(board, castle, turn);
      // valid = true;
      // }
      // // trying to castle big side
      // } else if (dy == 0 && dx < 0 && dist == 3) {
      // corner = board[move.y2()][move.x2() - 1];
      // castle = new Move(corner.coord, board[move.y2()][move.x2() + 1].coord, null);
      // if (corner.piece.type == ChessPiece.ROOK && corner.piece.moved == 0) {
      // movePiece(board, castle, turn);
      // valid = true;
      // }
      // }
      // }

      return valid;
  }

  public static boolean checkCollision(Board board, Move move){
      // knight is allowed to pass over pieces, all others cannot
      if (board.piece(move.x1(),move.y1()) != null && board.piece(move.x1(),move.y1()).type != ChessPiece.KNIGHT){
          int xp = move.x2();
          int yp = move.y2();
          while (true){
              // increment in direction of root
              if(xp > move.x1()) {
                  xp--;
              }else if (xp < move.x1()){
                  xp++;
              }
              if(yp > move.y1()){
                  yp--;
              }else if(yp < move.y1()){
                  yp++;
              }
              // if at root exit while
              if (yp == move.y1() && xp == move.x1()) break;
              // if piece found exit validation
              if (board.piece(xp,yp) != null) return true;
          }
      }
      return false;
  }

  public static Square getKing(Board board, int side) {

      for (int y = 0; y < 8; y++) {
          for (int x = 0; x < 8; x++) {
              if (board.piece(x,y) != null && board.piece(x,y).side == side && board.piece(x,y).type == ChessPiece.KING) {
                  Square sq = board.squares[y][x];
                  return sq;
              }
          }
      }
      
      return null;
  }

  static boolean listContains(List<Float> list, float key) {
      for (float elem : list)
          if (elem == key)
              return true;
      return false;
  }

}