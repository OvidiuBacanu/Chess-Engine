package Moves;

import Utils.Utils;

import static Moves.RanksFilesCenter.FILE_A;
import static Moves.RanksFilesCenter.FILE_H;

public class KingMoves {
    //PCT = Pre Calculated Tables
    public static long PCTKingMoves(byte square){
        long moves=0;
        long king= Utils.getBitboardForSquare(square);
        moves |= king << 8;
        moves |= king >>> 8;
        moves |= king << 1 & ~FILE_A;
        moves |= king >>> 1 & ~FILE_H;
        moves |= king << 9 & ~FILE_A;
        moves |= king >>> 9 & ~FILE_H;
        moves |= king << 7 & ~FILE_H;
        moves |= king >>> 7 & ~FILE_A;
        return moves;
    }
}
