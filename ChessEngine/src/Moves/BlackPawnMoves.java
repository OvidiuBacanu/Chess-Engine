package Moves;

import Board.Board;

import java.util.ArrayList;
import java.util.List;

import static Moves.RanksFilesCenter.FILE_A;
import static Moves.RanksFilesCenter.FILE_H;

public class BlackPawnMoves {
    //PCT = Pre Calculated Tables
    public static List<Integer> ListBlackPawnsMoves(Board board){
        List<Integer> moves=new ArrayList<>();
        return moves;
    }

    private long PCBlackPawns1Forward(long black_pawns){
        return black_pawns<<8;
    }

    private long PCTBlackPawns2Forward(long black_pawns){
        return black_pawns<<16;
    }

    private long PCTBlackPawnsCaptureRight(long black_pawns){
        return black_pawns<<7 & ~FILE_H;
    }

    private long PCTBlackPawnsCaptureLeft(long black_pawns){
        return black_pawns<<9 & ~FILE_A;
    }
}
