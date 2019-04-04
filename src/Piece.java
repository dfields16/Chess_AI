enum ChessPiece {
	EMPTY 	(0), 
	KING 	(100), 
	QUEEN 	(75), 
	BISCHOP (25), 
	KNIGHT	(50), 
	ROOK 	(25), 
	PAWN 	(10);
	

	final int value;
	
	ChessPiece (int v){value = v;}

}

class Piece {

  ChessPiece type;

  // FLAGS
  int side;
  int moved;
  int checked;

  public Piece(ChessPiece p, int c) {
//	int i = ChessPiece.BISCHOP.value;
    type = p;
    side = c;
  }

}