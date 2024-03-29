package Moves;

import Board.Board;
import Utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static Moves.RanksFilesCenter.*;

public class WhitePawnMoves {
    //PCT = Pre Calculated Tables
    public static List<Move> ListWhitePawnsPseudolegalMoves(Board board){
        List<Move> moves=new ArrayList<>();
        moves.addAll(generatePseudolegalMoves1ForwardWP(board));
        moves.addAll(generatePseudolegalMoves2ForwardWP(board));
        moves.addAll(generatePseudolegalMovesCaptureWP(board,true));
        moves.addAll(generatePseudolegalMovesCaptureWP(board,false));
        moves.addAll(generatePseudolegalMovesEnpassantWP(board));
        return moves;
    }

    public static long PCTWhitePawns1Forward(long white_pawns){
        return white_pawns>>>8;
    }

    public static long PCTWhitePawns2Forward(long white_pawns){
        return white_pawns>>>16;
    }

    public static long PCTWhitePawnsCaptureRight(long white_pawns){
        return white_pawns>>>7 & ~FILE_A;
    }

    public static long PCTWhitePawnsCaptureLeft(long white_pawns){
        return white_pawns>>>9 & ~FILE_H;
    }

    public static List<Move> generatePseudolegalMoves1ForwardWP(Board board){
        List<Move> moves=new ArrayList<>();
        long white_pawns=board.getWhite_pawns();
        long white_pawns_1_forward= PCTWhitePawns1Forward(white_pawns);

        long occupied_squares= board.getWhitePiecesAsBitboard()| board.getBlackPiecesAsBitboard();
        long empty_squares=~occupied_squares;

        //normal possible moves
        long possible_moves= white_pawns_1_forward & empty_squares &~RANK_8;

        while (possible_moves!=0){
            byte square= (byte) Long.numberOfTrailingZeros(possible_moves);
            possible_moves= Utils.popBitFromBitboard(possible_moves, square);

            Move move=new Move(square,(byte)(square+8), (byte)1, (byte)0, (byte)0, false, false, false, false, false);
            moves.add(move);
        }

        //pawn promotion by moving 1 forward move
        long possible_moves_promotion= white_pawns_1_forward & empty_squares &RANK_8;
        while (possible_moves_promotion!=0){
            byte square= (byte) Long.numberOfTrailingZeros(possible_moves_promotion);
            possible_moves_promotion= Utils.popBitFromBitboard(possible_moves_promotion, square);

            for(byte promoted_piece=2;promoted_piece<6;promoted_piece++) {
                Move move = new Move(square, (byte) (square + 8), (byte) 1, (byte) 0, promoted_piece, false, false, false, false, true);
                moves.add(move);
            }
        }

        return moves;
    }

    public static List<Move> generatePseudolegalMoves2ForwardWP(Board board){
        List<Move> moves=new ArrayList<>();
        long white_pawns=board.getWhite_pawns();
        long white_pawns_2_forward= PCTWhitePawns2Forward(white_pawns) & RANK_4;

        long occupied_squares= board.getWhitePiecesAsBitboard()| board.getBlackPiecesAsBitboard();
        long empty_squares=~occupied_squares;
        long possible_moves= empty_squares & white_pawns_2_forward &(empty_squares>>>8);

        while (possible_moves!=0){
            byte square= (byte) Long.numberOfTrailingZeros(possible_moves);
            possible_moves= Utils.popBitFromBitboard(possible_moves, square);

            Move move=new Move(square,(byte)(square+16), (byte)1, (byte)0, (byte)0, false, false, true, false, false);
            moves.add(move);
        }
        return moves;
    }

    public static List<Move> generatePseudolegalMovesCaptureWP(Board board, boolean right){
        List<Move> moves=new ArrayList<>();
        long white_pawns_capture;
        long white_pawns=board.getWhite_pawns();
        int plus_square;

        if(right){
            white_pawns_capture= PCTWhitePawnsCaptureRight(white_pawns);
            plus_square=7;
        }
        else{
            white_pawns_capture= PCTWhitePawnsCaptureLeft(white_pawns);
            plus_square=9;
        }


        long black_pieces= board.getBlackPiecesPossibleCaptures();

        //normal possible moves
        long possible_moves= white_pawns_capture &~RANK_8 & black_pieces;

        while (possible_moves!=0){
            byte square= (byte) Long.numberOfTrailingZeros(possible_moves);
            possible_moves= Utils.popBitFromBitboard(possible_moves, square);

            byte black_piece_captured=board.getBlackPieceTypeOnSquare(square);
            moves.add(new Move(square,(byte)(square+plus_square), (byte)1, black_piece_captured, (byte)0, false, true, false, false, false));

        }

        //pawn promotion by capture right/left
        long possible_moves_promotion= white_pawns_capture & RANK_8 & black_pieces;
        while (possible_moves_promotion!=0){
            byte square= (byte) Long.numberOfTrailingZeros(possible_moves_promotion);
            possible_moves_promotion= Utils.popBitFromBitboard(possible_moves_promotion, square);

            byte black_piece_captured=board.getBlackPieceTypeOnSquare(square);
            for(byte promoted_piece=2;promoted_piece<6;promoted_piece++)
                moves.add(new Move(square,(byte)(square+plus_square), (byte)1, black_piece_captured, promoted_piece, false, true, false, false, true));

        }
        return moves;
    }

    public static List<Move> generatePseudolegalMovesEnpassantWP(Board board){
        List<Move> moves=new ArrayList<>();
        long white_pawns=board.getWhite_pawns();

        long white_pawns_capture_right= PCTWhitePawnsCaptureRight(white_pawns);
        long white_pawns_capture_left= PCTWhitePawnsCaptureLeft(white_pawns);

        byte en_passant_target_square=board.getEn_passant_target_square();
        if((Utils.getBitboardForSquare((byte) (en_passant_target_square+8)) & RANK_5)!=0) {
            if (Utils.checkIfSquareIsOne(white_pawns_capture_right, en_passant_target_square))
                moves.add(new Move(en_passant_target_square, (byte) (en_passant_target_square + 7), (byte) 1, (byte) 1, (byte) 0, false, true, false, true, false));
            if (Utils.checkIfSquareIsOne(white_pawns_capture_left, en_passant_target_square))
                moves.add(new Move(en_passant_target_square, (byte) (en_passant_target_square + 9), (byte) 1, (byte) 1, (byte) 0, false, true, false, true, false));
        }
        return moves;
    }
}
