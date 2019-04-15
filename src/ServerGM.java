import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ServerGM {
  BroadcastServer.Client c0, c1;
  Board board;
  AI cpu = new AI(2);
  AI human = new AI(1);

  boolean ai = false;
  // WHO IS ON WHICH SIDE 0:ai 1:play 1 2: player 2
  // WILL NEED TO TWEAK BASED ON UNIQUE CLIENT ID AI WILL STILL BE FIXED
  int wside = 1;
  int bside = 0;

  ArrayList<Move> history;

  public ServerGM() {

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
              if (c0 != null) {
                c0.out.writeObject("TIME");
                c0.out.writeObject("LOSER");
              }
              if (c1 != null) {
                c1.out.writeObject("TIME");
                c1.out.writeObject("WINNER");
              }
            } else {
              if (c1 != null) {
                c1.out.writeObject("TIME");
                c1.out.writeObject("LOSER");
              }
              if (c0 != null) {
                c0.out.writeObject("TIME");
                c0.out.writeObject("WINNER");
              }
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

  public void isGameOver(Move move) throws IOException {
    Board bored = new Board(board);
    bored.turn = (bored.turn == 0) ? 1 : 0;
    bored.timer = 1;
    Move hint = human.getMove(bored);

    if (hint == null) {
      // GAME OVER
      Square king = Util.getKing(bored, bored.turn);
      if (king.piece.checked == 1) {
        if (bored.turn == 0) {
          c0.out.writeObject("WINNER");
          c1.out.writeObject("LOSER");
        } else {
          c1.out.writeObject("WINNER");
          c0.out.writeObject("LOSER");
        }
      } else {
        c0.out.writeObject("TIE");
        c1.out.writeObject("TIE");
      }

    }
  }

  public Move aiTurn() {
    Move move = cpu.getMove(board);
    if (move != null)
      Util.movePiece(board, move);
    else {
      Board bored = new Board(board);
      bored.turn = (bored.turn == 0) ? 1 : 0;
      bored.timer = 1;
      Move hint = human.getMove(bored);
      try{
      if (hint == null) {
        // GAME OVER
        Square king = Util.getKing(bored, bored.turn);
        if (king.piece.checked == 1) {
          if (bored.turn == 0) {
            c0.out.writeObject("WINNER");
            c1.out.writeObject("LOSER");
          } else {
            c1.out.writeObject("WINNER");
            c0.out.writeObject("LOSER");
          }
        } else {
          c0.out.writeObject("TIE");
          c1.out.writeObject("TIE");
        }

      }
    }catch(IOException e){}
    }
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