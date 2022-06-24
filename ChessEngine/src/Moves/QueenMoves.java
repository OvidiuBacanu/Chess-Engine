package Moves;

public class QueenMoves {
    //PCT = Pre Calculated Tables
    public static long PCTQueenMoves(byte square, long occupied_squares) {
        return RookMoves.PCTRookMoves(square,occupied_squares) | BishopMoves.PCTBishopMoves(square, occupied_squares);
    }
}
