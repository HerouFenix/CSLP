package Modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MinHashLSH extends MinHash {
    /***************************************************************************************************
     *                                           Attributes                                           *
     **************************************************************************************************/
    /*
     * For the LSH
     */
    private int rows;
    private int bands;
    private int prime;
    private int[] randValsA;
    private int[][] minHashR;


    /*************************************************************************************************
     *                                         Constructors                                         *
     ************************************************************************************************/
    public MinHashLSH(HashMap<String, ArrayList<Integer>> dataSet, int totalHashes, int r) {
        super(dataSet, totalHashes);
        this.rows = r;
        this.bands = (int) this.getTotalHashes() / rows;
        this.prime = 1000003;
        this.minHashR = new int[dataSet.keySet().size()][this.bands];

        /*
         * init parameters of Hash Function
         */
        this.randValsA = new int[this.rows];

        Random rand = new Random();
        for (int i = 0; i < this.rows; i++) {
            this.randValsA[i] = rand.nextInt(prime - 1);
        }
    }

    /************************************************************************************************
     *                                       Private Methods                                       *
     ***********************************************************************************************/
    /*
     * Purpose:
     *      Calculates Hash value of a string
     *
     * Arguments:
     *      -> String s: string to be hashed
     *      -> int i: number of row to be hashed (it will select the correct values of arrays
     *                  randValsA and randValsB
     *
     * Return:
     *      int: hash code (integer) of the given string
     */
    private int myHash(int val, int i) {
        int hK = 0;
        hK += (this.randValsA[0] * val) % this.prime;
        return hK;
    }

    /*
     * Purpose:
     *     Creates a minHash reduced (applies LSH)
     */
    private void createMinHashR() {
        int[] f = new int[this.rows];
        for (int u = 0; u < this.getDataSet().keySet().size(); u++) {
            for (int nB = 0; nB < this.bands; nB++) {
                for (int i = 0; i < this.rows; i++) {
                    f[i] = this.getMinHash()[u][i + this.rows * nB];
                }
                this.minHashR[u][nB] = 0;
                for (int i = 0; i < this.rows; i++) {
                    this.minHashR[u][nB] += myHash(f[i], i);
                }
            }
        }
    }

    private int getHashValue(int[] rows) {
        int HashValue = 0;
        for (int i = 0; i < rows.length; i++) {
            HashValue += myHash(rows[i], i);
        }
        return HashValue;
    }



    /***********************************************************************************************
     *                                        Public Methods                                      *
     **********************************************************************************************/
    /*
     * Purpose:
     *      Returns total amount of intersections between two 1D arrays
     *
     * Arguments:
     *      -> int[} a: array of integers a to be evaluated
     *      -> int[} b: array of integers b to be evaluated
     *
     * Return:
     * 		-> int: value of the intersection, i.e, number of equal "rows"
     */
    public int intersectionsLSH(int[] a, int[] b) {
        int sum = 0;
        //Check if entries at index I of array A and array B are the same
        for (int i = 0; i < a.length; i++) {
            if (a[i] == b[i]) {
                sum = 1;
                break;
            }
        }
        return sum;
    }


    /*
     * Purpose:
     * 		Prints all Similarities of the Initial Set taking in account a value of guidance (threshold)
     *
     * Argument:
     * 		-> double threshold: value that defines if some element is Similar to another element
     */
    @Override
    public void printSimilarities(double threshold) {
        int keysLength = this.getDataSet().keySet().size();
        String[] keys = this.getDataSet().keySet().toArray(new String[this.getDataSet().keySet().size()]);
        double sim;
        createMinHashR();
        int[] f1 = new int[this.rows];
        int[] f2 = new int[this.rows];
        for (int i = 0; i < keysLength; i++) {
            for (int j = i + 1; j < keysLength; j++) {
                if (intersectionsLSH(minHashR[i], minHashR[j]) == 1) {
                    sim = super.intersections(this.getMinHash()[i], this.getMinHash()[j]) / this.getTotalHashes();
                    if (1 - sim <= threshold) {
                        System.out.printf("Distancia : %f -> user 1: %s user 2: %s\n", 1 - sim, keys[i], keys[j]);
                    }
                }

            }
        }
    }

    /*
     * Purpose:
     * 		Returns a list with all Modules.Similarities of the Initial Set taking in account a value of guidance (threshold)
     *
     * Argument:
     * 		-> double threshold: value that defines if some element is Similar to another element
     */
    public ArrayList<String> getSimilarities(double threshold) {
        int keysLength = this.getDataSet().keySet().size();
        String[] keys = this.getDataSet().keySet().toArray(new String[this.getDataSet().keySet().size()]);
        ArrayList<String> similarities = new ArrayList<String>();
        createMinHashR();
        int[] f1 = new int[this.rows];
        int[] f2 = new int[this.rows];
        double sim;

        //For each key in the dataset
        for (int i = 0; i < keysLength; i++) {
            //Search all keys that come after key "i"
            for (int j = i + 1; j < keysLength; j++) {
                if (intersectionsLSH(minHashR[i], minHashR[j]) == 1) {
                    sim = super.intersections(this.getMinHash()[i], this.getMinHash()[j]) / this.getTotalHashes();
                    if (1 - sim <= threshold) {
                        similarities.add((1 - sim) + " ;- " + keys[i] + " ;- " + keys[j]);
                    }
                }

            }
        }
        return similarities;
    }


}
