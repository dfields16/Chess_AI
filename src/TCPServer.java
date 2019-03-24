import java.io.*;
import java.net.*;

class TCPServer {
	private int port1 = 0;
	private static ObjectOutputStream output;
	private static ObjectInputStream input;
	private ServerSocket serverSocket;
	private Socket connection;

	public TCPServer(int port) throws IOException {
		port1 = port;
		serverSocket = new ServerSocket(port1);
	}

	public String recieveData() throws IOException {
		String temp = "An Error Occurred while recieving data!";
		try {
			temp = (String) input.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return temp;
	}

	public void sendData(String data) throws IOException {
		try {
			output.writeObject(data);
			output.flush();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	public void waitForConnection() throws IOException {
		System.out.println("Waiting for a connection...");
		connection = serverSocket.accept();
		System.out.println("Connected to: " + connection.getInetAddress().getHostName());
		connection.setTcpNoDelay(true);
	}

	public void setupStreams() throws IOException {
		System.out.println("Setting up streams");
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
	}

	public void close() throws IOException {
		output.close();
		connection.close();
		input.close();
	}

	public void flush() throws IOException {
		output.flush();
	}
}