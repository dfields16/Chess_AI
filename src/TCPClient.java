import java.io.*;
import java.net.*;

public class TCPClient {
  public InetAddress IPAddress;
  public int port1 = 0;
  private ObjectOutputStream output;
  private ObjectInputStream input;
  private Socket connection;
  private boolean status = false;

  public TCPClient(InetAddress ip, int port) throws IOException {
    IPAddress = ip;
    port1 = port;
    connection = new Socket(IPAddress, port1);// 60183 works -> use for screenShare
    connection.setTcpNoDelay(true);
    output = new ObjectOutputStream(connection.getOutputStream());
    output.flush();
    input = new ObjectInputStream(connection.getInputStream());
    status = true;
  }

  public void sendData(String data) {
    try{
      output.writeObject(data);
    output.flush();
    }catch (IOException ioeException){
      ioeException.printStackTrace();
    }
  }

  public String recieveData() {
    String temp = "An Error Occurred while recieving data!";
    try {
      temp = (String) input.readObject();
    } catch (Exception e) {
      System.out.println("Connection Broken");
      status = false;
      close();
    }
    return temp;
  }

  public void close() {
    try {
      output.close();
      input.close();
      connection.close();
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }
  }

  public void flush() {
    try {
      output.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public boolean isActive() {
    return status;
  }

}
