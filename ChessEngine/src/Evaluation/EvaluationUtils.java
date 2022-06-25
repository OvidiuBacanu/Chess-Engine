package Evaluation;

import java.util.HashMap;
import java.util.Random;

/*
            0=none
            1=pawn
            2=knight
            3=bishop
            4=rook
            5=queen
            6=king
*/

public class EvaluationUtils {
    public static HashMap<Byte, Integer> piece_material_score=new HashMap<>(){{
       put((byte) 1,100);
       put((byte) 2,300);
       put((byte) 3,350);
       put((byte) 4,500);
       put((byte) 5,1000);
    }};

    public static int max_int=500000;
    public static int min_int=-500000;
    public static int mate_value=490000;
    public static int mate_score=480000;

    public static int[] pawn_square_table =new int[]{
            0,  0,  0,  0,  0,  0,  0,  0,
            50, 50, 50, 50, 50, 50, 50, 50,
            10, 10, 20, 30, 30, 20, 10, 10,
            5,  5, 10, 25, 25, 10,  5,  5,
            0,  0,  0, 20, 20,  0,  0,  0,
            5, -5,-10,  0,  0,-10, -5,  5,
            5, 10, 10,-20,-20, 10, 10,  5,
            0,  0,  0,  0,  0,  0,  0,  0
    };

    public static int[] knight_square_table =new int[]{
            -50,-40,-30,-30,-30,-30,-40,-50,
            -40,-20,  0,  0,  0,  0,-20,-40,
            -30,  0, 10, 15, 15, 10,  0,-30,
            -30,  5, 15, 20, 20, 15,  5,-30,
            -30,  0, 15, 20, 20, 15,  0,-30,
            -30,  5, 10, 15, 15, 10,  5,-30,
            -40,-20,  0,  5,  5,  0,-20,-40,
            -50,-40,-30,-30,-30,-30,-40,-50
    };

    public static int[] bishop_square_table =new int[]{
            -20,-10,-10,-10,-10,-10,-10,-20,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -10,  0,  5, 10, 10,  5,  0,-10,
            -10,  5,  5, 10, 10,  5,  5,-10,
            -10,  0, 10, 10, 10, 10,  0,-10,
            -10, 10, 10, 10, 10, 10, 10,-10,
            -10,  5,  0,  0,  0,  0,  5,-10,
            -20,-10,-10,-10,-10,-10,-10,-20
    };

    public static int[] rook_square_table =new int[]{
            0,  0,  0,  0,  0,  0,  0,  0,
            5, 10, 10, 10, 10, 10, 10,  5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            0,  0,  0,  5,  5,  0,  0,  0
    };

    public static int[] queen_square_table =new int[]{
            -20,-10,-10, -5, -5,-10,-10,-20,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -10,  0,  5,  5,  5,  5,  0,-10,
            -5,  0,  5,  5,  5,  5,  0, -5,
            0,  0,  5,  5,  5,  5,  0, -5,
            -10,  5,  5,  5,  5,  5,  0,-10,
            -10,  0,  5,  0,  0,  0,  0,-10,
            -20,-10,-10, -5, -5,-10,-10,-20
    };

    public static int[] king_square_table =new int[]{
            0,   0,   0,   0,   0,   0,   0,   0,
            0,   0,   5,   5,   5,   5,   0,   0,
            0,   5,   5,  10,  10,   5,   5,   0,
            0,   5,  10,  20,  20,  10,   5,   0,
            0,   5,  10,  20,  20,  10,   5,   0,
            0,   0,   5,  10,  10,   5,   0,   0,
            0,   5,   5,  -5,  -5,   0,   5,   0,
            0,   0,   5,   0, -15,   0,  10,   0
    };

    public static int[] mirrored_for_black =new int[]{
            56, 57, 58, 59, 60, 61, 62, 63,
            48, 49, 50, 51, 52, 53, 54, 55,
            40, 41, 42, 43, 44, 45, 46, 47,
            32, 33, 34, 35, 36, 37, 38, 39,
            24, 25, 26, 27, 28, 29, 30, 31,
            16, 17, 18, 19, 20, 21, 22, 23,
             8,  9, 10, 11, 12, 13, 14, 15,
             0,  1,  2,  3,  4,  5,  6,  7
    };

    // MVV LVA [attacker][victim]
    public static int[][] mvv_lva=new int[][]{
            {0,   0,   0,   0,   0,   0},
            {0, 105, 205, 305, 405, 505},
            {0, 104, 204, 304, 404, 504},
            {0, 103, 203, 303, 403, 503},
            {0, 102, 202, 302, 402, 502},
            {0, 101, 201, 301, 401, 501},
            {0, 100, 200, 300, 400, 500}
    };

    public static long[] File_Masks_EV =/*from fileA to FileH*/
            {
                    0x101010101010101L, 0x202020202020202L, 0x404040404040404L, 0x808080808080808L,
                    0x1010101010101010L, 0x2020202020202020L, 0x4040404040404040L, 0x8080808080808080L
            };
    public static long[] Rank_Masks_EV =/*from rank1 to rank8*/
            {
                    0xFF00000000000000L, 0xFF000000000000L, 0xFF0000000000L, 0xFF00000000L, 0xFF000000L, 0xFF0000L, 0xFF00L, 0xFFL,
            };

    //pawns
    public static int double_pawn_penalty = -25;
    public static int isolated_pawn_penalty = -25;
    public static int[] passed_pawn_bonus = new int[]{0, 10, 30, 50, 75, 100, 150, 200};

    //semi-open and open files score
    public static int semi_open_file_score=10;
    public static int open_file_score=25;

    //king safety
    public static int king_shield_bonus=5;
    public static int king_attacked_penalty=-3;

    public static int max_ply=64;

    public static Random random=new Random(1854366879885646465L);

    //keys for zobrist hashing
    //random piece keys [piece][square]
    public static long[][] white_pieces_keys=new long[7][64];
    public static long[][] black_pieces_keys=new long[7][64];
    public static long[] enpassant_keys=new long[64];
    public static long[] castling_keys=new long[4];
    public static long side_key;

    public static void init_random_keys(){
        //pieces
        for (int piece=1;piece<7;piece++){
            for(int square=0;square<64;square++){
               white_pieces_keys[piece][square]=random.nextLong();
               black_pieces_keys[piece][square]=random.nextLong();
            }
        }

        //en-passant
        for(int square=0;square<64;square++){
            enpassant_keys[square]= random.nextLong();
        }

        //side
        side_key=random.nextLong();

        //castling rights
        for(int i=0;i<4;i++)
            castling_keys[i]=random.nextLong();
    }
}
