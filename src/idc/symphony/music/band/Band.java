package idc.symphony.music.band;

import org.jfugue.midi.MidiDictionary;

import java.util.HashMap;
import java.util.Map;

public class Band {

    Faculty bmLaw;
    Faculty bmGovernDiplo;
    Faculty bmBusiness;
    Faculty bmCS;
    Faculty bmCommunication;
    Faculty bmEconomy;
    Faculty bmEntrepreneurship;
    Faculty bmAlumni;
    Faculty bmSocialCommit;
    Faculty bmSports;
    Faculty bmInternational;
    Faculty bmAdministration;
    Faculty bmPsychology;
    Faculty bmSustainability;

    private Map<Integer, Faculty> facultyCodeToBandMember = new HashMap<Integer, Faculty>() {
        {

            this.put(1, bmLaw);
            this.put(2, bmGovernDiplo);
            this.put(3, bmBusiness);
            this.put(4, bmCS);
            this.put(5, bmCommunication);
            this.put(6, bmEconomy);
            this.put(7, bmEntrepreneurship);
            this.put(9, bmAlumni);
            this.put(10, bmSocialCommit);
            this.put(11, bmSports);
            this.put(13, bmInternational);
            this.put(14, bmAdministration);
            this.put(15, bmPsychology);
            this.put(16, bmSustainability);

        }
    };

    public Band() {

        bmLaw = new Law
                (MidiDictionary.INSTRUMENT_STRING_TO_BYTE.get("VIOLIN"));

        bmGovernDiplo = new GovernDiplo
                (MidiDictionary.INSTRUMENT_STRING_TO_BYTE.get("TRUMPET"));

        bmBusiness = new Business
                (MidiDictionary.INSTRUMENT_STRING_TO_BYTE.get("GUITAR"));

        bmCS = new CS
                (MidiDictionary.INSTRUMENT_STRING_TO_BYTE.get("LEAD_SQUARE"));

        bmCommunication = new Communication
                (MidiDictionary.INSTRUMENT_STRING_TO_BYTE.get("CELLO"));

        bmEconomy = new Economy
                (MidiDictionary.INSTRUMENT_STRING_TO_BYTE.get("TREMOLO_STRINGS"));

        bmEntrepreneurship = new Entrepreneurship
                (MidiDictionary.INSTRUMENT_STRING_TO_BYTE.get("PAD_CHOIR"));

        bmAlumni = new Alumni
                (MidiDictionary.INSTRUMENT_STRING_TO_BYTE.get("FIDDLE"));

        bmSocialCommit = new SocialCommit
                (MidiDictionary.INSTRUMENT_STRING_TO_BYTE.get("FRENCH_HORN"));

        bmSports = new Sports
                (MidiDictionary.INSTRUMENT_STRING_TO_BYTE.get("WHISTLE"));

        bmInternational = new International
                (MidiDictionary.INSTRUMENT_STRING_TO_BYTE.get("KALIMBA"));

        bmAdministration = new Administration
                (MidiDictionary.INSTRUMENT_STRING_TO_BYTE.get("STEEL_DRUMS"));

        bmPsychology = new Psychology
                (MidiDictionary.INSTRUMENT_STRING_TO_BYTE.get("CONTRABASS"));

        bmSustainability = new Sustainability
                (MidiDictionary.INSTRUMENT_STRING_TO_BYTE.get("PICCOLO"));

    }

    public Map<Integer, Faculty> bandMembers() {
        return facultyCodeToBandMember;
    }
}
