
public class Board {
    public static final String wPawn = "♙";
    public static final String wRook = "♖";
    public static final String wKnight = "♘";
    public static final String wBishop = "♗";
    public static final String wQueen = "♕";
    public static final String wKing = "♔";
    public static final String bPawn = "♟";
    public static final String bRook = "♜";
    public static final String bKnight = "♞";
    public static final String bBishop = "♝";
    public static final String bQueen = "♛";
    public static final String bKing = "♚";

    String[][] state;
    
    public static final String[][] initState = {{"♖", "♘", "♗", "♕", "♔", "♗", "♘", "♖"},
                                                {"♙", "♙", "♙", "♙", "♙", "♙", "♙", "♙"},
                                                {"", "", "", "", "", "", "", ""},
                                                {"", "", "", "", "", "", "", ""}, 
                                                {"", "", "", "", "", "", "", ""}, 
                                                {"", "", "", "", "", "", "", ""}, 
                                                {"♟", "♟", "♟", "♟", "♟", "♟", "♟", "♟"}, 
                                                {"♜", "♞", "♝", "♛", "♚", "♝", "♞", "♜"}};

    public Board(){
        state = initState;
    }

    public String serialize(){


        return "";
    }

    public void print(){
        System.out.println("╔═══════╤═══════╤═══════╤═══════╤═══════╤═══════╤═══════╤═══════╗");
        for(int x = 0; x < state.length; x++){
            String line = "║";
            String border = "╟───────";
            for(int y = 0; y < state[x].length; y++){
                if(state[x][y] == ""){
                    line += "\t│";
                }else{
                    line += "   " + state[x][y] + "\t│";
                }
                if(y != state[x].length-1)border += "┼───────";
            }
            border += "╢";
            System.out.println(line.substring(0, line.length()-1) + "║");
            if(x != state.length -1)System.out.println(border);
            else System.out.println("╚═══════╧═══════╧═══════╧═══════╧═══════╧═══════╧═══════╧═══════╝");

        }
    }

}