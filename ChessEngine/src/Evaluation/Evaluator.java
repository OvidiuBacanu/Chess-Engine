package Evaluation;

import Board.Board;
import Moves.Move;
import Utils.Utils;
import java.util.List;
import java.util.stream.Collectors;

import static Evaluation.EvaluationUtils.*;

public class Evaluator {
    private Board board;
    private Move best_move;
    private int ply=0;
    private int nodes=0;
    private Move best_sofar;

    private final Move[] first_killer_moves=new Move[64];
    private final Move[] second_killer_moves=new Move[64];

    public Evaluator() {}

    public void setBoard(Board board) {
        this.board = board;
    }

    public Move getBest_move() {
        return best_move;
    }

    public int getNodes() {
        return nodes;
    }

    public int quiescence(int alpha, int beta){
        nodes++;

        int evaluation=evaluate_position(board.isWhite_turn());

        // fail-hard beta cutoff
        if (evaluation >= beta)
        {
            // node (move) fails high
            return beta;
        }

        // found a better move
        if (evaluation > alpha)
        {
            // PV node (move)
            alpha = evaluation;
        }

        List<Move> moves;
        if (board.isWhite_turn()) {
            moves = board.generateLegalMovesForWhite()
                    .stream()
                    .filter(m->m.capture_flag)
                    .collect(Collectors.toList());

        } else {
            moves = board.generateLegalMovesForBlack()
                    .stream()
                    .filter(m->m.capture_flag)
                    .collect(Collectors.toList());
        }

        //sort moves after score
        moves.sort((o1, o2) -> Integer.compare(score_move(o2), score_move(o1)));
        for (Move move : moves) {
            Board copy_board = board.getCopyBoard();
            ply++;

            board.makeMove(move);

            int score=-quiescence(-beta, -alpha);

            ply--;
            board = copy_board.getCopyBoard();

            //fail hard beta cutoff
            if(score>=beta)
                //node (move) fails high
                return beta;

            if(score>alpha) {
                //PV  node (move)
                alpha = score;
            }
        }
        return alpha;
    }

    public int negamax(int alpha, int beta, int depth){
        if (depth == 0) {
            return quiescence(alpha, beta);
        }
        nodes++;
        int old_alpha=alpha;
        boolean king_in_check;

        List<Move> moves;
        if (board.isWhite_turn()) {
            moves = board.generateLegalMovesForWhite();
            king_in_check=board.isWhiteKingInCheck(board.getBlackAttacksAsBitboard());
        } else {
            moves = board.generateLegalMovesForBlack();
            king_in_check=board.isBlackKingInCheck(board.getWhiteAttacksAsBitboard());
        }

        if(king_in_check)
            depth++;

        //sort moves after score
        moves.sort((o1, o2) -> Integer.compare(score_move(o2), score_move(o1)));
        for (Move move : moves) {
            Board copy_board = board.getCopyBoard();
            ply++;

            board.makeMove(move);

            int score=-negamax(-beta, -alpha, depth-1);

            ply--;
            board = copy_board.getCopyBoard();


            //fail hard beta cutoff
            if(score>=beta) {
                // store killer moves
                second_killer_moves[ply] = first_killer_moves[ply];
                first_killer_moves[ply] = move;

                //node (move) fails high
                return beta;
            }

            if(score>alpha) {
                //PV  node (move)
                alpha = score;
                if(ply==0)
                    best_sofar=move;
            }
        }
        if(moves.size()==0){
            if(king_in_check)
                return min_int+ply;
            else
                return 0;
        }

        if(old_alpha!=alpha)
            best_move=best_sofar;

        //node (move) fails low
        return alpha;
    }

    public int evaluate_position(boolean is_white_turn){
        int score=0;
        score+=evaluate_material();
        score+=evaluate_piece_on_square();
        if(is_white_turn)
            return score;
        else
            return -score;
    }

    public int evaluate_piece_on_square(){
        int score=0;
        long bitboard_white_piece=0;
        long bitboard_black_piece=0;

        int[] square_table_piece = new int[64];

        for(byte piece=1;piece<7;piece++){
            switch (piece){
                case 1 -> {
                    bitboard_white_piece=board.getWhite_pawns();
                    bitboard_black_piece=board.getBlack_pawns();
                    square_table_piece=pawn_square_table;
                }
                case 2 -> {
                    bitboard_white_piece=board.getWhite_knights();
                    bitboard_black_piece=board.getBlack_knights();
                    square_table_piece=knight_square_table;
                }
                case 3 -> {
                    bitboard_white_piece=board.getWhite_bishops();
                    bitboard_black_piece=board.getBlack_bishops();
                    square_table_piece=bishop_square_table;
                }
                case 4 -> {
                    bitboard_white_piece=board.getWhite_rooks();
                    bitboard_black_piece=board.getBlack_rooks();
                    square_table_piece=rook_square_table;
                }
                case 5 -> {
                    bitboard_white_piece=board.getWhite_queens();
                    bitboard_black_piece=board.getBlack_queens();
                    square_table_piece=queen_square_table;
                }

                case 6 -> {
                    bitboard_white_piece=board.getWhite_king();
                    bitboard_black_piece=board.getBlack_king();
                    square_table_piece=king_square_table;
                }
            }
            
            while (bitboard_white_piece!=0){
                byte square= (byte) Long.numberOfTrailingZeros(bitboard_white_piece);
                bitboard_white_piece= Utils.popBitFromBitboard(bitboard_white_piece, square);
                score+=square_table_piece[square];
            }

            while (bitboard_black_piece!=0){
                byte square= (byte) Long.numberOfTrailingZeros(bitboard_black_piece);
                bitboard_black_piece= Utils.popBitFromBitboard(bitboard_black_piece, square);
                score-=square_table_piece[mirrored_for_black[square]];
            }
        }
        return score;
    }

    public int evaluate_material(){
        int score=0;
        long bitboard_white_piece=0;
        long bitboard_black_piece=0;
        for(byte piece=1;piece<6;piece++){
            switch (piece){
                case 1 -> {
                    bitboard_white_piece=board.getWhite_pawns();
                    bitboard_black_piece=board.getBlack_pawns();
                }
                case 2 -> {
                    bitboard_white_piece=board.getWhite_knights();
                    bitboard_black_piece=board.getBlack_knights();
                }
                case 3 -> {
                    bitboard_white_piece=board.getWhite_bishops();
                    bitboard_black_piece=board.getBlack_bishops();
                }
                case 4 -> {
                    bitboard_white_piece=board.getWhite_rooks();
                    bitboard_black_piece=board.getBlack_rooks();
                }
                case 5 -> {
                    bitboard_white_piece=board.getWhite_queens();
                    bitboard_black_piece=board.getBlack_queens();
                }
            }
            while (bitboard_white_piece!=0){
                byte square= (byte) Long.numberOfTrailingZeros(bitboard_white_piece);
                bitboard_white_piece= Utils.popBitFromBitboard(bitboard_white_piece, square);
                score+=piece_material_score.get(piece);
            }

            while (bitboard_black_piece!=0){
                byte square= (byte) Long.numberOfTrailingZeros(bitboard_black_piece);
                bitboard_black_piece= Utils.popBitFromBitboard(bitboard_black_piece, square);
                score-=piece_material_score.get(piece);
            }
        }
        return score;
    }

    public int score_move(Move move){
        //capture move
        if(move.capture_flag){
            return mvv_lva[move.piece_moved][move.piece_captured]+10000;
        }
        //quiet move
        else{
            // score 1st killer move
            if(first_killer_moves[ply] != null)
                if (first_killer_moves[ply].equals(move))
                    return 9000;

            // score 2nd killer move
            if(second_killer_moves[ply] != null)
                if (second_killer_moves[ply].equals(move))
                    return 8000;
        }
        return 0;
    }
}
