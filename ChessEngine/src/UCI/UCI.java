package UCI;

import Board.Board;
import Evaluation.Evaluator;
import Moves.Move;
import Utils.Utils;

import java.util.List;
import java.util.Scanner;

import static Evaluation.EvaluationUtils.max_int;
import static Evaluation.EvaluationUtils.min_int;

public class UCI {
    private Board board;
    private final Evaluator evaluator;

    public UCI() {
        evaluator=new Evaluator();
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

    public Move parse_move(String string_move){
        List<Move> moves;
        if (board.isWhite_turn()) {
            moves = board.generateLegalMovesForWhite();
        } else {
            moves = board.generateLegalMovesForBlack();
        }
        byte source_square= (byte) ((string_move.charAt(0)-'a')+(8-(string_move.charAt(1)-'0'))*8);
        byte target_square= (byte) ((string_move.charAt(2)-'a')+(8-(string_move.charAt(3)-'0'))*8);

        byte promotion_piece=0;
        if(string_move.length()==5){
            switch (string_move.charAt(4)){
                case 'q'-> promotion_piece=5;
                case 'r'-> promotion_piece=4;
                case 'b'-> promotion_piece=3;
                case 'n'-> promotion_piece=2;
            }
        }

        for(Move move: moves){
            if(move.source_square==source_square && move.target_square==target_square && move.promotion_piece==promotion_piece)
                return move;
        }
        return null;
    }

    public void parse_position(String string_position){
        evaluator.setRepetition_index(0);
        if (string_position.contains("startpos")) {
            board=new Board();
            evaluator.add_position_to_repetition_table(board.getHash_key());
        }
        else if (string_position.contains("fen")) {
            string_position=string_position.substring(13);
            board=new Board(string_position);
            evaluator.add_position_to_repetition_table(board.getHash_key());
        }
        if (string_position.contains("moves")) {
            string_position=string_position.substring(string_position.indexOf("moves")+6);
            String[] moves=string_position.split(" ");
            for(String move:moves) {
                Move actual_move=parse_move(move);
                if(actual_move!=null){
                    board.makeMove(actual_move);
                    evaluator.add_position_to_repetition_table(board.getHash_key());
                }
                else
                    System.out.println(move+" is an illegal move");
            }
        }
    }

    public void parse_go(String string_go){
        if (string_go.contains("depth")) {
            int depth=Integer.parseInt(string_go.substring(9));
            search_position_for_depth(depth);
        }
        else if(string_go.contains("movetime")){
            int milliseconds=Integer.parseInt(string_go.substring(12));
            search_position_for_time(milliseconds);
        }
    }

    public void search_position_for_time(long milliseconds) {
        evaluator.setBoard(board);
        evaluator.initialize();

        //iterative deepening
        int current_depth=1;
        Move best_move = new Move();
        evaluator.milliseconds=milliseconds;
        evaluator.start_time=System.currentTimeMillis(); //fetch starting time

        while (!evaluator.stop_flag) {
            best_move=evaluator.pv_table[0][0];
            evaluator.follow_pv = true;
            int score = evaluator.negamax(min_int, max_int, current_depth);
            System.out.print("info score cp " + score + " depth " + current_depth + " nodes " + evaluator.getNodes() + " pv ");

            for (int count = 0; count < evaluator.pv_length[0]; count++) {
                System.out.print(evaluator.pv_table[0][count].toUCI() + " ");
            }
            System.out.println();
            current_depth++;
        }
        System.out.println("bestmove "+best_move.toUCI());
    }

    public void search_position_for_depth(int depth) {
        evaluator.setBoard(board);
        evaluator.initialize();

        //iterative deepening
        for(int current_depth=1;current_depth<=depth;current_depth++) {
            evaluator.follow_pv=true;
            int score = evaluator.negamax(min_int, max_int, current_depth);
            System.out.print("info score cp " + score + " depth " + current_depth + " nodes " + evaluator.getNodes() + " pv ");

            for (int count = 0; count < evaluator.pv_length[0]; count++) {
                System.out.print(evaluator.pv_table[0][count].toUCI() + " ");
            }
            System.out.println();
        }

        System.out.println("bestmove "+evaluator.pv_table[0][0].toUCI());
    }

    public void input_UCI(){
        System.out.println("id name Nicole");
        System.out.println("id author Ovidiu");
        System.out.println("uciok");
    }

    public void uci_loop(){
        Scanner input = new Scanner(System.in);
        while (true)
        {
            String input_string=input.nextLine();
            if ("uci".equals(input_string))
                input_UCI();
            else if ("isready".equals(input_string))
                System.out.println("readyok");
            else if ("ucinewgame".equals(input_string)){
                evaluator.transpositionTable.clear_transposition_table();
                parse_position("position startpos");
            }
            else if(input_string.contains("position"))
                parse_position(input_string);
            else if(input_string.contains("go"))
                parse_go(input_string);
            else if (input_string.equals("quit"))
                System.exit(0);
            else if (input_string.equals("print"))
                Utils.printBoard(board);

        }
    }
}
