import java.awt.Rectangle;
import java.awt.Shape;

class Square {

  int size = 75;
  int offx = 100;
  int offy = 100;

  String id;
  int col, row;

  Piece piece = null;
  Shape shape;

  public Square(String i, int c, int r) {
    id = i;
    col = c;
    row = r;
    shape = new Rectangle.Double((offx + c * size), (offy + r * size), size, size);
  }

}