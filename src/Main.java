import javax.swing.*;
import java.awt.Color;
import java.io.IOException;

public class Main {

  public static void main(String[] args) throws IOException {
    Board board = new Board();

    JFrame frame = new JFrame("Chess Master");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(775, 775);
    frame.setResizable(false);
    frame.setBackground(Color.decode("#003300"));

    frame.add(board);

    frame.setVisible(true);
  }

}