import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class Main{

  public static void main(String[] args) throws IOException
  {
    setLookAndFeel();
    JFrame frame = new JFrame("Chess Game");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(1020, 765);
    frame.setResizable(false);

    Menu menu = new Menu(frame);
    frame.add(menu);
    frame.setVisible(true);
  }

  static void setLookAndFeel(){
    try {
      for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
          if ("Nimbus".equals(info.getName())) {
              UIManager.setLookAndFeel(info.getClassName());
              break;
          }
      }
  } catch (Exception e) {
  }
  }
}