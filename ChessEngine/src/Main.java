import Board.Board;
import Evaluation.Evaluator;
import Moves.*;
import Perft.PerftTest;
import UCI.UCI;
import Utils.Utils;
import jdk.jshell.execution.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static Evaluation.EvaluationUtils.*;
import static Moves.BishopMoves.PCTBishopMoves;
import static Moves.KingMoves.PCTKingMoves;
import static Moves.KnightMoves.PCTKnightMoves;
import static Moves.QueenMoves.PCTQueenMoves;
import static Moves.RanksFilesCenter.RANK_8;
import static Moves.RanksFilesCenter.Squares_CD_8;
import static Moves.RookMoves.PCTRookMoves;

public class Main {
    public static void main(String[] args) throws CloneNotSupportedException {

//        Board board=new Board("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq e3 0 1");
////        Board board=new Board();
////        board.makeMove(new Move((byte) 42, (byte) 57, (byte) 2, (byte) 0, (byte) 0, false, false, false, false, false));
//        Utils.printBoard(board);
//
//        Evaluator evaluator=new Evaluator(board);
//        evaluator.setWhite_turn(board.isWhite_turn());
//        int score = evaluator.negamax(min_int,max_int,2);
//
//
//
//        System.out.println(score);
//        Move move=evaluator.getBest_move();
//        System.out.println(move);
//        System.out.println(map.get(move.source_square)+map.get(move.target_square));


        boolean debug=false;
        if(debug){
            Board board=new Board("rnbqkb1r/pp1p1pPp/8/2p1pP2/1P1P4/3P3P/P1P1P3/RNBQKBNR w KQkq e6 0 1");
            Evaluator evaluator=new Evaluator();
            evaluator.setBoard(board);

            evaluator.negamax(min_int,max_int,5);
            System.out.println(evaluator.getBest_move().toUCI());
            System.out.println(evaluator.getNodes());

        }
        else{
            UCI uci=new UCI();
            uci.uci_loop();
        }
    }

}
