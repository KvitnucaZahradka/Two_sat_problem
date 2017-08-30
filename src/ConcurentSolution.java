import org.jetbrains.annotations.Contract;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurentSolution {
    private ExecutorService executor;
    private int numberOfProcessors;
    private int numberOfBoolVariables;
    private LinkedList<Integer[]> clauses;




    ConcurentSolution(LinkedList<Integer[]> clauses, int numberOfBoolVariables){

        this.numberOfProcessors = Runtime.getRuntime().availableProcessors();
        this.clauses = clauses;
        this.numberOfBoolVariables = numberOfBoolVariables;

        this.executor = Executors.newFixedThreadPool(this.numberOfProcessors);

        System.out.println("NUMBER OF PROCESSORS IS : " + this.numberOfProcessors);

    }

    @Contract(pure = true)
    private Callable<Boolean> call(Solve solve){
        Callable<Boolean> task = () -> {
            try {
                return solve.solveTwoSat();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                throw e;
            }
            // throw new RuntimeException();
        };

        return task;
    }


    public void solve(){
        Solve solve;
        List<Callable<Boolean>> callables = new ArrayList<>(this.numberOfProcessors);


        for(int seed = 0; seed<this.numberOfProcessors; seed++){
            solve = new Solve(this.clauses, this.numberOfBoolVariables, seed, this.numberOfProcessors);

            callables.add(this.call(solve));
        }

        Boolean result = null;
        try {
            result = this.executor.invokeAny(callables);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println("THE TWO SAT INSTANCE IS " + result);

        this.executor.shutdown();

    }

}
