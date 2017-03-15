package batalhaagil.ufrpe.iversonluis.batalhaagil.firebase;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Iverson Luís on 14/02/2017.
 */
@IgnoreExtraProperties
public class MapaJogadores {
    private List<String> mapaJogador;//enviou desafio

    public MapaJogadores(List<String> mapaJogador) {
        this.mapaJogador = mapaJogador;
    }

    public MapaJogadores(){

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
        result.put("mapaJogador", mapaJogador);
        return result;
    }
}
