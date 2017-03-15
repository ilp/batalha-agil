package batalhaagil.ufrpe.iversonluis.batalhaagil.models;

import android.util.Log;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import batalhaagil.ufrpe.iversonluis.batalhaagil.Barcos;

/**
 * Created by carlos on 08/02/17.
 */

public class ShipsGridModel implements Serializable {
    private String[][] shipsGrid;
    private ArsenalGridModel arsenalGridModel;
    private static final String TAG = "ShipsModel";

    public ShipsGridModel() {
        shipsGrid = new String[10][10];
        this.initGrid();
    }

    public String[][] getShipsGrid() {
        return shipsGrid;
    }

    public ArsenalGridModel getArsenalGridModel() {
        return arsenalGridModel;
    }

    public void setArsenalGridModel(ArsenalGridModel arsenalGridModel) {
        this.arsenalGridModel = arsenalGridModel;
    }

    public void setShipsGrid(String[][] shipsGrid) {
        this.shipsGrid = shipsGrid;
    }

    public boolean setShip(int x, int y, int len, int ori) {
        boolean set = false;
        switch (len) {
            case 1:
                if (verifyShipsTotal(Barcos.BARCO_TAM1_1, Barcos.BARCO_TAM1_1,  4)) {
                    if (verifyPos(x, y)) {
                        this.shipsGrid[x][y] = Barcos.BARCO_TAM1_1;
                        set = true;
                    }
                }
                break;
            case 2:
                if (verifyShipsTotal(Barcos.BARCO_HORI_TAM2_1, Barcos.BARCO_VERT_TAM2_1, 3)) {
                    if (ori == 0) {
                        if (y + 1 < 9) {
                            if (verifyPos(x, y) && verifyPos(x, y + 1)) {
                                this.shipsGrid[x][y] = Barcos.BARCO_HORI_TAM2_1;
                                this.shipsGrid[x][y + 1] = Barcos.BARCO_HORI_TAM2_2;
                                set = true;
                            }
                        }
                    } else {
                        if (x + 1 < 9) {
                            if (verifyPos(x, y) && verifyPos(x + 1, y)) {
                                this.shipsGrid[x][y] = Barcos.BARCO_VERT_TAM2_1;
                                this.shipsGrid[x + 1][y] = Barcos.BARCO_VERT_TAM2_2;
                                set = true;
                            }
                        }
                    }
                }
                break;
            case 3:
                if (verifyShipsTotal(Barcos.BARCO_HORI_TAM3_1, Barcos.BARCO_VERT_TAM3_1, 2)) {
                    if (ori == 0) {
                        if (y + 2 < 9) {
                            if (verifyPos(x, y) && verifyPos(x, y + 1) && verifyPos(x, y + 2)) {
                                this.shipsGrid[x][y] = Barcos.BARCO_HORI_TAM3_1;
                                this.shipsGrid[x][y + 1] = Barcos.BARCO_HORI_TAM3_2;
                                this.shipsGrid[x][y + 2] = Barcos.BARCO_HORI_TAM3_3;
                                set = true;
                            }
                        }
                    } else {
                        if (x + 2 < 9) {
                            if (verifyPos(x, y) && verifyPos(x + 1, y) && verifyPos(x + 2, y)) {
                                this.shipsGrid[x][y] = Barcos.BARCO_VERT_TAM3_1;
                                this.shipsGrid[x + 1][y] = Barcos.BARCO_VERT_TAM3_2;
                                this.shipsGrid[x + 2][y] = Barcos.BARCO_VERT_TAM3_3;
                                set = true;
                            }
                        }
                    }
                }
                break;
            case 4:
                if (verifyShipsTotal(Barcos.BARCO_HORI_TAM4_1, Barcos.BARCO_VERT_TAM4_1, 1)) {
                    if (ori == 0) {
                        if (y + 3 < 9) {
                            if (verifyPos(x, y) && verifyPos(x, y + 1) && verifyPos(x, y + 2) && verifyPos(x, y + 3)) {
                                this.shipsGrid[x][y] = Barcos.BARCO_HORI_TAM4_1;
                                this.shipsGrid[x][y + 1] = Barcos.BARCO_HORI_TAM4_2;
                                this.shipsGrid[x][y + 2] = Barcos.BARCO_HORI_TAM4_3;
                                this.shipsGrid[x][y + 3] = Barcos.BARCO_HORI_TAM4_4;
                                set = true;
                            }
                        }
                    } else {
                        if (x + 3 < 9) {
                            if (verifyPos(x, y) && verifyPos(x + 1, y) && verifyPos(x + 2, y) && verifyPos(x + 3, y)) {
                                this.shipsGrid[x][y] = Barcos.BARCO_VERT_TAM4_1;
                                this.shipsGrid[x + 1][y] = Barcos.BARCO_VERT_TAM4_2;
                                this.shipsGrid[x + 2][y] = Barcos.BARCO_VERT_TAM4_3;
                                this.shipsGrid[x + 3][y] = Barcos.BARCO_VERT_TAM4_4;
                                set = true;
                            }
                        }
                    }
                    break;
                }
            default:
                this.shipsGrid[x][y] = "";
                break;
        }
        return set;
    }

    public boolean verifyShipsTotal(String shipTypeOne, String shipTypeTwo, int shipQuant) {
        int count = 0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (this.shipsGrid[i][j].equals(shipTypeOne) ||this.shipsGrid[i][j].equals(shipTypeTwo)) {
                    count++;
                }
            }
        }
        if (count >= shipQuant) {
            return false;
        }
        return true;
    }

    public boolean verifyPos(int x, int y) {
        boolean put = false;

        if ((x == 0) && (y == 0)) {
            Log.i(TAG, "Canto superior esquerdo");
            if ((this.shipsGrid[x][y + 1].equals("")) && (this.shipsGrid[x + 1][y + 1].equals("")) && (this.shipsGrid[x + 1][y].equals(""))) {
                put = true;
            }
        } else if ((x == 9) && (y == 9)) {
            Log.i(TAG, "Canto inferior direito");
            if ((this.shipsGrid[x - 1][y].equals("")) && (this.shipsGrid[x - 1][y - 1].equals("")) && (this.shipsGrid[x][y - 1].equals(""))) {
                put = true;
            }
        } else if ((x == 9) && (y == 0)) {
            Log.i(TAG, " Canto inferior esquerdo");
            if ((this.shipsGrid[x - 1][y].equals("")) && (this.shipsGrid[x - 1][y + 1].equals("")) && (this.shipsGrid[x][y + 1].equals(""))) {
                put = true;
            }
        } else if ((x == 0) && (y == 9)) {
            Log.i(TAG, "Canto superior direito");
            if ((this.shipsGrid[x][y - 1].equals("")) && (this.shipsGrid[x + 1][y - 1].equals("")) && (this.shipsGrid[x + 1][y].equals(""))) {
                put = true;
            }
        } else if ((x == 0) && (y > 0) && (y < 9)) {
            // linha superior

            if ((this.shipsGrid[x][y - 1].equals("")) && (this.shipsGrid[x + 1][y - 1].equals("")) && (this.shipsGrid[x + 1][y].equals("")) && (this.shipsGrid[x + 1][y + 1].equals("")) && (this.shipsGrid[x][y + 1].equals(""))) {
                put = true;
            }
        } else if ((y == 0) && (x > 0) && (x < 9)) {
            // linha lateral esquerda
            if ((this.shipsGrid[x - 1][y].equals("")) && (this.shipsGrid[x - 1][y + 1].equals("")) && (this.shipsGrid[x][y + 1].equals("")) && (this.shipsGrid[x + 1][y + 1].equals("")) && (this.shipsGrid[x + 1][y].equals(""))) {
                put = true;
            }
        } else if ((x == 9) && (y > 0) && (y < 9)) {
            // linha inferior
            if ((this.shipsGrid[x][y - 1].equals("")) && (this.shipsGrid[x - 1][y - 1].equals("")) && (this.shipsGrid[x - 1][y].equals("")) && (this.shipsGrid[x - 1][y + 1].equals("")) && (this.shipsGrid[x][y + 1].equals(""))) {
                put = true;
            }
        } else if ((y == 9) && (x > 0) && (x < 9)) {
            // linha lateral direita
            if ((this.shipsGrid[x - 1][y].equals("")) && (this.shipsGrid[x - 1][y - 1].equals("")) && (this.shipsGrid[x][y - 1].equals("")) && (this.shipsGrid[x + 1][y - 1].equals("")) && (this.shipsGrid[x + 1][y].equals(""))) {
                put = true;
            }
        } else {
            // middle
            if ((this.shipsGrid[x - 1][y - 1].equals("")) && (this.shipsGrid[x][y - 1].equals("")) && (this.shipsGrid[x + 1][y - 1].equals("")) && (this.shipsGrid[x + 1][y].equals("")) && (this.shipsGrid[x + 1][y + 1].equals("")) && (this.shipsGrid[x][y + 1].equals("")) && (this.shipsGrid[x - 1][y + 1].equals("")) && (this.shipsGrid[x - 1][y].equals(""))) {
                put = true;
            }
        }
        return put;
    }

    public void generateMap() {
        initGrid();
        Random random = new Random();
        int count = 0;
        int x = 0;
        int y = 0;
        int ori;

        while (count < 1) {
            x = random.nextInt(10);
            y = random.nextInt(10);
            ori = random.nextInt(2);
            if (this.setShip(x, y, 4, ori)) {
                Log.i(TAG, "Put 4 ships");
                count++;
            }
        }

        count = 0;
        while (count < 2) {
            x = random.nextInt(10);
            y = random.nextInt(10);
            ori = random.nextInt(2);
            if (this.setShip(x, y, 3, ori)) {
                Log.i(TAG, "put 3 ships");
                count++;
            }
        }

        count = 0;
        while (count < 3) {
            x = random.nextInt(10);
            y = random.nextInt(10);
            ori = random.nextInt(2);
            if (this.setShip(x, y, 2, ori)) {
                Log.i(TAG, "put 2 ships");
                count++;
            }
        }

        count = 0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Log.i(TAG, count + "");

                if (count < 4) {
                    if (this.setShip(i, j, 1, 0)) {
                        Log.i(TAG, "put 1 ship");
                        count++;
                    }
                }
            }
        }
    }

    public String getShipPosition(int x, int y) {
        return this.shipsGrid[x][y];
    }

    private void initGrid() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                this.shipsGrid[i][j] = "";
            }
        }
    }

    public boolean checkPos(int x, int y) {
        if (shipsGrid[x][y].equals("")) {
            return false;
        }
        return true;
    }

    public List<String> getList() {
        List<String> pos = new ArrayList<String>();
        for (int i = 0; i<10; i++) {
            for (int j = 0; j<10; j++){
                pos.add(this.shipsGrid[i][j]);
            }
        }

        return pos;
    }

    public String[][] getGrid(List<String> list) {
        for (int i = 0; i<100; i++) {
            this.shipsGrid[i/10][i%10] = list.get(i);
        }

        return this.shipsGrid;
    }

}