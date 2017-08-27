import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * IMPLEMENT:
 * THIS PROBLEM CAN BE SOLVED IN DISTRIBUTED WAY!
 *
 * IMPLEMENT DISTRIBUTED SOLUTION:
 *
 * I.E. THE FUNCTION THAT CHECKS THE ASSIGNMENT, WHETHER IT IS SATISFIABLE OR NOT, CAN BE MADE DISTRIBUTED
 * */
public class Solve {
    /* STATIC FIELDS */
    private Random rand;

    /* LOCAL FIELDS*/
    private List<Integer []> clauses;
    private Map<Integer, Boolean> assignments;
    private Integer numberOfBooleanVariables;
    private Integer seed;
    private Integer numberOfProcessors;


    /* CONSTRUCTOR */
    public Solve(List<Integer[]> clauses, Integer numberOfBooleanVariables){
        this.rand = new Random(System.nanoTime());
        this.seed = 1;
        this.clauses = clauses;
        this.assignments = new HashMap<>(clauses.size());
        this.numberOfProcessors = 1;
        this.numberOfBooleanVariables = numberOfBooleanVariables;

    }

    public Solve(List<Integer[]> clauses, Integer numberOfBooleanVariables, Integer seed,
                 Integer numberOfProcessors){
        this.rand = new Random(System.nanoTime());
        this.seed = seed;
        this.clauses = clauses;
        this.assignments = new HashMap<>(clauses.size());
        this.numberOfProcessors = numberOfProcessors;
        this.numberOfBooleanVariables = numberOfBooleanVariables;

    }


    /* STATIC METHODS */
    private int randInt(int min, int max) {

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return rand.nextInt((max - min) + 1) + min;
    }


    /* PRIVATE METHODS */
    private boolean distribution() throws NoSuchFieldException{
        /* if seed is  -1, the distribution is uniform */
        if(this.seed == -1){
            /* this means that at the beginning all boolean variables are false */
            return false;
        }
        else{
            /* this means that the initial state is distributed all as true */
            return true;
        }
    }

    /* this method returns value of specific index */
    // NOTE: this method has indices only of positive index
    private boolean value(Integer index) throws NoSuchFieldException{

        if(!this.assignments.keySet().contains(index)){
            return this.distribution();
        }
        else{
            return this.assignments.get(index);
        }
    }

    /* this function returns clause value of boolean variable, remember some vars in clause might be negative / positive */
    private boolean clauseValue(Integer index) throws NoSuchFieldException{
        if(index>0){
            return this.value(Math.abs(index));
        }
        else{
            return !this.value(Math.abs(index));
        }
    }



    /**
     * THIS METHOD CAN BE DONE IN PARALLEL FASHION
     * */
    /* THIS METHOD CHECKS WHETHER CLAUSES ARE SATISFIED AND IF NOT CHANGES THE BREAKING CLAUSE */
    private boolean satisfied() throws NoSuchFieldException{
        /* initialize as true */
        boolean[] satisfied = new boolean[]{true};

        /**
         * THIS PARTICULAR METHOD CAN BE MADE EASILY PARALLEL
         * */
        /* iterate through the clauses */
        for(Integer[] clause: this.clauses){

            /*
            if(this.seed==-1) {
                System.out.println("clause is " + clause[0] + " " + clause[1]);

                // printing
                System.out.println("printing the clause");



            for(Integer cl: clause) {
                System.out.println("clause variable " + cl + " current value assignment " + this.value(Math.abs(cl)));
                System.out.println("clause variable " + cl + " current clauseValue assignment " +
                        this.clauseValue(cl));
            }


            }
            */


            satisfied[0] = satisfied[0] && (this.clauseValue(clause[0]) || this.clauseValue(clause[1]));

            /*
            if(this.seed==1) {
                System.out.println("is it still satisfied? " + satisfied[0]);

                System.out.println("----");
                System.out.println();
            }
            */


            /* if still satisfied then OK continue further */
            if(satisfied[0]){
                continue;
            }
            else{
                /* change ONE of the boolean variables in breaking clause, pick one UNIFORMLY @ RANDOM */
                if(randInt(0, 1) < 0.5){
                    /* change the first assigned value to the opposite */
                    this.assignments.put(Math.abs(clause[0]), !this.value(clause[0]));
                }
                else{
                    /* change the second assigned value to the opposite */
                    this.assignments.put(Math.abs(clause[1]), !this.value(clause[1]));
                }
                return false;
            }
        }
        return true;
    }


    /* PUBLIC METHODS */

    /* this function statistically solves the two sat problem */
    boolean solveTwoSat() throws NoSuchFieldException{
        //System.out.println("NUMBER OF TASKS TO SOLVE: " + 2* Math.pow(this.numberOfBooleanVariables, 2)/this.numberOfProcessors.doubleValue());

        //System.out.println("YOU HAVE : " + this.numberOfBooleanVariables + " NUMBER OF BOOLEAN VARIABLES");
        /* repeat 2 * n^2 times @ MAX */
        //System.out.println("NUMBER OF TASKS: " + 2*Math.pow(this.numberOfBooleanVariables, 2));

        long startTime = System.currentTimeMillis();

        for(int i = 0;
            i < Math.ceil(2*Math.pow(this.numberOfBooleanVariables, 2)/this.numberOfProcessors.doubleValue()); i++ ){

            if(i%15000==0) {
                System.out.println("you are doing number " + i);
                long estimatedTime = System.currentTimeMillis() - startTime;

                System.out.println("the time elapsed in milliseconds for concurrent solution: " + estimatedTime);
                startTime = System.currentTimeMillis();
            }


            /* run the satisfied function */
            if(this.satisfied()){
                System.out.println("satisfied already at position " + i);
                return true;
            }
        }
        return false;
    }

    /* this method prints current solution */
    void printSolution(){
        for(Integer index: this.assignments.keySet()){
            System.out.println("X_" + index + "---> " + this.assignments.get(index));
        }

        if(this.seed==-1) {
            System.out.println("The rest of boolean variables (if any) ---> false");
        }
        else{
            System.out.println("The rest of boolean variables (if any) ---> true");
        }
    }


}
