import java.io.*;
import java.net.*;

class TCPServer {
  private int port1 = 0;
  private static ObjectOutputStream output;
  private static ObjectInputStream input;
  private ServerSocket serverSocket;
  private Socket connection;
  private boolean status = false;

  public TCPServer(int port) throws IOException {
    port1 = port;
    serverSocket = new ServerSocket(port1);
  }

  public String recieveData() {
    String temp = "An Error Occurred while recieving data!";
    try {
      temp = (String) input.readObject();
    } catch (Exception e) {
      System.out.println("Connection Broken");
      close();
    }
    return temp;
  }

  public void sendData(String data) {
    try {
      output.writeObject(data);
      output.flush();
    } catch (IOException ioException) {
      System.out.println("Connection Broken");
      ioException.printStackTrace();
    }
  }

  public void waitForConnection() {
    System.out.println("Waiting for a connection...");
    try {
      connection = serverSocket.accept();
      System.out.println("Connected to: " + connection.getInetAddress().getHostName());
      connection.setTcpNoDelay(true);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void setupStreams() {
    try {
      System.out.println("Setting up streams");
      output = new ObjectOutputStream(connection.getOutputStream());
      output.flush();
      input = new ObjectInputStream(connection.getInputStream());
      status = true;
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public void close() {
    try {
      output.close();
      connection.close();
      input.close();
      status = false;
    } catch (IOException e) {
      e.printStackTrace();
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