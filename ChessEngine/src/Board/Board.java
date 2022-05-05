package Board;

import Moves.BlackPawnMoves;
import Moves.Move;
import Moves.WhitePawnMoves;
import Utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static Moves.BishopMoves.PCTBishopMoves;
import static Moves.KingMoves.PCTKingMoves;
import static Moves.KnightMoves.PCTKnightMoves;
import static Moves.QueenMoves.PCTQueenMoves;
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

        en_passant_target_square=0;
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

    public void setWhite_pawns(long white_pawns) {
        this.white_pawns = white_pawns;
    }

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
                case 2 -> {
                    my_piece_to_move = white_knights;
                }
                case 3 -> {
                    my_piece_to_move = white_bishops;
                }
                case 4 -> {
                    my_piece_to_move = white_rooks;
                }
                case 5 -> {
                    my_piece_to_move = white_queens;
                }
                case 6 -> {
                    my_piece_to_move = white_king;
                }
            }
            while (my_piece_to_move!=0){
                byte source_square= (byte) Long.numberOfTrailingZeros(my_piece_to_move);
                my_piece_to_move=Utils.popBitFromBitboard(my_piece_to_move, source_square);

                long possible_moves=0;
                switch (piece_type) {
                    case 2 -> {
                        possible_moves = PCTKnightMoves(source_square) & ~my_pieces;
                    }
                    case 3 -> {
                        possible_moves = PCTBishopMoves(source_square, getOccupiedSquares()) & ~my_pieces;
                    }
                    case 4 -> {
                        possible_moves = PCTRookMoves(source_square, getOccupiedSquares()) & ~my_pieces;
                    }
                    case 5 -> {
                        possible_moves = PCTQueenMoves(source_square, getOccupiedSquares()) & ~my_pieces;
                    }
                    case 6 -> {
                        possible_moves = PCTKingMoves(source_square) & ~my_pieces;
                    }
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
                case 2 -> {
                    my_piece_to_move = black_knights;
                }
                case 3 -> {
                    my_piece_to_move = black_bishops;
                }
                case 4 -> {
                    my_piece_to_move = black_rooks;
                }
                case 5 -> {
                    my_piece_to_move = black_queens;
                }
                case 6 -> {
                    my_piece_to_move = black_king;
                }
            }
            while (my_piece_to_move!=0){
                byte source_square= (byte) Long.numberOfTrailingZeros(my_piece_to_move);
                my_piece_to_move=Utils.popBitFromBitboard(my_piece_to_move, source_square);

                long possible_moves=0;
                switch (piece_type) {
                    case 2 -> {
                        possible_moves = PCTKnightMoves(source_square) & ~my_pieces;
                    }
                    case 3 -> {
                        possible_moves = PCTBishopMoves(source_square, getOccupiedSquares()) & ~my_pieces;
                    }
                    case 4 -> {
                        possible_moves = PCTRookMoves(source_square, getOccupiedSquares()) & ~my_pieces;
                    }
                    case 5 -> {
                        possible_moves = PCTQueenMoves(source_square, getOccupiedSquares()) & ~my_pieces;
                    }
                    case 6 -> {
                        possible_moves = PCTKingMoves(source_square) & ~my_pieces;
                    }
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


}
