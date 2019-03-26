enum ChessPiece {
  KING, QUEEN, BISCHOP, KNIGHT, ROOK, PAWN
}

class Piece {

  ChessPiece type;

  // FLAGS
  int side;
  int moved;
  int checked;

  public Piece(ChessPiece p, int c) {
    type = p;
    side = c;
  }

}