import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class AI extends Game{

    public Square[][] state;
    //public Game game;
    //public Game AIGame;

    public AI() {
    	
    }

    
    public void setState(Square[][] squares) {
        // clear();
        //state = squares;
    }

    public ArrayList<Move> potentialMoves(Square[][] presentState, int side ) {
		
    	ArrayList<Move> foundMoves = new ArrayList<Move>();
    	
    	for(int y = 0; y < 8; y++) {
    		for(int x = 0; x < 8; x++) {
       			if(presentState[y][x].piece != null && presentState[y][x].piece.side == side) {
       				for(int y2 = 0; y2 < 8; y2++) {
       					for(int x2 = 0; x2 < 8; x2++) {
       						if(board[y][x].piece != null && board[y2][x2].piece != null && board[y][x].piece.side == side) {
        						Move testMove = new Move(x, y, x2, y2);
        						if(Util.validMove(state, testMove, turn)) {  
                   					foundMoves.add(testMove);
                   				}
               				}
        				}
       				}	
       			}
    		}
    	}
    	
    	return foundMoves;
    }
    
    public int miniMax(boolean Max, int depth, Square[][] state) {
    	int tempSide = (Max == true) ? 1 : 0;
    	ArrayList<Move> allMoves = potentialMoves(state, tempSide);
    	
    	if(depth == 0) {
    		return heuristic(state);
//    		return bestMove(state, true);
    	}
    	if(Max) {
    		int maxVal = 99999;
    		potentialMoves(state, 1);
    		for(int i = 0; i < allMoves.size(); i++) {
    			
    			state = Util.movePiece(state, allMoves.get(i));
    			//state = Util.board;
        		maxVal = Math.max(maxVal,  miniMax(!Max, depth-1, state));
    			Util.undoMove(state);
    		}
    		return maxVal;
    		
    	}else {
    		int minVal = -99999;
    		potentialMoves(state,0);
    		
    		for(int i = 0; i < allMoves.size(); i++) {
    			state = Util.movePiece(state, allMoves.get(i));
    			//state = game.board;
    			minVal = Math.min(minVal,  miniMax(Max, depth-1, state));
    			Util.undoMove(state);
    		}
    		return minVal;
    		
    	}
    }
    
    
    public Move bestMove(ArrayList<Move> moves, boolean bestWorst){
    	//bestWorst : true is best, false is worst
    	ArrayList<Move> findMoves = new ArrayList<Move>();
    	for(int i = 0; i < moves.size(); i++) {
    		if(Util.checkCapture(board, moves.get(i))) {
    			findMoves.add(moves.get(i));
    		}
    	}
    	int best = -9999;
    	int worst = 9999;
    	Move returnMove = new Move();
    	for(int j = 0; j < findMoves.size(); j++) {
    		if(!bestWorst && board[findMoves.get(j).y1()][findMoves.get(j).x1()].piece != null) {
    			if(board[findMoves.get(j).y1()][findMoves.get(j).x1()].piece.type.value > best) {
    				// return highest value
//    				best = board[findMoves.get(j).y1()][findMoves.get(j).x1()].piece.type.value;
    				best = heuristic(board);
    				returnMove = findMoves.get(j);
    			}
    			else if(board[findMoves.get(j).y1()][findMoves.get(j).x1()].piece.type.value < worst){
    				//return lowest value
    				worst = -1* heuristic(board);
    				returnMove = findMoves.get(j);
    			}
    		}
    	}
    	if(best == -9999 && bestWorst) { //assign randomly if a capture can't happen

    		Random chooseMove = new Random();
    		int i = chooseMove.nextInt(findMoves.size());
    		returnMove = findMoves.get(i);
    	}
    	else if(worst == 9999 && !bestWorst) {
    		Random chooseMove = new Random();
    		int i = chooseMove.nextInt(findMoves.size());
    		returnMove = findMoves.get(i);
    	}
    	
    	return returnMove;
    }
    
    private int heuristic(Square[][] board) {
    	int val = 0;
    	for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[y].length; x++) {
                if(board[y][x].piece.side == turn) {
                	val += board[y][x].piece.type.value;
                }
                else
                {
                	val -= board[y][x].piece.type.value;
                }
            }
        }
		return 0;
	}


	public void clear() {
        for (Square[] sqrs : state) {
            Arrays.fill(sqrs, null);
        }
    }

    public void print() {
        System.out.println("=================================================================");
        for (int x = 0; x < state.length; x++) {
            String line = "|";
            String border = "|=======";
            for (int y = 0; y < state[x].length; y++) {
                if (state[x][y].piece.type == ChessPiece.EMPTY) {
                    line += "\t|";
                } else {
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
                if (p == null)
                    p = new Piece(ChessPiece.EMPTY, -1);
                dat += p.type.toString() + ":" + ((p.side == 0) ? "WHITE" : "BLACK") + ":" + toPoint(x, y) + ":"
                        + p.moved + ",";
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
        for (String s : str.split(",")) {
            String[] dat = s.split(":");
            Piece p = new Piece(ChessPiece.valueOf(dat[0]), ((dat[1].equals("WHITE")) ? 0 : 1));
            // p.hasMoved = dat[3].from;
            int x = (int) dat[2].charAt(1) - '0';
            int y = (int) dat[2].charAt(0) - 'A';
            if (p.type == ChessPiece.EMPTY)
                p = null;
            Square sqr = new Square(x, y);
            sqr.piece = p;
            dState.state[y][x] = sqr;
        }
        return dState;
    }

    public boolean equals(AI s) {
        return this.serialize().equals(s.serialize());
    }
}