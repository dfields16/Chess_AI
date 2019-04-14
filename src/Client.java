
public class Client{

  //TEMPORARY DIRECT TO SERVER UNTIL DAWSON TIES IT ALL IN
  // CLIENT SHOULD NOT FETCH LIKE THIS I JUST USE server.xxxxxx
  // SO I CAN DEVELOP THE CLIENT SIDE FUNCTIONALITY WITHOUT A WORKING SERVER

  Server server = new Server();

  int side = server.getSide();

  public Client() {}

  public Board getBoard(){
    return server.getBoard();
  }

  public Board sendMove(Board board,Move move) {
    board = server.makeMove(move);
    side = board.turn;
    return board;
  }

}