import java.io.*;
import java.net.*;

public class TCPClient {
	public InetAddress IPAddress;
	public int port1 = 0;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Socket connection;

	public TCPClient(InetAddress ip, int port) throws IOException {
		IPAddress = ip;
		port1 = port;
		connection = new Socket(IPAddress, port1);// 60183 works -> use for screenShare
		connection.setTcpNoDelay(true);
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());

	}

	public void sendData(String data) throws IOException {
		output.writeObject(data);
		output.flush();

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

	public void close() throws IOException {
		output.close();
		input.close();
		connection.close();
	}

	public void flush() throws IOException {
		output.flush();
	}
}
