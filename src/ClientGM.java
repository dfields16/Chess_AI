import java.io.IOException;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

public class ClientGM {
  private TCPClient client;
  private TimerTask recieveDataTask, gameTimer;
  private Timer clientTimer, clock;
  private Game game;
  public int team = -1;
  private String m1, m2;
  public boolean gameActive = false;
  public boolean isSinglePlayer = false;

  public AI cpu = null;

  public ClientGM(Game gm, InetAddress ip, int port, boolean isAI) {
    if (isAI) {
      cpu = new AI(1);
    }
    game = gm;
    m1 = m2 = "";

    try {
      client = new TCPClient(ip, port);
      clientTimer = new Timer();
      clock = new Timer();
      recieveDataTask = new TimerTask() {
        public void run() {
          // RecieveData
          if (!client.isActive())
            return;
          String msg = (String) client.recieveData();
          System.out.println("[Server] " + msg);

          interpretData(msg.split(" "));
        }
      };
      gameTimer = new TimerTask() {
        public void run() {
          if (gameActive)
            game.board.timer++;
          game.updateUI();
        }
      };
      clientTimer.scheduleAtFixedRate(recieveDataTask, 0, 10);
      clock.scheduleAtFixedRate(gameTimer, 0, 1000);
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public void interpretData(String[] data) {
    switch (data[0]) {
    case "OK":
      if (m1 != m2 && !isSinglePlayer) {
        Util.movePiece(game.board, Move.deserialize(m1));
        game.board.nextTurn();
        m2 = m1;
        game.board.timer = 0;
        game.updateUI();
      }
      break;
    case "INFO":
      if (team == -1) {
        game.board.turnlen = Integer.parseInt(data[1]);
        team = (data[2].equals("White")) ? 0 : 1;
        client.sendData("READY");
      }
      break;
    case "ILLEGAL":
      break;
    case "WINNER":
      gameActive = false;
      GameMsg gWMsg = new GameMsg("Winner");
      gWMsg.setBounds(game.getWidth() / 2 - 145, game.getHeight() / 2 - 50, 300, 100);
      game.add(gWMsg);
      game.updateUI();
      break;
    case "LOSER":
      gameActive = false;
      GameMsg gLMsg = new GameMsg("Loser");
      gLMsg.setBounds(game.getWidth() / 2 - 145, game.getHeight() / 2 - 50, 300, 100);
      game.add(gLMsg);
      game.updateUI();
      break;
    case "TIE":
      gameActive = false;
      GameMsg gTMsg = new GameMsg("Tie");
      gTMsg.setBounds(game.getWidth() / 2 - 145, game.getHeight() / 2 - 50, 300, 100);
      game.add(gTMsg);
      game.updateUI();
      break;
    case "TIME":

      break;
    case "WELCOME":
      break;
    case "BEGIN":
      game.board.timer = 0;
      if (cpu != null && game.board.turn == team && !gameActive) {
        sendMove(aiTurn());
      }
      gameActive = true;
      break;
    case "ERROR":
    case "QUIT":
      GameMsg qGMsg = new GameMsg("Opponent Left");
      qGMsg.setBounds(game.getWidth() / 2 - 145, game.getHeight() / 2 - 50, 300, 100);
      game.add(qGMsg);
      game.updateUI();
      gameActive = false;
      break;
    default:
      game.board.timer = 0;
      System.out.println(data[0] + " " + data[1]);
      Util.movePiece(game.board, Move.deserialize(data[0] + " " + data[1]));
      game.board.nextTurn();
      if (cpu != null && game.board.turn == team && gameActive) {
        sendMove(aiTurn());
      }
      break;
    }
  }

  public void sendMove(Move move) {
    m1 = move.serialize();
    client.sendData(m1);
  }

  public Move aiTurn() {
    Move move = cpu.getMove(game.board);
    return move;
  }
}