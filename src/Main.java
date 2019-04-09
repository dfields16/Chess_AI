import java.io.IOException;

import javax.swing.JFrame;

public class Main{
  
  public static void main(String[] args) throws IOException 
  {
    Game game = new Game();
    
    JFrame frame  = new JFrame("Chess Master");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(1020,765);
    frame.setResizable(false);

    frame.add(game);
    frame.setVisible(true);
  }

}