import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Administrator on 7/16/17.
 */
public class Read_and_save implements reading, saving {

    /* STATIC FIELDS */
    private static String FILENAME;

    /* LOCAL FIELDS */
    Integer numberOfBooleanVariables;


    @Override
    public Integer readNumberOfClauses(String nameOfFile) throws IOException{
        String line;

        try{
            File file = new File(nameOfFile);
            FileReader fileReader = new FileReader(file);

            BufferedReader bufferedReader = new BufferedReader(fileReader);

            /* read first line */
            line = bufferedReader.readLine();

            /* closing the fileReader */
            fileReader.close();

            return Integer.parseInt(line);

        }
        catch (IOException e){
            e.printStackTrace();
            throw e;
        }
    }



    @Override
    public Object readClauses(String nameOfFile, boolean concurent, int numberOfClauses) throws IOException{
        String line;
        String[] arr;
        Object temp_list;

        if(concurent) {
            temp_list = new ConcurrentLinkedDeque<>();
        }
        else{
            temp_list = new LinkedList<>();
        }

        // HERE WE IMPLEMENT THE READING FUNCTIONALITY
        try{
            Set<Integer> set = new HashSet<>();
            LineNumberReader rdr = new LineNumberReader(new FileReader(nameOfFile));

            while( (line = rdr.readLine()) != null){
                if(rdr.getLineNumber()>1){
                    arr = line.split("\\s+");
                    //System.out.println(rdr.getLineNumber());

                    Integer intA = Integer.parseInt(arr[0]);
                    Integer intB = Integer.parseInt(arr[1]);

                    if(concurent){
                        ((ConcurrentLinkedDeque<Integer[]>) temp_list).add(new Integer[]{ intA, intB});
                    }
                    else{
                        ((LinkedList<Integer[]>) temp_list).add(new Integer[]{ intA, intB});
                    }

                    /* adding to hash set, to get a number of independent variables */
                    set.add(Math.abs(intA));
                    set.add(Math.abs(intB));
                }
            }

            /* figuring out how many boolean variables we have */
            this.numberOfBooleanVariables = set.size();
        }
        catch (IOException e){
            e.printStackTrace();
            throw e;
        }
        return temp_list;
    }


    @Override
    public void ListSave(String nameOfFile, List<?> listToSave) throws IOException{

        /* erase the last map */
        this.EraseOldList();

        /* get the new filename with the path */
        Read_and_save.FILENAME = System.getProperty("user.dir") + "/" + nameOfFile + ".temp";

        /* saving the file */
        try {
            FileOutputStream fos = new FileOutputStream(nameOfFile + ".temp");
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(listToSave);
            oos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void EraseOldList() throws NullPointerException{
        try{
            FileDelete delete = new FileDelete(Read_and_save.FILENAME);
            // System.out.println("DELETE, filename is " + FileSave.FILENAME);
            delete.deleteFile();
        }
        catch (java.lang.NullPointerException e){
            e.printStackTrace();
            throw e;
        }
    }



    public static void main(String[] args){
        System.out.println("testing library functionality");

    }
}























