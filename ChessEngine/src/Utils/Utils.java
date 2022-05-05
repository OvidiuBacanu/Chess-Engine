package Utils;

import Board.Board;

public class Utils {
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
                int square=rank*8+file;

                long value_on_square=(wp  & (1L << square));
                if(value_on_square!=0)
                    System.out.print("♟ ");
                else{
                    value_on_square=(wn  & (1L << square));
                    if(value_on_square!=0)
                        System.out.print("♞ ");
                    else {
                        value_on_square = (wb & (1L << square));
                        if (value_on_square != 0)
                            System.out.print("♝ ");
                        else{
                            value_on_square = (wr & (1L << square));
                            if (value_on_square != 0)
                                System.out.print("♜ ");
                            else{
                                value_on_square = (wq & (1L << square));
                                if (value_on_square != 0)
                                    System.out.print("♛ ");
                                else{
                                    value_on_square = (wk & (1L << square));
                                    if (value_on_square != 0)
                                        System.out.print("♚ ");
                                    else{
                                        value_on_square = (bp & (1L << square));
                                        if (value_on_square != 0)
                                            System.out.print("♙ ");
                                        else {
                                            value_on_square = (bn & (1L << square));
                                            if (value_on_square != 0)
                                                System.out.print("♘ ");
                                            else {
                                                value_on_square = (bb & (1L << square));
                                                if (value_on_square != 0)
                                                    System.out.print("♗ ");
                                                else {
                                                    value_on_square = (br & (1L << square));
                                                    if (value_on_square != 0)
                                                        System.out.print("♖ ");
                                                    else {
                                                        value_on_square = (bq & (1L << square));
                                                        if (value_on_square != 0)
                                                            System.out.print("♕ ");
                                                        else {
                                                            value_on_square = (bk & (1L << square));
                                                            if (value_on_square != 0)
                                                                System.out.print("♔ ");
                                                            else
                                                                System.out.print("\uD83D\uDEA9 ");
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            System.out.println();
        }
        System.out.println("\n    A  B  C D  E  F G  H");
    }


}
