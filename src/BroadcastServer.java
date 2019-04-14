import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class BroadcastServer {
    ServerSocket serverSocket;
    public static long timeLimit = 120*1000;
    private static Server gameServer;
    private static ArrayList<Client> clients = new ArrayList<>();

    public static void main(String args[]) {
        BroadcastServer server = new BroadcastServer();
        gameServer = new Server();
        gameServer.startGame();

        try {
            server.startServer(1200);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("[Listening] " + serverSocket.getInetAddress().toString() + ":" + port);
        while (true) {
            Socket connection = serverSocket.accept();
            connection.setTcpNoDelay(true);

            clients.add(new Client(String.valueOf(clients.size()), connection));

            System.out.println("[Connected] " + connection.getInetAddress());
            ClientHandler cl = new ClientHandler(clients.get(clients.size() - 1));
            Thread clientThread = new Thread(cl);
            clientThread.start();
        }
    }

    public void sendAll(String msg) {
        System.out.println("No of connections:" + clients.size());
        for (Client client : clients) {
            try {
                client.out.writeObject(msg);
                client.out.flush();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Client disconnected: " + client.socket.getInetAddress());
                client.close();
                clients.remove(client);
            }
        }

    }

    public void sendOthers(InetAddress addr, String msg) {
        System.out.println("No of connections:" + clients.size());
        for (Client client : clients) {
            if (client.socket.getInetAddress() == addr)
                continue;
            try {
                client.out.writeObject(msg);
                client.out.flush();
            } catch (Exception e) {
                System.out.println("Client disconnected: " + client.socket.getInetAddress());
                client.close();
                clients.remove(client);
            }
        }
    }

    public void gameEngine(Client client, String[] data) {
        try {
            switch (data[0]) {
            case "OK":
                // IDK
                break;
            case "READY":
                // Start Game
                client.ready();
                boolean startGame = false;
                if (clients.size() > 1) {
                    for (Client c : clients) {
                        if (!c.isReady()) {
                            startGame = false;
                            break;
                        }
                        startGame = true;
                    }
                }
                if (startGame) {
                    client.out.writeObject("BEGIN");
                }
                break;
            default:
                Move move = Move.deserialize(data[0] + " " + data[1]);
                boolean valid = false;
                if (gameServer.board.turn == Integer.parseInt(client.name)) {
                    valid = Util.validMove(gameServer.board.squares, move, gameServer.board.turn);
                }
                if (valid) {
                    System.out.print("[Move][Valid] " + move.serialize() + "\n");

                    Util.movePiece(gameServer.board, move, gameServer.board.turn);
                    gameServer.board.nextTurn();
                    sendOthers(client.socket.getInetAddress(), data[0] + " " + data[1]);

                    client.out.writeObject("OK");
                    // Message message = new Message("board", gameServer.board);
                    // sendAll(message);
                } else {
                    System.out.println("[Move][Invalid] " + move.serialize());
                    client.out.writeObject("ILLEGAL");
                    client.out.writeObject("LOSER");
                    sendOthers(client.socket.getInetAddress(), "WINNER");

                    // client.out.writeObject("ack ILLEGAL");
                    // client.out.writeObject(new Message("ack", "ILLEGAL"));
                }
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void quit(InetAddress addr) throws Exception {
        System.out.println("Server Shutting Down...");
        sendOthers(addr, "QUIT");
        for (int i = 0; i < clients.size(); i++) {
            clients.get(i).close();
        }
        serverSocket.close();
    }

    class ClientHandler implements Runnable {
        Client client;

        public ClientHandler(Client client) {
            this.client = client;

        }

        @Override
        public void run() {
            try {
                client.out.writeObject("WELCOME");
                client.out.writeObject(
                        "INFO " + timeLimit + ((Integer.parseInt(client.name) == 0) ? " White" : " Black"));
                while (true) {
                    String msg = null;
                    msg = (String) client.in.readObject();
                    if (msg != null) {
                        if (msg == "QUIT") {
                            quit(client.socket.getInetAddress());
                            return;
                        }
                        System.out.println("[" + client.name + "] " + msg);
                        gameEngine(client, msg.split(" "));
                    }

                }
            } catch (Exception e1) {
                System.out.println("[Disconnect] " + client.name);
                clients.remove(client);
                client.close();
                return;
            }

        }
    }

    class Client {
        public Socket socket;
        public ObjectOutputStream out;
        public ObjectInputStream in;
        public String name;
        private boolean ready = false;

        public Client(String name, Socket socket) throws IOException {
            this.name = name;
            this.socket = socket;
            this.out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            this.in = new ObjectInputStream(socket.getInputStream());
        }

        public void ready() {
            ready = true;
        }

        public boolean isReady() {
            return ready;
        }

        public void close() {
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}