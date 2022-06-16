package Moves;

import java.util.Objects;

import static Utils.Utils.map_files;

public class Move {
    public byte target_square;
    public byte source_square;

    public byte piece_moved;
    public byte piece_captured;
    public byte promotion_piece;

    public boolean castling_flag;
    public boolean capture_flag;
    public boolean double_push_flag;
    public boolean en_passant_flag;
    public boolean promotion_flag;


    public Move(byte target_square, byte source_square, byte piece_moved, byte piece_captured, byte promotion_piece, boolean castling_flag, boolean capture_flag, boolean double_push_flag, boolean en_passant_flag, boolean promotion_flag) {
        this.target_square = target_square;
        this.source_square = source_square;
        this.piece_moved = piece_moved;
        this.piece_captured = piece_captured;
        this.promotion_piece = promotion_piece;
        this.castling_flag = castling_flag;
        this.capture_flag = capture_flag;
        this.double_push_flag = double_push_flag;
        this.en_passant_flag = en_passant_flag;
        this.promotion_flag = promotion_flag;
    }

    public Move() {

    }

    @Override
    public String toString() {
        return "Move{" +
                "target_square=" + target_square +
                ", source_square=" + source_square +
                ", piece_moved=" + piece_moved +
                ", piece_captured=" + piece_captured +
                ", promotion_piece=" + promotion_piece +
                ", castling_flag=" + castling_flag +
                ", capture_flag=" + capture_flag +
                ", double_push_flag=" + double_push_flag +
                ", en_passant_flag=" + en_passant_flag +
                ", promotion_flag=" + promotion_flag +
                '}';
    }

    public String toUCI() {
        String move="";

        byte file = (byte) (source_square % 8);
        String source_square_string = map_files.get(file) + (8 - source_square / 8);
        move+=source_square_string;

        file = (byte) (target_square % 8);
        String target_square_string = map_files.get(file) + (8 - target_square / 8);
        move+=target_square_string;

        if(promotion_flag)
            switch (promotion_piece){
                case 2-> move+='n';
                case 3-> move+='b';
                case 4-> move+='r';
                case 5-> move+='q';
            }

        return move;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return target_square == move.target_square && source_square == move.source_square && piece_moved == move.piece_moved && piece_captured == move.piece_captured && promotion_piece == move.promotion_piece && castling_flag == move.castling_flag && capture_flag == move.capture_flag && double_push_flag == move.double_push_flag && en_passant_flag == move.en_passant_flag && promotion_flag == move.promotion_flag;
    }
}
