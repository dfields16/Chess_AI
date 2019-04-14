import java.io.Serializable;

enum ChessPiece {
  EMPTY(0, 0), KING(1250, 25), QUEEN(500, 150), BISCHOP(200, 75), KNIGHT(250, 100), ROOK(200, 75), PAWN(25, 30);

  final int value;
  final int moveValue;
  ChessPiece(int v, int m) {
    value = v;
    moveValue = m;
  }

}

class Piece implements Serializable {

  private static final long serialVersionUID = -3427679479734507507L;

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