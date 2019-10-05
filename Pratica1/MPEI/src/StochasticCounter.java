package src;


public class StochasticCounter {
    /***************************************************************************************************
     *                                           Attributes                                           *
     **************************************************************************************************/
    private double countingProb;     //Probability of counting an event
    private int counter;
    private int numberOfEvents;     //Total amount of events given

    /*************************************************************************************************
     *                                         Constructors                                         *
     ************************************************************************************************/
    /**
     * Purpose:
     *		Creates a new instance of the Stochastic Counter, initializing it's counting probability
     *	and counter variables;
     *
     * Argument:
     * 		-> double countingProb
     */
    public StochasticCounter(double countingProb) {
        this.countingProb = countingProb;
        this.counter = 0;
    }


    /***********************************************************************************************
     *                                             Getters                                        *
     **********************************************************************************************/
    /**
     * Purpose:
     *		Returns the counting probability
     *
     * Return:
     * 		-> double:
     */
    public double getCountingProb() {
        return countingProb;
    }


    /**
     * Purpose:
     *		Returns an accurate number of events that have tried to be counted up until the moment this function was called
     *
     * Return:
     * 		-> int:
     */
    public int getNumberOfEvents() {
        return numberOfEvents;
    }
    
    /**
     * Purpose:
     *		Returns an approximate number of events that have tried to be counted up until the moment this function was called
     *
     * Return:
     * 		-> int:
     */
    public int getAproximateNumberOfEvents() {
        return (int) (this.counter*(1/this.countingProb));
    }

    /**
     * Purpose:
     *		Returns the value of the counter
     *
     * Return:
     * 		-> int:
     */
    public int getCounter() {
        return this.counter;
    }


    /***********************************************************************************************
     *                                             Setters                                        *
     **********************************************************************************************/
    /**
     * Purpose:
     *		Manually changes the value of the counting probability
     *
     * Argument:
     * 		-> double countingProb:
     */
    public void setCountingProb(double countingProb) {
        this.countingProb = countingProb;
    }


    /***********************************************************************************************
     *                                        Public Methods                                      *
     **********************************************************************************************/
    /**
     * Purpose:
     *		Attempts to increment a counter (equals to trying to count an event)
     *
     * Return:
     * 		-> int:
     */
    public int incrementCounter() {
        this.numberOfEvents++;
        if (Math.random() < this.countingProb) {
            this.counter++;
        }
        return this.counter;
    }

    /**
     * Purpose:
     *		Resets the counter
     */
    public void resetCounter() {
        this.counter = 0;
        this.numberOfEvents = 0;
    }

    /**
     * Purpose:
     *		Returns the average number of events that should be counted
     *
     * Return:
     * 		-> double:
     */
    public double getMeanOfEvents() {
        return this.numberOfEvents * this.countingProb;
    }

    /**
     * Purpose:
     *		Returns the average variance of events that should be counted
     *
     * Return:
     * 		-> double:
     */
    public double getVariance() {
        double var = this.countingProb - Math.pow(this.countingProb, 2);
        return var * this.numberOfEvents;
    }

    /**
     * Purpose:
     *		Returns the factorial of a given number 
     *		ONLY WORKS FOR SMALL n's
     *
     * Argument:
     * 		-> int n:
     *
     * Return:
     * 		-> double:
     */
    public double factorial(int n) {
        if (n == 0)
            return 1;
        else
            return (n * factorial(n - 1));
    }

    /**
     * Purpose:
     *		Returns the probability of counting n out of the total ammount of events
     *
     * Argument:
     * 		-> int n:
     *
     * Return:
     * 		-> double:
     */
    public double probSum(int n) {    //Probability of having, in total, counted n events
        double comb = factorial(this.numberOfEvents) / (factorial(n) * factorial(this.numberOfEvents - n));
        return comb * Math.pow(this.countingProb, n) * Math.pow((1 - this.countingProb), this.numberOfEvents - n);
    }
}		
