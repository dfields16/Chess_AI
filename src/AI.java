import java.util.Arrays;

public class AI {
    
    Square[][] state = new Square[8][8];

    public AI() {}
    
    public AI(Square[][] squares) {
      setState(squares);
    }

    public void setState(Square[][] squares) {
        clear();
        state = squares;
    }

    public void clear() {
      Arrays.fill(state,null);
    }

    public void print() {
        //System.out.println("=================================================================");
        for (int x = 0; x < state.length; x++) {
            String line = "|";
            String border = "|=======";
            for (int y = 0; y < state[x].length; y++) {
                if (state[x][y].piece == null) {
                  line += "\t|";
                }else {
                  line += state[x][y].piece.type.toString().substring(0, 4) + "\t|";
                }
                if (y != state[x].length - 1)
                  border += "|=======";
            }
            border += "|";
            System.out.println(line.substring(0, line.length() - 1) + "|");
            if (x != state.length - 1)
                System.out.println(border);
            else
                System.out.println("=================================================================");

        }
    }

    public String serialize() {
        String dat = "";
        for (int y = 0; y < state.length; y++) {
            for (int x = 0; x < state.length; x++) {
                Piece p = state[y][x].piece;
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

    public static AI deserialize(String str) {
        AI dState = new AI();
        dState.clear();
        for (String s : str.split(",")) {
            String[] dat = s.split(":");
            Piece p = new Piece(ChessPiece.valueOf(dat[0]), ((dat[1].equals("WHITE")) ? 0 : 1));
            //p.hasMoved = dat[3].from;
            int x = (int)dat[2].charAt(1) - '0';
            int y = (int)dat[2].charAt(0) - 'A';

            Square s = new Square(String.valueOf(x) + String.valueOf(y), x, y);
            s.piece = p;
            dState.state[y][x] = s;
        }
        return dState;
    }

    public boolean equals(AI s){
        return this.serialize().equals(s.serialize());
    }
}