package batalhaagil.ufrpe.iversonluis.batalhaagil.firebase;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Iverson Luís on 12/03/2017.
 */

public class Mapas {
    private Jogador player1;
    private Jogador player2;

    public Mapas(Jogador player1, Jogador player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public Mapas() {
    }

    public Jogador getPlayer1() {
        return player1;
    }

    public void setPlayer1(Jogador player1) {
        this.player1 = player1;
    }

    public Jogador getPlayer2() {
        return player2;
    }

    public void setPlayer2(Jogador player2) {
        this.player2 = player2;
    }
}
