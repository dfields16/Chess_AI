import java.io.IOException;

import javax.swing.JFrame;

public class Main{

  public static void main(String[] args) throws IOException
  {

    JFrame frame = new JFrame("Chess Game");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(1020, 765);
    frame.setResizable(false);

    Menu menu = new Menu(frame);
    frame.add(menu);
    frame.setVisible(true);
  }

}