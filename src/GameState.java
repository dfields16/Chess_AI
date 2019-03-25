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
                    p = new Piece(t, Color.BLACK);
                } else if (y > 5) {
                    ChessPiece t = bOrder[x];
                    if (y == 6)
                        t = ChessPiece.PAWN;
                    p = new Piece(t, Color.WHITE);
                } else {
                    p = new Piece(ChessPiece.EMPTY, Color.RED);
                }
                state[y][x] = p;
            }
        }
        return state;
    }

    public void update(Piece[] pieces) {
        clear();
        for (int i = 0; i < pieces.length; i++) {
            if (pieces[i].type != ChessPiece.EMPTY) {
                int loc = pieces[i].square;
                int y = (int) Math.floor(loc / 8);
                int x = loc % 8;
                state[y][x] = pieces[i];
            }
        }
    }

    public void clear() {
        for (int y = 0; y < state.length; y++) {
            for (int x = 0; x < state[y].length; x++) {
                state[y][x] = new Piece(ChessPiece.EMPTY, Color.red);
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
}