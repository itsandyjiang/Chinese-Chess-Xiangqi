package GameLogic;

import GameLogic.Pieces.Piece;

/**
 * Missing features: Doesn't check who the current player is. Needs player class to integrate
 *
 * @version 4/10/20
 */
public class MoveChecker {
    private Board board;
    private Move move;

    private int obstacleCount; //number of pieces in the way
    private boolean isClear; //if obstacles = 0
    private boolean attack;
    private boolean legal;

    MoveChecker(Board board, Move move) {
        this.board = board;
        this.move = move;
        this.legal = true;

        Piece curr = board.getPoint(move.getOriginX(), move.getOriginY()).getPiece();
        Piece captured = board.getPoint(move.getFinalX(), move.getFinalY()).getPiece();


        //  1. first check if movement pattern is legal (ie horse moves 1 up 2 left)
        CheckPiece();
        if (legal) {
            obstacleStats();
            isAttack(); //  2. check if we are doing an attack, and also check if the end point is blocked by a friendly piece

            if (!isClear && curr.toString().equals("Cannon") && !(obstacleCount == 1 && attack)) {  //3. Check if the path is clear, if not See if we're an attacking cannon or a non attacking cannon that can't move
                this.legal = false;

            } else { //3. Check if the path is clear, if not See if we're an attacking cannon or a non attacking cannon that can't move
                if (curr.toString().equals("Cannon") && attack) {
                    legal = false;
                }
            }

            if (legal) {
                board.doMove(move);
                if (generalsOpen()) {
                    board.undoMove(move, captured);
                    System.out.println(" Generals Open ");
                }
            }
        }


    }


    private boolean generalsOpen() {

        for (int x = 3; x < 6; x++) {
            for (int y = 0; y < 3; y++) {
                if (board.getPoint(x, y).getPiece().toString().equals("General")) {
                    board.setUpGeneralX(x);
                    board.setUpGeneralY(y);

                }
            }

            for (int y = 7; y < 10; y++) {
                if (board.getPoint(x, y).getPiece().toString().equals("General")) {
                    board.setDownGeneralX(x);
                    board.setUpGeneralY(y);
                }
            }
        }

        if (board.getUpGeneralX() != board.getDownGeneralX()) {
            return false;
        } else {
            for (int i = board.getUpGeneralY() + 1; i < board.getDownGeneralY(); i++) {
                if (board.getPoint(board.getDownGeneralX(), i).getPiece() != null) {
                    obstacleCount++;
                }
            }
        }
        if (obstacleCount == 0) {
            System.out.print(" Generals Exposed!");
            return true;
        }
        return false;
    }

    /**
     * Checks if the move pattern is a valid move pattern and if there's a piece present.
     * If not, terminates the process
     */
    private void CheckPiece() {
        Piece temp = board.getPoint(move.getOriginX(), move.getOriginY()).getPiece();

        if (temp == null) {
            this.legal = false;

        } else {
            temp.checkPattern(move);
            if (!move.isValid()) {
                this.legal = false;

            }
        }

    }

    /**
     * Checks the destination piece to see if we're attacking or self blocked
     * If we're self blocked, we terminate the whole operation because it's definitely illegal
     */
    private void isAttack() {
        if (board.getPoint(move.getFinalX(), move.getFinalY()).getPiece() == null) {
            attack = false;
        } else {
            Piece.Side origin = board.getPoint(move.getOriginX(), move.getOriginY()).getPiece().getSide();
            Piece.Side dest = board.getPoint(move.getFinalX(), move.getFinalY()).getPiece().getSide();
            if (origin != dest) {
                attack = true;
            }
            if (origin == dest) {
                this.attack = false;
                this.legal = false;
                //this.isClear = false; //shouldn't matter because illegal anyway now
            }
        }

    }

    /**
     * Finds out if there are obstacles, and if so, how many?
     * Useful for seeing if we have one obstacle for the cannon
     * Useful for seeing if a piece is blocked, handles knights as well.
     */
    private void obstacleStats() {

        isClear = true;
        this.obstacleCount = 0;

        //vertical move
        if (move.isVertical()) {
            if (move.getDy() > 0) {
                for (int y = move.getOriginY() + 1; y < move.getFinalY(); y++) {
                    if (board.getPoint(move.getOriginX(), y).getPiece() != null) {
                        obstacleCount++;
                    }
                }
            } else if (move.getDy() < 0) {
                for (int y = move.getOriginY() - 1; y > move.getFinalY(); y--) {
                    if (board.getPoint(move.getOriginX(), y).getPiece() != null) {
                        obstacleCount++;
                    }
                }
            }


        }
        //horizontal move
        else if (move.isHorizontal()) {
            if (move.getDx() > 0) {
                for (int x = move.getOriginX() + 1; x < move.getFinalX(); x++) {
                    if (board.getPoint(x, move.getOriginY()).getPiece() != null) {
                        obstacleCount++;
                    }
                }
            } else if (move.getDx() < 0) {
                for (int x = move.getOriginX() - 1; x > move.getFinalX(); x--) {
                    if (board.getPoint(x, move.getOriginY()).getPiece() != null) {
                        obstacleCount++;
                    }
                }
            }
            //diagonal move
        }
        //diagonal move
        else if (move.isDiagonal()) {

            //left up
            if (move.getDx() < 0 && move.getDy() < 0) {
                for (int x = 1; x < move.getDx(); x++) {
                    if (board.getPoint(move.getOriginX() - x, move.getOriginY() - x).getPiece() != null) {
                        obstacleCount++;
                    }
                }
            }
            //left down
            else if (move.getDx() < 0 && move.getDy() > 0) {
                for (int x = 1; x < move.getDx(); x++) {
                    if (board.getPoint(move.getOriginX() - x, move.getOriginY() + x).getPiece() != null) {
                        obstacleCount++;
                    }
                }
            }
            //right down
            else if (move.getDx() > 0 && move.getDy() > 0) {
                for (int x = 1; x < move.getDx(); x++) {
                    if (board.getPoint(move.getOriginX() + x, move.getOriginY() + x).getPiece() != null) {
                        obstacleCount++;
                    }
                }
            }

            //right up
            else {// (move.getDx() > 0 && move.getDy() > 0) {
                for (int x = 1; x < move.getDx(); x++) {
                    if (board.getPoint(move.getOriginX() + x, move.getOriginY() - x).getPiece() != null) {
                        obstacleCount++;
                    }
                }
            }
        }
        //only for knights, as only knights have non linear moves. Knights are only blocked by the nearest pieces.
        else {

            if (move.getDx() == 2) {
                if (board.getPoint(move.getOriginX() + 1, move.getOriginY()).getPiece() != null) {
                    obstacleCount++;
                }
            } else if (move.getDx() == -2) {
                if (board.getPoint(move.getOriginX() - 1, move.getOriginY()).getPiece() != null) {
                    obstacleCount++;
                }
            } else if (move.getDy() == 2) {
                if (board.getPoint(move.getOriginX(), move.getOriginY() + 1).getPiece() != null) {
                    obstacleCount++;
                }
            } else if (move.getDy() == -2) {
                if (board.getPoint(move.getOriginX(), move.getOriginY() - 1).getPiece() != null) {
                    obstacleCount++;
                }
            }


        }
        if (obstacleCount != 0) {
            isClear = false;
        }


    }

    public boolean isLegal() {
        return legal;
    }
}
