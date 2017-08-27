import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

import static java.lang.Thread.sleep;

public class ConcurentSolve {



    //synchronized locks
    int count = 0;

    void incrementSync() {
        synchronized (this) {
            count = count + 1;
        }
    }

    private void synch() {

        ExecutorService executor = Executors.newFixedThreadPool(2);

        executor.submit(this::incrementSync);
        IntStream.range(0, 10000)
                .forEach(i -> executor.submit(this::incrementSync));

        executor.shutdown();

        System.out.println(this.count);  // 10000


    }



    public static void main(String[] args) {

        /**
        Runnable runnable = () -> {
            try {
                String name = Thread.currentThread().getName();
                System.out.println("Foo " + name);
                TimeUnit.SECONDS.sleep(1);
                System.out.println("Bar " + name);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();


        // executor services
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.submit(() -> {
            String threadName = Thread.currentThread().getName();
            System.out.println("Hello " + threadName);
        });

        // callables like runnables, but return value
        Callable<Integer> task = () -> {
            try {
                TimeUnit.SECONDS.sleep(1);
                System.out.println(" handling callable result 123");
                return 123;
            }
            catch (InterruptedException e) {
                throw new IllegalStateException("task interrupted", e);
            }
        };

        Future<Integer> future = executor.submit(task);

        Integer result = null;
        try {
            result = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if(future.isDone())
            System.out.println("returned future is " + result);


        // shut down the executor smoothly
        try {
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
        catch (InterruptedException e){
            System.err.println("tasks interrupted");
        }
        finally {
            if (!executor.isTerminated()) {
                System.err.println("cancel non-finished tasks");
            }
            executor.shutdownNow();
            System.out.println("shutdown finished");
        }

        System.out.println("starting new var");
        */

        // calling method increment() concurrently
        ConcurentSolve slv = new ConcurentSolve();

        slv.synch();




        ExecutorService executor_III = Executors.newFixedThreadPool(2);
        ReentrantLock lock = new ReentrantLock();

        executor_III.submit(() -> {
            lock.lock();
            try {
                sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        });

        executor_III.submit(() -> {
            System.out.println("Locked: " + lock.isLocked());
            System.out.println("Held by me: " + lock.isHeldByCurrentThread());
            boolean locked = lock.tryLock();
            System.out.println("Lock acquired: " + locked);
        });

        executor_III.shutdown();
    }

    private class CancellableRunnable {
    }
}
