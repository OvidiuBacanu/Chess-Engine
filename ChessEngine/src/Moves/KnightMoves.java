package Moves;

import Utils.Utils;

import static Moves.RanksFilesCenter.*;

public class KnightMoves {
    //PCT = Pre Calculated Tables
    public static long PCTKnightMoves(byte square){
        long moves=0;
        long knight=Utils.getBitboardForSquare(square);
        moves |= knight<<17 & ~ FILE_A;
        moves |= knight>>>15 & ~ FILE_A;
        moves |= knight>>>6 & ~ FILE_AB;
        moves |= knight<<10 & ~ FILE_AB;

        moves |= knight>>>17 & ~ FILE_H;
        moves |= knight<<15 & ~ FILE_H;
        moves |= knight<<6 & ~ FILE_GH;
        moves |= knight>>>10 & ~ FILE_GH;
        return moves;
    }
}
