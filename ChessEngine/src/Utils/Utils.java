package Utils;

import Board.Board;

import java.util.HashMap;

public class Utils {
    public static HashMap<Byte, String> map_files = new HashMap<>(){{
        put((byte) 0, "a");
        put((byte) 1, "b");
        put((byte) 2, "c");
        put((byte) 3, "d");
        put((byte) 4, "e");
        put((byte) 5, "f");
        put((byte) 6, "g");
        put((byte) 7, "h");
    }};

    public static void printBitboard(long bitboard){
        for(int rank=0;rank<8;rank++){
            System.out.print((8-rank)+"   ");
            for(int file=0;file<8;file++)
            {
                int square=rank*8+file;
                long value_on_square=(bitboard  & (1L << square));
                if(value_on_square!=0)
                    System.out.print(" 1 ");
                else
                    System.out.print(" 0 ");
            }
            System.out.println();
        }
        System.out.println("\n     A  B  C  D  E  F  G  H");
    }

    public static long getBitboardForSquare(byte square){
        return 1L<<square;
    }

    public static long popBitFromBitboard(long bitboard, byte square){
        return bitboard^(1L<<square);
    }

    public static boolean checkIfSquareIsOne(long bitboard, byte square){
        return (bitboard&(1L<<square)) == getBitboardForSquare(square);
    }

    public static int countBits(long bitboard){
        int nr_bits=0;
        while (bitboard!=0){
            byte square= (byte) Long.numberOfTrailingZeros(bitboard);
            bitboard= Utils.popBitFromBitboard(bitboard, square);
            nr_bits++;
        }
        return nr_bits;
    }

    public static void printBoard(Board board){
        long wp=board.getWhite_pawns();
        long wn=board.getWhite_knights();
        long wb=board.getWhite_bishops();
        long wr=board.getWhite_rooks();
        long wq=board.getWhite_queens();
        long wk=board.getWhite_king();

        long bp=board.getBlack_pawns();
        long bn=board.getBlack_knights();
        long bb=board.getBlack_bishops();
        long br=board.getBlack_rooks();
        long bq=board.getBlack_queens();
        long bk=board.getBlack_king();

        for(int rank=0;rank<8;rank++){
            System.out.print((8-rank)+"   ");
            for(int file=0;file<8;file++)
            {
                boolean piece_found=false;
                int square=rank*8+file;

                long value_on_square=(wp  & (1L << square));
                if(value_on_square!=0) {
                    System.out.print("♟ ");
                    piece_found=true;
                }

                value_on_square=(wn  & (1L << square));
                if(value_on_square!=0){
                    System.out.print("♞ ");
                    piece_found=true;
                }

                value_on_square = (wb & (1L << square));
                if (value_on_square != 0) {
                    System.out.print("♝ ");
                    piece_found=true;
                }

                value_on_square = (wr & (1L << square));
                if (value_on_square != 0) {
                    System.out.print("♜ ");
                    piece_found=true;
                }

                value_on_square = (wq & (1L << square));
                if (value_on_square != 0) {
                    System.out.print("♛ ");
                    piece_found=true;
                }

                value_on_square = (wk & (1L << square));
                if (value_on_square != 0) {
                    piece_found=true;
                    System.out.print("♚ ");
                }

                value_on_square = (bp & (1L << square));
                if (value_on_square != 0) {
                    piece_found=true;
                    System.out.print("♙ ");
                }

                value_on_square = (bn & (1L << square));
                if (value_on_square != 0) {
                    System.out.print("♘ ");
                    piece_found=true;
                }

                value_on_square = (bb & (1L << square));
                if (value_on_square != 0) {
                    System.out.print("♗ ");
                    piece_found=true;
                }

                value_on_square = (br & (1L << square));
                if (value_on_square != 0) {
                    System.out.print("♖ ");
                    piece_found=true;
                }

                value_on_square = (bq & (1L << square));
                if (value_on_square != 0) {
                    System.out.print("♕ ");
                    piece_found=true;
                }

                value_on_square = (bk & (1L << square));
                if (value_on_square != 0) {
                    System.out.print("♔ ");
                    piece_found=true;
                }

                if(!piece_found)
                    System.out.print("\uD83D\uDEA9 ");
            }
            System.out.println();
        }
        System.out.println("\n    A  B  C D  E  F G  H\n");

        if(board.isWhite_turn())
            System.out.println("    Turn: White");
        else
            System.out.println("    Turn: Black");

        String castling_rights="";
        if(board.isWhite_castling_KS_right())
            castling_rights+='K';
        else
            castling_rights+='-';

        if(board.isWhite_castling_QS_right())
            castling_rights+='Q';
        else
            castling_rights+='-';

        if(board.isBlack_castling_KS_right())
            castling_rights+='k';
        else
            castling_rights+='-';

        if(board.isBlack_castling_QS_right())
            castling_rights+='q';
        else
            castling_rights+='-';

        System.out.println("    Castling: "+castling_rights);

        String en_passant="null";
        if(board.getEn_passant_target_square()>0) {
            byte file = (byte) (board.getEn_passant_target_square() % 8);
            en_passant = map_files.get(file) + (8 - board.getEn_passant_target_square() / 8);
        }
        System.out.println("    En-passant: "+en_passant);
        System.out.println("    Hash key: "+board.getHash_key());
    }
}
