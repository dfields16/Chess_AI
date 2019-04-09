
public class Client{

  //TEMPORARY DIRECT TO SERVER UNTIL DAWSON TIES IT ALL IN
  // CLIENT SHOULD NOT FETCH LIKE THIS I JUST USE server.xxxxxx
  // SO I CAN DEVELOP THE CLIENT SIDE FUNCTIONALITY WITHOUT A WORKING SERVER
  
  Server server = new Server();

  int side = server.getSide();
  int turn = 0;

  public Client() {}

  public Square[][] getBoard(){
    return server.getBoard();
  }

  public Response sendMove(Square[][] board,Move move) {
    
    Response r = server.makeMove(board,move);

    if( r.valid ) {
      turn = r.turn;
      // temp fix so you can test both sides on one client, remove once server done
      //side = turn;
    }
    
    return r;   
  }

}