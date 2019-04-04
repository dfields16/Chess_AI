import java.util.List;
import java.util.ArrayList;

public class Util {
    private static ArrayList<Move> history = new ArrayList<Move>();
    private static int turn = 0;

    public static boolean inCheck(Square[][] board, int side) {

        Square king = getKing(board, side);
        System.out.println(king.coord.getX() + " " + king.coord.getY());

        boolean checked = false;
        Move move;

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (board[y][x].piece != null && board[y][x].piece.side != side) {
                    move = new Move(board[y][x].coord, king.coord);
                    // System.out.println( move.x1() + " " + move.y1() + " " + move.x2() + " " +
                    // move.y2() );
                    if (validMove(board, move, side))
                        checked = true;
                }
            }
        }

        if (checked) {
            king.piece.checked = 1;
        } else {
            king.piece.checked = 0;
        }

        return checked;
    }

    public static Square[][] movePiece(Square[][] board, Move move) {

        if (checkCapture(board, move)) {
            history.add(new Move(move.start, move.end, board[move.y2()][move.x2()].piece));
            System.out.println("captured");
        } else {
            history.add(new Move(move.start, move.end, null));
            System.out.println("not captured");
        }

        board[move.y1()][move.x1()].piece.moved = 1;
        board[move.y2()][move.x2()].piece = board[move.y1()][move.x1()].piece;
        board[move.y1()][move.x1()].piece = null;

        // see if we moved into
        int opponent = (turn == 0) ? 1 : 0;

        // if(isCheckmate(opponent)){
        // System.out.println("Checkmate");
        // }

        if (inCheck(board, turn)) {
            undoMove(board);

        } else {
            // see if they are in check
            if (inCheck(board, (turn == 0) ? 1 : 0)) {
                System.out.println("Check");
            }

            turn = (turn == 0) ? 1 : 0;
            // timer = 0;
            sendMove();
        }
        return board;

    }

    public static void sendMove() {
        // Update Current State
        // ai.setState(board);
        // currentState.print();
        // if (Main.client != null && Main.client.isActive()) {
        // Main.client.sendData(ai.serialize());
        // }
        // if (Main.server != null && Main.server.isActive()) {
        // Main.server.sendData(ai.serialize());
        // }
    }

    public static void undoMove(Square[][] board) {
        Move move = history.get(history.size() - 1);

        board[move.y1()][move.x1()].piece = board[move.y2()][move.x2()].piece;
        board[move.y1()][move.x1()].piece.moved = 0;
        board[move.y2()][move.x2()].piece = move.captured;

        history.remove(history.size() - 1);
    }

    public static boolean checkCapture(Square[][] board, Move move) {
        if (board[move.y2()][move.x2()].piece != null) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean validMove(Square[][] board, Move move, int turn) {

        Piece start = board[move.y1()][move.x1()].piece;
        Piece end = board[move.y2()][move.x2()].piece;

        boolean valid = true;
        boolean pawntest = true;

        int dx = move.x2() - move.x1();
        int dy = move.y2() - move.y1();
        int py = (start.side == 0) ? (-1) * dy : dy;

        List<Float> slopes = new ArrayList<>();
        List<Float> distances = new ArrayList<>();

        // CALCULATE THE SLOPE/DISTANCE FOR THE DESIRED MOVE
        float slope = (dx == 0 || dy == 0) ? 0 : (float) dy / (float) dx;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        // IF TARGET IS ON SAME TEAM LEAVE EARLY
        if (end != null && end.side == start.side)
            valid = false;

        // ASSIGN EACH OF THE PIECES A SLOPE AND DISTANCE THEY CAN MOVE
        switch (start.type) {
        case KING:
            slopes.add((float) 0);
            slopes.add((float) 1);
            distances.add((float) 1);
            distances.add((float) Math.sqrt(2));
            break;
        case QUEEN:
            slopes.add((float) 0);
            slopes.add((float) 1);
            distances.add((float) 0);
            break;
        case BISCHOP:
            slopes.add((float) 1);
            distances.add((float) 0);
            break;
        case KNIGHT:
            slopes.add((float) 2);
            slopes.add((float) .5);
            distances.add((float) Math.sqrt(5));
            break;
        case ROOK:
            slopes.add((float) 0);
            distances.add((float) 0);
            break;
        case PAWN:
            slopes.add((float) 0);
            slopes.add((float) 1);
            distances.add((float) 1);
            distances.add((float) Math.sqrt(2));
            if (start.moved == 0)
                distances.add((float) 2);

            // prevent horizontal pawn movement
            if (dy == 0)
                pawntest = false;
            // prevent backward movement
            if (py < 0)
                pawntest = false;
            // if trying to go forward prevent capture
            if (Math.abs(slope) == 0 && (end != null))
                pawntest = false;
            // if trying to go diagonal make sure it is a capture
            if (Math.abs(slope) == 1 && (end == null || start.side == end.side))
                pawntest = false;
            break;
        case EMPTY:
            break;
        }

        // DID PAWN PASS
        if (!pawntest)
            valid = false;

        // CHECK FOR COLLISION
        if (checkCollision(board, move))
            valid = false;

        // EVALUATE FINAL RESPONSE
        if (!listContains(slopes, Math.abs(slope)))
            valid = false;
        if (!listContains(distances, 0) && !listContains(distances, dist))
            valid = false;

        // MOVING INTO CHECK?
        // if(start.side == turn) {
        // System.out.println("check validation");
        // if(inCheck(turn)) valid = false;
        // }

        // CAN CASTLE?
        // If king is moving, and not in check and has not moved yet
        if (start.type == ChessPiece.KING && start.side == turn && start.checked == 0 && start.moved == 0) {

            Square corner;
            Move castle;
            // DETECT DIRECTION/AVAILABILITY
            // trying to castle small side
            if (dy == 0 && dx > 0 && dist == 2) {
                corner = board[move.y2()][move.x2() + 1];
                castle = new Move(corner.coord, board[move.y2()][move.x2() - 1].coord, null);
                if (corner.piece.type == ChessPiece.ROOK && corner.piece.moved == 0 && !checkCollision(board, move)) {
                    movePiece(board, castle);
                    valid = true;
                }
                // trying to castle big side
            } else if (dy == 0 && dx < 0 && dist == 3) {
                corner = board[move.y2()][move.x2() - 1];
                castle = new Move(corner.coord, board[move.y2()][move.x2() + 1].coord, null);
                if (corner.piece.type == ChessPiece.ROOK && corner.piece.moved == 0) {
                    movePiece(board, castle);
                    valid = true;
                }
            }
        }

        return valid;
    }

    public static boolean checkCollision(Square[][] board, Move move) {
        // knight is allowed to pass over pieces, all others cannot
        if (board[move.y1()][move.x1()].piece != null && board[move.y1()][move.x1()].piece.type != ChessPiece.KNIGHT) {
            int xp = move.x2();
            int yp = move.y2();
            while (true) {
                // increment in direction of root
                if (xp > move.x1()) {
                    xp--;
                } else if (xp < move.x1()) {
                    xp++;
                }
                if (yp > move.y1()) {
                    yp--;
                } else if (yp < move.y1()) {
                    yp++;
                }
                // if at root exit while
                if (yp == move.y1() && xp == move.x1())
                    break;
                // if piece found exit validation
                if (board[yp][xp].piece != null)
                    return true;
            }
        }
        return false;
    }

    public static Square getKing(Square[][] board, int side) {
        Square sq = null;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (board[y][x].piece != null && board[y][x].piece.side == side
                        && board[y][x].piece.type == ChessPiece.KING) {
                    sq = board[y][x];
                    return sq;
                }
            }
        }
        return sq;
    }

    static boolean listContains(List<Float> list, float key) {
        for (float elem : list)
            if (elem == key)
                return true;
        return false;
    }

    static void updateUI(Square[][] board, Game g) {
        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[y].length; x++) {
                g.board[y][x] = board[y][x];
            }
        }
    }

}