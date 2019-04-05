import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class AI {

    // public Square[][] state;
    private Game game;

    public AI() {
    }

    public AI(Game g) {
        game = g;
    }

    public Pair maxFun(Square[][] board, int depth, int maxDepth) {
        Util.print(board);
        Pair p = new Pair(board, 0);
        if (depth >= maxDepth) {
            p.second = heuristic(board);
        } else {
            ArrayList<Move> potentialM = potentialMoves(board, 1);
            int topScore = -1000000;
            int topBoardNo = 0;
            for (int i = 0; i < potentialM.size(); i++) {
                Square[][] curr = Util.movePiece(board, potentialM.get(i));

                Pair pair = minFun(curr, depth + 1, maxDepth);
                if (pair.second > topScore) {
                    topBoardNo = i;
                    topScore = pair.second;
                    p = pair;
                }
            }
        }

        return p;
    }

    public Pair minFun(Square[][] board, int depth, int maxDepth) {
        Util.print(board);
        Pair p = new Pair(board, 0);
        if (depth >= maxDepth) {
            p.second = heuristic(board);
        } else {
            ArrayList<Move> potentialM = potentialMoves(board, 0);
            int lowScore = 1000000;
            int topBoardNo = 0;
            for (int i = 0; i < potentialM.size(); i++) {
                Square[][] curr = Util.movePiece(board, potentialM.get(i));
                // if (Util.inCheck(curr, 0)) continue;
                Pair pair = maxFun(curr, depth + 1, maxDepth);
                if (pair.second < lowScore) {
                    topBoardNo = i;
                    lowScore = pair.second;
                    p = pair;
                }
            }

        }

        return p;
    }

    public Square[][] MiniMax(Square[][] squares, int turn, int currDepth, int maxDepth) {

        return null;
    }

    public void setState(Square[][] squares) {
        // clear();
        // state = squares;
    }

    public ArrayList<Move> potentialMoves(Square[][] presentState, int side) {
        ArrayList<Move> foundMoves = new ArrayList<Move>();

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (presentState[y][x].piece != null && presentState[y][x].piece.side == side) {
                    for (int y2 = 0; y2 < 8; y2++) {
                        for (int x2 = 0; x2 < 8; x2++) {
                            if (presentState[y2][x2].piece != null && presentState[y][x].piece.side == side) {
                                Move testMove = new Move(new Point(x, y), new Point(x2, y2));
                                if (Util.validMove(presentState, testMove, side)) {
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
        // int tempSide = (Max == true) ? 1 : 0;
        ArrayList<Move> allMoves;

        if (depth == 0) {
            return heuristic(state);
            // return bestMove(state, true);
        } else if (Max) {
            int maxVal = 99999;
            allMoves = potentialMoves(state, 1);

            for (int i = 0; i < allMoves.size(); i++) {
                state = Util.movePiece(state, allMoves.get(i));
                maxVal = Math.max(maxVal, miniMax(!Max, depth - 1, state));
                Util.undoMove(state);
            }
            return maxVal;
        } else {
            int minVal = -99999;
            allMoves = potentialMoves(state, 0);

            for (int i = 0; i < allMoves.size(); i++) {
                state = Util.movePiece(state, allMoves.get(i));
                minVal = Math.min(minVal, miniMax(Max, depth - 1, state));
                Util.undoMove(state);
            }
            return minVal;
        }
    }

    public Move bestMove(ArrayList<Move> moves, boolean bestWorst) {
        // bestWorst : true is best, false is worst
        ArrayList<Move> findMoves = new ArrayList<Move>();
        for (int i = 0; i < moves.size(); i++) {
            if (Util.checkCapture(game.board, moves.get(i))) {
                findMoves.add(moves.get(i));
            }
        }
        int best = -9999;
        int worst = 9999;
        Move returnMove = new Move();
        for (int j = 0; j < findMoves.size(); j++) {
            if (!bestWorst && game.board[findMoves.get(j).y1()][findMoves.get(j).x1()].piece != null) {
                if (game.board[findMoves.get(j).y1()][findMoves.get(j).x1()].piece.type.value > best) {
                    // return highest value
                    // best = board[findMoves.get(j).y1()][findMoves.get(j).x1()].piece.type.value;
                    best = heuristic(game.board);
                    returnMove = findMoves.get(j);
                } else if (game.board[findMoves.get(j).y1()][findMoves.get(j).x1()].piece.type.value < worst) {
                    // return lowest value
                    worst = -1 * heuristic(game.board);
                    returnMove = findMoves.get(j);
                }
            }
        }
        if (best == -9999 && bestWorst) { // assign randomly if a capture can't happen

            Random chooseMove = new Random();
            int i = chooseMove.nextInt(findMoves.size());
            returnMove = findMoves.get(i);
        } else if (worst == 9999 && !bestWorst) {
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
                if (board[y][x].piece == null)
                    continue;
                if (board[y][x].piece.side == game.turn) {
                    val += board[y][x].piece.type.value;
                } else {
                    val -= board[y][x].piece.type.value;
                }
            }
        }
        return val;
    }

    public String serialize(Square[][] state) {
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

    public static Square[][] deserialize(String str) {
        Square[][] dState = new Square[8][8];
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
            dState[y][x] = sqr;
        }
        return dState;
    }

    public boolean equals(Square[][] s1, Square[][] s2) {
        return this.serialize(s1).equals(serialize(s2));
    }
}

class Pair {
    Square[][] first;
    int second;

    public Pair() {

    }

    public Pair(Square[][] f, int s) {
        first = f;
        second = s;
    }
}