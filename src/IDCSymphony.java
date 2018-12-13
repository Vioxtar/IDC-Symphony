import java.io.File;
import java.io.IOException;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;


public class IDCSymphony {

    public static void main(String [] args) {

        try (Database db = new DatabaseBuilder(new File("data/IDC Events.accdb"))
            .setReadOnly(true)
            .open()) {

            Table table = db.getTable("DiscreteEvents");
            for(Row row : table) {
                System.out.println(row);
            }

        } catch (IOException e){
            e.printStackTrace();
        }
    }

}