package Board;

import Evaluation.EvaluationUtils;
import Moves.BlackPawnMoves;
import Moves.Move;
import Moves.WhitePawnMoves;
import Utils.Utils;

import java.util.*;

import static Moves.BishopMoves.PCTBishopMoves;
import static Moves.KingMoves.PCTKingMoves;
import static Moves.KnightMoves.PCTKnightMoves;
import static Moves.QueenMoves.PCTQueenMoves;
import static Moves.RanksFilesCenter.*;
import static Moves.RookMoves.PCTRookMoves;

public class Board {
    //bitboards will be represented in little endian format
    private long white_pawns;
    private long white_knights;
    private long white_bishops;
    private long white_rooks;
    private long white_queens;
    private long white_king;

    private long black_pawns;
    private long black_knights;
    private long black_bishops;
    private long black_rooks;
    private long black_queens;
    private long black_king;

    //KS= king side, QS=queen side
    private boolean white_castling_KS_right;
    private boolean white_castling_QS_right;

    private boolean black_castling_KS_right;
    private boolean black_castling_QS_right;

    //variable to see which player has to move
    // if white_turn=true it's white's turn to move else it's black's turn
    private boolean white_turn;

    private byte en_passant_target_square;

    private long hash_key;

    public long getHash_key() {
        return hash_key;
    }

    //initial position constructor
    public Board(){

//         white pawns
//    8    0 0 0 0 0 0 0 0
//    7    0 0 0 0 0 0 0 0
//    6    0 0 0 0 0 0 0 0
//    5    0 0 0 0 0 0 0 0
//    4    0 0 0 0 0 0 0 0
//    3    0 0 0 0 0 0 0 0
//    2    1 1 1 1 1 1 1 1
//    1    0 0 0 0 0 0 0 0
//
//         A B C D E F G H
        white_pawns=0X00FF000000000000L;

//         white knights
//    8    0 0 0 0 0 0 0 0
//    7    0 0 0 0 0 0 0 0
//    6    0 0 0 0 0 0 0 0
//    5    0 0 0 0 0 0 0 0
//    4    0 0 0 0 0 0 0 0
//    3    0 0 0 0 0 0 0 0
//    2    0 0 0 0 0 0 0 0
//    1    0 1 0 0 0 0 1 0
//
//         A B C D E F G H
        white_knights=0X4200000000000000L;

//         white bishops
//    8    0 0 0 0 0 0 0 0
//    7    0 0 0 0 0 0 0 0
//    6    0 0 0 0 0 0 0 0
//    5    0 0 0 0 0 0 0 0
//    4    0 0 0 0 0 0 0 0
//    3    0 0 0 0 0 0 0 0
//    2    0 0 0 0 0 0 0 0
//    1    0 0 1 0 0 1 0 0
//
//         A B C D E F G H
        white_bishops=0X2400000000000000L;

//         white rooks
//    8    0 0 0 0 0 0 0 0
//    7    0 0 0 0 0 0 0 0
//    6    0 0 0 0 0 0 0 0
//    5    0 0 0 0 0 0 0 0
//    4    0 0 0 0 0 0 0 0
//    3    0 0 0 0 0 0 0 0
//    2    0 0 0 0 0 0 0 0
//    1    1 0 0 0 0 0 0 1
//
//         A B C D E F G H
        white_rooks=0X8100000000000000L;

//         white queen
//    8    0 0 0 0 0 0 0 0
//    7    0 0 0 0 0 0 0 0
//    6    0 0 0 0 0 0 0 0
//    5    0 0 0 0 0 0 0 0
//    4    0 0 0 0 0 0 0 0
//    3    0 0 0 0 0 0 0 0
//    2    0 0 0 0 0 0 0 0
//    1    0 0 0 1 0 0 0 0
//
//         A B C D E F G H
        white_queens=0X0800000000000000L;

//         white king
//    8    0 0 0 0 0 0 0 0
//    7    0 0 0 0 0 0 0 0
//    6    0 0 0 0 0 0 0 0
//    5    0 0 0 0 0 0 0 0
//    4    0 0 0 0 0 0 0 0
//    3    0 0 0 0 0 0 0 0
//    2    0 0 0 0 0 0 0 0
//    1    0 0 0 0 1 0 0 0
//
//         A B C D E F G H
        white_king=0X1000000000000000L;

//         black pawns
//    8    0 0 0 0 0 0 0 0
//    7    1 1 1 1 1 1 1 1
//    6    0 0 0 0 0 0 0 0
//    5    0 0 0 0 0 0 0 0
//    4    0 0 0 0 0 0 0 0
//    3    0 0 0 0 0 0 0 0
//    2    0 0 0 0 0 0 0 0
//    1    0 0 0 0 0 0 0 0
//
//         A B C D E F G H
        black_pawns=0X000000000000FF00L;

//         black knights
//    8    0 1 0 0 0 0 1 0
//    7    0 0 0 0 0 0 0 0
//    6    0 0 0 0 0 0 0 0
//    5    0 0 0 0 0 0 0 0
//    4    0 0 0 0 0 0 0 0
//    3    0 0 0 0 0 0 0 0
//    2    0 0 0 0 0 0 0 0
//    1    0 0 0 0 0 0 0 0
//
//         A B C D E F G H
        black_knights=0X0000000000000042L;

//         black bishops
//    8    0 0 1 0 0 1 0 0
//    7    0 0 0 0 0 0 0 0
//    6    0 0 0 0 0 0 0 0
//    5    0 0 0 0 0 0 0 0
//    4    0 0 0 0 0 0 0 0
//    3    0 0 0 0 0 0 0 0
//    2    0 0 0 0 0 0 0 0
//    1    0 0 0 0 0 0 0 0
//
//         A B C D E F G H
        black_bishops=0X0000000000000024L;

//         black rooks
//    8    1 0 0 0 0 0 0 1
//    7    0 0 0 0 0 0 0 0
//    6    0 0 0 0 0 0 0 0
//    5    0 0 0 0 0 0 0 0
//    4    0 0 0 0 0 0 0 0
//    3    0 0 0 0 0 0 0 0
//    2    0 0 0 0 0 0 0 0
//    1    0 0 0 0 0 0 0 0
//
//         A B C D E F G H
        black_rooks=0X0000000000000081L;

//         black queen
//    8    0 0 0 1 0 0 0 0
//    7    0 0 0 0 0 0 0 0
//    6    0 0 0 0 0 0 0 0
//    5    0 0 0 0 0 0 0 0
//    4    0 0 0 0 0 0 0 0
//    3    0 0 0 0 0 0 0 0
//    2    0 0 0 0 0 0 0 0
//    1    0 0 0 0 0 0 0 0
//
//         A B C D E F G H
        black_queens=0X0000000000000008L;

//         black king
//    8    0 0 0 0 1 0 0 0
//    7    0 0 0 0 0 0 0 0
//    6    0 0 0 0 0 0 0 0
//    5    0 0 0 0 0 0 0 0
//    4    0 0 0 0 0 0 0 0
//    3    0 0 0 0 0 0 0 0
//    2    0 0 0 0 0 0 0 0
//    1    0 0 0 0 0 0 0 0
//
//         A B C D E F G H
        black_king=0X0000000000000010L;

        white_castling_KS_right=true;
        white_castling_QS_right=true;

        black_castling_KS_right=true;
        black_castling_QS_right=true;

        white_turn=true;

        en_passant_target_square=-1;
        hash_key=hash_key_Zobrist();
    }

    public Board(long white_pawns, long white_knights, long white_bishops, long white_rooks, long white_queens, long white_king, long black_pawns, long black_knights, long black_bishops, long black_rooks, long black_queens, long black_king, boolean white_castling_KS_right, boolean white_castling_QS_right, boolean black_castling_KS_right, boolean black_castling_QS_right, boolean white_turn, byte en_passant_target_square, long hash_key) {
        this.white_pawns = white_pawns;
        this.white_knights = white_knights;
        this.white_bishops = white_bishops;
        this.white_rooks = white_rooks;
        this.white_queens = white_queens;
        this.white_king = white_king;
        this.black_pawns = black_pawns;
        this.black_knights = black_knights;
        this.black_bishops = black_bishops;
        this.black_rooks = black_rooks;
        this.black_queens = black_queens;
        this.black_king = black_king;
        this.white_castling_KS_right = white_castling_KS_right;
        this.white_castling_QS_right = white_castling_QS_right;
        this.black_castling_KS_right = black_castling_KS_right;
        this.black_castling_QS_right = black_castling_QS_right;
        this.white_turn = white_turn;
        this.en_passant_target_square = en_passant_target_square;
        this.hash_key=hash_key;
    }

    public Board(String fen){
        int charIndex = 0;
        int boardIndex = 0;
        while (fen.charAt(charIndex) != ' ')
        {
            switch (fen.charAt(charIndex++)) {
                case 'P' -> white_pawns |= (1L << boardIndex++);
                case 'p' -> black_pawns |= (1L << boardIndex++);
                case 'N' -> white_knights |= (1L << boardIndex++);
                case 'n' -> black_knights |= (1L << boardIndex++);
                case 'B' -> white_bishops |= (1L << boardIndex++);
                case 'b' -> black_bishops |= (1L << boardIndex++);
                case 'R' -> white_rooks |= (1L << boardIndex++);
                case 'r' -> black_rooks |= (1L << boardIndex++);
                case 'Q' -> white_queens |= (1L << boardIndex++);
                case 'q' -> black_queens |= (1L << boardIndex++);
                case 'K' -> white_king |= (1L << boardIndex++);
                case 'k' -> black_king |= (1L << boardIndex++);

//                case '/':
//                    break;
                case '1' -> boardIndex++;
                case '2' -> boardIndex += 2;
                case '3' -> boardIndex += 3;
                case '4' -> boardIndex += 4;
                case '5' -> boardIndex += 5;
                case '6' -> boardIndex += 6;
                case '7' -> boardIndex += 7;
                case '8' -> boardIndex += 8;
                default -> {
                }
            }
        }
        white_turn = (fen.charAt(++charIndex) == 'w');
        charIndex += 2;
        while (fen.charAt(charIndex) != ' ')
        {
            switch (fen.charAt(charIndex++)) {
                case 'K' -> white_castling_KS_right = true;
                case 'Q' -> white_castling_QS_right = true;
                case 'k' -> black_castling_KS_right = true;
                case 'q' -> black_castling_QS_right = true;
                default -> {
                }
            }
        }
        if (fen.charAt(++charIndex) != '-') {
            en_passant_target_square = (byte) ((fen.charAt(charIndex)-'a')+(8-(fen.charAt(++charIndex)-'0'))*8);
        }
        else{
            en_passant_target_square = -1;
        }
        hash_key=hash_key_Zobrist();
    }

    public Board getCopyBoard(){
        return new Board(this.white_pawns,this.white_knights,this.white_bishops,this.white_rooks,this.white_queens,this.white_king,this.black_pawns, this.black_knights, this.black_bishops, this.black_rooks, this.black_queens, this.black_king, this.white_castling_KS_right,this.white_castling_QS_right, this.black_castling_KS_right,this.black_castling_QS_right,this.white_turn,this.en_passant_target_square, this.hash_key);
    }

    public long getWhitePiecesAsBitboard(){
        return white_pawns|white_knights|white_bishops|white_rooks|white_queens|white_king;
    }

    public long getBlackPiecesAsBitboard(){
        return black_pawns|black_knights|black_bishops|black_rooks|black_queens|black_king;
    }

    public long getOccupiedSquares(){
        return getWhitePiecesAsBitboard()| getBlackPiecesAsBitboard();
    }

    public long getBlackPiecesPossibleCaptures() {
        return black_pawns|black_knights|black_bishops|black_rooks|black_queens;
    }

    public long getWhitePiecesPossibleCaptures(){
        return white_pawns|white_knights|white_bishops|white_rooks|white_queens;
    }

    /*
            0=none
            1=pawn
            2=knight
            3=bishop
            4=rook
            5=queen
            6=king
    */

    public byte getBlackPieceTypeOnSquare(byte square){
        if(Utils.checkIfSquareIsOne(black_pawns,square))
           return 1;
        if(Utils.checkIfSquareIsOne(black_knights,square))
            return 2;
        if(Utils.checkIfSquareIsOne(black_bishops,square))
            return 3;
        if(Utils.checkIfSquareIsOne(black_rooks,square))
            return 4;
        if(Utils.checkIfSquareIsOne(black_queens,square))
            return 5;
        return 0;
    }

    public byte getWhitePieceTypeOnSquare(byte square){
        if(Utils.checkIfSquareIsOne(white_pawns,square))
            return 1;
        if(Utils.checkIfSquareIsOne(white_knights,square))
            return 2;
        if(Utils.checkIfSquareIsOne(white_bishops,square))
            return 3;
        if(Utils.checkIfSquareIsOne(white_rooks,square))
            return 4;
        if(Utils.checkIfSquareIsOne(white_queens,square))
            return 5;
        return 0;
    }

    ///////////////////////////////////////
    ///////////////////////////////////////
    //////////GETTERS AND SETTERS//////////
    ///////////////////////////////////////
    ///////////////////////////////////////
    public long getWhite_pawns() {
        return white_pawns;
    }

    public void setWhite_pawns(long white_pawns) {this.white_pawns = white_pawns;}

    public long getWhite_knights() {
        return white_knights;
    }

    public void setWhite_knights(long white_knights) {
        this.white_knights = white_knights;
    }

    public long getWhite_bishops() {
        return white_bishops;
    }

    public void setWhite_bishops(long white_bishops) {
        this.white_bishops = white_bishops;
    }

    public long getWhite_rooks() {
        return white_rooks;
    }

    public void setWhite_rooks(long white_rooks) {
        this.white_rooks = white_rooks;
    }

    public long getWhite_queens() {
        return white_queens;
    }

    public void setWhite_queens(long white_queen) {
        this.white_queens = white_queen;
    }

    public long getWhite_king() {
        return white_king;
    }

    public void setWhite_king(long white_king) {
        this.white_king = white_king;
    }

    public long getBlack_pawns() {
        return black_pawns;
    }

    public void setBlack_pawns(long black_pawns) {
        this.black_pawns = black_pawns;
    }

    public long getBlack_knights() {
        return black_knights;
    }

    public void setBlack_knights(long black_knights) {
        this.black_knights = black_knights;
    }

    public long getBlack_bishops() {
        return black_bishops;
    }

    public void setBlack_bishops(long black_bishops) {
        this.black_bishops = black_bishops;
    }

    public long getBlack_rooks() {
        return black_rooks;
    }

    public void setBlack_rooks(long black_rooks) {
        this.black_rooks = black_rooks;
    }

    public long getBlack_queens() {
        return black_queens;
    }

    public void setBlack_queens(long black_queen) {
        this.black_queens = black_queen;
    }

    public long getBlack_king() {
        return black_king;
    }

    public void setBlack_king(long black_king) {
        this.black_king = black_king;
    }

    public boolean isWhite_castling_KS_right() {
        return white_castling_KS_right;
    }

    public void setWhite_castling_KS_right(boolean white_castling_KS_right) {
        this.white_castling_KS_right = white_castling_KS_right;
    }

    public boolean isWhite_castling_QS_right() {
        return white_castling_QS_right;
    }

    public void setWhite_castling_QS_right(boolean white_castling_QS_right) {
        this.white_castling_QS_right = white_castling_QS_right;
    }

    public boolean isBlack_castling_KS_right() {
        return black_castling_KS_right;
    }

    public void setBlack_castling_KS_right(boolean black_castling_KS_right) {
        this.black_castling_KS_right = black_castling_KS_right;
    }

    public boolean isBlack_castling_QS_right() {
        return black_castling_QS_right;
    }

    public void setBlack_castling_QS_right(boolean black_castling_QS_right) {
        this.black_castling_QS_right = black_castling_QS_right;
    }

    public boolean isWhite_turn() {
        return white_turn;
    }

    public void setWhite_turn(boolean white_turn) {
        this.white_turn = white_turn;
    }

    public byte getEn_passant_target_square() {
        return en_passant_target_square;
    }

    public void setEn_passant_target_square(byte en_passant_target_square) {
        this.en_passant_target_square = en_passant_target_square;
    }

    public List<Move> generatePseudolegalMovesForSlidersAndLeapers(byte piece_type, boolean white_turn){
        long my_pieces;
        long enemy_pieces;
        long my_piece_to_move=0;
        if(white_turn) {
            my_pieces = getWhitePiecesAsBitboard();
            enemy_pieces = getBlackPiecesPossibleCaptures();
            List<Move> moves=new ArrayList<>();
            switch (piece_type) {
                case 2 -> my_piece_to_move = white_knights;
                case 3 -> my_piece_to_move = white_bishops;
                case 4 -> my_piece_to_move = white_rooks;
                case 5 -> my_piece_to_move = white_queens;
                case 6 -> my_piece_to_move = white_king;
            }
            while (my_piece_to_move!=0){
                byte source_square= (byte) Long.numberOfTrailingZeros(my_piece_to_move);
                my_piece_to_move=Utils.popBitFromBitboard(my_piece_to_move, source_square);

                long possible_moves=0;
                switch (piece_type) {
                    case 2 -> possible_moves = PCTKnightMoves(source_square) & ~my_pieces;
                    case 3 -> possible_moves = PCTBishopMoves(source_square, getOccupiedSquares()) & ~my_pieces;
                    case 4 -> possible_moves = PCTRookMoves(source_square, getOccupiedSquares()) & ~my_pieces;
                    case 5 -> possible_moves = PCTQueenMoves(source_square, getOccupiedSquares()) & ~my_pieces;
                    case 6 -> possible_moves = PCTKingMoves(source_square) & ~my_pieces;
                }

                while (possible_moves!=0){
                    byte target_square= (byte) Long.numberOfTrailingZeros(possible_moves);
                    possible_moves= Utils.popBitFromBitboard(possible_moves, target_square);

                    //if true that's a capture
                    if(Utils.checkIfSquareIsOne(enemy_pieces,target_square)){
                        byte black_piece_captured=getBlackPieceTypeOnSquare(target_square);
                        moves.add(new Move(target_square,source_square, piece_type, black_piece_captured, (byte)0, false, true, false, false, false));

                    }

                    //normal move
                    else{
                        moves.add(new Move(target_square,source_square, piece_type, (byte)0, (byte)0, false, false, false, false, false));
                    }
                }
            }

            return moves;
        }
        else{
            my_pieces = getBlackPiecesAsBitboard();
            enemy_pieces = getWhitePiecesPossibleCaptures();
            List<Move> moves=new ArrayList<>();
            switch (piece_type) {
                case 2 -> my_piece_to_move = black_knights;
                case 3 -> my_piece_to_move = black_bishops;
                case 4 -> my_piece_to_move = black_rooks;
                case 5 -> my_piece_to_move = black_queens;
                case 6 -> my_piece_to_move = black_king;
            }
            while (my_piece_to_move!=0){
                byte source_square= (byte) Long.numberOfTrailingZeros(my_piece_to_move);
                my_piece_to_move=Utils.popBitFromBitboard(my_piece_to_move, source_square);

                long possible_moves=0;
                switch (piece_type) {
                    case 2 -> possible_moves = PCTKnightMoves(source_square) & ~my_pieces;
                    case 3 -> possible_moves = PCTBishopMoves(source_square, getOccupiedSquares()) & ~my_pieces;
                    case 4 -> possible_moves = PCTRookMoves(source_square, getOccupiedSquares()) & ~my_pieces;
                    case 5 -> possible_moves = PCTQueenMoves(source_square, getOccupiedSquares()) & ~my_pieces;
                    case 6 -> possible_moves = PCTKingMoves(source_square) & ~my_pieces;
                }

                while (possible_moves!=0){
                    byte target_square= (byte) Long.numberOfTrailingZeros(possible_moves);
                    possible_moves= Utils.popBitFromBitboard(possible_moves, target_square);

                    //if true that's a capture
                    if(Utils.checkIfSquareIsOne(enemy_pieces,target_square)){
                        byte white_piece_captured=getWhitePieceTypeOnSquare(target_square);
                        moves.add(new Move(target_square,source_square, piece_type, white_piece_captured, (byte)0, false, true, false, false, false));

                    }

                    //normal move
                    else{
                        moves.add(new Move(target_square,source_square, piece_type, (byte)0, (byte)0, false, false, false, false, false));
                    }
                }
            }

            return moves;
        }
    }

    public boolean isWhiteKingInCheck(long black_attacks){
        return (white_king & black_attacks)==white_king;
    }

    public boolean isBlackKingInCheck(long white_attacks){
        return (black_king & white_attacks)==black_king;
    }

    public List<Move> generateLegalCastlingMovesWhite(){
        List<Move> moves=new ArrayList<>();
        long black_attacks;
        boolean white_king_in_check;
        long occupied_squares=getOccupiedSquares();

        if(white_castling_KS_right) {
            if ((occupied_squares & Squares_FG_1) == 0) {
                black_attacks = getBlackAttacksAsBitboard();
                white_king_in_check = isWhiteKingInCheck(black_attacks);
                if ((black_attacks & Squares_FG_1) == 0 && !white_king_in_check) {
                    moves.add(new Move((byte) 62, (byte) 60, (byte) 6, (byte) 0, (byte) 0,true, false,false,false,false));
                }
            }
        }

        if(white_castling_QS_right) {
            if ((occupied_squares & Squares_BCD_1) == 0) {
                black_attacks = getBlackAttacksAsBitboard();
                white_king_in_check = isWhiteKingInCheck(black_attacks);
                if ((black_attacks & Squares_CD_1) == 0 && !white_king_in_check) {
                    moves.add(new Move((byte) 58, (byte) 60, (byte) 6, (byte) 0, (byte) 0,true, false,false,false,false));
                }
            }
        }
        return moves;
    }

    public List<Move> generateLegalCastlingMovesBlack(){
        List<Move> moves=new ArrayList<>();
        long white_attacks;
        boolean black_king_in_check;
        long occupied_squares=getOccupiedSquares();

        if(black_castling_KS_right) {
            if ((occupied_squares & Squares_FG_8) == 0) {
                white_attacks = getWhiteAttacksAsBitboard();
                black_king_in_check = isBlackKingInCheck(white_attacks);
                if ((white_attacks & Squares_FG_8) == 0 && !black_king_in_check) {
                    moves.add(new Move((byte) 6, (byte) 4, (byte) 6, (byte) 0, (byte) 0,true, false,false,false,false));
                }
            }
        }

        if(black_castling_QS_right) {
            if ((occupied_squares & Squares_BCD_8) == 0) {
                white_attacks = getWhiteAttacksAsBitboard();
                black_king_in_check = isBlackKingInCheck(white_attacks);

                if ((white_attacks & Squares_CD_8) == 0 && !black_king_in_check) {
                    moves.add(new Move((byte) 2, (byte) 4, (byte) 6, (byte) 0, (byte) 0,true, false,false,false,false));
                }
            }
        }
        return moves;
    }

    public long getBlackAttacksAsBitboard() {
        long black_attacks=BlackPawnMoves.PCTBlackPawnsCaptureLeft(black_pawns) | BlackPawnMoves.PCTBlackPawnsCaptureRight(black_pawns);
        long my_pieces=getBlackPiecesAsBitboard();
        long my_piece_to_move=0;
        for(byte piece=2;piece<7;piece++){
            switch (piece) {
                case 2 -> my_piece_to_move = black_knights;
                case 3 -> my_piece_to_move = black_bishops;
                case 4 -> my_piece_to_move = black_rooks;
                case 5 -> my_piece_to_move = black_queens;
                case 6 -> my_piece_to_move = black_king;
            }
            while (my_piece_to_move!=0) {
                byte source_square = (byte) Long.numberOfTrailingZeros(my_piece_to_move);
                my_piece_to_move = Utils.popBitFromBitboard(my_piece_to_move, source_square);

                switch (piece) {
                    case 2 -> black_attacks|= PCTKnightMoves(source_square) & ~my_pieces;
                    case 3 -> black_attacks|= PCTBishopMoves(source_square, getOccupiedSquares()) & ~my_pieces;
                    case 4 -> black_attacks|= PCTRookMoves(source_square, getOccupiedSquares()) & ~my_pieces;
                    case 5 -> black_attacks|= PCTQueenMoves(source_square, getOccupiedSquares()) & ~my_pieces;
                    case 6 -> black_attacks|= PCTKingMoves(source_square) & ~my_pieces;
                }
            }

        }
        return black_attacks;
    }

    public long getWhiteAttacksAsBitboard() {
        long white_attacks=WhitePawnMoves.PCTWhitePawnsCaptureLeft(white_pawns) | WhitePawnMoves.PCTWhitePawnsCaptureRight(white_pawns);
        long my_pieces=getWhitePiecesAsBitboard();
        long my_piece_to_move=0;
        for(byte piece=2;piece<7;piece++){
            switch (piece) {
                case 2 -> my_piece_to_move = white_knights;
                case 3 -> my_piece_to_move = white_bishops;
                case 4 -> my_piece_to_move = white_rooks;
                case 5 -> my_piece_to_move = white_queens;
                case 6 -> my_piece_to_move = white_king;
            }
            while (my_piece_to_move!=0) {
                byte source_square = (byte) Long.numberOfTrailingZeros(my_piece_to_move);
                my_piece_to_move = Utils.popBitFromBitboard(my_piece_to_move, source_square);

                switch (piece) {
                    case 2 -> white_attacks|= PCTKnightMoves(source_square) & ~my_pieces;
                    case 3 -> white_attacks|= PCTBishopMoves(source_square, getOccupiedSquares()) & ~my_pieces;
                    case 4 -> white_attacks|= PCTRookMoves(source_square, getOccupiedSquares()) & ~my_pieces;
                    case 5 -> white_attacks|= PCTQueenMoves(source_square, getOccupiedSquares()) & ~my_pieces;
                    case 6 -> white_attacks|= PCTKingMoves(source_square) & ~my_pieces;
                }
            }

        }
        return white_attacks;

    }

    public List<Move> generatePseudolegalMovesForWhite(){
        List<Move> moves=new ArrayList<>();
        for(byte piece=2;piece<7;piece++){
            moves.addAll(generatePseudolegalMovesForSlidersAndLeapers(piece,true));
        }
        moves.addAll(WhitePawnMoves.ListWhitePawnsPseudolegalMoves(this));
        return moves;
    }

    public List<Move> generatePseudolegalMovesForBlack(){
        List<Move> moves=new ArrayList<>();
        for(byte piece=2;piece<7;piece++){
            moves.addAll(generatePseudolegalMovesForSlidersAndLeapers(piece,false));
        }
        moves.addAll(BlackPawnMoves.ListBlackPawnsPseudolegalMoves(this));
        return moves;
    }

    public List<Move> generateLegalMovesForWhite(){
        List<Move> legal_moves=generateLegalCastlingMovesWhite();
        List<Move> pseudolegal_moves=generatePseudolegalMovesForWhite();
        for(Move move:pseudolegal_moves){
            //make pseudolegal move
            makePseudolegalMoveWhite(move);

            //check if the king is in check
            //if the king is not in check, then this is a legal move
            long black_attacks=getBlackAttacksAsBitboard();
            if(!isWhiteKingInCheck(black_attacks))
                legal_moves.add(move);

            //undo move
            undoPseudolegalMoveWhite(move);
        }
        return legal_moves;
    }

    public List<Move> generateLegalMovesForBlack(){
        List<Move> legal_moves=generateLegalCastlingMovesBlack();
        List<Move> pseudolegal_moves=generatePseudolegalMovesForBlack();
        for(Move move:pseudolegal_moves){
            //make pseudolegal move
            makePseudolegalMoveBlack(move);

            //check if the king is in check
            //if the king is not in check, then this is a legal move
            long white_attacks=getWhiteAttacksAsBitboard();
            if(!isBlackKingInCheck(white_attacks))
                legal_moves.add(move);

            //undo move
            undoPseudolegalMoveBlack(move);
        }
        return legal_moves;
    }

    public void makePseudolegalMoveWhite(Move move){
        switch (move.piece_moved) {
            case 1 -> {
                if(move.promotion_flag){
                    white_pawns=Utils.popBitFromBitboard(white_pawns,move.source_square);
                    switch (move.promotion_piece){
                        case 2 -> white_knights|= Utils.getBitboardForSquare(move.target_square);
                        case 3 -> white_bishops|= Utils.getBitboardForSquare(move.target_square);
                        case 4 -> white_rooks|= Utils.getBitboardForSquare(move.target_square);
                        case 5 -> white_queens|= Utils.getBitboardForSquare(move.target_square);
                    }
                }
                else
                    white_pawns=Utils.popBitFromBitboard(white_pawns,move.source_square) | Utils.getBitboardForSquare(move.target_square);
            }
            case 2 -> white_knights=Utils.popBitFromBitboard(white_knights,move.source_square) | Utils.getBitboardForSquare(move.target_square);
            case 3 -> white_bishops=Utils.popBitFromBitboard(white_bishops,move.source_square) | Utils.getBitboardForSquare(move.target_square);
            case 4 -> white_rooks=Utils.popBitFromBitboard(white_rooks,move.source_square) | Utils.getBitboardForSquare(move.target_square);
            case 5 -> white_queens=Utils.popBitFromBitboard(white_queens,move.source_square) | Utils.getBitboardForSquare(move.target_square);
            case 6 -> white_king=Utils.popBitFromBitboard(white_king,move.source_square) | Utils.getBitboardForSquare(move.target_square);
        }

        if(move.capture_flag){
            switch (move.piece_captured) {
                case 1 -> {
                    if(move.en_passant_flag)
                        black_pawns = Utils.popBitFromBitboard(black_pawns, (byte) (move.target_square+8));
                    else
                        black_pawns = Utils.popBitFromBitboard(black_pawns, move.target_square);
                }
                case 2 -> black_knights = Utils.popBitFromBitboard(black_knights, move.target_square);
                case 3 -> black_bishops = Utils.popBitFromBitboard(black_bishops, move.target_square);
                case 4 -> black_rooks = Utils.popBitFromBitboard(black_rooks, move.target_square);
                case 5 -> black_queens = Utils.popBitFromBitboard(black_queens, move.target_square);
            }

        }

    }

    public void undoPseudolegalMoveWhite(Move move){
        switch (move.piece_moved) {
            case 1 -> {
                if(move.promotion_flag){
                    white_pawns|=Utils.getBitboardForSquare(move.source_square);
                    switch (move.promotion_piece){
                        case 2 -> white_knights=Utils.popBitFromBitboard(white_knights,move.target_square);
                        case 3 -> white_bishops=Utils.popBitFromBitboard(white_bishops,move.target_square);
                        case 4 -> white_rooks=Utils.popBitFromBitboard(white_rooks,move.target_square);
                        case 5 -> white_queens=Utils.popBitFromBitboard(white_queens,move.target_square);
                    }
                }
                else
                    white_pawns=Utils.popBitFromBitboard(white_pawns,move.target_square) | Utils.getBitboardForSquare(move.source_square);
            }
            case 2 -> white_knights=Utils.popBitFromBitboard(white_knights,move.target_square) | Utils.getBitboardForSquare(move.source_square);
            case 3 -> white_bishops=Utils.popBitFromBitboard(white_bishops,move.target_square) | Utils.getBitboardForSquare(move.source_square);
            case 4 -> white_rooks=Utils.popBitFromBitboard(white_rooks,move.target_square) | Utils.getBitboardForSquare(move.source_square);
            case 5 -> white_queens=Utils.popBitFromBitboard(white_queens,move.target_square) | Utils.getBitboardForSquare(move.source_square);
            case 6 -> white_king=Utils.popBitFromBitboard(white_king,move.target_square) | Utils.getBitboardForSquare(move.source_square);
        }

        if(move.capture_flag){
            switch (move.piece_captured) {
                case 1 -> {
                    if(move.en_passant_flag)
                        black_pawns  |= Utils.getBitboardForSquare((byte) (move.target_square+8));
                    else
                        black_pawns |= Utils.getBitboardForSquare(move.target_square);
                }
                case 2 -> black_knights |= Utils.getBitboardForSquare(move.target_square);
                case 3 -> black_bishops |= Utils.getBitboardForSquare(move.target_square);
                case 4 -> black_rooks |= Utils.getBitboardForSquare(move.target_square);
                case 5 -> black_queens |= Utils.getBitboardForSquare(move.target_square);
            }
        }
    }

    public void makePseudolegalMoveBlack(Move move){
        switch (move.piece_moved) {
            case 1 -> {
                if(move.promotion_flag){
                    black_pawns=Utils.popBitFromBitboard(black_pawns,move.source_square);
                    switch (move.promotion_piece){
                        case 2 -> black_knights|= Utils.getBitboardForSquare(move.target_square);
                        case 3 -> black_bishops|= Utils.getBitboardForSquare(move.target_square);
                        case 4 -> black_rooks|= Utils.getBitboardForSquare(move.target_square);
                        case 5 -> black_queens|= Utils.getBitboardForSquare(move.target_square);
                    }
                }
                else
                    black_pawns=Utils.popBitFromBitboard(black_pawns,move.source_square) | Utils.getBitboardForSquare(move.target_square);
            }
            case 2 -> black_knights=Utils.popBitFromBitboard(black_knights,move.source_square) | Utils.getBitboardForSquare(move.target_square);
            case 3 -> black_bishops=Utils.popBitFromBitboard(black_bishops,move.source_square) | Utils.getBitboardForSquare(move.target_square);
            case 4 -> black_rooks=Utils.popBitFromBitboard(black_rooks,move.source_square) | Utils.getBitboardForSquare(move.target_square);
            case 5 -> black_queens=Utils.popBitFromBitboard(black_queens,move.source_square) | Utils.getBitboardForSquare(move.target_square);
            case 6 -> black_king=Utils.popBitFromBitboard(black_king,move.source_square) | Utils.getBitboardForSquare(move.target_square);
        }

        if(move.capture_flag){
            switch (move.piece_captured) {
                case 1 -> {
                    if(move.en_passant_flag)
                        white_pawns = Utils.popBitFromBitboard(white_pawns, (byte) (move.target_square-8));
                    else
                        white_pawns = Utils.popBitFromBitboard(white_pawns, move.target_square);
                }
                case 2 -> white_knights = Utils.popBitFromBitboard(white_knights, move.target_square);
                case 3 -> white_bishops = Utils.popBitFromBitboard(white_bishops, move.target_square);
                case 4 -> white_rooks = Utils.popBitFromBitboard(white_rooks, move.target_square);
                case 5 -> white_queens = Utils.popBitFromBitboard(white_queens, move.target_square);
            }
        }
    }

    public void undoPseudolegalMoveBlack(Move move){
        switch (move.piece_moved) {
            case 1 -> {
                if(move.promotion_flag){
                    black_pawns|=Utils.getBitboardForSquare(move.source_square);
                    switch (move.promotion_piece){
                        case 2 -> black_knights=Utils.popBitFromBitboard(black_knights,move.target_square);
                        case 3 -> black_bishops=Utils.popBitFromBitboard(black_bishops,move.target_square);
                        case 4 -> black_rooks=Utils.popBitFromBitboard(black_rooks,move.target_square);
                        case 5 -> black_queens=Utils.popBitFromBitboard(black_queens,move.target_square);
                    }
                }
                else
                    black_pawns=Utils.popBitFromBitboard(black_pawns,move.target_square) | Utils.getBitboardForSquare(move.source_square);
            }
            case 2 -> black_knights=Utils.popBitFromBitboard(black_knights,move.target_square) | Utils.getBitboardForSquare(move.source_square);
            case 3 -> black_bishops=Utils.popBitFromBitboard(black_bishops,move.target_square) | Utils.getBitboardForSquare(move.source_square);
            case 4 -> black_rooks=Utils.popBitFromBitboard(black_rooks,move.target_square) | Utils.getBitboardForSquare(move.source_square);
            case 5 -> black_queens=Utils.popBitFromBitboard(black_queens,move.target_square) | Utils.getBitboardForSquare(move.source_square);
            case 6 -> black_king=Utils.popBitFromBitboard(black_king,move.target_square) | Utils.getBitboardForSquare(move.source_square);
        }

        if(move.capture_flag){
            switch (move.piece_captured) {
                case 1 -> {
                    if(move.en_passant_flag)
                        white_pawns  |= Utils.getBitboardForSquare((byte) (move.target_square-8));
                    else
                        white_pawns |= Utils.getBitboardForSquare(move.target_square);
                }
                case 2 -> white_knights |= Utils.getBitboardForSquare(move.target_square);
                case 3 -> white_bishops |= Utils.getBitboardForSquare(move.target_square);
                case 4 -> white_rooks |= Utils.getBitboardForSquare(move.target_square);
                case 5 -> white_queens |= Utils.getBitboardForSquare(move.target_square);
            }
        }
    }

    public void makeMove(Move move){
        if(white_turn){
            if(!move.castling_flag) {
                makePseudolegalMoveWhite(move);
            }
            white_turn=false;
            hash_key^=EvaluationUtils.side_key;

            if(!move.promotion_flag) {
                hash_key ^= EvaluationUtils.white_pieces_keys[move.piece_moved][move.source_square];
                hash_key ^= EvaluationUtils.white_pieces_keys[move.piece_moved][move.target_square];
            }
            else{
                hash_key ^= EvaluationUtils.white_pieces_keys[move.piece_moved][move.source_square];
                hash_key ^= EvaluationUtils.white_pieces_keys[move.promotion_piece][move.target_square];
            }

            if(move.capture_flag)
                if(move.en_passant_flag)
                    hash_key ^= EvaluationUtils.black_pieces_keys[move.piece_captured][move.target_square+8];
                else
                    hash_key ^= EvaluationUtils.black_pieces_keys[move.piece_captured][move.target_square];
            if(en_passant_target_square!=-1)
                hash_key^=EvaluationUtils.enpassant_keys[en_passant_target_square];

            //update en_passant if move was a pawn double push move
            if(move.double_push_flag) {
                en_passant_target_square = (byte) (move.target_square + 8);
                hash_key^=EvaluationUtils.enpassant_keys[move.target_square+8];
            }
            else{
                en_passant_target_square=-1;

                //if the piece moved was the king update castling rights to false
                if(move.piece_moved==6){
                    if(white_castling_KS_right){
                        white_castling_KS_right=false;
                        hash_key^=EvaluationUtils.castling_keys[0];
                    }
                    if(white_castling_QS_right) {
                        white_castling_QS_right = false;
                        hash_key ^= EvaluationUtils.castling_keys[1];
                    }
                }

                //if the piece moves was a rook update castling rights to false
                if (move.piece_moved==4){
                    if(move.source_square==63) {
                        if(white_castling_KS_right){
                            white_castling_KS_right=false;
                            hash_key^=EvaluationUtils.castling_keys[0];
                        }
                    }
                    else
                        if(move.source_square==56) {
                            if(white_castling_QS_right) {
                                white_castling_QS_right = false;
                                hash_key ^= EvaluationUtils.castling_keys[1];
                            }
                        }
                }

                //if the opponent's rook was captured update castling rights to false
                if(move.piece_captured==4) {
                    if (move.target_square == 7) {
                        if(black_castling_KS_right){
                            black_castling_KS_right = false;
                            hash_key^=EvaluationUtils.castling_keys[2];
                        }
                    }
                    else if (move.target_square == 0) {
                        if(black_castling_QS_right) {
                            black_castling_QS_right = false;
                            hash_key ^= EvaluationUtils.castling_keys[3];
                        }
                    }
                }

                //update pieces after castling
                if(move.castling_flag){
                    if(move.target_square==62){
                        white_rooks=Utils.popBitFromBitboard(white_rooks, (byte) 63) | Utils.getBitboardForSquare((byte) 61);
                        white_king=white_king<<2;
                        hash_key^=EvaluationUtils.white_pieces_keys[4][63];
                        hash_key^=EvaluationUtils.white_pieces_keys[4][61];
                    }
                    if(move.target_square==58){
                        white_rooks=Utils.popBitFromBitboard(white_rooks, (byte) 56) | Utils.getBitboardForSquare((byte) 59);
                        white_king=white_king>>>2;
                        hash_key^=EvaluationUtils.white_pieces_keys[4][56];
                        hash_key^=EvaluationUtils.white_pieces_keys[4][59];
                    }
                }

            }
        }
        else{
            if(!move.castling_flag) {
                makePseudolegalMoveBlack(move);
            }
            white_turn=true;
            hash_key^=EvaluationUtils.side_key;

            if(!move.promotion_flag) {
                hash_key ^= EvaluationUtils.black_pieces_keys[move.piece_moved][move.source_square];
                hash_key ^= EvaluationUtils.black_pieces_keys[move.piece_moved][move.target_square];
            }
            else{
                hash_key ^= EvaluationUtils.black_pieces_keys[move.piece_moved][move.source_square];
                hash_key ^= EvaluationUtils.black_pieces_keys[move.promotion_piece][move.target_square];
            }

            if(move.capture_flag)
                if(move.en_passant_flag)
                    hash_key ^= EvaluationUtils.white_pieces_keys[move.piece_captured][move.target_square-8];
                else
                    hash_key ^= EvaluationUtils.white_pieces_keys[move.piece_captured][move.target_square];
            if(en_passant_target_square!=-1)
                hash_key^=EvaluationUtils.enpassant_keys[en_passant_target_square];


            //update en_passant if move was a pawn double push move
            if(move.double_push_flag) {
                en_passant_target_square = (byte) (move.target_square - 8);
                hash_key^=EvaluationUtils.enpassant_keys[move.target_square-8];
            }
            else{
                en_passant_target_square=-1;

                //if the piece moved was the king update castling rights to false
                if(move.piece_moved==6){
                    if(black_castling_KS_right){
                        black_castling_KS_right=false;
                        hash_key^=EvaluationUtils.castling_keys[2];
                    }
                    if(black_castling_QS_right) {
                        black_castling_QS_right = false;
                        hash_key ^= EvaluationUtils.castling_keys[3];
                    }
                }

                //if the piece moves was a rook update castling rights to false
                if (move.piece_moved==4){
                    if(move.source_square==7){
                        if(black_castling_KS_right){
                            black_castling_KS_right=false;
                            hash_key^=EvaluationUtils.castling_keys[2];
                        }
                    }
                    else
                        if(move.source_square==0){
                            if(black_castling_QS_right) {
                                black_castling_QS_right = false;
                                hash_key ^= EvaluationUtils.castling_keys[3];
                            }
                        }
                }

                //if the opponent's rook was captured update castling rights to false
                if(move.piece_captured==4) {
                    if (move.target_square == 63) {
                        if(white_castling_KS_right) {
                            white_castling_KS_right = false;
                            hash_key ^= EvaluationUtils.castling_keys[0];
                        }
                    }
                    else if (move.target_square == 56){
                        if(white_castling_QS_right) {
                            white_castling_QS_right = false;
                            hash_key ^= EvaluationUtils.castling_keys[1];
                        }
                    }
                }

                //update pieces after castling
                if(move.castling_flag){
                    if(move.target_square==6){
                        black_rooks=Utils.popBitFromBitboard(black_rooks, (byte) 7) | Utils.getBitboardForSquare((byte) 5);
                        black_king=black_king<<2;
                        hash_key^=EvaluationUtils.black_pieces_keys[4][7];
                        hash_key^=EvaluationUtils.black_pieces_keys[4][5];
                    }
                    if(move.target_square==2){
                        black_rooks=Utils.popBitFromBitboard(black_rooks, (byte) 0) | Utils.getBitboardForSquare((byte) 3);
                        black_king=black_king>>>2;
                        hash_key^=EvaluationUtils.black_pieces_keys[4][0];
                        hash_key^=EvaluationUtils.black_pieces_keys[4][3];
                    }
                }
            }
        }

        //Zobrist hash key incremental update check
//        long hash_from_scratch=hash_key_Zobrist();
//        if(hash_key!=hash_from_scratch){
//            System.out.println("Move: "+move.toUCI());
//            Utils.printBoard(this);
//            System.out.println("hash key should be "+hash_from_scratch);
//
//            Scanner in = new Scanner(System.in);
//            String s = in.nextLine();
//        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return white_pawns == board.white_pawns && white_knights == board.white_knights && white_bishops == board.white_bishops && white_rooks == board.white_rooks && white_queens == board.white_queens && white_king == board.white_king && black_pawns == board.black_pawns && black_knights == board.black_knights && black_bishops == board.black_bishops && black_rooks == board.black_rooks && black_queens == board.black_queens && black_king == board.black_king && white_castling_KS_right == board.white_castling_KS_right && white_castling_QS_right == board.white_castling_QS_right && black_castling_KS_right == board.black_castling_KS_right && black_castling_QS_right == board.black_castling_QS_right && white_turn == board.white_turn && en_passant_target_square == board.en_passant_target_square;
    }

    public long hash_key_Zobrist() {
        long hash_key=0;
        long white_piece_bitboard=0;
        long black_piece_bitboard=0;

        for(int piece=1;piece<7;piece++){
            switch (piece) {
                case 1 -> {
                    white_piece_bitboard = white_pawns;
                    black_piece_bitboard = black_pawns;
                }
                case 2 -> {
                    white_piece_bitboard = white_knights;
                    black_piece_bitboard = black_knights;
                }
                case 3 -> {
                    white_piece_bitboard = white_bishops;
                    black_piece_bitboard = black_bishops;
                }
                case 4 -> {
                    white_piece_bitboard = white_rooks;
                    black_piece_bitboard = black_rooks;
                }
                case 5 -> {
                    white_piece_bitboard = white_queens;
                    black_piece_bitboard = black_queens;
                }
                case 6 -> {
                    white_piece_bitboard = white_king;
                    black_piece_bitboard = black_king;
                }
            }

            while (white_piece_bitboard!=0) {
                int source_square = Long.numberOfTrailingZeros(white_piece_bitboard);
                white_piece_bitboard = Utils.popBitFromBitboard(white_piece_bitboard, (byte) source_square);
                hash_key^=EvaluationUtils.white_pieces_keys[piece][source_square];
            }

            while (black_piece_bitboard!=0) {
                int source_square = Long.numberOfTrailingZeros(black_piece_bitboard);
                black_piece_bitboard = Utils.popBitFromBitboard(black_piece_bitboard, (byte) source_square);
                hash_key^=EvaluationUtils.black_pieces_keys[piece][source_square];
            }
        }

        if(en_passant_target_square!=-1)
            hash_key^=EvaluationUtils.enpassant_keys[en_passant_target_square];

        if(white_castling_KS_right)
            hash_key^=EvaluationUtils.castling_keys[0];
        if(white_castling_QS_right)
            hash_key^=EvaluationUtils.castling_keys[1];
        if(black_castling_KS_right)
            hash_key^=EvaluationUtils.castling_keys[2];
        if(black_castling_QS_right)
            hash_key^=EvaluationUtils.castling_keys[3];

        if(!white_turn)
            hash_key^=EvaluationUtils.side_key;

        return hash_key;
    }
}
