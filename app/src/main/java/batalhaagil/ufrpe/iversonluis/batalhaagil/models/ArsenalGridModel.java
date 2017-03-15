package batalhaagil.ufrpe.iversonluis.batalhaagil.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import batalhaagil.ufrpe.iversonluis.batalhaagil.Arsenal;

/**
 * Created by Iverson Luís on 12/02/2017.
 */

public class ArsenalGridModel implements Serializable {
    private int arsenalAirPlane;
    private int[] arsenalMissil;
    private int[] arsenalRadar;

    public ArsenalGridModel () {
        arsenalRadar = new int[9];
        arsenalMissil = new int[9];
    }

    public int getArsenalAirPlane() {
        return arsenalAirPlane;
    }

    public void setArsenalAirPlane(int arsenalAirPlane) {
        this.arsenalAirPlane = arsenalAirPlane;
    }

    public int[] getArsenalMissil() {
        return arsenalMissil;
    }

    public void setArsenalMissil(int[] arsenalMissil) {
        this.arsenalMissil = arsenalMissil;
    }

    public int[] getArsenalRadar() {
        return arsenalRadar;
    }

    public void setArsenalRadar(int[] arsenalRadar) {
        this.arsenalRadar = arsenalRadar;
    }

}
