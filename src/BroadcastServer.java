import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BroadcastServer {
    ServerSocket serverSocket;
    public static long timeLimit = 20;
    private static Map<Integer, Server> games = new HashMap<>();
    private static ArrayList<Client> clients = new ArrayList<>();

    public static void main(String args[]) {
        BroadcastServer server = new BroadcastServer();

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
            Client c = new Client(connection);

            for (Map.Entry<Integer, Server> entry : games.entrySet()) {
                if (c.game != -1)
                    break;
                if (entry.getValue().c0 == null) {
                    c.name = "0";
                    c.game = entry.getKey();
                    entry.getValue().c0 = c;
                    break;
                } else if (entry.getValue().c1 == null) {
                    c.name = "1";
                    c.game = entry.getKey();
                    entry.getValue().c1 = c;
                    break;
                }
                if (entry.getValue().isFull()) {
                    entry.getValue().startGame();
                }
            }
            if (c.game == -1) {
                int gameID = (int) (Math.random() * 1000000);
                Server game = new Server();
                game.board.turnlen = (int) timeLimit;
                games.put(gameID, game);
                c.name = "0";
                c.game = gameID;
                game.c0 = c;
            }

            clients.add(c);
            System.out.println("[Connected] IP:" + connection.getInetAddress() + " Game_ID:" + c.game);
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
                    games.get(client.game).c0.out.writeObject("BEGIN");
                    games.get(client.game).c1.out.writeObject("BEGIN");
                    games.get(client.game).board.timer = 0;
                }
                break;
            default:
                Server game = games.get(client.game);
                Move move = Move.deserialize(data[0] + " " + data[1]);
                boolean valid = false;
                if (game.board.turn == Integer.parseInt(client.name)) {
                    valid = Util.validMove(game.board.squares, move, game.board.turn);
                }
                if (valid) {
                    System.out.print("[Move][Valid] " + move.serialize() + "\n");
                    game.board.timer = 0;
                    Util.movePiece(game.board, move, game.board.turn);
                    game.board.nextTurn();
                    if (client.name.equals("0")) {
                        game.c1.out.writeObject(data[0] + " " + data[1]);
                    } else {
                        game.c0.out.writeObject(data[0] + " " + data[1]);
                    }

                    client.out.writeObject("OK");
                } else {
                    System.out.println("[Move][Invalid] " + move.serialize());
                    client.out.writeObject("ILLEGAL");
                    client.out.writeObject("LOSER");
                    if (client.name.equals("0")) {
                        game.c1.out.writeObject("WINNER");
                    } else {
                        game.c0.out.writeObject("WINNER");
                    }
                }
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void quit(InetAddress addr, boolean shutdownServer) {
        sendOthers(addr, "QUIT");
        for (int i = 0; i < clients.size(); i++) {
            clients.get(i).close();
        }
        if (shutdownServer)
            try {
                serverSocket.close();
                System.out.println("Server Shutting Down...");
            } catch (IOException e) {
                e.printStackTrace();
            }
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
                            quit(client.socket.getInetAddress(), false);
                            return;
                        }
                        System.out.println("[" + client.name + "] " + msg);
                        gameEngine(client, msg.split(" "));
                    }
                }
            } catch (IOException | ClassNotFoundException e1) {
                System.out.println("[Disconnect] " + client.name);
                try {
                    if (games.get(client.game).c0 == client) {
                        games.get(client.game).c1.out.writeObject("QUIT");
                    } else {
                        games.get(client.game).c0.out.writeObject("QUIT");
                    }
                    games.remove(client.game);
                    clients.remove(client);
                    client.close();
                } catch (Exception e) {
                    // e.printStackTrace();
                }
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
        public int game = -1;

        public Client(String name, Socket socket) throws IOException {
            this.name = name;
            this.socket = socket;
            this.out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            this.in = new ObjectInputStream(socket.getInputStream());
        }

        public Client(Socket socket) throws IOException {
            this.socket = socket;
            this.out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            this.in = new ObjectInputStream(socket.getInputStream());
            name = "-1";
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