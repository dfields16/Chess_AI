import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Server {

  Square[][] board = new Square[8][8];
  AI cpu      = new AI();

  int   turn  = 0;
  int turnlen = 5;
  int timer   = 0;
  
  boolean ai  = true;
  
  // WHO IS ON WHICH SIDE 0:ai 1:play 1 2: player 2
  // WILL NEED TO TWEAK BASED ON UNIQUE CLIENT ID AI WILL STILL BE FIXED  
  int wside   = 1;
  int bside   = 0;

  ArrayList<Move> history;

  public Server(){

    startGame();

    // SET UP TIMER    
    Timer clock    = new Timer();
    TimerTask task = new TimerTask() {
      @Override
      public void run() {
        timer++;
        if (timer > turnlen) {    
          // is AI playing or real person
          turn = (turn == 0) ? 1 : 0; 
          if((turn==0 && wside==0) || (turn==1 && bside==0)) {
            aiTurn();
          }
          timer = 1;
        }
      }
    };
    clock.scheduleAtFixedRate(task, 1000, 1000);

  }

  public Response makeMove(Square[][] board,Move click) {
    
    Response response = new Response();

    if( Util.movePiece(board, click, turn) ) {

      turn = (turn == 0) ? 1 : 0;
      timer = 1;
      
      // IF PLAYING AI LET IT MOVE THEN SET RESPONSE OTHERWISE PUSH THE PLAYERS
      // MOVE SO THE OTHER CLIENT WILL KNOW WHAT IT WAS
      if((turn==0 && wside==0) || (turn==1 && bside==0)){        
        response.move  = aiTurn();
      }else {
        response.move  = click;
      }

      timer = 1;

      response.valid = true;
    }else {      
      response.valid = false;
    }
    
    response.turn  = turn;
    response.board = board;
    
    return response;
  }
  
  public Move aiTurn() {
    Move move = cpu.getMove(board,2,turn);
    if(move != null) Util.movePiece(board, move, turn);
    turn = (turn == 0) ? 1 : 0;
    return move;
  }

  public int getSide() {
    return 0;
  }
  public Square[][] getBoard(){
    return board;
  }
  
  public void startGame(){
    // RESET HISTORY
    history = new ArrayList<Move>();
    // SET UP THE BOARD
    Util.loadSquares(board);
    Util.resetPieces(board);
  }

}