package Modules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Map.Entry;


public class Shingles {
    /***************************************************************************************************
     *                                           Attributes                                           *
     **************************************************************************************************/
    private int gram = 10;
    private HashMap<String, ArrayList<String>> shingles;

    /*
     * For the HashFunction
     */
    private HashFunction ourHashFunction;


    /*************************************************************************************************
     *                                         Constructors                                         *
     ************************************************************************************************/
    public Shingles(String[] docs) {

        this.shingles = new HashMap<String, ArrayList<String>>();
        ourHashFunction = new HashFunction(this.gram);

        for (String key : docs) {
            shingles.put(key, createShingles(key));
        }
    }
    
    public Shingles(HashMap<String,String> map) {
        this.shingles = new HashMap<String, ArrayList<String>>();
        ourHashFunction = new HashFunction(this.gram);
        

    	for (Entry<String,String> item : map.entrySet()) {
    		if(item.getValue().length() < 20) {
    			continue;
    		}
            shingles.put(item.getKey(), createShingles2(item.getKey(),item.getValue()));
        }
    }
    
    public Shingles(String[] docs,int gram) {
    	this.gram = gram;
        this.shingles = new HashMap<String, ArrayList<String>>();
        ourHashFunction = new HashFunction(this.gram);

        for (String key : docs) {
            shingles.put(key, createShingles(key));
        }
    }
    
    public Shingles(HashMap<String,String> map, int gram) {
    	this.gram = gram;
        this.shingles = new HashMap<String, ArrayList<String>>();
        ourHashFunction = new HashFunction(this.gram);
        

    	for (Entry<String,String> item : map.entrySet()) {
    		if(item.getValue().length() < 20) {
    			continue;
    		}
            shingles.put(item.getKey(), createShingles2(item.getKey(),item.getValue()));
        }
    }



    /************************************************************************************************
     *                                       Private Methods                                       *
     ***********************************************************************************************/
    /*
     * Purpose:
     * 		Converts Text from file into an ArrayList of Shingles
     *
     * Return:
     * 		->  ArrayList<String>: ArrayLit of strings (our shingles)
     */
    private ArrayList<String> createShingles(String file) {
        ArrayList<String> shingles = new ArrayList<String>();

        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(file));
            String line, temp;

            //While we haven't read all lines of the file
            while ((line = fileReader.readLine()) != null) {
            	//For each char in the line
                for (int i = 0; i < line.length() - this.gram; i++) {
                    temp = "";
                    //Concatenate the "gram" successive chars to temp
                    for (int j = i; j < i + this.gram; j++) {
                        temp += line.charAt(j);
                    }
                    //Add temp to the shingles list
                    shingles.add(temp);
                }
            }

            fileReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return shingles;
    }

    /*
     * Purpose:
     * 		Converts Text from an ArrayList of strings into an ArrayList of Shingles
     *
     * Return:
     * 		->  ArrayList<String>: ArrayLit of strings (our shingles)
     */
    private ArrayList<String> createShingles2(String key, String value) {
        ArrayList<String> shingles = new ArrayList<String>();
        String temp;
        //For each char in the line
        for (int i = 0; i < value.length() - this.gram; i++) {
        	temp = "";
            //Concatenate the "gram" successive chars to temp
            for (int j = i; j < i + this.gram; j++) {
            	temp += value.charAt(j);
            }
            //Add temp to the shingles list
            shingles.add(temp);
        }
        return shingles;
    }

    /***********************************************************************************************
     *                                             Getters                                        *
     **********************************************************************************************/
    /*
     * Purpose:
     * 		Get gram value (size of each shingle)
     *
     * Return:
     * 		-> int: value of gram
     */
    public int getGram() {
        return gram;
    }


    /*
     * Purpose:
     * 		Get shingles map
     *
     * Return:
     * 		-> HashMap<String, ArrayList<String>>: map where he keys are the names of the docs
     * 				and the respective values are ArrayLists with the shingles of each doc
     */
    public HashMap<String, ArrayList<String>> getShingles() {
        return shingles;
    }


    /***********************************************************************************************
     *                                        Public Methods                                      *
     **********************************************************************************************/
    /*
     * Purpose:
     * 		Convert the shingles of strings to ints.
     * 		Instead of a map with this structure {String: ArrayList<String>}, we will have
     * 		a structure like this: 	{String: ArrayList<Integer>}
     *
     * Return:
     * 		-> HashMap<String,ArrayList<Integer>>: hashMap with this structure: {String: ArrayList<Integer>}
     */
    public HashMap<String, ArrayList<Integer>> convertShingles() {
        HashMap<String, ArrayList<Integer>> convertedShingles = new HashMap<String, ArrayList<Integer>>();

        int keysLength = shingles.keySet().size();
        int valuesLength;
        String[] keys = shingles.keySet().toArray(new String[shingles.keySet().size()]);
        
        Integer[] hK;

        //For each file in the shingles
        for (int n = 0; n < keysLength; n++) {
            valuesLength = shingles.get(keys[n]).size();
            hK = new Integer[valuesLength];
            //For each shingle of the file
            for (int i = 0; i < valuesLength; i++) {
                hK[i] = 0;
                //Get a hash for each char in the shingle and add it to the total hash of the shingle
                for (int k = 0; k < shingles.get(keys[n]).get(i).length(); k++) {
                    hK[i] += ourHashFunction.getHash((int) shingles.get(keys[n]).get(i).charAt(k), 1);
                }
                
                //Make sure that we don't get a Hash value that's too big
                hK[i] = hK[i] % ourHashFunction.getPrime();
            }
            //hK now has an array in which each of the elements corresponds to each of the shingles of the file "n" hashed into an integer
            convertedShingles.put(keys[n], new ArrayList<>(Arrays.asList(hK)));
        }
        return convertedShingles;
    }

    /*
     * Purpose:
     * 		Returns the total amount of chars in a file
     *
     * Argument:
     * 		-> String file: name of the file to be analysed
     *
     * Return:
     * 		-> int: number of chars of that file
     */
    public int getFileChars(String file) {
        try {
            Scanner fileScanner = new Scanner(new File(file));

            int totalChars = 0;
            while (fileScanner.hasNext()) {
                String word = fileScanner.next();
                totalChars += word.length();
            }
            fileScanner.close();
            return totalChars;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /*
     * Purpose:
     * 		Prints Shingles
     */
    public void printShingles() {
        for (String key : shingles.keySet()) {
            System.out.println(key + " : " + shingles.get(key));
        }

    }
}
