package Perft;

import Board.Board;
import Moves.Move;
import Utils.Utils;
import jdk.jshell.execution.Util;

import java.util.HashMap;
import java.util.List;

import static Moves.BishopMoves.PCTBishopMoves;
import static Moves.RanksFilesCenter.Squares_CD_8;
import static Moves.RanksFilesCenter.Squares_FG_8;

public class PerftTest {
    public long nodes = 0;
    public Board board;
    public HashMap<Byte, String> map = new HashMap<>();


    public PerftTest(Board board) {
        this.board = board;

        HashMap<Byte, String> map_files = new HashMap<>();
        map_files.put((byte) 0, "a");
        map_files.put((byte) 1, "b");
        map_files.put((byte) 2, "c");
        map_files.put((byte) 3, "d");
        map_files.put((byte) 4, "e");
        map_files.put((byte) 5, "f");
        map_files.put((byte) 6, "g");
        map_files.put((byte) 7, "h");


        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                int square = rank * 8 + file;
                map.put((byte) square, map_files.get((byte) file) + (8 - rank));
            }
        }
    }

    public void perftDriver(int depth) {
        if (depth == 0) {
            nodes++;
            return;
        }
        List<Move> moves;
        if (board.isWhite_turn()) {
            moves = board.generateLegalMovesForWhite();
        } else {
            moves = board.generateLegalMovesForBlack();
        }


        for (Move move : moves) {
            Board copy_board = board.getCopyBoard();
            board.makeMove(move);

            perftDriver(depth - 1);

            board = copy_board.getCopyBoard();
        }
    }

    public void perftTest(int depth){
        List<Move> moves;
        if (board.isWhite_turn()) {
            moves = board.generateLegalMovesForWhite();
        } else {
            moves = board.generateLegalMovesForBlack();
        }


        for (Move move : moves) {
            Board copy_board = board.getCopyBoard();
            board.makeMove(move);

            long commulative_nodes=nodes;

            perftDriver(depth - 1);

            long old_nodes= nodes - commulative_nodes;

            board = copy_board.getCopyBoard();

            System.out.print(map.get(move.source_square)+map.get(move.target_square)+": ");
            System.out.println(old_nodes);
        }
    }
}
