import Board.Board;
import Moves.*;
import Perft.PerftTest;
import Utils.Utils;
import jdk.jshell.execution.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static Moves.BishopMoves.PCTBishopMoves;
import static Moves.KingMoves.PCTKingMoves;
import static Moves.KnightMoves.PCTKnightMoves;
import static Moves.QueenMoves.PCTQueenMoves;
import static Moves.RanksFilesCenter.RANK_8;
import static Moves.RanksFilesCenter.Squares_CD_8;
import static Moves.RookMoves.PCTRookMoves;

public class Main {



    public static HashMap<Byte, String>map=new HashMap<>();
    public static void main(String[] args) throws CloneNotSupportedException {
        HashMap<Byte, String>map_files=new HashMap<>();
        map_files.put((byte) 0,"A");
        map_files.put((byte) 1,"B");
        map_files.put((byte) 2,"C");
        map_files.put((byte) 3,"D");
        map_files.put((byte) 4,"E");
        map_files.put((byte) 5,"F");
        map_files.put((byte) 6,"G");
        map_files.put((byte) 7,"H");


        for(int rank=0;rank<8;rank++) {
            for (int file = 0; file < 8; file++) {
                int square = rank * 8 + file;
                map.put((byte) square, map_files.get((byte)file)+(8-rank));
            }
        }

        //Board board=new Board("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -  ");
        Board board=new Board();
        Utils.printBoard(board);

        PerftTest perftTest=new PerftTest(board);

        perftTest.perftTest(5);
        System.out.println(perftTest.nodes);



    }

}
