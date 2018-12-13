import java.io.File;
import java.io.IOException;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;


public class IDCSymphony {

    public static void main(String [] args) {
        try {
            Database db = new DatabaseBuilder(new File("data/IDC Events.mdb"))
                .setReadOnly(true)
                .open();

            } catch (IOException e){
            e.printStackTrace();
        } finally {

        }
    }

}