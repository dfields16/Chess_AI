import java.awt.Color;

public class GameState {
    public Piece[][] state;
    private static ChessPiece[] tOrder = { ChessPiece.ROOK, ChessPiece.KNIGHT, ChessPiece.BISCHOP, ChessPiece.QUEEN,
            ChessPiece.KING, ChessPiece.BISCHOP, ChessPiece.KNIGHT, ChessPiece.ROOK };
    private static ChessPiece[] bOrder = { ChessPiece.ROOK, ChessPiece.KNIGHT, ChessPiece.BISCHOP, ChessPiece.KING,
            ChessPiece.QUEEN, ChessPiece.BISCHOP, ChessPiece.KNIGHT, ChessPiece.ROOK };

    public GameState() {
        state = getInitState();
    }

    public Piece[] toGUI() {
        Piece[] pieces = new Piece[64];
        for (int y = 0; y < state.length; y++) {
            for (int x = 0; x < state[y].length; x++) {
                pieces[y * state[y].length + x] = state[y][x];
            }
        }
        return pieces;
    }

    public static Piece[][] getInitState() {
        Piece[][] state = new Piece[8][8];
        for (int y = 0; y < state.length; y++) {
            for (int x = 0; x < state[y].length; x++) {
                Piece p;
                if (y < 2) {
                    ChessPiece t = tOrder[x];
                    if (y == 1)
                        t = ChessPiece.PAWN;
                    p = new Piece(t, 1);
                } else if (y > 5) {
                    ChessPiece t = bOrder[x];
                    if (y == 6)
                        t = ChessPiece.PAWN;
                    p = new Piece(t, 0);
                } else {
                    //p = new Piece(ChessPiece.EMPTY, -1);
                    p = null;
                }
                state[y][x] = p;
            }
        }
        return state;
    }

    public void update(Square[][] squares) {
        clear();
        for (int y = 0; y < squares.length; y++) {
            for (int x = 0; x < squares.length; x++) {
                state[y][x] = squares[y][x].piece;
            }
        }
    }

    public void clear() {
        for (int y = 0; y < state.length; y++) {
            for (int x = 0; x < state[y].length; x++) {
                state[y][x] = null;
            }
        }
    }

    public void print() {
        System.out.println("╔═══════╤═══════╤═══════╤═══════╤═══════╤═══════╤═══════╤═══════╗");
        for (int x = 0; x < state.length; x++) {
            String line = "║";
            String border = "╟───────";
            for (int y = 0; y < state[x].length; y++) {
                if (state[x][y].type == ChessPiece.EMPTY) {
                    line += "\t│";
                } else {
                    line += state[x][y].type.toString().substring(0, 4) + "\t│";
                }
                if (y != state[x].length - 1)
                    border += "┼───────";
            }
            border += "╢";
            System.out.println(line.substring(0, line.length() - 1) + "║");
            if (x != state.length - 1)
                System.out.println(border);
            else
                System.out.println("╚═══════╧═══════╧═══════╧═══════╧═══════╧═══════╧═══════╧═══════╝");

        }
    }

    public String serialize() {
        String dat = "";
        for (int y = 0; y < state.length; y++) {
            for (int x = 0; x < state.length; x++) {
                Piece p = state[y][x];
                if(p == null)p = new Piece(ChessPiece.EMPTY, -1);
                dat += p.type.toString() + ":" + ((p.side == 0) ? "WHITE" : "BLACK") + ":" + toPoint(x, y)
                        + ":" + p.moved + ",";
            }
        }
        return dat.substring(0, dat.length() - 2);
    }

    private String toPoint(int x, int y) {
        String tmp = "";
        tmp += (char) ('A' + y);
        tmp += x;
        return tmp;
    }

    public static GameState deserialize(String str) {
        GameState dState = new GameState();
        dState.clear();
        for (String s : str.split(",")) {
            String[] dat = s.split(":");
            Piece p = new Piece(ChessPiece.valueOf(dat[0]), ((dat[1].equals("WHITE")) ? 0 : 1));
            //p.hasMoved = dat[3].from;
            int x = (int)dat[2].charAt(1) - '0';
            int y = (int)dat[2].charAt(0) - 'A';
            if(p.type == ChessPiece.EMPTY)p = null;
            dState.state[y][x] = p;
        }
        return dState;
    }

    public boolean equals(GameState s){
        return this.serialize().equals(s.serialize());
    }
}