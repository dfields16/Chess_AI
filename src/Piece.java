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
  int side;
  int moved;
  int checked;

  public Piece(ChessPiece p, int c) {
    // int i = ChessPiece.BISCHOP.value;
    type = p;
    side = c;
  }

}