enum ChessPiece {
  EMPTY(0), KING(1000), QUEEN(100), BISCHOP(50), KNIGHT(75), ROOK(50), PAWN(25);

  final int value;

  ChessPiece(int v) {
    value = v;
  }

}

class Piece {

  ChessPiece type;

  // FLAGS
  int side    =-1;
  int moved   = 0;
  int checked = 0;

  public Piece(ChessPiece p, int c) {
    type = p;
    side = c;
  }
  
  public Piece(Piece p) {
    type    = p.type; 
    side    = p.side;
    moved   = p.moved;
    checked = p.checked;
  }

}