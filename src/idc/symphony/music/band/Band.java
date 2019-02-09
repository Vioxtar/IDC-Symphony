package idc.symphony.music.band;

import org.jfugue.midi.MidiDictionary;

import java.util.HashMap;
import java.util.Map;

public class Band {

    public static final int RHYTHM_FACULTY = 14;

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

    private Map<Integer, Faculty> facultyCodeToBandMember = new HashMap<>();

    public Band() {

        bmLaw = new Law
                (MidiDictionary.INSTRUMENT_STRING_TO_BYTE.get("SYNTH_STRINGS_2"));

        bmGovernDiplo = new GovernDiplo
                (MidiDictionary.INSTRUMENT_STRING_TO_BYTE.get("TRUMPET"));

        bmBusiness = new Business
                (MidiDictionary.INSTRUMENT_STRING_TO_BYTE.get("GUITAR"));

        bmCS = new CS
                (MidiDictionary.INSTRUMENT_STRING_TO_BYTE.get("ACOUSTIC_BASS"));

        bmCommunication = new Communication
                (MidiDictionary.INSTRUMENT_STRING_TO_BYTE.get("CELLO"));

        bmEconomy = new Economy
                (MidiDictionary.INSTRUMENT_STRING_TO_BYTE.get("TREMOLO_STRINGS"));

        bmEntrepreneurship = new Entrepreneurship
                (MidiDictionary.INSTRUMENT_STRING_TO_BYTE.get("CHOIR"));

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
                (MidiDictionary.INSTRUMENT_STRING_TO_BYTE.get("PAN_FLUTE"));

        bmSustainability = new Sustainability
                (MidiDictionary.INSTRUMENT_STRING_TO_BYTE.get("PICCOLO"));

        facultyCodeToBandMember.put(1, bmLaw);
        facultyCodeToBandMember.put(2, bmGovernDiplo);
        facultyCodeToBandMember.put(3, bmBusiness);
        facultyCodeToBandMember.put(4, bmCS);
        facultyCodeToBandMember.put(5, bmCommunication);
        facultyCodeToBandMember.put(6, bmEconomy);
        facultyCodeToBandMember.put(7, bmEntrepreneurship);
        facultyCodeToBandMember.put(9, bmAlumni);
        facultyCodeToBandMember.put(10, bmSocialCommit);
        facultyCodeToBandMember.put(11, bmSports);
        facultyCodeToBandMember.put(13, bmInternational);
        facultyCodeToBandMember.put(14, bmAdministration);
        facultyCodeToBandMember.put(15, bmPsychology);
        facultyCodeToBandMember.put(16, bmSustainability);
    }

    public Map<Integer, Faculty> bandMembers() {
        return facultyCodeToBandMember;
    }
}
