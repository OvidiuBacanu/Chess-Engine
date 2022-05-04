import Board.Board;
import Moves.Move;
import Moves.WhitePawnMoves;
import Utils.Utils;

import java.util.HashMap;
import java.util.List;

public class Main {
    public static HashMap<Byte, String>map=new HashMap<>();
    public static void main(String[] args) {
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

        ////////////////////////
        ///////////////////////
        ///////////////////////
        Board board = new Board();


        WhitePawnMoves whitePawnMoves= new WhitePawnMoves();
        List<Move> moves = whitePawnMoves.generatePseudolegalMoves1ForwardWP(board);
        for(Move move:moves){
            System.out.println(map.get(move.source_square)+map.get(move.target_square)+ " "+ move.promotion_piece);
        }



    }

}
