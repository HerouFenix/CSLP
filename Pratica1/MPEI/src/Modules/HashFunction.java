package Modules;

import java.util.Random;

public class HashFunction {
    /***************************************************************************************************
     *                                           Attributes                                           *
     **************************************************************************************************/
    private int prime;
    private int[] randValsA, randValsB;
    private int totalHashes;

    /*
     * Universal Hash:
     *     hK(x) = ((a(K) * x + b(K)) mod p)), where p >= # of Elements in Set
     */


    /*************************************************************************************************
     *                                         Constructors                                         *
     ************************************************************************************************/

    /*
     * Purpose:
     *      Initializes parameters of HashFunction Class
     */
    public HashFunction(int totalHashes) {
        this.totalHashes = totalHashes;
        initHashFunction();
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
        for (int i = 2; 2 * i < n; i++) {
            if (n % i == 0)
                return false;
        }
        return true;
    }

    /*
     * Purpose:
     *      Initializes variables to use in our Universal Hash Function
     */
    private void initHashFunction() {

        /*
         * Get a prime number
         */
        this.prime = 10001; 
        if (this.prime % 2 == 0) {
            this.prime++;
        }

        while (isPrime(this.prime) != true) {
            this.prime += 2;
        }

        /*
         * Get 2 2-Dimensional Arrays of Random Numbers
         */
        this.randValsA = new int[this.totalHashes];
        this.randValsB = new int[this.totalHashes];

        Random rand = new Random();

        for (int i = 0; i < this.totalHashes; i++) {
            this.randValsA[i] = rand.nextInt(prime - 1);
            this.randValsB[i] = rand.nextInt(prime - 1);
        }

    }


    /***********************************************************************************************
     *                                             Getters                                        *
     **********************************************************************************************/

    /*
     * Purpose:
     *      Gets value of attribute prime of Class
     *
     * Return:
     *      -> int: value of the prime attribute
     */
    public int getPrime() {
        return prime;
    }

    /*
     * Purpose:
     *      Gets array of ints RandValsA
     *
     * Return:
     *      -> int[]: array of ints with random values of A
     */
    public int[] getRandValsA() {
        return randValsA;
    }

    /*
     * Purpose:
     *      Gets array of ints RandValsB
     *
     * Return:
     *      -> int[]: array of ints with random values of B
     */
    public int[] getRandValsB() {
        return randValsB;
    }


    /***********************************************************************************************
     *                                        Public Methods                                      *
     **********************************************************************************************/
    /*
     * Purpose:
     *      Calculates Hash value of an integer value
     *
     * Arguments:
     *      -> int val: integer to be hashed
     *      -> int i: number of hash function to be used (it will select the correct values of arrays
     *                  randValsA and randValsB
     *
     * Return:
     *      int: hash code (integer) of the given integer
     */
    public int getHash(int val) {
        int hK = randValsA[0] * val + randValsB[0];
        hK = hK % this.prime;

        return hK;
    }

    /*
     * Purpose:
     *      Calculates Hash value of an integer value
     *
     * Arguments:
     *      -> int val: integer to be hashed
     *      -> int i: number of hash function to be used (it will select the correct values of arrays
     *                  randValsA and randValsB
     *
     * Return:
     *      int: hash code (integer) of the given integer
     */
    public int getHash(int val, int i) {
        int hK = randValsA[i] * val + randValsB[i];
        hK = hK % this.prime;

        return hK;
    }
}