package Moves;

import Utils.Utils;

import static Moves.RanksFilesCenter.File_Masks;
import static Moves.RanksFilesCenter.Rank_Masks;

public class RookMoves {
    //PCT = Pre Calculated Tables
    public static long PCTRookMoves(byte square, long occupied_squares) {
        long rook = Utils.getBitboardForSquare(square);
        int rank_index= square/8;
        int file_index= square%8;

        //horizontal
        long possibilities_horizontal_right=occupied_squares - 2 * rook;
        long possibilities_horizontal_left=Long.reverse(Long.reverse(occupied_squares) - (2 * Long.reverse(rook)));
        long possibilities_horizontal = possibilities_horizontal_right^possibilities_horizontal_left;
        long possibilities_horizontal_final=possibilities_horizontal&Rank_Masks[rank_index];

        //vertical
        long possibilities_vertical_down=(occupied_squares&File_Masks[file_index]) - (2 * rook);
        long possibilities_vertical_up= Long.reverse(Long.reverse(occupied_squares&File_Masks[file_index]) - (2 * Long.reverse(rook)));
        long possibilities_vertical = possibilities_vertical_up^possibilities_vertical_down;
        long possibilities_vertical_final=possibilities_vertical&File_Masks[file_index];

        return possibilities_horizontal_final | possibilities_vertical_final;
    }
}
