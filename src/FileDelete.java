import java.io.File;

class FileDelete {


    /* FIELDS */
    private String pathToFile;

    /* CONSTRUCTOR */
    FileDelete(String fileToDelete){

        this.pathToFile =  fileToDelete;
    }

    /* PUBLIC METHODS */
    void deleteFile(){
        File file = new File(this.pathToFile);
        this.delete(file);
    }

    /* PRIVATE METHODS */
    private void delete(File file) throws NullPointerException {
        boolean success;

        try{
            if (file.isDirectory()) {
                for (File deleteMe : file.listFiles()) {
                    // recursive delete
                    delete(deleteMe);
                }
            }
            success = file.delete();
        }
        catch (NullPointerException e){
            e.printStackTrace();
            throw e;
        }

        if (success) {
            System.out.println(file.getAbsoluteFile() + " Deleted");
        } else {
            System.out.println(file.getAbsoluteFile() + " Deletion failed!!!");
        }
    }

}
