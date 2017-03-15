package batalhaagil.ufrpe.iversonluis.batalhaagil;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import batalhaagil.ufrpe.iversonluis.batalhaagil.firebase.MapaJogadores;
import batalhaagil.ufrpe.iversonluis.batalhaagil.models.ShipsGridModel;
import batalhaagil.ufrpe.iversonluis.batalhaagil.util.Mapas;

public class CreateMapa extends AppCompatActivity {

    GridLayout gridMap;
    GridLayout gridShips;
    GridView gridViewLinhas;
    GridView gridViewColunas;
    Button btnNext;
    Button btnAuto;
    ImageView[] barcos;
    ImageView[] barcosToSelect;
    TextView textTitle;

    String[] linhas = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    String[] colunas = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
    String temaEscolhido;

    private SelectBarco selectBarco;
    private ShipsGridModel shipsGridModel;
    private ShipsGridModel shipsGridModelOpponent;

    private boolean finishCreate = false;

    private int tipoBatalha;
    private int player = 2;/////JOGADOR 1 OU JOGADOR 2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_mapa);

        //gridMap = (GridView) findViewById(R.id.gridMap);
        gridMap = (GridLayout) findViewById(R.id.gridMap);
        gridShips = (GridLayout) findViewById(R.id.gridShips);
        textTitle = (TextView) findViewById(R.id.title_create_map);
        Typeface typeface=Typeface.createFromAsset(getAssets(), "fonts/Bungasai.ttf");
        textTitle.setTypeface(typeface);

        shipsGridModel = new ShipsGridModel();
        shipsGridModelOpponent = new ShipsGridModel();

        Bundle bundle = getIntent().getExtras();
        temaEscolhido = bundle.getString("temaEscolhido");
        tipoBatalha = bundle.getInt("tipoBatalha");

        carregarShips();
        carregarMapa();

        //numeros das colunas e linhas
        gridViewLinhas = (GridView) findViewById(R.id.gridviewLinhas);
        gridViewColunas = (GridView) findViewById(R.id.gridviewColunas);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.linhas_colunas, linhas);
        gridViewLinhas.setAdapter(adapter);
        adapter = new ArrayAdapter<String>(this, R.layout.linhas_colunas, colunas);
        gridViewColunas.setAdapter(adapter);

        btnNext = (Button) findViewById(R.id.btn_next);
        btnAuto = (Button) findViewById(R.id.btn_auto);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(finishCreate) {
                    shipsGridModelOpponent.generateMap();
                    Bundle bundle = getIntent().getExtras();
                    temaEscolhido = bundle.getString("temaEscolhido");

                    Intent it = new Intent(CreateMapa.this, CreateArsenal.class);

                    if(tipoBatalha == 1){

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("Mapas");

                        if(player == 1){
                            MapaJogadores dados = new MapaJogadores(shipsGridModel.getList());
                            Map<String, Object> childUpdates = new HashMap<>();
                            Map<String, Object> mapValues = dados.toMap();
                            childUpdates.put("/userMapas/" + "player1", mapValues);
                            myRef.updateChildren(childUpdates);
                        } else{
                            MapaJogadores dados = new MapaJogadores(shipsGridModel.getList());
                            Map<String, Object> childUpdates = new HashMap<>();
                            Map<String, Object> mapValues = dados.toMap();
                            childUpdates.put("/userMapas/" + "player2", mapValues);
                            myRef.updateChildren(childUpdates);
                        }

                        it.putExtra("batalhaagil.models.ShipsGridModel", shipsGridModel);
                        it.putExtra("batalhaagil.models.ShipsGridModelOpponent", shipsGridModelOpponent);
                        it.putExtra("temaEscolhido", temaEscolhido);
                        it.putExtra("tipoBatalha", tipoBatalha);
                        it.putExtra("player", player);
                        startActivity(it);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();

                    } else {
                        it.putExtra("batalhaagil.models.ShipsGridModel", shipsGridModel);
                        it.putExtra("batalhaagil.models.ShipsGridModelOpponent", shipsGridModelOpponent);
                        it.putExtra("temaEscolhido", temaEscolhido);
                        startActivity(it);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                } else {
                    showDialog("Posicione seus navios primeiro!");
                }

            }
        });

        btnAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridMap.removeAllViews();
                carregarMapaAuto(shipsGridModel, gridMap);
                gridShips.setVisibility(View.INVISIBLE);
                //System.out.println("ok");
            }
        });

    }

    public void carregarShips() {
        barcosToSelect = new ImageView[100];
        selectBarco = new SelectBarco();
        for (int i = 0; i < 100; i++) {
            barcosToSelect[i] = new ImageView(CreateMapa.this);
            barcosToSelect[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            if (i == 11) {
                barcosToSelect[i].setImageResource(R.drawable.ic_barc_tam4_1);
                gridShips.addView(barcosToSelect[i]);
            } else if (i == 12) {
                barcosToSelect[i].setImageResource(R.drawable.ic_barc_tam4_2);
                gridShips.addView(barcosToSelect[i]);
            } else if (i == 13) {
                barcosToSelect[i].setImageResource(R.drawable.ic_barc_tam4_3);
                gridShips.addView(barcosToSelect[i]);
            } else if (i == 14) {
                barcosToSelect[i].setImageResource(R.drawable.ic_barc_tam4_4);
                gridShips.addView(barcosToSelect[i]);
            } else if (i == 31 || i == 35) {
                barcosToSelect[i].setImageResource(R.drawable.ic_barc_tam3_1);
                gridShips.addView(barcosToSelect[i]);
            } else if (i == 32 || i == 36) {
                barcosToSelect[i].setImageResource(R.drawable.ic_barc_tam3_2);
                gridShips.addView(barcosToSelect[i]);
            } else if (i == 33 || i == 37) {
                barcosToSelect[i].setImageResource(R.drawable.ic_barc_tam3_3);
                gridShips.addView(barcosToSelect[i]);
            } else if (i == 51 || i == 54 || i == 57) {
                barcosToSelect[i].setImageResource(R.drawable.ic_barc_tam2_1);
                gridShips.addView(barcosToSelect[i]);
            } else if (i == 52 || i == 55 || i == 58) {
                barcosToSelect[i].setImageResource(R.drawable.ic_barc_tam2_2);
                gridShips.addView(barcosToSelect[i]);
            } else if (i == 71 || i == 73 || i == 75 || i == 77) {
                barcosToSelect[i].setImageResource(R.drawable.ic_barco_tam1);
                gridShips.addView(barcosToSelect[i]);
            } else {
                barcosToSelect[i].setImageResource(R.drawable.ic_espaco_vazio);
                gridShips.addView(barcosToSelect[i]);
            }
        }

        int item = 0;

        for (item = 0; item < 100; item++) {
            final int finalItem = item;
            barcosToSelect[item].setOnClickListener(new View.OnClickListener() {

                int pos = finalItem;

                public void onClick(View v) {
                    selectBarco.setStartPos(pos);

                    if (pos >= 11 && pos <= 14)
                        selectBarco.setTipoBarco(Barcos.BARCO_TAM4);
                    else if ((pos >= 31 && pos <= 33) || (pos >= 35 && pos <= 37))
                        selectBarco.setTipoBarco(Barcos.BARCO_TAM3);
                    else if ((pos >= 51 && pos <= 52)|| (pos >= 54 && pos <= 55) || (pos >= 57 && pos<=58))
                        selectBarco.setTipoBarco(Barcos.BARCO_TAM2);
                    else if (pos == 71 || pos ==73 || pos == 75 || pos == 77)
                        selectBarco.setTipoBarco(Barcos.BARCO_TAM1);
                }
            });
        }
    }

    public void carregarMapa() {
        barcos = new ImageView[100];
        for (int i = 0; i < 100; i++) {
            barcos[i] = new ImageView(CreateMapa.this);
            barcos[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            barcos[i].setImageResource(R.drawable.ic_espaco_vazio);
            barcos[i].setTag(R.drawable.ic_espaco_vazio);
            gridMap.addView(barcos[i]);
        }

        int item = 0;

        for (item = 0; item < 100; item++) {
            final int finalItem = item;
            barcos[item].setOnClickListener(new View.OnClickListener() {

                int pos = finalItem;

                public void onClick(View v) {
                    selectBarco.setFinalPos(pos);
                    if(selectBarco.getStartPos() != -1) {
                        if (shipsGridModel.setShip(finalItem/10,finalItem%10, selectBarco.getTipoBarco(), 1)) {
                            colocarBarcos(finalItem, selectBarco.getStartPos());
                            selectBarco.setStartPos(-1);
                        } else {
                            Log.i("Tag", "Já tem barco nesse lugar " + finalItem/10 + "  " + finalItem%10);
                        }
                    } else {

                       showDialog("Selecione um barco primeiro!");
                    }
                }

            });
        }
    }


    private void showDialog(String msg) throws Resources.NotFoundException {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(CreateMapa.this);
        builder.setTitle("Erro");
        builder.setIcon(R.drawable.ic_cancel);
        builder.setMessage(msg).setPositiveButton("OK", dialogClickListener).show();
    }

    public void colocarBarcos(int posicao, int startPosition) {

        if (Barcos.BARCO_TAM1 == selectBarco.getTipoBarco()) {
            barcos[posicao].setImageResource(R.drawable.ic_barco_tam1);
            barcos[posicao].setTag(R.drawable.ic_barco_tam1);
            barcosToSelect[startPosition].setImageResource((R.drawable.ic_espaco_vazio));

        } else if (Barcos.BARCO_TAM2 == selectBarco.getTipoBarco()) {
            barcos[posicao].setImageResource(R.drawable.ic_barc_tam2_1);
            barcos[posicao + 1].setImageResource(R.drawable.ic_barc_tam2_2);
            barcos[posicao].setTag(R.drawable.ic_barco_tam1);
            barcosToSelect[startPosition].setImageResource((R.drawable.ic_espaco_vazio));
            barcosToSelect[startPosition+1].setImageResource((R.drawable.ic_espaco_vazio));

        } else if (Barcos.BARCO_TAM3 == selectBarco.getTipoBarco()) {
            barcos[posicao].setImageResource(R.drawable.ic_barc_tam3_1);
            barcos[posicao + 1].setImageResource(R.drawable.ic_barc_tam3_2);
            barcos[posicao + 2].setImageResource(R.drawable.ic_barc_tam3_3);
            barcos[posicao].setTag(R.drawable.ic_barco_tam1);
            barcosToSelect[startPosition].setImageResource((R.drawable.ic_espaco_vazio));
            barcosToSelect[startPosition+1].setImageResource((R.drawable.ic_espaco_vazio));
            barcosToSelect[startPosition+2].setImageResource((R.drawable.ic_espaco_vazio));

        } else if (Barcos.BARCO_TAM4 == selectBarco.getTipoBarco()) {
            barcos[posicao].setImageResource(R.drawable.ic_barc_tam4_1);
            barcos[posicao + 1].setImageResource(R.drawable.ic_barc_tam4_2);
            barcos[posicao + 2].setImageResource(R.drawable.ic_barc_tam4_3);
            barcos[posicao + 3].setImageResource(R.drawable.ic_barc_tam4_4);
            barcos[posicao].setTag(R.drawable.ic_barco_tam1);
            barcosToSelect[startPosition].setImageResource((R.drawable.ic_espaco_vazio));
            barcosToSelect[startPosition+1].setImageResource((R.drawable.ic_espaco_vazio));
            barcosToSelect[startPosition+2].setImageResource((R.drawable.ic_espaco_vazio));
            barcosToSelect[startPosition+3].setImageResource((R.drawable.ic_espaco_vazio));

        }

        barcos[posicao].setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Dialog dialog = new Dialog(CreateMapa.this);
                dialog.setContentView(R.layout.dialog_remove_barco);
                dialog.setTitle(R.string.remove_title);
                dialog.show();
                return false;
            }
        });
    }

    public void createMapOpponent(ShipsGridModel shipsGridModelPlayer) throws JSONException {
        shipsGridModelOpponent.generateMap();
    }

    public void carregarMapaAuto(ShipsGridModel shipsPlayers, GridLayout gridPlayers) {
        shipsPlayers.generateMap();
        ImageView[] ships = barcos;

        for (int i = 0; i < 100; i++) {

            if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_TAM1_1)) {
                ships[i] = new ImageView(CreateMapa.this);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barco_tam1);
                gridPlayers.addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_HORI_TAM2_1)) {
                ships[i] = new ImageView(CreateMapa.this);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam2_1);
                gridPlayers.addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_HORI_TAM2_2)) {
                ships[i] = new ImageView(CreateMapa.this);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam2_2);
                gridPlayers.addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_HORI_TAM3_1)) {
                ships[i] = new ImageView(CreateMapa.this);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam3_1);
                gridPlayers.addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_HORI_TAM3_2)) {
                ships[i] = new ImageView(CreateMapa.this);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam3_2);
                gridPlayers.addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_HORI_TAM3_3)) {
                ships[i] = new ImageView(CreateMapa.this);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam3_3);
                gridPlayers.addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_HORI_TAM4_1)) {
                ships[i] = new ImageView(CreateMapa.this);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam4_1);
                gridPlayers.addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_HORI_TAM4_2)) {
                ships[i] = new ImageView(CreateMapa.this);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam4_2);
                gridPlayers.addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_HORI_TAM4_3)) {
                ships[i] = new ImageView(CreateMapa.this);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam4_3);
                gridPlayers.addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_HORI_TAM4_4)) {
                ships[i] = new ImageView(CreateMapa.this);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam4_4);
                gridPlayers.addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            }else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_VERT_TAM2_1)) {
                ships[i] = new ImageView(CreateMapa.this);
                ships[i].setRotation(90);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam2_1);
                gridPlayers.addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_VERT_TAM2_2)) {
                ships[i] = new ImageView(CreateMapa.this);
                ships[i].setRotation(90);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam2_2);
                gridPlayers.addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_VERT_TAM3_1)) {
                ships[i] = new ImageView(CreateMapa.this);
                ships[i].setRotation(90);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam3_1);
                gridPlayers.addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_VERT_TAM3_2)) {
                ships[i] = new ImageView(CreateMapa.this);
                ships[i].setRotation(90);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam3_2);
                gridPlayers.addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_VERT_TAM3_3)) {
                ships[i] = new ImageView(CreateMapa.this);
                ships[i].setRotation(90);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam3_3);
                gridPlayers.addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_VERT_TAM4_1)) {
                ships[i] = new ImageView(CreateMapa.this);
                ships[i].setRotation(90);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam4_1);
                gridPlayers.addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_VERT_TAM4_2)) {
                ships[i] = new ImageView(CreateMapa.this);
                ships[i].setRotation(90);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam4_2);
                gridPlayers.addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_VERT_TAM4_3)) {
                ships[i] = new ImageView(CreateMapa.this);
                ships[i].setRotation(90);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam4_3);
                gridPlayers.addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_VERT_TAM4_4)) {
                ships[i] = new ImageView(CreateMapa.this);
                ships[i].setRotation(90);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam4_4);
                gridPlayers.addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals("")) {
                ships[i] = new ImageView(CreateMapa.this);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_espaco_vazio);
                gridPlayers.addView(ships[i]);
                //gridMap.addView(arsenal[i]);
            }
        }

        finishCreate = true;
    }
}