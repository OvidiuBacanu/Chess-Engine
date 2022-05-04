package Moves;

import Board.Board;
import Utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static Moves.RanksFilesCenter.*;

public class KingMoves {
    //PCT = Pre Calculated Tables
    public static List<Integer> ListKingtMoves(Board board){
        List<Integer> moves=new ArrayList<>();
        return moves;
    }

    private long PCTKingMoves(byte square){
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
