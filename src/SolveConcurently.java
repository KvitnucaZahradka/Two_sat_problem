import java.util.*;
import java.util.concurrent.*;

public class SolveConcurently {

    /* STATIC FIELDS */
    private static int counter = 0;
    private Random rand;
    private static Integer SEED = -1;
    //private static ExecutorService executor = Executors.newFixedThreadPool(2);

    private ConcurrentLinkedDeque<Integer[]> clauses;

    private ConcurrentMap<Integer, Boolean> assignments;
    private Integer numberOfBooleanVariables;

    private boolean[] shouldRun, foundSolution;



    /* CONSTRUCTOR */
    public SolveConcurently(ConcurrentLinkedDeque<Integer[]> clauses, Integer numberOfBooleanVariables){
        this.rand = new Random(System.nanoTime());

        this.clauses = clauses;
        this.assignments = new ConcurrentHashMap<>(numberOfBooleanVariables);
        this.numberOfBooleanVariables = numberOfBooleanVariables;

        this.foundSolution = new boolean[]{false};
        this.shouldRun = new boolean[]{true};
    }


    /* STATIC METHODS */
    private int randInt(int min, int max) {

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return this.rand.nextInt((max - min) + 1) + min;
    }

    public static void stop(ExecutorService executor) {
        try {
            executor.shutdown();
            executor.awaitTermination(60, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            System.err.println("termination interrupted");
        }
        finally {
            if (!executor.isTerminated()) {
                System.err.println("killing non-finished tasks");
            }
            executor.shutdownNow();
        }
    }


    /* PRIVATE METHODS */
    private void checkClause(Iterator<Integer[]> iter) throws NoSuchFieldException{

        //synchronized (this) {
        Integer[] clause;
        boolean[] sat = new boolean[]{true};


        while (iter.hasNext()) {

            clause = iter.next();
            //System.out.println("clause is " + clause[0] + " " + clause[1]);

            sat[0] =  (this.clauseValue(clause[0]) || this.clauseValue(clause[1]));

            // if the the field is not satisfied, then change the
            //System.out.println("still satisfied CLAUSE " + clause[0] + " " + clause[1] +  " is " + sat[0]);

            if (this.shouldRun[0]) {

                if(!sat[0]){
                    //System.out.println("here you are");

                    // tell globally that the given state is not satisfied and you should not run on current state
                    this.shouldRun[0] = false;

                    // change one boolean variable in the assignment
                    if (this.randInt(0, 1) < 0.5) {
                    /* change the first assigned value to the opposite */
                        this.assignments.put(Math.abs(clause[0]), !this.value(clause[0]));

                    } else {
                    /* change the second assigned value to the opposite */
                        this.assignments.put(Math.abs(clause[1]), !this.value(clause[1]));
                    }
                    break;
                }

                //cntr++;
            }
            else {
                // if it should not run, then unlock and break
                break;
            }
        }
    }

    private void solve() throws NoSuchFieldException{
        int counter = 0;
        //System.out.println("number of boolean variables is " + this.numberOfBooleanVariables);
        //System.out.println("NUMBER OF TASKS: " + 2*Math.pow(this.numberOfBooleanVariables, 2));

        long startTime = System.currentTimeMillis();

        ExecutorService executor;

        while(counter<2*Math.pow(this.numberOfBooleanVariables, 2)){

            Iterator<Integer[]> iter = this.clauses.iterator();

            Runnable task = () -> {
                try {
                    this.checkClause(iter);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
                // throw new RuntimeException();
            };

            // reset the satisfied to true again
            this.shouldRun[0] = true;

            int nmbr = Runtime.getRuntime().availableProcessors();
            executor = Executors.newFixedThreadPool(nmbr);
            Future<?>[] futures = new Future[nmbr];

            // do the concurrent calculations on checking the current STATE
            for(int i = 0; i<nmbr; i++) {
                futures[i] = executor.submit(task);
            }


            try {
                for(int i = 0; i<nmbr; i++) {
                    futures[i].get();
                }
            }
            catch (ExecutionException e){
                e.printStackTrace();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            executor.shutdown();

            //System.out.println("the number of iterations " + 2*Math.pow(this.numberOfBooleanVariables, 2) );
            if(counter%20000==0) {
                System.out.println("you are doing number " + counter);
                long estimatedTime = System.currentTimeMillis() - startTime;

                System.out.println("the time elapsed in milliseconds for concurrent solution: " + estimatedTime);
                startTime = System.currentTimeMillis();
            }

            if(!iter.hasNext() && this.checkAssignment()){
                this.foundSolution[0] = true;
                break;
            }
            counter++;
            //System.out.println("counter is " + counter);
        }



    }

    boolean solveTwoSat() throws NoSuchFieldException {
        // first solve the instance
        this.solve();

        return this.foundSolution[0];
    }

    /* this method prints current solution */
    void printSolution(){
        for(Integer index: this.assignments.keySet()){
            System.out.println("X_" + index + "---> " + this.assignments.get(index));
        }

        System.out.println("The rest of boolean variables (if any) ---> false");
    }


    private boolean distribution(Integer index) throws NoSuchFieldException{
        /* if seed is  -1, the distribution is uniform */
        if(SolveConcurently.SEED == -1){
            /* this means that at the beginning all boolean variables are false */
            return false;
        }
        else{
            NoSuchFieldException e = new NoSuchFieldException();
            System.out.println("YOUR SEED " + SolveConcurently.SEED +  " IS NOT YET IMPLEMENTED");
            throw e;
        }
    }

    /* this method returns value of specific index */
    // NOTE: this method has indices only of positive index
    private boolean value(Integer index) throws NoSuchFieldException{
        try {
            if (!this.assignments.keySet().contains(index)) {
                return this.distribution(index);
            } else {
                return this.assignments.get(index);
            }
        }
        catch (NoSuchFieldException e){
            throw e;
        }
    }

    /* this function returns clause value of boolean variable, remember some vars in clause might be negative / positive */
    private boolean clauseValue(Integer index) throws NoSuchFieldException{
        try {
            if (index > 0) {
                return value(Math.abs(index));
            } else {
                return !value(Math.abs(index));
            }
        }
        catch (NoSuchFieldException e){
            throw e;
        }
    }


    /* this function checks whether final assignment is satisfied by the final values */
    private boolean checkAssignment(){
        Iterator<Integer[]> iter = this.clauses.iterator();
        Integer[] clause;

        boolean[] result = new boolean[]{true};

        while(iter.hasNext()){
            clause = iter.next();

            try {
                result[0] = (this.clauseValue(clause[0]) || this.clauseValue(clause[1]));

                if(!result[0]){
                    return result[0];
                }

            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return result[0];
    }


}
