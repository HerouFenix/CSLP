package Tests;

import Modules.MinHashLSH;
import Modules.Shingles;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
     * Purpose:
     *		Test used to check if the LSH was well implemented and is capable of generating the correct similarities
*/
public class SimilaritiesLSHTest {

    public static HashMap<String, ArrayList<Integer>> getDataSet(String file) {
        HashMap<String, ArrayList<Integer>> dataSet = new HashMap<String, ArrayList<Integer>>();
        String user;
        int movie;

        try {
            Scanner fileScanner = new Scanner(new File(file));

            while (fileScanner.hasNext()) {
                user = fileScanner.next();
                movie = Integer.parseInt(fileScanner.next());
                if (dataSet.get(user) != null) {
                    dataSet.get(user).add(movie);
                } else {
                    ArrayList<Integer> temp = new ArrayList<Integer>();
                    temp.add(movie);
                    dataSet.put(user, temp);
                }
                //System.out.printf("User - %s ; Movie - %s\n",fileScanner.next(),fileScanner.next());
                fileScanner.next();
                fileScanner.next();
            }
            fileScanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return dataSet;
    }

    public static void main(String[] args) {
        long start, end;

        /// TEST A - Check Similarities with Documents/ Texts
        System.out.println("TEST A - Check Similarities with Documents/ Texts");
        String[] docs = {"testFile1.txt", "testFile1 - Copy.txt","testFile2.txt","testOof.txt","testOof2.txt"};
        Shingles ourShingles = new Shingles(docs);
        MinHashLSH ourMinHash2 = new MinHashLSH(ourShingles.convertShingles(), 1000, 10);
        ourMinHash2.printSimilarities(0.20);

        /// TEST B - Data Set taken from movieLens
        HashMap<String, ArrayList<Integer>> dataSet = getDataSet("u.data");
        System.out.println("\nTEST B - Data Set taken from movieLens");
        MinHashLSH ourMinHash = new MinHashLSH(dataSet, 6650, 10);
        start = System.currentTimeMillis();
        ourMinHash.printSimilarities(0.4);
        end = System.currentTimeMillis();
        System.out.println("Test took " + (end - start) + "ms");

    }

}
