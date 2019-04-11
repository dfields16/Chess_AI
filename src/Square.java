import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;

class Square {

  int size = 75;
  int offx = 215;
  int offy = 75;

  Point coord = new Point();
  Shape shape;

  Piece piece = null;

  public Square(int c, int r){
    coord.setLocation(c,r);
    shape = new Rectangle.Double((offx + c * size), (offy + r * size), size, size);
  }

  public Square(Square s){
    coord = new Point(s.coord);
    shape = new Rectangle.Double((s.offx + s.coord.y * size), (s.offy + s.coord.x * size), s.size, s.size);
    piece = (s.piece==null) ? null : new Piece(s.piece);
  }

}
