import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by Administrator on 7/16/17.
 */
public interface reading<T> {

    /**
     * function that reads clauses, returns the array of the :
     *
     * */
    Object readClauses(String nameOfFile, boolean concurent, int numberOfClauses) throws IOException;

    /**
     * reads number of clauses from the file
     * returns T
     */
    T readNumberOfClauses(String nameOfFile) throws IOException;


}
