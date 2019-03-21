
public class Board {
    public static String wPawn = "♙";
    public static String wRook = "♖";
    public static String wKnight = "♘";
    public static String wBishop = "♗";
    public static String wQueen = "♕";
    public static String wKing = "♔";
    public static String bPawn = "♟";
    public static String bRook = "♜";
    public static String bKnight = "♞";
    public static String bBishop = "♝";
    public static String bQueen = "♛";
    public static String bKing = "♚";

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
        System.out.println("┌───────┬───────┬───────┬───────┬───────┬───────┬───────┬───────┐");
        for(int x = 0; x < state.length; x++){
            String line = "│";
            String border = "├───────";
            for(int y = 0; y < state[x].length; y++){
                if(state[x][y] == ""){
                    line += "\t│";
                }else{
                    line += "   " + state[x][y] + "\t│";
                }
                if(y != state[x].length-1)border += "┼───────";
            }
            border += "┤";
            System.out.println(line);
            if(x != state.length -1)System.out.println(border);
            else System.out.println("└───────┴───────┴───────┴───────┴───────┴───────┴───────┴───────┘");

        }
    }

}