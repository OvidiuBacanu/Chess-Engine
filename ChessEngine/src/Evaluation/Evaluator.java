package Evaluation;

import Board.Board;
import Moves.Move;
import Utils.Utils;
import java.util.List;
import java.util.stream.Collectors;

import static Evaluation.EvaluationUtils.*;

public class Evaluator {
    private Board board;
    private int ply;
    private int nodes;

    private Move[] first_killer_moves;
    private Move[] second_killer_moves;
    public int[] pv_length;
    public Move[][] pv_table;

    public boolean follow_pv=true;
    public boolean score_pv=true;

    public boolean stop_flag=false;
    public long start_time=0;
    public long milliseconds=0;

    public static long current_hash_key;
    public TranspositionTable transpositionTable=new TranspositionTable();

    public long[] repetition_table=new long[1000];
    public int repetition_index=0;

    public Evaluator() {
        EvaluationUtils.init_random_keys();
        transpositionTable.init_transposition_table();
    }

    public void setBoard(Board board) {
        this.board = board;
        current_hash_key=this.board.getHash_key();
    }

    public void setNodes(int nodes) {
        this.nodes = nodes;
    }

    public void setRepetition_index(int repetition_index) {
        this.repetition_index = repetition_index;
    }

    public int getNodes() {
        return nodes;
    }

    public void initialize(){
        nodes=0;
        ply=0;
        stop_flag=false;
        start_time=0;
        milliseconds=0;
        first_killer_moves=new Move[max_ply];
        second_killer_moves=new Move[max_ply];
        pv_length=new int[max_ply];
        pv_table=new Move[max_ply][max_ply];
        follow_pv=false;
        score_pv=false;
    }

    public int quiescence(int alpha, int beta){
        if(nodes%2048==0)
            if(start_time!=0)
                if((System.currentTimeMillis() - start_time) >= milliseconds)
                    stop_flag=true;

        nodes++;

        int evaluation=evaluate_position(board.isWhite_turn());
        if(ply>max_ply-1)
            return evaluation;

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

            if(stop_flag)
                return 0;

            if(score>alpha) {
                //PV  node (move)
                alpha = score;

                //fail hard beta cutoff
                if(score>=beta)
                    //node (move) fails high
                    return beta;
            }
        }
        return alpha;
    }

    public int negamax(int alpha, int beta, int depth){
        if(nodes%2048==0)
            if(start_time!=0)
                if((System.currentTimeMillis() - start_time) >= milliseconds)
                    stop_flag=true;

        pv_length[ply]=ply;

        if(ply!=0 && is_repetition())
            return 0;

        int hash_flag=TranspositionTable.hash_flag_alpha;
        boolean is_pv_node=beta-alpha>1;

        if(ply!=0) {
            int score_tt = transpositionTable.read_transposition_table_entry(alpha, beta, depth, ply);
            if (score_tt != TranspositionTable.no_hash_entry && !is_pv_node)
                return score_tt;
        }

        if (depth == 0) {
            return quiescence(alpha, beta);
        }

        if(ply>max_ply-1)
            return evaluate_position(board.isWhite_turn());


        nodes++;
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

        if(follow_pv)
            enable_pv_scoring(moves);

        //sort moves after score
        moves.sort((o1, o2) -> Integer.compare(score_move(o2), score_move(o1)));
        for (Move move : moves) {
            Board copy_board = board.getCopyBoard();

            ply++;
            repetition_table[repetition_index]=current_hash_key;
            repetition_index++;

            board.makeMove(move);
            current_hash_key=board.getHash_key();

            int score=-negamax(-beta, -alpha, depth-1);

            ply--;
            repetition_index--;
            board = copy_board.getCopyBoard();
            current_hash_key=board.getHash_key();

            if(stop_flag)
                return 0;

            if(score>alpha) {
                hash_flag= TranspositionTable.hash_flag_exact;

                //PV  node (move)
                alpha = score;
                //write PV move
                pv_table[ply][ply]=move;

                //copy move from deeper ply
                if (pv_length[ply + 1] - (ply + 1) >= 0)
                    System.arraycopy(pv_table[ply + 1], ply + 1, pv_table[ply], ply + 1, pv_length[ply + 1] - (ply + 1));
                pv_length[ply]=pv_length[ply+1];

                //fail hard beta cutoff
                if(score>=beta) {
                    transpositionTable.write_transposition_table_entry(beta,depth,TranspositionTable.hash_flag_beta,ply);

                    // store killer moves
                    if(!move.capture_flag) {
                        second_killer_moves[ply] = first_killer_moves[ply];
                        first_killer_moves[ply] = move;
                    }
                    //node (move) fails high
                    return beta;
                }
            }
        }
        if(moves.size()==0){
            if(king_in_check)
                return -mate_value+ply;
            else
                return 0;
        }

        transpositionTable.write_transposition_table_entry(alpha,depth,hash_flag,ply);

        //node (move) fails low
        return alpha;
    }

    private void enable_pv_scoring(List<Move> moves) {
        follow_pv=false;
        for(Move move:moves){
            if(pv_table[0][ply]!=null)
                if(pv_table[0][ply].equals(move)){
                    score_pv=true;
                    follow_pv=true;
            }
        }
    }

    public boolean is_repetition(){
        for(int i=0;i<repetition_index;i++){
            if(repetition_table[i]==current_hash_key)
                return true;
        }
        return false;
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
        if(score_pv){
            if(pv_table[0][ply]!=null)
                if(pv_table[0][ply].equals(move)){
                    score_pv=false;
                    return 20000;
                }
        }

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

    public void add_position_to_repetition_table(long hash_key) {
        repetition_table[repetition_index]=hash_key;
        repetition_index++;
    }
}
