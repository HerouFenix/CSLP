package Tests;

import Modules.StochasticCounter;

public class StochasticCounterTest {

    public static void main(String[] args) {

        /////////////////////////////////////TEST A/////////////////////////////////////
        //Imagining we have a data set with 100 000 elements && Counting Probability of 1/2
        double countingProb = 1.0 / 2.0;
        int events = 100000;

        System.out.printf("Test A:\n%d Cases with a %f probability of counting\n", events, countingProb);

        StochasticCounter ourCounter = new StochasticCounter(countingProb);

        //NUMBER OF CASES CONSIDERED (counter)
        //Theoretically, in this case we should have, in average, about 100000/2 events counted, so our counter should be at about 50 000 (give or take)
        for (int i = 0; i < events; i++) {
            ourCounter.incrementCounter();
        }
        System.out.printf("Counter Value = %d ; Aproximate number of events = %d\n\n", ourCounter.getCounter(), ourCounter.getAproximateNumberOfEvents()); //Checks out !!

        //MEAN OF CASES CONSIDERED
        //Theoretically, the mean of cases considered should be #ofCases * countingProbability. In our case, that would be 100000 * 1/2 = 50 000
        double mean = ourCounter.getMeanOfEvents();
        double tMean = 0 * 0.5 + 1 * 0.5 * events;
        System.out.printf("Theoretical Mean of cases counted = %f\n", tMean); //Checks out !!
        System.out.printf("Average ammount of cases counted = %f\n\n", mean); //Checks out !!


        //VARIANCE OF CASES CONSIDERED
        //The formula for the Variance of Cases Counted is the same as the normal formula for the Variance of a Random Variable. In truth the counter itself could be
        //considered a variable that takes the value 1 if a case is counted or 0 if its not !

        //So in our case, Var(X) = E[X^2] - E^2[X]
        // E[X^2] = 1/2 * 0 + 1/2 * 1^2 = 1/2 ; E^2[X] = 1/4 ;Var(X) = 1/2-1/4 = 1/2 ; But we want the Variance of cases counted out of K which we can get with: k * Var(X),
        //and since we have 100 000 cases, we get
        //Var(X) = Var(X1) + Var(X2) + ... + Var(X100000) = 100000/4 = 25 000
        double var = ourCounter.getVariance();
        double tVar = ((0 * 0.5 + Math.pow(1, 2) * 0.5) - Math.pow(tMean / events, 2)) * events;
        System.out.printf("Theoretical Variance of cases counted = %f\n", tVar); //Checks out !!
        System.out.printf("Variance of cases counted = %f\n\n", var); //Checks out !!


        /////////////////////////////////////TEST B/////////////////////////////////////
        //Imagining we have a data set with 50 elements && Counting Prob. of 1/32
        countingProb = 1.0 / 32.0;
        events = 50;

        System.out.printf("Test B:\n%d Cases with a %f probability of counting\n", events, countingProb);

        ourCounter.setCountingProb(countingProb);
        ourCounter.resetCounter();

        //NUMBER OF CASES CONSIDERED (counter)
        //Theoretically, in this case we should have, in average, about 100000/2 events counted, so our counter should be at about 50 000 (give or take)
        for (int i = 0; i < events; i++) {
            ourCounter.incrementCounter();
        }
        System.out.printf("Counter Value = %d ; Aproximate number of events = %d\n\n", ourCounter.getCounter(), ourCounter.getAproximateNumberOfEvents()); //Checks out !!

        //MEAN OF CASES CONSIDERED
        //Theoretically, the mean of cases considered should be #ofCases * countingProbability. In our case, that would be 100000 * 1/2 = 50 000
        mean = ourCounter.getMeanOfEvents();
        tMean = 0 * (1 - countingProb) + 1 * countingProb * events;
        System.out.printf("Theoretical Mean of cases counted = %f\n", tMean); //Theoretical Value
        System.out.printf("Average ammount of cases counted = %f\n\n", mean); //Checks out !!


        //VARIANCE OF CASES CONSIDERED
        //The formula for the Variance of Cases Counted is the same as the normal formula for the Variance of a Random Variable. In truth the counter itself could be
        //considered a variable that takes the value 1 if a case is counted or 0 if its not !

        //So in our case, Var(X) = E[X^2] - E^2[X]
        // E[X^2] = 1/2 * 0 + 1/2 * 1^2 = 1/2 ; E^2[X] = 1/4 ;Var(X) = 1/2-1/4 = 1/2 ; But we want the Variance of cases counted out of K which we can get with: k * Var(X),
        //and since we have 100 000 cases, we get
        //Var(X) = Var(X1) + Var(X2) + ... + Var(X100000) = 100000/4 = 25 000
        var = ourCounter.getVariance();
        tVar = ((0 * (1 - countingProb) + Math.pow(1, 2) * countingProb) - Math.pow(tMean / events, 2)) * events;
        System.out.printf("Theoretical Variance of cases counted = %f\n", tVar); //Theoretical Value
        System.out.printf("Variance of cases counted = %f\n\n", var); //Checks out !!


        //PROBABILITY OF N CASES BEING COUNTED
        //The formula for the probability of n cases being considered out of k, with a counting probability of p is:
        // C(k,n)*p^n * (1-p)^(k-n)
        //In our case, the probability of counting just, for example, 1 case should be: 50!/1!*(50-1)! * 1/64 ^5000 * 63/64^(50-1)
        double prob = ourCounter.probSum(1);
        System.out.printf("Prob of counting only 1 case out of 50 = %f\n\n", prob); //Checks out !!

    }
}
