import java.awt.Point;
import java.io.Serializable;

class Move implements Serializable{

  private static final long serialVersionUID = -6401644785337543164L;
  Point start = null;
  Point end      = null;
  Piece captured = null;

  Move(){}

  Move(Move m){
    if(m == null) {
      start    = null;
      end      = null;
      captured = null;
    }else {
      start    = (m.start == null) ? null : new Point(m.start);
      end      = (m.end   == null) ? null : new Point(m.end);
      captured = (m.captured == null) ? null : new Piece(m.captured);
    }
  }

  Move(int x1, int y1,int x2, int y2){
    start = new Point(x1,y1);
    end   = new Point(x2,y2);
  }

  Move(Point f, Point t){
    start = f;
    end   = t;
  }

  Move(Point f, Point t,Piece c){
    start = f;
    end   = t;
    captured = c;
  }

  public int x1(){
    return (int)start.getX();
  }
  public int y1(){
    return (int)start.getY();
  }
  public int x2(){
    return (int)end.getX();
  }
  public int y2(){
    return (int)end.getY();
  }

  public void clear(){
    start = null;
    end   = null;
  }

  public static Move deserialize(String str) {
    Move mv = new Move();
    mv.start = new Point(str.charAt(0) - 'A', '8' - str.charAt(1));
    mv.end = new Point(str.charAt(3) - 'A', '8' - str.charAt(4));
    return mv;
  }

  public String serialize() {
    String s = "";
    s += (char)('A' + start.getX());
    s += (char)('8' - start.getY());
    s += " ";
    s += (char)('A' + end.getX());
    s += (char)('8' - end.getY());
    return s;
  }
}