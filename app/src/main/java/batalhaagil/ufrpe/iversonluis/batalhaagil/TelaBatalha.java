package batalhaagil.ufrpe.iversonluis.batalhaagil;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import batalhaagil.ufrpe.iversonluis.batalhaagil.firebase.VezJogador;
import batalhaagil.ufrpe.iversonluis.batalhaagil.models.ArsenalGridModel;
import batalhaagil.ufrpe.iversonluis.batalhaagil.models.ShipsGridModel;
import batalhaagil.ufrpe.iversonluis.batalhaagil.util.JsonRead;
import batalhaagil.ufrpe.iversonluis.batalhaagil.util.Resumes;
import pl.droidsonroids.gif.GifImageView;

import static android.widget.FrameLayout.*;

public class TelaBatalha extends AppCompatActivity {

    GridLayout mapPlayer;
    GridLayout mapOpponent;
    GridView gridViewLinhas;
    GridView gridViewColunas;
    ImageView btnCancelar;
    FrameLayout frameAnimation;

    ImageView imageVezPlayer;
    MediaPlayer soundButton;
    int[] animationLocal;

    String [] linhas = new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    String [] colunas = new String[] {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
    String temaEscolhido;

    TextView txtTema;

    private ShipsGridModel shipsGridModel;
    private ShipsGridModel shipsGridModelOpponent;
    private ArsenalGridModel arsenalGridModel;
    private boolean player1 = true;
    private boolean vezPlayer = true;
    private boolean acertouP2 = false;
    private boolean acertouP1 = false;
    private boolean arsenal = false;
    private int tipoBatalha; // 0 - AI, 1 - ONLINE

    ImageView[] ships;
    FrameLayout[] frameLayoutPlayer = new FrameLayout[100];
    FrameLayout[] frameLayoutPlayer2 = new FrameLayout[100];

    private List<Integer> listPosicoesSorteadas;
    private List<Integer> listPosicoesClicked;

    private Handler myHandler;

    //animation
    ImageView imgAirplane;

    //pontuation
    TextView txtPontuation;
    private String pontText = "Pontuação: ";
    private int pontuationPlayer = 0;
    private int pontuationResumos = 0;
    private int pontuationErros = 0;
    private int contadorShips = 0;

    //menu asernal
    LinearLayout menuDropDownArsenal;
    TextView btnMenuArsenal;
    ImageView btnAirplane;
    TextView txtAirplane;
    ImageView btnRadar;
    TextView txtRadar;
    ImageView btnMissil;
    TextView txtMissil;
    CardView cardArsenal;

    int player;
    int jogadorAtual = 1;

    //boolean vezPlayer1 = true;

    //firebase database
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_batalha);

        mapOpponent = (GridLayout) findViewById(R.id.gridMapOpponent);
        mapPlayer = (GridLayout) findViewById(R.id.gridMapSelf);
        frameAnimation = (FrameLayout) findViewById(R.id.frame_principal);
        imageVezPlayer = (ImageView) findViewById(R.id.image_vez_player);

        //Colunas e linhas
        gridViewLinhas = (GridView) findViewById(R.id.gridviewLinhas);
        gridViewColunas = (GridView) findViewById(R.id.gridviewColunas);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.linhas_colunas, linhas);
        gridViewLinhas.setAdapter(adapter);
        adapter = new ArrayAdapter<String>(this, R.layout.linhas_colunas, colunas);
        gridViewColunas.setAdapter(adapter);

        gridViewLinhas = (GridView) findViewById(R.id.gridviewLinhas2);
        gridViewColunas = (GridView) findViewById(R.id.gridviewColunas2);

        adapter = new ArrayAdapter<String>(this, R.layout.linhas_colunas, linhas);
        gridViewLinhas.setAdapter(adapter);
        adapter = new ArrayAdapter<String>(this, R.layout.linhas_colunas, colunas);
        gridViewColunas.setAdapter(adapter);
        //End colunas e linhas

        //Obtendo mapa
        Bundle bundle = getIntent().getExtras();
        shipsGridModel = (ShipsGridModel) bundle.getSerializable("batalhaagil.models.ShipsGridModel");
        shipsGridModelOpponent = (ShipsGridModel) bundle.getSerializable("batalhaagil.models.ShipsGridModelOpponent");
        arsenalGridModel = (ArsenalGridModel) bundle.getSerializable("batalhaagil.models.ArsenalGridModel");
        temaEscolhido = bundle.getString("temaEscolhido");
        tipoBatalha = bundle.getInt("tipoBatalha");
        player = bundle.getInt("player");



        if (shipsGridModel != null && shipsGridModelOpponent != null) {
            carregarMapa(shipsGridModel, mapPlayer, frameLayoutPlayer);
            carregarMapa(shipsGridModelOpponent, mapOpponent, frameLayoutPlayer2);
            if(tipoBatalha == 1){
                jogarOnline();
            }
            if(player == 2){
                vezPlayer = false;
                imageVezPlayer.setImageResource(R.drawable.vez_adversario);
            }
        }


        btnCancelar = (ImageView) findViewById(R.id.btn_cancelar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelarGame();
            }
        });
        animationLocal = new int[2];
        soundButton = MediaPlayer.create(this, R.raw.boom_2);


        txtPontuation = (TextView) findViewById(R.id.text_pontuacao);
        txtPontuation.setText(pontText + pontuationPlayer);
       //title theme
        txtTema = (TextView) findViewById(R.id.text_tema);
        txtTema.setText("Tema:\n"+temaEscolhido);

        listPosicoesSorteadas = new ArrayList<>();
        listPosicoesClicked = new ArrayList<>();
        sortearPosicoes(listPosicoesSorteadas);


        imgAirplane = (ImageView) findViewById(R.id.img_airplane);
        cardArsenal = (CardView) findViewById(R.id.card_arsenal);
        imgAirplane.setVisibility(INVISIBLE);

        //menu arsenal

        menuDropDownArsenal = (LinearLayout) findViewById(R.id.vertical_menu_arsenal);
        menuDropDownArsenal.animate().setDuration(2000);
        btnMenuArsenal = (TextView) findViewById(R.id.btn_dropdown_vertical_arsenal);
        btnAirplane = (ImageView) findViewById(R.id.btn_airplane);
        btnRadar = (ImageView) findViewById(R.id.btn_radar);
        btnMissil = (ImageView) findViewById(R.id.btn_missil);
        txtAirplane = (TextView) findViewById(R.id.qtd_arsenal_airplane);
        txtRadar = (TextView) findViewById(R.id.qtd_arsenal_radar);
        txtMissil = (TextView) findViewById(R.id.qtd_arsenal_missile);

        btnAirplane.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vezPlayer) {
                    final MediaPlayer soundButton = MediaPlayer.create(TelaBatalha.this, R.raw.airplane_sound);
                    Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide);
                    imgAirplane.setVisibility(VISIBLE);
                    soundButton.start();
                    imgAirplane.startAnimation(animSlide);

                    menuDropDownArsenal.setVisibility(INVISIBLE);
                    cardArsenal.setVisibility(INVISIBLE);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            soundButton.stop();
                        }
                    }, 3000);

                    activeArsenal(Arsenal.AIRPLANE);
                    txtAirplane.setText("0");
                    btnAirplane.setClickable(false);
                }
            }
        });

        final GifImageView gifImageView = (GifImageView) findViewById(R.id.gif_radar);

        btnRadar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if(vezPlayer) {

                    if (gifImageView.getVisibility() == INVISIBLE) {
                        gifImageView.setVisibility(VISIBLE);
                    }

                    final MediaPlayer soundButton = MediaPlayer.create(TelaBatalha.this, R.raw.radar);
                    menuDropDownArsenal.setVisibility(INVISIBLE);
                    cardArsenal.setVisibility(INVISIBLE);
                    gifImageView.setImageResource(R.drawable.progress3);
                    soundButton.start();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            gifImageView.setVisibility(INVISIBLE);
                        }
                    }, 3000);

                    if (gifImageView.getParent() != null)
                        ((ViewGroup) gifImageView.getParent()).removeView(gifImageView);
                    frameAnimation.addView(gifImageView);

                    txtRadar.setText("0");
                    btnRadar.setClickable(false);
                    activeArsenal(Arsenal.RADAR);
                }
            }
        });

        btnMissil.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vezPlayer) {
                    if (gifImageView.getVisibility() == INVISIBLE) {
                        gifImageView.setVisibility(VISIBLE);
                    }
                    gifImageView.setImageResource(R.drawable.exlosion_missile);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            gifImageView.setVisibility(INVISIBLE);
                        }
                    }, 1400);

                    final MediaPlayer soundButton = MediaPlayer.create(TelaBatalha.this, R.raw.boom);
                    menuDropDownArsenal.setVisibility(INVISIBLE);
                    cardArsenal.setVisibility(INVISIBLE);

                    soundButton.start();
                    if (gifImageView.getParent() != null)
                        ((ViewGroup) gifImageView.getParent()).removeView(gifImageView);
                    frameAnimation.addView(gifImageView);

                    txtMissil.setText("0");

                    btnMissil.setClickable(false);
                    activeArsenal(Arsenal.MISSIL);
                }
            }
        });

        Typeface typeface=Typeface.createFromAsset(getAssets(), "fonts/Bungasai.ttf");
        btnMenuArsenal.setTypeface(typeface);
        btnMenuArsenal.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final MediaPlayer soundButton = MediaPlayer.create(TelaBatalha.this, R.raw.btn_click);
                soundButton.start();
                if (menuDropDownArsenal.getVisibility() == View.VISIBLE) {
                    menuDropDownArsenal.setVisibility(View.INVISIBLE);
                    cardArsenal.setVisibility(INVISIBLE);

                } else {
                    menuDropDownArsenal.setVisibility(View.VISIBLE);
                    cardArsenal.setVisibility(VISIBLE);
                }
            }
        });


        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference("position");
        myRef.setValue(-1);
    }

    private void cancelarGame() {

        final Dialog dialog = new Dialog(TelaBatalha.this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_cancel_game);
        dialog.setTitle(R.string.cancel_title);
        dialog.show();

        Button btnCancelar2 = (Button) dialog.findViewById(R.id.btn_cancelar_game);
        btnCancelar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button btnFechar = (Button) dialog.findViewById(R.id.btn_quit);
        btnFechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(TelaBatalha.this, TelaInicial.class);
                startActivity(it);
                dialog.dismiss();
                finish();
            }
        });
    }

    public void carregarMapa(final ShipsGridModel shipsPlayers, final GridLayout gridPlayers, final FrameLayout[] frameLayout) {

        ships = new ImageView[100];

        for (int i = 0; i < 100; i++) {
            frameLayout[i] = new FrameLayout(this);
            frameLayout[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_TAM1_1) && shipsPlayers == shipsGridModel) {
                ships[i] = new ImageView(this);
                ships[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barco_tam1);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10,i%10).equals(Barcos.BARCO_HORI_TAM2_1) && shipsPlayers == shipsGridModel) {
                ships[i] = new ImageView(this);
                ships[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam2_1);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_HORI_TAM2_2) && shipsPlayers == shipsGridModel) {
                ships[i] = new ImageView(this);
                ships[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam2_2);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_HORI_TAM3_1) && shipsPlayers == shipsGridModel) {
                ships[i] = new ImageView(this);
                ships[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam3_1);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_HORI_TAM3_2) && shipsPlayers == shipsGridModel) {
                ships[i] = new ImageView(this);
                ships[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam3_2);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10,i%10).equals(Barcos.BARCO_HORI_TAM3_3) && shipsPlayers == shipsGridModel) {
                ships[i] = new ImageView(this);
                ships[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam3_3);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_HORI_TAM4_1)&& shipsPlayers == shipsGridModel) {
                ships[i] = new ImageView(this);
                ships[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam4_1);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_HORI_TAM4_2)&& shipsPlayers == shipsGridModel) {
                ships[i] = new ImageView(this);
                ships[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam4_2);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_HORI_TAM4_3) && shipsPlayers == shipsGridModel) {
                ships[i] = new ImageView(this);
                ships[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam4_3);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_HORI_TAM4_4) && shipsPlayers == shipsGridModel) {
                ships[i] = new ImageView(this);
                ships[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam4_4);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);
            }else if (shipsPlayers.getShipPosition(i/10,i%10).equals(Barcos.BARCO_VERT_TAM2_1) && shipsPlayers == shipsGridModel) {
                ships[i] = new ImageView(this);
                ships[i].setRotation(90);
                ships[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam2_1);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_VERT_TAM2_2) && shipsPlayers == shipsGridModel) {
                ships[i] = new ImageView(this);
                ships[i].setRotation(90);
                ships[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam2_2);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_VERT_TAM3_1) && shipsPlayers == shipsGridModel) {
                ships[i] = new ImageView(this);
                ships[i].setRotation(90);
                ships[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam3_1);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_VERT_TAM3_2) && shipsPlayers == shipsGridModel) {
                ships[i] = new ImageView(this);
                ships[i].setRotation(90);
                ships[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam3_2);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10,i%10).equals(Barcos.BARCO_VERT_TAM3_3) && shipsPlayers == shipsGridModel) {
                ships[i] = new ImageView(this);
                ships[i].setRotation(90);
                ships[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam3_3);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_VERT_TAM4_1)&& shipsPlayers == shipsGridModel) {
                ships[i] = new ImageView(this);
                ships[i].setRotation(90);
                ships[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam4_1);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_VERT_TAM4_2)&& shipsPlayers == shipsGridModel) {
                ships[i] = new ImageView(this);
                ships[i].setRotation(90);
                ships[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam4_2);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_VERT_TAM4_3) && shipsPlayers == shipsGridModel) {
                ships[i] = new ImageView(this);
                ships[i].setRotation(90);
                ships[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam4_3);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);

            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals(Barcos.BARCO_VERT_TAM4_4) && shipsPlayers == shipsGridModel) {
                ships[i] = new ImageView(this);
                ships[i].setRotation(90);
                ships[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ships[i].setImageResource(R.drawable.ic_barc_tam4_4);
                gridPlayers.addView(frameLayout[i]);
                frameLayout[i].addView(ships[i]);
                //gridMap.addView(arsenal[i]);
            } else if (shipsPlayers.getShipPosition(i/10, i%10).equals("") || shipsGridModelOpponent == shipsPlayers) {
                ships[i] = new ImageView(this);
                ships[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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

                    if(shipsPlayers == shipsGridModelOpponent){

                        if(!listPosicoesClicked.contains(pos)){

                            if(vezPlayer) {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();

                                DatabaseReference myRef = database.getReference("position");
                                DatabaseReference myRef2 = database.getReference("jogador");
                                myRef2.setValue(player);
                                myRef.setValue(pos);

                                player1 = true;
                                verificarClick(pos, shipsGridModelOpponent, frameLayoutPlayer2, false);
                                listPosicoesClicked.add(pos);

                                if(!vezPlayer) {
                                    imageVezPlayer.setImageResource(R.drawable.vez_adversario);

                                    if(tipoBatalha==0) {
                                        player1 = false;
                                        jogadorAI();
                                    }
                                }
                                if(tipoBatalha == 0) {
                                    myHandler = new Handler() {
                                        @Override
                                        public void handleMessage(Message msg) {
                                            if (msg.what == 0) {
                                                if (acertouP2)
                                                    jogadorAI();
                                            }
                                        }

                                        ;
                                    };
                                }
                            }

                        }

                    }
                }
            });
        }

    }

    private void jogadorAI(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                int posicaoUsada = listPosicoesSorteadas.get(0);
                verificarClick(listPosicoesSorteadas.get(0), shipsGridModel, frameLayoutPlayer,false);
                listPosicoesSorteadas.remove(0);

                if(acertouP2) {

                    if (posicaoUsada < 99) {

                        int indexElemento1 = listPosicoesSorteadas.indexOf(posicaoUsada + 1);
                        if(indexElemento1 != -1) {
                            int aux1 = listPosicoesSorteadas.get(indexElemento1);

                            listPosicoesSorteadas.set(indexElemento1, listPosicoesSorteadas.get(0));

                            listPosicoesSorteadas.set(0, aux1);
                        }

                    }
                }

                if (vezPlayer)
                    imageVezPlayer.setImageResource(R.drawable.vez_jogador);
            }
        }, 1500);
    }

    private void verificarClick(int positionClicked, ShipsGridModel shipsGridPlayer, FrameLayout[] frameLayout, boolean radar) {
        int y = positionClicked%10;
        int x = positionClicked/10;
        if (shipsGridPlayer.checkPos(x, y)) {
            ImageView imageBarco = new ImageView(TelaBatalha.this);
            imageBarco.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));


            if (shipsGridPlayer.getShipPosition(x, y).equals(Barcos.BARCO_TAM1_1)) {
                if(!radar)
                    imageBarco.setImageResource(R.drawable.ic_barco_tam1);
                else
                    imageBarco.setImageResource(R.drawable.ic_select_line_radar);
                if (vezPlayer)
                    pontuationPlayer += 100;
            } else if (shipsGridPlayer.getShipPosition(x, y).equals(Barcos.BARCO_HORI_TAM2_1) || shipsGridPlayer.getShipPosition(x, y).equals(Barcos.BARCO_VERT_TAM2_1)) {
                if(!radar)
                    imageBarco.setImageResource(R.drawable.ic_barc_tam2_1);
                else
                    imageBarco.setImageResource(R.drawable.ic_select_line_radar);
                if (vezPlayer)
                    pontuationPlayer += 20;
            } else if (shipsGridPlayer.getShipPosition(x, y).equals(Barcos.BARCO_HORI_TAM2_2) || shipsGridPlayer.getShipPosition(x, y).equals(Barcos.BARCO_VERT_TAM2_2)) {
                if(!radar)
                    imageBarco.setImageResource(R.drawable.ic_barc_tam2_2);
                else
                    imageBarco.setImageResource(R.drawable.ic_select_line_radar);
                if (vezPlayer)
                    pontuationPlayer += 20;
            } else if (shipsGridPlayer.getShipPosition(x, y).equals(Barcos.BARCO_VERT_TAM3_1) || shipsGridPlayer.getShipPosition(x, y).equals(Barcos.BARCO_VERT_TAM3_1)) {
                if(!radar)
                    imageBarco.setImageResource(R.drawable.ic_barc_tam3_1);
                else
                    imageBarco.setImageResource(R.drawable.ic_select_line_radar);
                if (vezPlayer)
                    pontuationPlayer += 30;
            } else if (shipsGridPlayer.getShipPosition(x, y).equals(Barcos.BARCO_HORI_TAM3_2) || shipsGridPlayer.getShipPosition(x, y).equals(Barcos.BARCO_VERT_TAM3_2)) {
                if(!radar)
                    imageBarco.setImageResource(R.drawable.ic_barc_tam3_2);
                else
                    imageBarco.setImageResource(R.drawable.ic_select_line_radar);
                if (vezPlayer)
                    pontuationPlayer += 30;
            } else if (shipsGridPlayer.getShipPosition(x, y).equals(Barcos.BARCO_HORI_TAM3_3) || shipsGridPlayer.getShipPosition(x, y).equals(Barcos.BARCO_VERT_TAM3_3)) {
                if(!radar)
                    imageBarco.setImageResource(R.drawable.ic_barc_tam3_3);
                else
                    imageBarco.setImageResource(R.drawable.ic_select_line_radar);
                if (vezPlayer)
                    pontuationPlayer += 30;
            } else if (shipsGridPlayer.getShipPosition(x, y).equals(Barcos.BARCO_HORI_TAM4_1) || shipsGridPlayer.getShipPosition(x, y).equals(Barcos.BARCO_VERT_TAM4_1)) {
                if(!radar)
                    imageBarco.setImageResource(R.drawable.ic_barc_tam4_1);
                else
                    imageBarco.setImageResource(R.drawable.ic_select_line_radar);
                if (vezPlayer)
                    pontuationPlayer += 40;
            } else if (shipsGridPlayer.getShipPosition(x, y).equals(Barcos.BARCO_HORI_TAM4_2) || shipsGridPlayer.getShipPosition(x, y).equals(Barcos.BARCO_VERT_TAM4_2)) {
                if(!radar)
                    imageBarco.setImageResource(R.drawable.ic_barc_tam4_2);
                else
                    imageBarco.setImageResource(R.drawable.ic_select_line_radar);
                if (vezPlayer)
                    pontuationPlayer += 40;
            } else if (shipsGridPlayer.getShipPosition(x, y).equals(Barcos.BARCO_HORI_TAM4_3) || shipsGridPlayer.getShipPosition(x, y).equals(Barcos.BARCO_VERT_TAM4_3)) {
                if(!radar)
                    imageBarco.setImageResource(R.drawable.ic_barc_tam4_3);
                else
                    imageBarco.setImageResource(R.drawable.ic_select_line_radar);
                if (vezPlayer)
                    pontuationPlayer += 40;
            } else if (shipsGridPlayer.getShipPosition(x, y).equals(Barcos.BARCO_HORI_TAM4_4) || shipsGridPlayer.getShipPosition(x, y).equals(Barcos.BARCO_VERT_TAM4_4)) {
                if(!radar)
                    imageBarco.setImageResource(R.drawable.ic_barc_tam4_4);
                else
                    imageBarco.setImageResource(R.drawable.ic_select_line_radar);
                if (vezPlayer)
                    pontuationPlayer += 40;
            }

            if (imageBarco.getParent() != null)
                ((ViewGroup) imageBarco.getParent()).removeView(imageBarco);
            frameLayout[positionClicked].addView(imageBarco);

            if(!radar) {
                soundButton.start();
                updatePontuation();

                if (vezPlayer && player1) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            showResumoExplicativo();
                        }
                    }, 500);
                }

                ImageView imageView = new ImageView(TelaBatalha.this);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setImageResource(R.drawable.ic_cross);
                if (imageView.getParent() != null)
                    ((ViewGroup) imageView.getParent()).removeView(imageView);
                frameLayout[positionClicked].addView(imageView);

                if (vezPlayer) {
                    if(!arsenal)
                        acertouP1 = true;
                    contadorShips++;

                } else if (!vezPlayer && !arsenal) {
                    acertouP2 = true;
                    if(tipoBatalha == 0)
                        myHandler.sendEmptyMessage(0);
                }
            }

        } else {

            if(!radar) {
                ImageView imageBarco = new ImageView(TelaBatalha.this);
                imageBarco.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                imageBarco.setImageResource(R.drawable.ic_explosion);
                if (imageBarco.getParent() != null)
                    ((ViewGroup) imageBarco.getParent()).removeView(imageBarco);
                frameLayout[positionClicked].addView(imageBarco);

                if (vezPlayer && !arsenal) {
                    acertouP1 = false;
                    pontuationErros += 1;
                    vezPlayer = false;

                } else if (!vezPlayer && !arsenal) {
                    acertouP2 = false;
                    vezPlayer = true;
                }
            }
        }

    }

    private void updatePontuation() {
        txtPontuation.setText(pontText+ String.valueOf(pontuationPlayer));
    }

    public void sortearPosicoes(List<Integer> listaNumbers){
        for(int i = 0; i<100; i++) {
            listaNumbers.add(i);
        }
        Collections.shuffle(listaNumbers);
    }

    public void showResumoExplicativo(){

        pontuationResumos += 1;

        int positionResume = 0;
        Resumes resumes = null;

        try {
            JSONObject jsonObject = new JSONObject(JsonRead.loadJSONFromAsset(this, "resumos2.json"));
            String json = jsonObject.toString();

            Gson gson = new Gson();
            resumes = gson.fromJson(json, Resumes.class);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        resumes.getAgile();


        final Dialog dialog = new Dialog(TelaBatalha.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_show_resume);

        TextView textTitle = (TextView) dialog.findViewById(R.id.title_resume);
        TextView textResume = (TextView) dialog.findViewById(R.id.txt_resume);
        Button btnLearned = (Button) dialog.findViewById(R.id.btn_i_learned);

        int range2 = 0;
        range2 = 2;

        Random gerador2 = new Random();

        int tema = gerador2.nextInt(range2);

        if(temaEscolhido.equals("ALEATÓRIO")){

            int range = 0;
            Random gerador = new Random();
            range = gerador.nextInt(4);

            if(range == 0){
                range = resumes.getLean().size();;
                gerador = new Random();
                positionResume = gerador.nextInt(range);
                textTitle.setText(resumes.getLean().get(positionResume).getTitle());
                textResume.setText(resumes.getLean().get(positionResume).getResume());
            } else if(range == 1) {
                range = resumes.getAgile().size();
                gerador = new Random();
                positionResume = gerador.nextInt(range);
                textTitle.setText(resumes.getAgile().get(positionResume).getTitle());
                textResume.setText(resumes.getAgile().get(positionResume).getResume());
            } else if(range == 2){
                range = resumes.getScrum().size();
                gerador = new Random();
                positionResume = gerador.nextInt(range);
                textTitle.setText(resumes.getScrum().get(positionResume).getTitle());
                textResume.setText(resumes.getScrum().get(positionResume).getResume());
            } else if(range == 3){
                range = resumes.getXP().size();
                gerador = new Random();
                positionResume = gerador.nextInt(range);
                textTitle.setText(resumes.getXP().get(positionResume).getTitle());
                textResume.setText(resumes.getXP().get(positionResume).getResume());

            }

        } else if (temaEscolhido.equals("LEAN")){

            if(tema == 0){
                int range;
                Random gerador;
                range = resumes.getAgile().size();;
                gerador = new Random();
                positionResume = gerador.nextInt(range);
                textTitle.setText(resumes.getAgile().get(positionResume).getTitle());
                textResume.setText(resumes.getAgile().get(positionResume).getResume());
            } else if( tema == 1){
                int range;
                Random gerador;
                range = resumes.getLean().size();;
                gerador = new Random();
                positionResume = gerador.nextInt(range);
                textTitle.setText(resumes.getLean().get(positionResume).getTitle());
                textResume.setText(resumes.getLean().get(positionResume).getResume());
            }


        } else if(temaEscolhido.equals("XP")){
            if(tema == 0){
                int range;
                Random gerador;
                range = resumes.getAgile().size();;
                gerador = new Random();
                positionResume = gerador.nextInt(range);
                textTitle.setText(resumes.getAgile().get(positionResume).getTitle());
                textResume.setText(resumes.getAgile().get(positionResume).getResume());
            } else if( tema == 1){
                int range;
                Random gerador;
                range = resumes.getLean().size();;
                gerador = new Random();
                positionResume = gerador.nextInt(range);
                textTitle.setText(resumes.getXP().get(positionResume).getTitle());
                textResume.setText(resumes.getXP().get(positionResume).getResume());
            }

        } else if(temaEscolhido.equals("SCRUM")){

            if(tema == 0){
                int range;
                Random gerador;
                range = resumes.getAgile().size();;
                gerador = new Random();
                positionResume = gerador.nextInt(range);
                textTitle.setText(resumes.getAgile().get(positionResume).getTitle());
                textResume.setText(resumes.getAgile().get(positionResume).getResume());
            } else if( tema == 1){
                int range;
                Random gerador;
                range = resumes.getScrum().size();;
                gerador = new Random();
                positionResume = gerador.nextInt(range);
                textTitle.setText(resumes.getScrum().get(positionResume).getTitle());
                textResume.setText(resumes.getScrum().get(positionResume).getResume());
            }

        }

        btnLearned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Log.i("contador", contadorShips+"");
                if(contadorShips == 20)
                    showVictory();
            }
        });

        dialog.show();

    }

    ///method for active the arsenal
    public void activeArsenal(int typeArsenal){

        if(arsenalGridModel != null){
            arsenal = true;
            if(typeArsenal == Arsenal.AIRPLANE){
                final int linha = arsenalGridModel.getArsenalAirPlane() * 10;

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        for(int i = 0; i < 10; i++){
                            listPosicoesClicked.add(linha+i);
                            verificarClick(linha+ i, shipsGridModelOpponent, frameLayoutPlayer2, false);
                        }

                        arsenal = false;
                    }
                }, 1500);

            } else if (typeArsenal == Arsenal.RADAR){

                final int [] radar = arsenalGridModel.getArsenalRadar();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        for(int i = 0; i < 9; i++){
                            verificarClick(radar[i], shipsGridModelOpponent, frameLayoutPlayer2, true);
                        }

                        arsenal = false;
                    }
                }, 1500);

            } else  if(typeArsenal == Arsenal.MISSIL){
                final int [] missil = arsenalGridModel.getArsenalMissil();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        for(int i = 0; i < 9; i++){
                            listPosicoesClicked.add(missil[i]);
                            verificarClick(missil[i], shipsGridModelOpponent, frameLayoutPlayer2, false);
                        }
                        arsenal = false;
                    }
                }, 300);
            }
        }

    }

    public void showVictory(){
        final Dialog dialog = new Dialog(TelaBatalha.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_show_victory);

        final MediaPlayer soundButton = MediaPlayer.create(this, R.raw.sucess);
        soundButton.start();

        TextView textPontuationPlayer = (TextView) dialog.findViewById(R.id.txt_pont_barcos);
        TextView textPontuationTotal = (TextView) dialog.findViewById(R.id.txt_pont_total);
        TextView textPontuationResume = (TextView) dialog.findViewById(R.id.txt_pont_resumos);
        TextView textPontuationErros = (TextView) dialog.findViewById(R.id.txt_pont_erros);
        Button btnOK = (Button) dialog.findViewById(R.id.btn_ok_victory);

        textPontuationPlayer.setText("Pontuação dos barcos: "+pontuationPlayer);
        textPontuationErros.setText("Bombas erradas: -" + pontuationErros);
        textPontuationResume.setText("Resumos aprendidos: "+pontuationResumos);

        int total = pontuationResumos+pontuationPlayer-pontuationErros;
        textPontuationTotal.setText("Total: "+total);

        btnOK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dialog.show();
    }

    @Override
    public void onBackPressed() {

        cancelarGame();

    }


    public void jogarOnline(){
        final VezJogador vezjogador = new VezJogador();

        mFirebaseInstance = FirebaseDatabase.getInstance();

        mFirebaseDatabase = mFirebaseInstance.getReference("position");
        mFirebaseDatabase = mFirebaseInstance.getReference("jogador");

        mFirebaseInstance.getReference("position").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("changed", "App title updated");

                Integer positionClicked = dataSnapshot.getValue(Integer.class);
                if(positionClicked != -1) {
                    if(jogadorAtual == player){
                        verificarClick(positionClicked.intValue(), shipsGridModel, frameLayoutPlayer, false);
                        vezPlayer = true;
                    } else{
                        verificarClick(positionClicked.intValue(), shipsGridModel, frameLayoutPlayer, false);
                        vezPlayer = false;
                    }

                }
                Log.i("recebido", "Posicao clicada: "+positionClicked + " Vez: "+vezPlayer);

            }



            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e("", "Failed to read app title value.", error.toException());
            }
        });


        mFirebaseInstance.getReference("jogador").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("changed", "App title updated");

                Integer valor = dataSnapshot.getValue(Integer.class);
                jogadorAtual = valor.intValue();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e("", "Failed to read app title value.", error.toException());
            }
        });

    }
}
