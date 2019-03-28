import javax.swing.*;
import java.io.IOException;
import java.net.InetAddress;
import java.util.TimerTask;
import java.util.Timer;

public class Main {
  static TCPClient client = null;
  static TCPServer server = null;

  public static void main(String[] args) throws IOException {
    Game board = new Game();

    JFrame frame = new JFrame("Chess Master");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(1020, 765);
    frame.setResizable(false);

    frame.add(board);

    frame.setVisible(true);

    if (JOptionPane.showConfirmDialog(null, "Is this a multiplayer session?", "Networking",
        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

      if (JOptionPane.showConfirmDialog(null, "Are you the host?", "Networking",
          JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
        // Connect to game
        String ip = JOptionPane.showInputDialog("Enter an IP address: ", "localhost");
        client = new TCPClient(InetAddress.getByName(ip), 60183);
      } else {
        // Host Game
        server = new TCPServer(60183);
        server.waitForConnection();
        server.setupStreams();

      }
      Timer timer = new Timer();
      TimerTask task = new TimerTask() {
        @Override
        public void run() {
          try {
            if (server != null && server.isActive()) {
              AI state = AI.deserialize(server.recieveData());
              if (!state.equals(board.ai)) {
                board.ai = state;
                board.loadSquares();
                board.turn = (board.turn == 1) ? 0 : 1;
              }
            }
            if (client != null && client.isActive()) {
              AI state = AI.deserialize(client.recieveData());
              if (!state.equals(board.ai)) {
                board.ai = state;
                board.loadSquares();
                board.turn = (board.turn == 1) ? 0 : 1;
              }
            }
            board.updateUI();
          } catch (Exception e) {
          }
        }
      };
      timer.scheduleAtFixedRate(task, 0, 100);

    }
  }

}
