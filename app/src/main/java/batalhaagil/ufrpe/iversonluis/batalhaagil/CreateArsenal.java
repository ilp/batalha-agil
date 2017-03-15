package batalhaagil.ufrpe.iversonluis.batalhaagil;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import batalhaagil.ufrpe.iversonluis.batalhaagil.firebase.ArsenalJogadores;
import batalhaagil.ufrpe.iversonluis.batalhaagil.firebase.Jogador;
import batalhaagil.ufrpe.iversonluis.batalhaagil.firebase.MapaJogadores;
import batalhaagil.ufrpe.iversonluis.batalhaagil.firebase.Mapas;
import batalhaagil.ufrpe.iversonluis.batalhaagil.firebase.Terminou;
import batalhaagil.ufrpe.iversonluis.batalhaagil.models.ArsenalGridModel;
import batalhaagil.ufrpe.iversonluis.batalhaagil.models.ShipsGridModel;
import batalhaagil.ufrpe.iversonluis.batalhaagil.util.Resumes;

public class CreateArsenal extends AppCompatActivity {

    private static final String TAG = "okkokookokk";
    GridLayout gridMap;
    GridView gridViewLinhas;
    GridView gridViewColunas;
    Button btnNext;
    TextView titleArsenal;
    TextView textInfo;

    ImageView itemAirplane;
    ImageView itemMissil;
    ImageView itemRadar;

    ImageView[] ships;
    FrameLayout[] frameLayout;

    private ShipsGridModel shipsGridModel;
    private ShipsGridModel shipsGridModelOpponent;
    private ArsenalGridModel arsenalGridModel;

    private int typeArsenal = -1;

    String [] linhas = new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    String [] colunas = new String[] {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
    String temaEscolhido;

    private int tipoBatalha;
    private int player;
    private boolean player1Pronto = false;
    private boolean player2Pronto = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_arsenal);

        gridMap = (GridLayout) findViewById(R.id.gridMap);
        titleArsenal = (TextView) findViewById(R.id.title_create_arsenal);
        textInfo = (TextView) findViewById(R.id.text_avisos);

        //arsenal
        itemAirplane = (ImageView) findViewById(R.id.arsenal_airplan);
        itemMissil = (ImageView) findViewById(R.id.arsenal_missil);
        itemRadar = (ImageView) findViewById(R.id.arsenal_radar);

        Typeface typeface=Typeface.createFromAsset(getAssets(), "fonts/Bungasai.ttf");
        titleArsenal.setTypeface(typeface);

        //Obtendo mapa
        Bundle bundle = getIntent().getExtras();
        shipsGridModel = (ShipsGridModel) bundle.getSerializable("batalhaagil.models.ShipsGridModel");
        arsenalGridModel = new ArsenalGridModel();

        shipsGridModelOpponent = (ShipsGridModel) bundle.getSerializable("batalhaagil.models.ShipsGridModelOpponent");

        temaEscolhido = bundle.getString("temaEscolhido");
        tipoBatalha = bundle.getInt("tipoBatalha");
        player = bundle.getInt("player");

        if(shipsGridModel != null && shipsGridModelOpponent != null){
            carregarMapa(shipsGridModel, gridMap);
        }

        //numeros das colunas e linhas
        gridViewLinhas = (GridView) findViewById(R.id.gridviewLinhas);
        gridViewColunas = (GridView) findViewById(R.id.gridviewColunas);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.linhas_colunas, linhas);
        gridViewLinhas.setAdapter(adapter);
        adapter = new ArrayAdapter<String>(this, R.layout.linhas_colunas, colunas);
        gridViewColunas.setAdapter(adapter);

        btnNext = (Button)findViewById(R.id.btn_go_battle);



        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shipsGridModel.setArsenalGridModel(arsenalGridModel);
                esperarconstruirMapa();

                if(tipoBatalha == 1) {

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("Mapas");

                    List<Integer> missil = new ArrayList<Integer>();
                    List<Integer> radar = new ArrayList<Integer>();
                    for (int index = 0; index < arsenalGridModel.getArsenalMissil().length; index++) {
                        missil.add(arsenalGridModel.getArsenalMissil()[index]);
                        radar.add(arsenalGridModel.getArsenalMissil()[index]);
                    }

                    Terminou term = new Terminou();
                    if (player == 1) {

                        ArsenalJogadores dados = new ArsenalJogadores(missil, radar, arsenalGridModel.getArsenalAirPlane());
                        Map<String, Object> childUpdates = new HashMap<>();
                        Map<String, Object> arsenalValues = dados.toMap();
                        childUpdates.put("/userMapas/" + "player1/" + "/arsenal", arsenalValues);
                        myRef.updateChildren(childUpdates);
                        term.setTerminou(true);
                    } else {
                        ArsenalJogadores dados = new ArsenalJogadores(missil, radar, arsenalGridModel.getArsenalAirPlane());
                        Map<String, Object> childUpdates = new HashMap<>();
                        Map<String, Object> arsenalValues = dados.toMap();
                        childUpdates.put("/userMapas/" + "player2/" + "/arsenal", arsenalValues);
                        myRef.updateChildren(childUpdates);
                        term.setTerminou(true);
                    }

                    myRef.child("status").setValue(term);

                    if(verificarPlayersProntos()) {
                        iniciarBatalha();
                    } else{
                        Toast.makeText(getApplicationContext(), "Esperando jogador finalizar a construção do mapa!", Toast.LENGTH_LONG);
                    }
                } else {
                    Intent it = new Intent(CreateArsenal.this, TelaBatalha.class);
                    it.putExtra("batalhaagil.models.ShipsGridModel", shipsGridModel);
                    it.putExtra("batalhaagil.models.ShipsGridModelOpponent", shipsGridModelOpponent);
                    it.putExtra("batalhaagil.models.ArsenalGridModel", arsenalGridModel);
                    it.putExtra("temaEscolhido", temaEscolhido);
                    it.putExtra("tipoBatalha", 0);
                    it.putExtra("player", player);
                    startActivity(it);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }

            }
        });

        final Animation a = AnimationUtils.loadAnimation(CreateArsenal.this, R.anim.milkshake);
        final MediaPlayer soundButton = android.media.MediaPlayer.create(this, R.raw.btn_click);

        itemAirplane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textInfo.setText(R.string.aviso_clicar_arsenal_airplane);
                textInfo.startAnimation(a);
                typeArsenal = Arsenal.AIRPLANE;
                soundButton.start();
            }
        });

        itemRadar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textInfo.setText(R.string.aviso_clicar_arsenal_radar);
                textInfo.startAnimation(a);
                typeArsenal = Arsenal.RADAR;
                soundButton.start();
            }
        });

        itemMissil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textInfo.setText(R.string.aviso_clicar_arsenal_missil);
                textInfo.startAnimation(a);
                typeArsenal = Arsenal.MISSIL;
                soundButton.start();
            }
        });

    }

    public void positionArsenal(int typeArsenal, int positionArsenal){

        if(Arsenal.AIRPLANE == typeArsenal){

            for(int i = 0; i<10; i++){
                int x = (positionArsenal/10)*10 + i;
                ImageView imageView = new ImageView(CreateArsenal.this);
                imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                imageView.setImageResource(R.drawable.ic_select_line);
                if(imageView.getParent()!=null)
                    ((ViewGroup)imageView.getParent()).removeView(imageView); // <- fix
                frameLayout[x].addView(imageView);
                itemAirplane.setVisibility(View.INVISIBLE);
            }

            int linha = positionArsenal/10;
            arsenalGridModel.setArsenalAirPlane(linha);

        } else if(Arsenal.MISSIL == typeArsenal){

            int[] missilPos = new int[9];
            int aux = 0;
            for(int i = 0; i< 3; i++){
                ImageView imageView = new ImageView(CreateArsenal.this);
                imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                imageView.setImageResource(R.drawable.ic_select_line_missil);
                if(imageView.getParent()!=null)
                    ((ViewGroup)imageView.getParent()).removeView(imageView);
                frameLayout[positionArsenal+i].addView(imageView);

                ImageView imageView2 = new ImageView(CreateArsenal.this);
                imageView2.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                imageView2.setImageResource(R.drawable.ic_select_line_missil);
                if(imageView2.getParent()!=null)
                    ((ViewGroup)imageView.getParent()).removeView(imageView2);
                frameLayout[positionArsenal+10+i].addView(imageView2);

                ImageView imageView3 = new ImageView(CreateArsenal.this);
                imageView3.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                imageView3.setImageResource(R.drawable.ic_select_line_missil);
                if(imageView3.getParent()!=null)
                    ((ViewGroup)imageView3.getParent()).removeView(imageView3);
                frameLayout[positionArsenal+20+i].addView(imageView3);

                missilPos[aux] = positionArsenal+i;
                aux = aux + 1;
                missilPos[aux] = positionArsenal+10+i;
                aux = aux + 1;
                missilPos[aux] = positionArsenal+20+i;
                aux++;

                itemMissil.setVisibility(View.INVISIBLE);
            }

            arsenalGridModel.setArsenalMissil(missilPos);

        } else if(Arsenal.RADAR == typeArsenal) {
            int[] radarPos = new int[9];
            int aux = 0;

            for (int i = 0; i < 3; i++) {
                ImageView imageView = new ImageView(CreateArsenal.this);
                imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                imageView.setImageResource(R.drawable.ic_select_line_radar);
                if (imageView.getParent() != null)
                    ((ViewGroup) imageView.getParent()).removeView(imageView);

                frameLayout[positionArsenal + i].addView(imageView);

                ImageView imageView2 = new ImageView(CreateArsenal.this);
                imageView2.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                imageView2.setImageResource(R.drawable.ic_select_line_radar);
                if (imageView2.getParent() != null)
                    ((ViewGroup) imageView.getParent()).removeView(imageView2);
                frameLayout[positionArsenal + 10 + i].addView(imageView2);

                ImageView imageView3 = new ImageView(CreateArsenal.this);
                imageView3.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                imageView3.setImageResource(R.drawable.ic_select_line_radar);
                if (imageView3.getParent() != null)
                    ((ViewGroup) imageView3.getParent()).removeView(imageView3);
                frameLayout[positionArsenal + 20 + i].addView(imageView3);

                radarPos[aux] = positionArsenal+i;
                aux = aux + 1;
                radarPos[aux] = positionArsenal+10+i;
                aux = aux + 1;
                radarPos[aux] = positionArsenal+20+i;
                aux++;

                itemRadar.setVisibility(View.INVISIBLE);
            }

            arsenalGridModel.setArsenalRadar(radarPos);
        }
    }

    public void carregarMapa(ShipsGridModel shipsPlayers, GridLayout gridPlayers) {

        ships = new ImageView[100];
        frameLayout = new FrameLayout[100];

        for (int i = 0; i < 100; i++) {
            frameLayout[i] = new FrameLayout(this);
            frameLayout[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_TAM1_1)) {
                ships[i] = new ImageView(CreateArsenal.this);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barco_tam1);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_HORI_TAM2_1)) {
                ships[i] = new ImageView(CreateArsenal.this);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam2_1);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_HORI_TAM2_2)) {
                ships[i] = new ImageView(CreateArsenal.this);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam2_2);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_HORI_TAM3_1)) {
                ships[i] = new ImageView(CreateArsenal.this);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam3_1);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_HORI_TAM3_2)) {
                ships[i] = new ImageView(CreateArsenal.this);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam3_2);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_HORI_TAM3_3)) {
                ships[i] = new ImageView(CreateArsenal.this);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam3_3);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_HORI_TAM4_1)) {
                ships[i] = new ImageView(CreateArsenal.this);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam4_1);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_HORI_TAM4_2)) {
                ships[i] = new ImageView(CreateArsenal.this);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam4_2);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_HORI_TAM4_3)) {
                ships[i] = new ImageView(CreateArsenal.this);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam4_3);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_HORI_TAM4_4)) {
                ships[i] = new ImageView(CreateArsenal.this);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam4_4);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            }else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_VERT_TAM2_1)) {
                ships[i] = new ImageView(CreateArsenal.this);
                ships[i].setRotation(90);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam2_1);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_VERT_TAM2_2)) {
                ships[i] = new ImageView(CreateArsenal.this);
                ships[i].setRotation(90);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam2_2);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_VERT_TAM3_1)) {
                ships[i] = new ImageView(CreateArsenal.this);
                ships[i].setRotation(90);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam3_1);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_VERT_TAM3_2)) {
                ships[i] = new ImageView(CreateArsenal.this);
                ships[i].setRotation(90);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam3_2);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_VERT_TAM3_3)) {
                ships[i] = new ImageView(CreateArsenal.this);
                ships[i].setRotation(90);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam3_3);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_VERT_TAM4_1)) {
                ships[i] = new ImageView(CreateArsenal.this);
                ships[i].setRotation(90);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam4_1);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_VERT_TAM4_2)) {
                ships[i] = new ImageView(CreateArsenal.this);
                ships[i].setRotation(90);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam4_2);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_VERT_TAM4_3)) {
                ships[i] = new ImageView(CreateArsenal.this);
                ships[i].setRotation(90);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam4_3);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_VERT_TAM4_4)) {
                ships[i] = new ImageView(CreateArsenal.this);
                ships[i].setRotation(90);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam4_4);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals("")) {
                ships[i] = new ImageView(CreateArsenal.this);
                ships[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_espaco_vazio);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);
            }
        }

        int item = 0;

        for(item=0;item<100;item++)
        {
            final int finalItem = item;
            ships[item].setOnClickListener(new View.OnClickListener() {

                int pos = finalItem;

                public void onClick(View v) {

                    positionArsenal(typeArsenal, finalItem);

                }
            });
        }
    }

    public void esperarconstruirMapa(){

        int valor = 0;

        DatabaseReference mFirebaseDatabase;
        DatabaseReference mFirebaseDatabase2;
        FirebaseDatabase mFirebaseInstance;

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("montarMapaP1");
        mFirebaseDatabase2 = mFirebaseInstance.getReference("montarMapaP2");
        mFirebaseDatabase.setValue(valor);
        mFirebaseDatabase2.setValue(valor);

        if(player == 1){
            valor = 1;
            mFirebaseDatabase.setValue(valor);

        } else if (player == 2){
            valor = 2;
            mFirebaseDatabase2.setValue(valor);
        }

        mFirebaseInstance.getReference("montarMapaP1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("changed", "App title updated");

                Integer valor = dataSnapshot.getValue(Integer.class);
                if(valor.intValue() == 1){
                    player1Pronto = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e("", "Failed to read app title value.", error.toException());
            }
        });

        mFirebaseInstance.getReference("montarMapaP2").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("changed", "App title updated");

                Integer valor = dataSnapshot.getValue(Integer.class);
                if(valor.intValue() == 2){
                    player2Pronto = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e("", "Failed to read app title value.", error.toException());
            }
        });

    }

    public void iniciarBatalha(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Mapas");

        myRef.child("userMapas").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value

                        HashMap<String, Object> usermapas = (HashMap<String, Object>) dataSnapshot.getValue();
                        Gson gson = new Gson();
                        String json = gson.toJson(usermapas);
                        Log.w("fail", json);
                        Mapas mapas = gson.fromJson(json, Mapas.class);

                        if (player == 1) {
                            shipsGridModel.setShipsGrid(shipsGridModel.getGrid(mapas.getPlayer1().getMapaJogador()));
                            shipsGridModelOpponent.setShipsGrid(shipsGridModelOpponent.getGrid(mapas.getPlayer2().getMapaJogador()));
                        } else if (player == 2) {
                            shipsGridModel.setShipsGrid(shipsGridModel.getGrid(mapas.getPlayer2().getMapaJogador()));
                            shipsGridModelOpponent.setShipsGrid(shipsGridModelOpponent.getGrid(mapas.getPlayer1().getMapaJogador()));
                        }

                        int[] aux = new int[9];
                        int[] aux2 = new int[9];
                        for (int i = 0; i < aux.length; i++) {
                            aux[i] = mapas.getPlayer1().getArsenal().getArsenalRadar().get(i);
                            aux2[i] = mapas.getPlayer1().getArsenal().getArsenalMissil().get(i);
                        }

                        arsenalGridModel.setArsenalRadar(aux);
                        arsenalGridModel.setArsenalMissil(aux2);
                        arsenalGridModel.setArsenalAirPlane(mapas.getPlayer1().getArsenal().getArsenalAirPlane());

                        Intent it = new Intent(CreateArsenal.this, TelaBatalha.class);
                        it.putExtra("batalhaagil.models.ShipsGridModel", shipsGridModel);
                        it.putExtra("batalhaagil.models.ShipsGridModelOpponent", shipsGridModelOpponent);
                        it.putExtra("batalhaagil.models.ArsenalGridModel", arsenalGridModel);
                        it.putExtra("temaEscolhido", temaEscolhido);
                        it.putExtra("tipoBatalha", 1);
                        it.putExtra("player", player);
                        startActivity(it);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("fail", "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    public boolean verificarPlayersProntos(){
        return player1Pronto && player2Pronto;

    }
}
