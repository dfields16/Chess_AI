import javax.swing.*;
import java.awt.Color;
import java.io.IOException;

public class Main {

  public static void main(String[] args) throws IOException {
    // if(JOptionPane.showConfirmDialog (null, "Is this a multiplayer session?","Networking",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
    //   if(JOptionPane.showConfirmDialog (null, "Are you the host?","Networking",JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION){
    //     //Connect to game
    //     String ip = JOptionPane.showInputDialog(null, "Enter an IP address:");

    //   }else{
    //     //Host Game

    //   }
    // }
    
    
    
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