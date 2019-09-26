package Tests;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import Modules.*;

public class actualFinalTest {
    public static void main(String[] args) throws IOException{
    	BufferedReader br;
 
    	StochasticCounter thisCounterNoReviews = new StochasticCounter(0.5);  //Use to determine how many games have no reviews
    	StochasticCounter thisCounterTotal = new StochasticCounter(0.3); //Determine how many total games we're dealing with
    	
        ArrayList<String> gameDevs = new ArrayList<String>();	//DataSet: Developers
        HashMap<String, HashMap<String,String>> gameReviews = new HashMap<String,  HashMap<String,String>>();	//DataSet: Name of Game - [User - Review]

        String line;
        String[] cutLine,reviews;
        HashMap<String,String> temp;
        
        Scanner scan = new Scanner(System.in);
        System.out.println("Pick a data set:\n1 - Full Data Set (2700+ Games)\n2 - Half Data Set (1350+ Games)\n3 - Quarter Data Set(675+ Games)");
        int inp = scan.nextInt();
        scan.close();
        switch(inp){
	        case 1: 
	        	br = new BufferedReader(new FileReader("allGamesData.txt"));
	        	break;
	        case 2: 
	        	br = new BufferedReader(new FileReader("halfGamesData.txt"));
	        	break;
	        case 3: 
	        	br = new BufferedReader(new FileReader("quarterGamesData.txt"));
	        	break;
	        
	        default:
	        	System.out.println("Invalid Option! Terminating program");
	        	return ;
        }
   //PART 1 - Determine how many games havent been reviewed (And initialize the dataSet Maps)
        while((line = br.readLine()) != null) {
        	thisCounterTotal.incrementCounter();
        	cutLine = line.split(",;,");

        	if(cutLine.length<10) { //Ignore games that have no reviews
        		thisCounterNoReviews.incrementCounter();
            	continue;
        	}
        	        	
        	//Add to gameDevs List
        	gameDevs.add(cutLine[4]);
        	    
        	//Add to gameReviews hash map
            temp = new HashMap<String,String>();
            reviews = cutLine[9].split(",-,");
            String user,review;
            for(int i = 0 ; i < reviews.length ; i++) {
            	if(reviews[i].split("--.--").length == 2)	//Some reviews are incomplete in our data set so they contain only the name of the user and whether their verified or not
                    continue;								//therefore we should ignore these
            	user = reviews[i].split("--.--")[0];
            	review = reviews[i].split("--.--")[2];
            	temp.put(user, review);
            } 
            gameReviews.put(cutLine[0],temp);
        }        
        	   
        br.close();
        System.out.printf("\nIn our dataset we have, approximately, %d games that haven't been reviewed by users and thus, won't be taken into account on further analysys\n",thisCounterNoReviews.getAproximateNumberOfEvents());
        //System.out.println(thisCounter.getAproximateNumberOfEvents());
        //System.out.println(thisCounter.getNumberOfEvents());        
        
        System.out.println();
        
   //PART 2 - Determine how many games are made by the same developer (and check by false positives)
        CountingBloomFilter ourFilter = new CountingBloomFilter(thisCounterTotal.getNumberOfEvents(),0.2);
        ourFilter.init();
        ourFilter.initHashFunction(60);
 
		for (int i = 0 ; i < gameDevs.size() ; i++) {
			ourFilter.insert(gameDevs.get(i));
		}
		
		//External list of companies that we KNOW FOR SURE aren't in the developer's list that we have
        ArrayList<String> companies = new ArrayList<String>();	//DataSet: Developers
    	br = new BufferedReader(new FileReader("companiesList.txt"));
        while((line = br.readLine()) != null) {
        	companies.add(line);
        }
        
        //Check for false positives
        int fp = 0;
        boolean r;
        for (int i = 0 ; i < companies.size() ; i++) {
            r = ourFilter.check(companies.get(i));
            if (r)
                fp++;
        }
        System.out.printf("Number of false positives: %d\n", fp);
        
        System.out.println("\n==========================================================");
        System.out.println("=========Number of games made by each developer:==========");
        System.out.println("==========================================================");
        
        //Count number of games produced by each company
        Set<String> devsSet = new HashSet<String>(gameDevs);
        String[] uniqueDevs = new String[devsSet.size()];
        int i = 0;
        Integer[] values = new Integer[devsSet.size()];
        for (String s : devsSet) {
            uniqueDevs[i] = s;
            i++;
        }
        for (i = 0; i < devsSet.size(); i++) {
            values[i] = ourFilter.count(uniqueDevs[i]);
            System.out.printf("%s developed: %d out of %d games\n", uniqueDevs[i], values[i],thisCounterTotal.getNumberOfEvents());
        }
        
        //Determine which company made the most games
        int iMax = 0, max = 0;
        for (i = 0; i < uniqueDevs.length; i++) {
            if (values[i] > max) {
                max = values[i];
                iMax = i;
            }
        }
        
        System.out.println("==========================================================");
        System.out.printf("Developer who made the most games: %s	(%d games).\n", uniqueDevs[iMax], max);
        System.out.println("==========================================================");
        
        
		
   //PART 3 - For each game check for similar reviews for Spammers/Plagiarists/Same person with diferent username
        
	    Shingles shingles;
	    MinHash minHash; 
	    HashMap<String,Integer> bannableUsers = new HashMap<String,Integer>();
	    HashMap<String,ArrayList<String>> allSimilarities = new HashMap<String,ArrayList<String>>();
	
	    //Iterate over every game
		for (Entry<String, HashMap<String,String>> item : gameReviews.entrySet()) {
		    String game = item.getKey();
	        System.out.printf("Checking %s for similar reviews...\n",game);
		    HashMap<String,String> thisReviews = item.getValue();
		    shingles = new Shingles(thisReviews);
		    
		    minHash = new MinHash(shingles.convertShingles(), 200);
		    
		    ArrayList<String> similarities = minHash.getSimilarities(0.15);
		    
		    if(similarities.size() == 0) {
		    	continue;
		    }
		    
			allSimilarities.put(game, similarities);
		}	
	    System.out.println("\nGames with potential spam reviews:\n");
	    
		for (Entry<String, ArrayList<String>> game : allSimilarities.entrySet()) {
		    System.out.println("=====================================================================");
		    System.out.printf("%s\n", game.getKey());
		    System.out.println("=====================================================================");
		    
		    String[] thisSimilarity;
		    for(int j = 0 ; j < game.getValue().size() ; j++) {
		    	thisSimilarity = game.getValue().get(j).split(" ;- ");
		    	System.out.printf("User 1: %-15s   User 2: %-15s   Distance %-10f\n",thisSimilarity[1],thisSimilarity[2],Double.parseDouble(thisSimilarity[0]));
		    	
		    	for(int k = 1 ; k < 3; k++) {
			    	if(bannableUsers.get(thisSimilarity[k])==null) {
			    		bannableUsers.put(thisSimilarity[k], 1);
			    	}else {
			    		int newVal = bannableUsers.get(thisSimilarity[k]) + 1;
			    		bannableUsers.replace(thisSimilarity[k], newVal);
			    	}
		    	}
		    	
		    }
		    System.out.println("=====================================================================\n");
		}
		
	    System.out.println("\nList of Candidate Users for banning (and ammount of times they posted a similar review to that of another user):\n");
		for (Entry<String,Integer> users : bannableUsers.entrySet()) {
			System.out.printf("%-18s -> %6d \n", users.getKey(),users.getValue());
		}
		System.out.println();
    }
}
