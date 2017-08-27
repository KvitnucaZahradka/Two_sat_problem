import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by Administrator on 7/16/17.
 */
public class Two_sat {

    /* LOCAL FIELDS */
    private String filename;

    private Object clauses;

    private Read_and_save read;


    /* CONSTRUCTOR */
    public Two_sat(String filename){
        this.filename = filename;
    }



    /* PRIVATE METHODS */



    /* PUBLIC METHODS */
    public void solve2sat() throws IOException, NoSuchFieldException{
        ExecutorService executor;

        /* read clauses */
        this.read = new Read_and_save();

        this.clauses = read.readClauses(this.filename, false, this.read.readNumberOfClauses(this.filename));


        //int nmbr = Runtime.getRuntime().availableProcessors();
        int nmbr = 2;
        executor = Executors.newFixedThreadPool(nmbr);
        Future<?> future, future1;

        //System.out.println(nmbr);
        /* this is awkward, but ok for now is ok */
        if(nmbr == 1){
            Object satisfiable;
            System.out.println("Number of available processors is " + nmbr);
            Solve solve = new Solve((LinkedList<Integer[]>)this.clauses,
                    this.read.numberOfBooleanVariables, -1, 1);

            Runnable task = () -> {
                try {
                    solve.solveTwoSat();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
                // throw new RuntimeException();
            };

            future = executor.submit(task);

            try {
                satisfiable = future.get();
                executor.shutdown();

                if((boolean) satisfiable){
                    System.out.println("The assignment is satisfiable");
                }
                else{
                    System.out.println("The assignment is unsatisfiable");
                }


            }
            catch (ExecutionException e){
                e.printStackTrace();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }




        }
        else if(nmbr == 2){

            Object satisfiable, satisfiable1;
            System.out.println("Number of available processors is " + nmbr);

            Solve solve = new Solve((LinkedList<Integer[]>)this.clauses,
                    this.read.numberOfBooleanVariables, -1, 2);

            Solve solve1 = new Solve((LinkedList<Integer[]>)this.clauses,
                    this.read.numberOfBooleanVariables, 1, 2);

            Callable<Boolean> task = () -> {
                try {
                    return solve.solveTwoSat();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                    throw e;
                }
                // throw new RuntimeException();
            };

            Callable<Boolean> task1 = () -> {
                try {
                    return solve1.solveTwoSat();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                    throw e;
                }
                // throw new RuntimeException();
            };

            future = executor.submit(task);
            future1 = executor.submit(task1);

            try {

                satisfiable = future.get();

                System.out.println("Satisfiable is " + satisfiable);


                if((boolean) satisfiable ){
                    System.out.println("The assignment is satisfiable");
                }
                else{
                    try {

                        satisfiable1 = future1.get();

                        System.out.println("Satisfiable is ----> " + satisfiable1);


                        if((boolean) satisfiable1 ){
                            System.out.println("The assignment is satisfiable");
                        }
                        else{
                            System.out.println("The assignment is unsatisfiable");
                        }

                    }
                    catch (ExecutionException e){
                        e.printStackTrace();
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

                //solve1.printSolution();

            }
            catch (ExecutionException e){
                e.printStackTrace();

                try {

                    satisfiable1 = future1.get();

                    if((boolean) satisfiable1 ){
                        System.out.println("The assignment is satisfiable");
                    }
                    else{
                        System.out.println("The assignment is unsatisfiable");
                    }

                }
                catch (ExecutionException e1){
                    e1.printStackTrace();
                }
                catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

            }
            catch (InterruptedException e2) {
                e2.printStackTrace();

                try {

                    satisfiable1 = future1.get();



                    if((boolean) satisfiable1 ){
                        System.out.println("The assignment is satisfiable");
                    }
                    else{
                        System.out.println("The assignment is unsatisfiable");
                    }

                }
                catch (ExecutionException e){
                    e.printStackTrace();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }


            executor.shutdown();





        }
        else{
            System.out.println("tuna si");
            return;
        }

    }



    public void solve2sat(boolean verbose, boolean concurrent) throws IOException, NoSuchFieldException{
        boolean satisfialble, satisfialble_concurently;

        /* read clauses */
        this.read = new Read_and_save();

        /* solve clauses */
        // Solve<? extends Integer> solve = new Solve<>(this.clauses);
        if(!concurrent) {
            this.clauses = read.readClauses(this.filename, concurrent, this.read.readNumberOfClauses(this.filename));


            Solve solve = new Solve((LinkedList<Integer[]>)this.clauses, this.read.numberOfBooleanVariables);
            /* on one core, NOT CONCURRENTLY */

            long startTime = System.currentTimeMillis();

            satisfialble = solve.solveTwoSat();

            long estimatedTime = System.currentTimeMillis() - startTime;

            System.out.println("the time elapsed in milliseconds for non - concurrent solution: " + estimatedTime);

            if(satisfialble) {
                System.out.println("the given two sat problem is satisfiable non-concurrently");

                if(verbose){
                    solve.printSolution();
                }
            }
            else{
                System.out.println("the given two sat problem is probably unsatisfiable non-concurrently");
            }

        }
        else {
            System.out.println("DOING CONCURENT HERE");
            this.clauses = read.readClauses(this.filename, concurrent, this.read.readNumberOfClauses(this.filename));


            SolveConcurently solve_concurently = new SolveConcurently((ConcurrentLinkedDeque<Integer[]>)this.clauses,
                    this.read.numberOfBooleanVariables);

            long startTime = System.currentTimeMillis();

            /* on two cores, CONCURRENTLY */
            satisfialble_concurently = solve_concurently.solveTwoSat();

            long estimatedTime = System.currentTimeMillis() - startTime;


            System.out.println("the time elapsed in milliseconds for concurrent solution: " + estimatedTime);


            if(satisfialble_concurently) {
                System.out.println("the given two sat problem is satisfiable concurrently");

                if(verbose){
                    solve_concurently.printSolution();
                }

            }
            else{
                System.out.println("the given two sat problem is probably unsatisfiable concurrently");
            }

        }

    }

    /* MAIN METHOD */
    /* the main method, where the two sat solution is executed */
    public static void main(String[] args) throws NoSuchFieldException, IOException{
        /* initialize class TwoSat */
        Two_sat two_sat = new Two_sat("2sat2.txt");

        /* solve */
        //two_sat.solve2sat(true, false);


        /* solve */
        //two_sat.solve2sat(true, false);
        two_sat.solve2sat();
    }
}
