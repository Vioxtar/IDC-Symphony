import idc.symphony.music.band.CS;
import idc.symphony.music.band.Faculty;
import org.jfugue.player.Player;
import org.jfugue.theory.Key;

public class IDCSymphony {
    public static void main(String [] args) {
        Faculty cs = new CS();

        Player player = new Player();
        Key key = new Key("Gmaj");
        player.play(cs.playMainMelody(1000, key));



    }
}