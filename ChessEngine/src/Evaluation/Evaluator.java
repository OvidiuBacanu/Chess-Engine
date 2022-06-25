package Evaluation;

import Board.Board;
import Moves.Move;
import Utils.Utils;
import java.util.List;
import java.util.stream.Collectors;

import static Evaluation.EvaluationUtils.*;
import static Moves.KingMoves.PCTKingMoves;

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

    //SEARCH
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

    //STATIC EVALUATION
    public int evaluate_position(boolean is_white_turn){
        int score=0;
        score+=evaluate_material();
        score+=evaluate_piece_on_square();
        score+=evaluate_pawns_structure();
        score+=evaluate_open_semi_open_files();
        score+=evaluate_mobility();
        score+=evaluate_king_safety();
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

    public int evaluate_pawns_structure(){
        int score=0;
        long white_pawns=board.getWhite_pawns();
        long black_pawns= board.getBlack_pawns();

        for(int file_index=0;file_index<8;file_index++){
            long file=File_Masks_EV[file_index];
            long white_pawns_on_file=white_pawns&file;
            long black_pawns_on_file=black_pawns&file;
            long neighbour_files=File_Masks_EV[file_index];
            if(file_index==0)
                neighbour_files|=File_Masks_EV[file_index+1];
            else if(file_index==7)
                neighbour_files|=File_Masks_EV[file_index-1];
            else
                neighbour_files|=File_Masks_EV[file_index-1] | File_Masks_EV[file_index+1];

            //check if there are any pawns on current file
            if(white_pawns_on_file!=0) {
                byte source_square = (byte) Long.numberOfTrailingZeros(white_pawns_on_file);
                white_pawns_on_file = Utils.popBitFromBitboard(white_pawns_on_file, source_square);

                //if it enters this while loop, there are double_pawns
                while (white_pawns_on_file != 0) {
                    source_square = (byte) Long.numberOfTrailingZeros(white_pawns_on_file);
                    white_pawns_on_file = Utils.popBitFromBitboard(white_pawns_on_file, source_square);
                    score+=double_pawn_penalty;
                }

                //check if the pawns on the current file are isolated
                white_pawns_on_file=white_pawns&file;
                while (white_pawns_on_file != 0) {
                    source_square = (byte) Long.numberOfTrailingZeros(white_pawns_on_file);
                    white_pawns_on_file = Utils.popBitFromBitboard(white_pawns_on_file, source_square);

                    if (file_index == 0) {
                        if ((white_pawns & File_Masks_EV[file_index + 1]) == 0)
                            score += isolated_pawn_penalty;
                    } else if (file_index == 7) {
                        if ((white_pawns & File_Masks_EV[file_index - 1]) == 0)
                            score += isolated_pawn_penalty;
                    } else {
                        if ((white_pawns & File_Masks_EV[file_index + 1]) == 0 && (white_pawns & File_Masks_EV[file_index - 1]) == 0)
                            score += isolated_pawn_penalty;
                    }
                }

                //check for passed pawns on the current file
                white_pawns_on_file=white_pawns&file;
                while (white_pawns_on_file != 0) {
                    source_square = (byte) Long.numberOfTrailingZeros(white_pawns_on_file);
                    white_pawns_on_file = Utils.popBitFromBitboard(white_pawns_on_file, source_square);

                    int rank_index=8-source_square/8;
                    long ranks=0;
                    for(int rank=rank_index;rank<8;rank++)
                        ranks|=Rank_Masks_EV[rank];

                    long passed_pawn_mask=ranks&neighbour_files;

                    if((passed_pawn_mask&black_pawns)==0)
                        score+=get_passed_pawn_bonus_for_rank(Utils.getBitboardForSquare(source_square),true);
                }
            }

            //check if there are any pawns on current file
            if(black_pawns_on_file!=0) {
                byte source_square = (byte) Long.numberOfTrailingZeros(black_pawns_on_file);
                black_pawns_on_file = Utils.popBitFromBitboard(black_pawns_on_file, source_square);

                //if it enters this while loop, there are double_pawns
                while (black_pawns_on_file != 0) {
                    source_square = (byte) Long.numberOfTrailingZeros(black_pawns_on_file);
                    black_pawns_on_file = Utils.popBitFromBitboard(black_pawns_on_file, source_square);
                    score-=double_pawn_penalty;
                }

                //check if the pawns on the current file are isolated
                black_pawns_on_file=black_pawns&file;
                while (black_pawns_on_file != 0) {
                    source_square = (byte) Long.numberOfTrailingZeros(black_pawns_on_file);
                    black_pawns_on_file = Utils.popBitFromBitboard(black_pawns_on_file, source_square);
                    if (file_index == 0) {
                        if ((black_pawns & File_Masks_EV[file_index + 1]) == 0)
                            score -= isolated_pawn_penalty;
                    } else if (file_index == 7) {
                        if ((black_pawns & File_Masks_EV[file_index - 1]) == 0)
                            score -= isolated_pawn_penalty;
                    } else {
                        if ((black_pawns & File_Masks_EV[file_index + 1]) == 0 && (black_pawns & File_Masks_EV[file_index - 1]) == 0)
                            score -= isolated_pawn_penalty;
                    }
                }

                //check for passed pawns on the current file
                black_pawns_on_file=black_pawns&file;
                while (black_pawns_on_file != 0) {
                    source_square = (byte) Long.numberOfTrailingZeros(black_pawns_on_file);
                    black_pawns_on_file = Utils.popBitFromBitboard(black_pawns_on_file, source_square);

                    int rank_index=8-source_square/8-2;
                    long ranks=0;
                    for(int rank=rank_index;rank>=0;rank--)
                        ranks|=Rank_Masks_EV[rank];

                    long passed_pawn_mask=ranks&neighbour_files;

                    if((passed_pawn_mask&white_pawns)==0)
                        score-=get_passed_pawn_bonus_for_rank(Utils.getBitboardForSquare(source_square),false);
                }
            }
        }
        return score;
    }

    public int get_passed_pawn_bonus_for_rank(long pawn, boolean for_white){
        for(int rank_index=0;rank_index<8;rank_index++)
            if((pawn&Rank_Masks_EV[rank_index])!=0) {
                if (for_white)
                    return passed_pawn_bonus[rank_index];
                else
                    return passed_pawn_bonus[7-rank_index];
            }
        return 0;
    }

    public int evaluate_open_semi_open_files(){
        int score=0;
        long white_rooks=board.getWhite_rooks();
        long black_rooks=board.getBlack_rooks();

        long white_pawns=board.getWhite_pawns();
        long black_pawns=board.getBlack_pawns();

        long white_king=board.getWhite_king();
        long black_king=board.getBlack_king();

        for(int file_index=0;file_index<8;file_index++){
            long file=File_Masks_EV[file_index];

            //check if rooks are on current file
            if((white_rooks&file)!=0){
                long white_rooks_on_file=white_rooks&file;
                while (white_rooks_on_file!=0){
                    byte square= (byte) Long.numberOfTrailingZeros(white_rooks_on_file);
                    white_rooks_on_file= Utils.popBitFromBitboard(white_rooks_on_file, square);
                    //check for own pawns
                    if((white_pawns&file)==0){
                        //open file
                        if((black_pawns&file)==0)
                            score+=open_file_score;
                        //semi-open file
                        else
                            score+=semi_open_file_score;
                    }
                }
            }

            if((white_king&file)!=0){
                //check for own pawns
                if((white_pawns&file)==0){
                    //open file
                    if((black_pawns&file)==0)
                        score-=open_file_score;
                    //semi-open file
                    else
                        score-=semi_open_file_score;
                }
            }

            //check if rooks are on current file
            if((black_rooks&file)!=0){
                long black_rooks_on_file=black_rooks&file;
                while (black_rooks_on_file!=0){
                    byte square= (byte) Long.numberOfTrailingZeros(black_rooks_on_file);
                    black_rooks_on_file= Utils.popBitFromBitboard(black_rooks_on_file, square);
                    //check for own pawns
                    if((black_pawns&file)==0){
                        //open file
                        if((white_pawns&file)==0)
                            score-=open_file_score;
                        //semi-open file
                        else
                            score-=semi_open_file_score;
                    }
                }
            }

            if((black_king&file)!=0){
                //check for own pawns
                if((black_pawns&file)==0) {
                    //open file
                    if ((white_pawns & file) == 0)
                        score += open_file_score;
                    //semi-open file
                    else
                        score += semi_open_file_score;
                }
            }
        }
        return score;
    }

    public int evaluate_mobility(){
        int score=0;
        score+=board.generatePseudolegalMovesForWhite().size();
        score-=board.generatePseudolegalMovesForBlack().size();
        return score;
    }

    public int evaluate_king_safety(){
        int score=0;
        long white_king_attacks=PCTKingMoves((byte) Long.numberOfTrailingZeros(board.getWhite_king()));
        long black_king_attacks=PCTKingMoves((byte) Long.numberOfTrailingZeros(board.getBlack_king()));

        long white_pieces=board.getWhitePiecesAsBitboard();
        long black_pieces=board.getBlackPiecesAsBitboard();

        long white_pieces_attacks=board.getWhiteAttacksAsBitboard();
        long black_pieces_attacks=board.getBlackAttacksAsBitboard();

        score+=Utils.countBits(white_king_attacks&white_pieces)*king_shield_bonus;
        score-=Utils.countBits(black_king_attacks&black_pieces)*king_shield_bonus;

        score+=Utils.countBits(white_king_attacks&black_pieces_attacks)*king_attacked_penalty;
        score-=Utils.countBits(black_king_attacks&white_pieces_attacks)*king_attacked_penalty;
        return score;
    }


    //SCORE MOVE
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
        if(move.castling_flag)
            return 7000;
        return 0;
    }

    //REPETITION
    public boolean is_repetition(){
        for(int i=0;i<repetition_index;i++){
            if(repetition_table[i]==current_hash_key)
                return true;
        }
        return false;
    }

    public void add_position_to_repetition_table(long hash_key) {
        repetition_table[repetition_index]=hash_key;
        repetition_index++;
    }
}
