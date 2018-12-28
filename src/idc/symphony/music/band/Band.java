package idc.symphony.music.band;

import java.util.HashMap;
import java.util.Map;

public class Band {



    Faculty bmCS;

    private Map<Integer, Faculty> facultyCodeToBandMember = new HashMap<Integer, Faculty>() {
        {
            this.put(0, null); // Administration
            this.put(1, null); // Law
            this.put(2, null); // Government & Diplomacy
            this.put(3, null); // Business
            this.put(4, bmCS); // Computer Science
            this.put(5, null); // Communication
            this.put(6, null); // Economy
            this.put(7, null); // Entrepreneurship
            this.put(8, null); // Counter-Terrorism
            this.put(15, null); // Psychology
            this.put(16, null); // Sustainability
        }
    };

    public Band() {
        bmCS = new CS((byte)40);
    }

    public Map<Integer, Faculty> bandMembers() {
        return facultyCodeToBandMember;
    }
}
