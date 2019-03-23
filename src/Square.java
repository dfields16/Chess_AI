import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;

class Square{

  int   id,x,y,size;
  int   piece = -1;
  String coord;
  Color color;
  Shape shape;

  public Square(int i, String n, int h, int v, int d, Color r)
  {
    id    = i;
    coord = n;
    x     = h;
    y     = v;
    size  = d;
    color = r;
    shape = new Rectangle.Double(x,y,size,size);
  }

}