package Modules;

import java.util.Random;

public class CountingBloomFilter {

    /***************************************************************************************************
     *                                           Attributes                                           *
     **************************************************************************************************/
    private Integer[] CBloomFilter;     // Counting Bloom Filter array
    private int m;                      // number of elements to insert
    private int n;                      // size of Bloom Filter
    private int k;                      // number of hash functions
    private int[][] randValsA;          // random values for Hash Functions
    private int[][] randValsB;          // random values for Hash Functions
    private int prime;                  // prime number to be used in Hash Functions


    /*************************************************************************************************
     *                                         Constructors                                         *
     ************************************************************************************************/

    /*
     * Purpose:
     *      Initializes parameters of Counting BLoom Filter
     *
     * Arguments:
     *      -> int m: number of elements to be inserted
     *      -> double factor: factor of charge of Counting Bloom Filter
     */
    public CountingBloomFilter(int m, double factor) {
        this.m = m;
        this.n = (int) Math.round(this.m/factor);
        this.k = (int) Math.floor((this.n*Math.log(2)/this.m));
    }


    /************************************************************************************************
     *                                       Private Methods                                       *
     ***********************************************************************************************/

    /*
     * Purpose:
     *     Checks if a given number is prime
     *
     * Arguments:
     *      -> int n: number to be evaluated as prime or not
     *
     * Return:
     *      -> boolean: true if number is prime | false if not
     */
    private boolean isPrime(int n) {
        for(int i = 2 ; 2*i<n ; i++) {
            if (n%i == 0)
                return false;
        }
        return true;
    }

    /*
     * Purpose:
     *      Calculates Hash value of a string
     *
     * Arguments:
     *      -> String s: string to be hashed
     *      -> int i: number of hash function to be used (it will select the correct values of arrays
     *                  randValsA and randValsB
     *
     * Return:
     *      int: hash code (integer) of the given string
     */
    private int myHash(String s, int i) {
        int hK = 0;
        for (int c = 0; c < s.length(); c++)
        {
            hK += (randValsA[i][c] * (int) s.charAt(c) + randValsB[i][c]) % this.prime;
        }
        return hK;
    }


    /***********************************************************************************************
     *                                             Getters                                        *
     **********************************************************************************************/

    /*
     * Purpose:
     *      Gets the size of Counting Bloom Filter Array
     *
     * Return:
     *      -> int: size of Counting Bloom Filter
     */
    public int getN() {
        return n;
    }


    /*
     * Purpose:
     *      Gets the Counting Bloom Filter Array
     *
     * Return:
     *      -> Integer[]: Counting Bloom Filter array
     */
    public Integer[] getCBloomFilter() {
        return CBloomFilter;
    }


    /***********************************************************************************************
     *                                        Public Methods                                      *
     **********************************************************************************************/

    /*
     * Purpose:
     *      Initializes variables to use in our Universal Hash Function
     *
     * Arguments:
     *      -> int len: max length of the Set of words in use
     */
    public void initHashFunction(int len) {

        /*
         * Get a prime number
         */
        this.prime = 10001; //CHECK WHAT PRIME WE SHOULD START AT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        if (this.prime % 2 == 0) {
            this.prime++;
        }

        while (isPrime(this.prime) != true) {
            this.prime += 2;
        }

        /*
         * Get 2 Bi-Dimensional Arrays of Random Numbers
         */
        this.randValsA = new int[this.k][len];
        this.randValsB = new int[this.k][len];

        Random rand = new Random();

        for (int i = 0; i < this.k; i++) {
            for (int j = 0; j < len; j++) {
                this.randValsA[i][j] = rand.nextInt(prime - 1);
                this.randValsB[i][j] = rand.nextInt(prime - 1);
            }
        }
    }

    /*
     * Purpose:
     *      Initializes Counting BLoom Filter
     */
    public void init() {
        CBloomFilter = new Integer[this.n];
        for (int i = 0; i < n; i++)
            CBloomFilter[i] = 0;
    }


    /*
     * Purpose:
     *      Insert element to Counting Bloom FIlter
     *
     * Arguments:
     *      -> String elm: element to be inserted
     */
    public void insert(String elm) {
        int pos;
        for (int i = 0; i < k; i++) {
            pos = myHash(elm,i) % (CBloomFilter.length);
            CBloomFilter[pos]++;
        }
    }


    /*
     * Purpose:
     *      Check if element belongs to Counting Bloom Filter
     *
     * Arguments:
     *      -> String elm: element to be checked
     *
     * Return:
     *      -> boolean: true if element belongs | false if not
     */
    public boolean check(String elm) {
        // elm	-> element to insert
        // k	-> number of hash functions
        int pos;
        boolean retVal = true;
        for (int i = 0; i < k; i++) {
            pos = myHash(elm,i) % (CBloomFilter.length);

            // check if element belongs to the array
            if (CBloomFilter[pos] == 0) {
                retVal = false;
                break;
            }
        }

        return retVal;
    }


    /*
     * Purpose:
     *      Gets the count of the element in Counting Bloom Filter
     *
     * Argument:
     *      -> String elm: element to be counted
     *
     * Return:
     *      -> int: count of the element
     */
    public int count(String elm) {
        int pos;
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < k; i++) {
            pos = myHash(elm,i) % (CBloomFilter.length);
            if (CBloomFilter[pos] < min)
                min = CBloomFilter[pos];
        }
        return min;
    }


    /*
     * Purpose:
     *      Deletes a element of the Counting Bloom Filter if element truly belongs to it
     *
     * Argument:
     *      -> String elm: element to be deleted
     */
    public void delete(String elm) {
        int pos;
        int[] positions = new int[k];
        boolean r;
        // check if element belongs to the array
        r = check(elm);

        // if element belongs delete element
        if (r) {
            /*
             * gets to array all the positions of element
             */
            for (int i = 0; i < this.k; i++) {
                pos = myHash(elm, i);
                positions[i] = pos;
            }

            /*
             * deletes word in every position, i.e, decrements one in every position
             */
            for (int i = 0; i < k; i++) {
                CBloomFilter[positions[k]]--;
            }
        }
    }

}
