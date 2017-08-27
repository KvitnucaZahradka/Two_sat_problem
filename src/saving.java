import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 7/16/17.
 */
interface saving {

    /* function that will save*/
    void ListSave(String nameOfFile, List<?> listToSave) throws IOException;

    /* private method that erases the old list from local drive */
    void EraseOldList() throws NullPointerException;
}
