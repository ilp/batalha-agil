package batalhaagil.ufrpe.iversonluis.batalhaagil.firebase;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import batalhaagil.ufrpe.iversonluis.batalhaagil.Arsenal;

/**
 * Created by Iverson Luís on 12/03/2017.
 */
@IgnoreExtraProperties
public class Jogador {
    private ArsenalJogadores arsenal;
    private List<String> mapaJogador;

    public Jogador(ArsenalJogadores arsenal, List<String> mapaJogador) {
        this.arsenal = arsenal;
        this.mapaJogador = mapaJogador;
    }

    public Jogador() {
    }

    public ArsenalJogadores getArsenal() {
        return arsenal;
    }

    public void setArsenal(ArsenalJogadores arsenal) {
        this.arsenal = arsenal;
    }

    public List<String> getMapaJogador() {
        return mapaJogador;
    }

    public void setMapaJogador(List<String> mapaJogador) {
        this.mapaJogador = mapaJogador;
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("arsenal", arsenal);
        result.put("mapaJogador", mapaJogador);

        return result;
    }
}