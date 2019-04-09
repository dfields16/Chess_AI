import java.awt.Point;

class Move{
  
  Point start    = null;
  Point end      = null;
  Piece captured = null;
  
  Move(){}
  
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
  
}