package Moves;

import Utils.Utils;

import static Moves.RanksFilesCenter.Anti_Diagonal_Masks;
import static Moves.RanksFilesCenter.Diagonal_Masks;


public class BishopMoves {
    //PCT = Pre Calculated Tables
    public static long PCTBishopMoves(byte square, long occupied_squares){
        long bishop= Utils.getBitboardForSquare(square);
        int diagonal_index=(square / 8) + (square % 8);
        int anti_diagonal_index=(square / 8) + 7 - (square % 8);

        //diagonal
        long possibilities_diagonal_down=(occupied_squares&Diagonal_Masks[diagonal_index]) - (2 * bishop);
        long possibilities_diagonal_up=Long.reverse(Long.reverse(occupied_squares&Diagonal_Masks[diagonal_index]) - (2 * Long.reverse(bishop)));
        long possibilities_diagonal = possibilities_diagonal_up^possibilities_diagonal_down;
        long possibilities_diagonal_final=possibilities_diagonal&Diagonal_Masks[diagonal_index];

        //anti_diagonal
        long possibilities_anti_diagonal_down=(occupied_squares&Anti_Diagonal_Masks[anti_diagonal_index]) - (2 * bishop);
        long possibilities_anti_diagonal_up=Long.reverse(Long.reverse(occupied_squares&Anti_Diagonal_Masks[anti_diagonal_index]) - (2 * Long.reverse(bishop)));
        long possibilities_anti_diagonal = possibilities_anti_diagonal_up^possibilities_anti_diagonal_down;
        long possibilities_anti_diagonal_final=possibilities_anti_diagonal&Anti_Diagonal_Masks[anti_diagonal_index];

        return possibilities_diagonal_final | possibilities_anti_diagonal_final;
    }
}
