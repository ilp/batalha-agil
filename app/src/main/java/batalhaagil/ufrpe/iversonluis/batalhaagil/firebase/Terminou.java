package batalhaagil.ufrpe.iversonluis.batalhaagil.firebase;

/**
 * Created by Iverson Luís on 12/03/2017.
 */

public class Terminou {
    private boolean terminou;

    public Terminou(boolean terminou) {
        this.terminou = terminou;
    }

    public boolean isTerminou() {
        return terminou;
    }

    public void setTerminou(boolean terminou) {
        this.terminou = terminou;
    }

    public Terminou() {
    }
}
