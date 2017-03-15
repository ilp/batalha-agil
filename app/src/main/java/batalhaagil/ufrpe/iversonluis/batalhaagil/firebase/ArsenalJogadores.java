package batalhaagil.ufrpe.iversonluis.batalhaagil.firebase;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Iverson Luís on 14/02/2017.
 */
@IgnoreExtraProperties
public class ArsenalJogadores {
    private List<Integer> arsenalMissil;//enviou desafio
    private List<Integer> arsenalRadar;//aceitou desafio
    private int arsenalAirPlane;

    public ArsenalJogadores(List<Integer> arsenalMissil, List<Integer> arsenalRadar, int arsenalAirPlane) {
        this.arsenalMissil = arsenalMissil;
        this.arsenalRadar = arsenalRadar;
        this.arsenalAirPlane = arsenalAirPlane;
    }

    public ArsenalJogadores() {
    }

    public List<Integer> getArsenalMissil() {
        return arsenalMissil;
    }

    public void setArsenalMissil(List<Integer> arsenalMissil) {
        this.arsenalMissil = arsenalMissil;
    }

    public List<Integer> getArsenalRadar() {
        return arsenalRadar;
    }

    public void setArsenalRadar(List<Integer> arsenalRadar) {
        this.arsenalRadar = arsenalRadar;
    }

    public int getArsenalAirPlane() {
        return arsenalAirPlane;
    }

    public void setArsenalAirPlane(int arsenalAirPlane) {
        this.arsenalAirPlane = arsenalAirPlane;
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("arsenalMissil", arsenalMissil);
        result.put("arsenalRadar",arsenalRadar);
        result.put("arsenalAirPlane", arsenalAirPlane);

        return result;
    }
}
