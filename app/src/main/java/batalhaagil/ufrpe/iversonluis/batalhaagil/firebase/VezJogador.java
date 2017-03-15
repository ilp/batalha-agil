package batalhaagil.ufrpe.iversonluis.batalhaagil.firebase;

/**
 * Created by Iverson Luís on 12/03/2017.
 */

public class VezJogador {
    private boolean vezPlayer1;
    private int position;

    public VezJogador(boolean vezPlayer1, int position) {
        this.vezPlayer1 = vezPlayer1;
        this.position = position;
    }

    public VezJogador() {
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isVezPlayer1() {
        return vezPlayer1;
    }

    public void setVezPlayer1(boolean vezPlayer1) {
        this.vezPlayer1 = vezPlayer1;
    }
}
