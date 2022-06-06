package Moves;

import Board.Board;
import Utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static Moves.RanksFilesCenter.*;
import static Moves.RanksFilesCenter.RANK_8;

public class BlackPawnMoves {
    //PCT = Pre Calculated Tables
    public static List<Move>  ListBlackPawnsPseudolegalMoves(Board board){
        List<Move> moves=new ArrayList<>();
        moves.addAll(generatePseudolegalMoves1ForwardBP(board));
        moves.addAll(generatePseudolegalMoves2ForwardBP(board));
        moves.addAll(generatePseudolegalMovesCaptureBP(board,true));
        moves.addAll(generatePseudolegalMovesCaptureBP(board,false));
        moves.addAll(generatePseudolegalMovesEnpassantBP(board));
        return moves;
    }

    public static long PCTBlackPawns1Forward(long black_pawns){
        return black_pawns<<8;
    }

    public static long PCTBlackPawns2Forward(long black_pawns){
        return black_pawns<<16;
    }

    public static long PCTBlackPawnsCaptureLeft(long black_pawns){
        return black_pawns<<7 & ~FILE_H;
    }

    public static long PCTBlackPawnsCaptureRight(long black_pawns){
        return black_pawns<<9 & ~FILE_A;
    }

    public static List<Move> generatePseudolegalMoves1ForwardBP(Board board){
        List<Move> moves=new ArrayList<>();
        long black_pawns=board.getBlack_pawns();
        long black_pawns_1_forward= PCTBlackPawns1Forward(black_pawns);

        long occupied_squares= board.getWhitePiecesAsBitboard()| board.getBlackPiecesAsBitboard();
        long empty_squares=~occupied_squares;

        //normal possible moves
        long possible_moves= black_pawns_1_forward & empty_squares &~RANK_1;

        while (possible_moves!=0){
            byte square= (byte) Long.numberOfTrailingZeros(possible_moves);
            possible_moves= Utils.popBitFromBitboard(possible_moves, square);

            Move move=new Move(square,(byte)(square-8), (byte)1, (byte)0, (byte)0, false, false, false, false, false);
            moves.add(move);
        }

        //pawn promotion by moving 1 forward move
        long possible_moves_promotion= black_pawns_1_forward & empty_squares &RANK_1;
        while (possible_moves_promotion!=0){
            byte square= (byte) Long.numberOfTrailingZeros(possible_moves_promotion);
            possible_moves_promotion= Utils.popBitFromBitboard(possible_moves_promotion, square);

            for(byte promoted_piece=2;promoted_piece<6;promoted_piece++) {
                Move move = new Move(square, (byte) (square - 8), (byte) 1, (byte) 0, promoted_piece, false, false, false, false, true);
                moves.add(move);
            }
        }

        return moves;
    }

    public static List<Move> generatePseudolegalMoves2ForwardBP(Board board){
        List<Move> moves=new ArrayList<>();
        long black_pawns=board.getBlack_pawns();
        long black_pawns_2_forward= PCTBlackPawns2Forward(black_pawns) & RANK_5;

        long occupied_squares= board.getWhitePiecesAsBitboard()| board.getBlackPiecesAsBitboard();
        long empty_squares=~occupied_squares;
        long possible_moves= empty_squares & black_pawns_2_forward &(empty_squares<<8);

        while (possible_moves!=0){
            byte square= (byte) Long.numberOfTrailingZeros(possible_moves);
            possible_moves= Utils.popBitFromBitboard(possible_moves, square);

            Move move=new Move(square,(byte)(square-16), (byte)1, (byte)0, (byte)0, false, false, true, false, false);
            moves.add(move);
        }
        return moves;
    }

    public static List<Move> generatePseudolegalMovesCaptureBP(Board board, boolean right){
        List<Move> moves=new ArrayList<>();
        long black_pawns_capture=0L;
        long black_pawns=board.getBlack_pawns();
        int minus_square=0;

        if(right){
            black_pawns_capture= PCTBlackPawnsCaptureRight(black_pawns);
            minus_square=9;
        }
        else{
            black_pawns_capture= PCTBlackPawnsCaptureLeft(black_pawns);
            minus_square=7;
        }

        long white_pieces= board.getWhitePiecesPossibleCaptures();

        //normal possible moves
        long possible_moves= black_pawns_capture &~RANK_1 & white_pieces;

        while (possible_moves!=0){
            byte square= (byte) Long.numberOfTrailingZeros(possible_moves);
            possible_moves= Utils.popBitFromBitboard(possible_moves, square);

            byte white_piece_captured=board.getWhitePieceTypeOnSquare(square);
            moves.add(new Move(square,(byte)(square-minus_square), (byte)1, white_piece_captured, (byte)0, false, true, false, false, false));

        }

        //pawn promotion by capture right/left
        long possible_moves_promotion= black_pawns_capture & RANK_1 & white_pieces;
        while (possible_moves_promotion!=0){
            byte square= (byte) Long.numberOfTrailingZeros(possible_moves_promotion);
            possible_moves_promotion= Utils.popBitFromBitboard(possible_moves_promotion, square);

            byte white_piece_captured=board.getWhitePieceTypeOnSquare(square);
            for(byte promoted_piece=2;promoted_piece<6;promoted_piece++)
                moves.add(new Move(square,(byte)(square-minus_square), (byte)1, white_piece_captured, promoted_piece, false, true, false, false, true));
        }
        return moves;
    }

    public static List<Move> generatePseudolegalMovesEnpassantBP(Board board){
        List<Move> moves=new ArrayList<>();
        long black_pawns=board.getBlack_pawns();

        long black_pawns_capture_right= PCTBlackPawnsCaptureRight(black_pawns);
        long black_pawns_capture_left= PCTBlackPawnsCaptureLeft(black_pawns);

        byte en_passant_target_square=board.getEn_passant_target_square();
        if((Utils.getBitboardForSquare((byte) (en_passant_target_square-8)) & RANK_4)!=0) {
            if (Utils.checkIfSquareIsOne(black_pawns_capture_right, en_passant_target_square))
                moves.add(new Move(en_passant_target_square, (byte) (en_passant_target_square - 9), (byte) 1, (byte) 1, (byte) 0, false, true, false, true, false));
            if (Utils.checkIfSquareIsOne(black_pawns_capture_left, en_passant_target_square))
                moves.add(new Move(en_passant_target_square, (byte) (en_passant_target_square - 7), (byte) 1, (byte) 1, (byte) 0, false, true, false, true, false));
        }
        return moves;
    }
}
