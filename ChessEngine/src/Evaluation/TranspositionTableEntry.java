package Evaluation;

public class TranspositionTableEntry {
    public long hash_key;
    public int depth;
    public int flag;
    public int score;

    public TranspositionTableEntry() {
        hash_key=0;
        depth=0;
        flag=0;
        score=0;
    }
}

/*
* long = 64 bits
* int = 32 bits
*
* tt = 64 + 32 * 3 = 160 bits = 20 bytes = 0.00002 MBS
* 1 MB = tt[50.000]
* 2 MB = tt[100.000]
* 3 MB = tt[150.000]
* ...
* */
