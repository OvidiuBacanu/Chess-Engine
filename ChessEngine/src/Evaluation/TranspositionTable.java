package Evaluation;

import static Evaluation.EvaluationUtils.mate_score;
import static Evaluation.EvaluationUtils.max_int;

public class TranspositionTable {
    public static int transpositionTable_size=50000*10;
    public TranspositionTableEntry[] transpositionTableEntries;

    public static int hash_flag_exact=0;
    public static int hash_flag_alpha=1;
    public static int hash_flag_beta=2;

    public static int no_hash_entry=max_int*2;

    public void init_transposition_table(){
        for(int i=0;i<transpositionTable_size;i++){
            transpositionTableEntries[i]=new TranspositionTableEntry();
        }
    }
    public void clear_transposition_table(){
        for(int i=0;i<transpositionTable_size;i++){
            transpositionTableEntries[i].flag=0;
            transpositionTableEntries[i].depth=0;
            transpositionTableEntries[i].score=0;
            transpositionTableEntries[i].hash_key=0;
        }
    }

    public TranspositionTable() {
        transpositionTableEntries =new TranspositionTableEntry[transpositionTable_size];
    }

    public int read_transposition_table_entry(int alpha, int beta, int depth, int ply){
        long current_hash_key=Evaluator.current_hash_key;
        int index= (int) (current_hash_key%transpositionTable_size);
        if(index<0)
            index*=-1;
        TranspositionTableEntry tte=transpositionTableEntries[index];

        if (tte.hash_key==current_hash_key) {
            if (tte.depth >= depth) {
                int score = tte.score;
                if(score<(-mate_score))
                    score+=ply;

                if(score>mate_score)
                    score-=ply;

                //pv
                if (tte.flag == hash_flag_exact)
                    return score;

                //fail low
                if (tte.flag == hash_flag_alpha && score <= alpha)
                    return alpha;

                //fail high
                if (tte.flag == hash_flag_beta && score >= beta)
                    return beta;
            }
        }
        return no_hash_entry;
    }

    public void write_transposition_table_entry(int score, int depth, int hash_flag, int ply){
        long current_hash_key=Evaluator.current_hash_key;
        int index= (int) (current_hash_key%transpositionTable_size);
        if(index<0)
            index*=-1;

        if(score<(-mate_score))
            score-=ply;

        if(score>mate_score)
            score+=ply;



        transpositionTableEntries[index].hash_key=current_hash_key;
        transpositionTableEntries[index].score=score;
        transpositionTableEntries[index].flag=hash_flag;
        transpositionTableEntries[index].depth=depth;
    }
}
