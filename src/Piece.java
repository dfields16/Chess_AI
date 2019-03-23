import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

class Piece{

  BufferedImage icon;
  ChessPiece    type;
  Color        color;
  Integer     square;

  public Piece(ChessPiece p,String imgPath,Color c,int s)
  {
    square = s;
    color  = c;
    type   = p;

    try {
      icon = ImageIO.read(new File(imgPath));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}