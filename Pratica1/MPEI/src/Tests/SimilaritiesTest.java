package Tests;

import Modules.MinHash;
import Modules.Shingles;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class SimilaritiesTest {

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

        // TEST A - Check Similarities with Documents/ Texts
        System.out.println("TEST A - Check Similarities with Documents/ Texts");
        String[] docs = {"testFile1.txt", "testFile1 - Copy.txt","testFile2.txt","testOof.txt","testOof2.txt"};
        Shingles ourShingles = new Shingles(docs);
        MinHash ourMinHash2 = new MinHash(ourShingles.convertShingles(), 1000);
        ourMinHash2.printSimilarities(0.2);

        //TEST B - Data Set taken from movieLens
        HashMap<String, ArrayList<Integer>> dataSet = getDataSet("u.data");
        System.out.println("\nTEST B - Data Set taken from movieLens");
        MinHash ourMinHash = new MinHash(dataSet, 6650);
        start = System.currentTimeMillis();
        ourMinHash.printSimilarities(0.4);
        end = System.currentTimeMillis();
        System.out.println("Test took " + (end - start) + "ms");


    }

}
