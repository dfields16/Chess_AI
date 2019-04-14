import java.io.IOException;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

public class Client {
  private TCPClient client;
  private TimerTask recieveDataTask;
  private Timer timer;
  private Game game;
  public int team;
  public long timeLimit = 120000;
  private String m1, m2;
  public Client(Game gm, InetAddress ip, int port) {
    game = gm;
    m1 = m2 = "";

    try {
      client = new TCPClient(ip, port);
      timer = new Timer();
      recieveDataTask = new TimerTask() {
        public void run() {
          // RecieveData
          if (!client.isActive())
            return;
          String msg = (String) client.recieveData();
          System.out.println("[Server] " + msg);

          interpretData(msg.split(" "));
          game.updateUI();
        }
      };
      timer.scheduleAtFixedRate(recieveDataTask, 0, 10);
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public void interpretData(String[] data) {
    switch (data[0]) {
    case "OK":
      if(m1 != m2){
        System.out.println(m1);
        Util.movePiece(game.board, Move.deserialize(m1), game.board.turn);
        game.board.nextTurn();
        m2 = m1;
      }
      break;
    case "INFO":
      timeLimit = Long.parseLong(data[1]);
      team = (data[2].equals("White")) ? 0 : 1;
      client.sendData("READY");
      break;
    case "ILLEGAL":

      break;
    case "WINNER":

      break;
    case "LOSER":

      break;
    case "WELCOME":
      break;
    default:
      System.out.println(data[0]);
      Util.movePiece(game.board, Move.deserialize(data[0] + " " + data[1]), game.board.turn);
      game.board.nextTurn();
      break;
    }
  }

  public void sendMove(Move move) {
    m1 = move.serialize();
    client.sendData(m1);
  }

}