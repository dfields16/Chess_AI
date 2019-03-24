import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

enum ChessPiece{EMPTY,KING,QUEEN,BISCHOP,KNIGHT,ROOK,PAWN}


class Piece{

  BufferedImage icon;
  ChessPiece    type;
  Color        color;
  Integer     square;
  boolean isChecked, hasMoved;

  
  
  public Piece(ChessPiece p,Color c,int s)
  {
    square = s;
    color  = c;
    type   = p;
    if(p == ChessPiece.EMPTY)return;
    try {
      String imgPath = "./img/" + ((color == Color.BLACK) ? 'b' : 'w') + "_" + type.name().toLowerCase() + ".png";
      //System.out.println(imgPath);
      icon = ImageIO.read(new File(imgPath));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}