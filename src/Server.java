import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Server {
  BroadcastServer.Client c0, c1;
  Board board;
  AI cpu = new AI(2);

  boolean ai = true;
  // WHO IS ON WHICH SIDE 0:ai 1:play 1 2: player 2
  // WILL NEED TO TWEAK BASED ON UNIQUE CLIENT ID AI WILL STILL BE FIXED
  int wside = 1;
  int bside = 0;

  ArrayList<Move> history;

  public Server() {

    startGame();

    // SET UP TIMER
    Timer clock = new Timer();
    TimerTask task = new TimerTask() {
      @Override
      public void run() {
        board.timer++;
        try {
          if (board.timer > board.turnlen) {
            if (board.turn == 0) {
              c0.out.writeObject("TIME");
              c0.out.writeObject("LOSER");
              c1.out.writeObject("TIME");
              c1.out.writeObject("WINNER");
            } else {
              c1.out.writeObject("TIME");
              c1.out.writeObject("LOSER");
              c0.out.writeObject("TIME");
              c0.out.writeObject("WINNER");
            }
            clock.cancel();
            clock.purge();
          }
        } catch (IOException e) {
        }
      }
    };
    clock.scheduleAtFixedRate(task, 1000, 1000);

  }

  public Board makeMove(Move move) {

    if (Util.movePiece(board, move, board.turn)) {

      board.turn = (board.turn == 0) ? 1 : 0;
      board.timer = 1;

      if ((board.turn == 0 && wside == 0) || (board.turn == 1 && bside == 0)) {
        aiTurn();
      }

      board.timer = 1;
      board.valid = true;
    }

    return board;
  }

  public Move aiTurn() {
    Move move = cpu.getMove(board, board.turn);
    if (move != null)
      Util.movePiece(board, move, board.turn);
    board.turn = (board.turn == 0) ? 1 : 0;
    return move;
  }

  public Board getBoard() {
    return board;
  }

  public void startGame() {
    board = new Board(8, 8);
    Util.resetPieces(board);
  }

  public boolean isFull() {
    return (c0 != null && c1 != null);
  }

  public boolean addClient(BroadcastServer.Client c) {
    if (c0 != null) {
      c0 = c;
      return true;
    } else if (c1 != null) {
      c1 = c;
      return true;
    }
    return false;
  }

}