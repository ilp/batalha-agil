package batalhaagil.ufrpe.iversonluis.batalhaagil.util;

import java.util.ArrayList;

/**
 * Created by Iverson Luís on 12/02/2017.
 */

public class Resumes {
    private ArrayList<Resume> Agile;
    private ArrayList<Resume> Scrum;
    private ArrayList<Resume> XP;
    private ArrayList<Resume> Lean;

    public ArrayList<Resume> getAgile() {
        return Agile;
    }

    public void setAgile(ArrayList<Resume> agile) {
        Agile = agile;
    }

    public ArrayList<Resume> getScrum() {
        return Scrum;
    }

    public void setScrum(ArrayList<Resume> scrum) {
        Scrum = scrum;
    }

    public ArrayList<Resume> getXP() {
        return XP;
    }

    public void setXP(ArrayList<Resume> XP) {
        this.XP = XP;
    }

    public ArrayList<Resume> getLean() {
        return Lean;
    }

    public void setLean(ArrayList<Resume> lean) {
        Lean = lean;
    }
}


