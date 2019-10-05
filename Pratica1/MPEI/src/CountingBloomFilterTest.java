package src;

import java.io.*;
import java.util.*;

/**
     * Purpose:
     *		Test used to check if the Counting Bloom Filter was well implemented
*/

public class CountingBloomFilterTest {

    public static void main(String[] args) throws IOException {
        int m = 1000;            // number of elements to insert
        double factor = 0.1;    // factor of charge of Counting Bloom Filter

        /**
         * Initializing Counting Bloom Filter
         */
        CountingBloomFilter B = new CountingBloomFilter(m, factor);
        B.init();

        String[] words = new String[m];

        /**
         * Inserting elements to Counting Bloom Filter
         */
        int len = 40;                        // length of strings to be generated
        B.initHashFunction(len);            // Initializing of Hash Function Parameters
        for (int i = 0; i < m; i++) {
            String s = stringGen(len);
            words[i] = s;
            B.insert(s);
        }

        // Write Hash Table in a file
        PrintWriter output = new PrintWriter(new FileWriter("HashTable.csv"));
        for (int i = 0; i < B.getN(); i++) {
            output.printf("%d;%d\n", i, B.getCBloomFilter()[i]);
        }
        output.close();

        /**
         * checks false negatives
         */
        checkFalseNegatives(words, B);
        System.out.println();

        /**
         * checks false positives
         */
        checkFalsePositives(B, len);
        System.out.println();

        /**
         * checks number of words that do not belong to a certain file form other file
         */
        checkWordsNotBelong("pg26017.txt", "pg16425.txt");
        System.out.println();

        /**
         * print number of occurrences of each word in the file
         * and print the word with bigger occurrences
         */
        printNumberOfOccurrences("pg26017.txt");

    }


    /**
     * Purpose:
     * 		Generates a pseudo-random string with length of len (argument passed to function)
     *
     * Argument:
     * 		-> int len: length of word to be generated
     *
     * Return:
     * 		-> String: string generated
     */
    public static String stringGen(int len) {
        char[] alphabet = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
                'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
                'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        StringBuilder s = new StringBuilder();
        Random rand = new Random();
        int code;
        for (int i = 0; i < len; i++) {
            code = alphabet[rand.nextInt(alphabet.length - 1)];
            s.append((char) code);
        }
        return s.toString();
    }


    /**
     * Purpose:
     * 		Checks if there are false negatives in Counting Bloom Filter
     *
     * Arguments:
     * 		-> String[] words: array of strings to be checked
     * 		-> Modules.CountingBloomFilter B: the Counting Bloom Filter used
     */
    public static void checkFalseNegatives(String[] words, CountingBloomFilter B) {
        int fn = 0, i;
        boolean r;
        for (i = 0; i < words.length; i++) {
            r = B.check(words[i]);
            if (!r)
                fn++;
        }
        System.out.printf("Number of false negatives: %d\n", fn);
    }


    /**
     * Purpose:
     * 		Checks if there is false positives in Counting Bloom Filter
     *
     * Arguments:
     * 		-> Modules.CountingBloomFilter B: the Counting Bloom Filter used
     * 		-> int len: length of the pseudo-random words to be generated
     */
    public static void checkFalsePositives(CountingBloomFilter B, int len) {
        int fp = 0, i;
        boolean r;
        for (i = 0; i < 10000; i++) {
            String s = stringGen(len);
            r = B.check(s);
            if (r)
                fp++;
        }
        System.out.printf("Number of false positives: %d\n", fp);
    }


    /**
     * Purpose:
     * 		Reads file and puts words in ArrayList
     *
     * Argument:
     * 		-> String fileName: name of the file to be read
     *
     * Return:
     * 		-> ArrayList<String>: ArrayList of Strings with all words of the file read
     */
    public static ArrayList<String> readFile(String fileName) {
        ArrayList<String> wordsList = new ArrayList<>();
        try {
            Scanner input = new Scanner(new FileReader(fileName));
            String word;

            while (input.hasNext()) {
                word = input.next();
                word = word.replaceAll("[.:;<>_,?!*/($)��'']", "");
                if (word.compareTo("") != 0) {
                    //System.out.println(word);
                    wordsList.add(word);
                }
            }

            input.close();

        } catch (IOException e) {
            System.out.println("ERROR: bad file name!");
        }
        return wordsList;
    }


    /**
     * Purpose:
     * 		Calculates de number of words in file 2 that do not belong in file 1
     *
     * Arguments:
     * 		-> String fileName1: name of the first file to be read
     * 		-> String fileName2: name of the second file to be read
     */
    public static void checkWordsNotBelong(String fileName1, String fileName2) {
        // first file
        ArrayList<String> WordsListFile1 = new ArrayList<>();
        WordsListFile1 = readFile(fileName1);
        if (WordsListFile1.size() == 0) {
            System.out.println("WARNING: Null File!");
            System.exit(1);
        }
        // first file
        ArrayList<String> WordsListFile2 = new ArrayList<>();
        WordsListFile2 = readFile(fileName2);
        if (WordsListFile2.size() == 0) {
            System.out.println("WARNING: Null File!");
            System.exit(1);
        }

        int m = WordsListFile1.size();    // number of elements to insert
        double factor = 0.1;            // factor of charge of Counting Bloom Filter

        // Initialize Counting BLoom Filter
        CountingBloomFilter B = new CountingBloomFilter(m, factor);
        B.init();
        B.initHashFunction(40);

        // insert all words of file 1 to Counting BLoom Filter B
        for (int i = 0; i < m; i++) {
            B.insert(WordsListFile1.get(i));
        }

        // check words of file 2 in Counting BLoom Filter B
        boolean r;
        int negatives = 0; // number of elements that do not belong to B
        for (int i = 0; i < WordsListFile2.size(); i++) {
            r = B.check(WordsListFile2.get(i));
            if (!r) {
                negatives++;
            }
        }
        System.out.printf("Number of elements that do not belong to %s: %d\n", fileName1, negatives);

    }


    /**
     * Purpose:
     * 		Prints the number of occurrences of each word.
     * 		In the end prints the word with the bigger number of occurrences
     * 		and it's respective occurrence
     *
     * Argument:
     * 		-> String fileName: name of the file to be read
     */
    public static void printNumberOfOccurrences(String fileName) {
        // file
        ArrayList<String> wordsListFile = new ArrayList<>();
        wordsListFile = readFile(fileName);
        if (wordsListFile.size() == 0) {
            System.out.println("WARNING: Null File!");
            System.exit(1);
        }

        System.out.printf("FILE: %s\n############################################################\n\n", fileName);

        int m = wordsListFile.size();    // number of elements to insert
        double factor = 0.1;            // factor of charge of Counting Bloom Filter

        // Initialize Counting BLoom Filter
        CountingBloomFilter B = new CountingBloomFilter(m, factor);
        B.init();
        B.initHashFunction(40);

        // insert all words of file 1 to Counting BLoom Filter B
        for (int i = 0; i < m; i++) {
            B.insert(wordsListFile.get(i));
        }

        // remove duplicates
        Set<String> wordsSet = new HashSet<String>(wordsListFile);
        String[] wordsArrayUnique = new String[wordsSet.size()];
        int i = 0;
        Integer[] values = new Integer[wordsSet.size()];
        for (String s : wordsSet) {
            wordsArrayUnique[i] = s;
            i++;
        }
        for (i = 0; i < wordsSet.size(); i++) {
            values[i] = B.count(wordsArrayUnique[i]);
            System.out.printf("%s	->	%d\n", wordsArrayUnique[i], values[i]);
        }

        int iMax = 0, max = 0;
        for (i = 0; i < wordsArrayUnique.length; i++) {
            if (values[i] > max) {
                max = values[i];
                iMax = i;
            }
        }
        System.out.println("==========================================================");
        System.out.printf("Most frequent word: %s	(%d times).\n", wordsArrayUnique[iMax], max);
        System.out.println("==========================================================");
    }
}
