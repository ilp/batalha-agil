package batalhaagil.ufrpe.iversonluis.batalhaagil;

/**
 * Created by Iverson Luís on 06/02/2017.
 */

public class SelectBarco {
    private int tipoBarco;
    private int startPos;
    private int finalPos;
    private int numberType1;
    private int numberType2;
    private int numberType3;
    private int numberType4;

    public SelectBarco() {
        //quantidade de arsenal
        this.numberType1 = 4;
        this.numberType2 = 3;
        this.numberType3 = 2;
        this.numberType4 = 1;
        this.startPos = -1;
    }

    public int getNumberType4() {
        return numberType4;
    }

    public int getNumberType1() {
        return numberType1;
    }

    public int getNumberType2() {
        return numberType2;
    }

    public int getNumberType3() {
        return numberType3;
    }

    public int getTipoBarco() {
        return tipoBarco;
    }

    public void setTipoBarco(int tipoBarco) {
        this.tipoBarco = tipoBarco;
    }

    public int getStartPos() {
        return startPos;
    }

    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }

    public int getFinalPos() {
        return finalPos;
    }

    public void setFinalPos(int finalPos) {
        this.finalPos = finalPos;
    }
}
