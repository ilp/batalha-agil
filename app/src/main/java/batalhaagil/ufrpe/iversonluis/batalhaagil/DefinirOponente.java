package batalhaagil.ufrpe.iversonluis.batalhaagil;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Set;

import batalhaagil.ufrpe.iversonluis.batalhaagil.bluetooth.BluetoothAdapterInfo;
import batalhaagil.ufrpe.iversonluis.batalhaagil.bluetooth.ConnectionThread;
import batalhaagil.ufrpe.iversonluis.batalhaagil.firebase.Jogador;

public class DefinirOponente extends AppCompatActivity {
    //configuracao bluetooth
    public static int NOT_BLUETOOTH = -1;
    public static int ENABLE_BLUETOOTH = 1;
    public static int ON_BLUETOOTH = 2;
    public static int SELECT_PAIRED_DEVICE = 2;
    public static final int SIGN_IN_REQUEST_CODE = 1;
    public static int SELECT_DISCOVERED_DEVICE = 3;
    public static DefinirOponente instance = null;

    private String temaEscolhido;
    private int tipoBatalha;

    ConnectionThread connect;
    private ArrayList<BluetoothAdapterInfo> listDescoveredDevices;
    AlertDialog.Builder builderSingle;
    ArrayAdapter<String> mArrayAdapter;

    private Spinner spinTema;
    private TextView btnDesafiar;
    private TextView btnDesafiarWeb;
    private TextView btnDesafiarAI;
    TextView txtTitle;

    int flags[] = {R.drawable.ic_random, R.drawable.ic_lean, R.drawable.ic_scrum, R.drawable.ic_xp};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escolher_tema);
        this.instance = this;

        spinTema = (Spinner) findViewById(R.id.spinner_tema);
        btnDesafiar = (TextView) findViewById(R.id.btn_desafiar);
        btnDesafiarWeb = (TextView) findViewById(R.id.btn_desafiar_web);
        btnDesafiarAI = (TextView) findViewById(R.id.btn_desafiar_ai);
        txtTitle = (TextView) findViewById(R.id.titulo_escolher_tema);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Bungasai.ttf");
        txtTitle.setTypeface(typeface);
        btnDesafiar.setTypeface(typeface);
        btnDesafiarAI.setTypeface(typeface);
        btnDesafiarWeb.setTypeface(typeface);

        final CustomSpinnerTema customAdapter = new CustomSpinnerTema(getApplicationContext(), flags, getResources().getStringArray(R.array.temas_array));
        spinTema.setAdapter(customAdapter);
        temaEscolhido = customAdapter.getItem(spinTema.getSelectedItemPosition());

        final MediaPlayer soundButton = MediaPlayer.create(this, R.raw.btn_press);

        btnDesafiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation a = AnimationUtils.loadAnimation(DefinirOponente.this, R.anim.milkshake);
                btnDesafiar.startAnimation(a);
                btnDesafiar.setTextColor(ContextCompat.getColor(DefinirOponente.this, R.color.colorPrimaryDark));
                soundButton.start();
                searchOpponent();
                temaEscolhido = customAdapter.getItem(spinTema.getSelectedItemPosition());
                btnDesafiar.setTextColor(ContextCompat.getColor(DefinirOponente.this, R.color.colorWhite));
            }
        });

        btnDesafiarWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipoBatalha = 1;
                if(verificaConexao()){
                    soundButton.start();

                    if(FirebaseAuth.getInstance().getCurrentUser() == null) {

                        startActivityForResult(
                                AuthUI.getInstance()
                                        .createSignInIntentBuilder()
                                        .build(),
                                SIGN_IN_REQUEST_CODE
                        );
                    } else {

                        Intent it = new Intent(DefinirOponente.this, CreateMapa.class);
                        it.putExtra("temaEscolhido", temaEscolhido);
                        it.putExtra("tipoBatalha", tipoBatalha);
                        startActivity(it);

                        Toast.makeText(getApplicationContext(),
                                "Vamos jogar " + FirebaseAuth.getInstance()
                                        .getCurrentUser()
                                        .getDisplayName(),
                                Toast.LENGTH_LONG)
                                .show();
                    }

                } else{
                    Toast.makeText(getApplicationContext(),"Sem conexão com a Internet!",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnDesafiarAI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipoBatalha = 0;
                Animation a = AnimationUtils.loadAnimation(DefinirOponente.this, R.anim.milkshake);
                btnDesafiarAI.startAnimation(a);
                soundButton.start();
                temaEscolhido = customAdapter.getItem(spinTema.getSelectedItemPosition());
                Intent it = new Intent(DefinirOponente.this, CreateMapa.class);
                it.putExtra("temaEscolhido", temaEscolhido);
                it.putExtra("tipoBatalha", tipoBatalha);
                startActivity(it);
            }
        });

    }

    //Exibe dialog para o usuario configurar o bluetooth e posteriomente escolher seu adversario
    public void searchOpponent() {

        if (verifyBluetoothAdapter() == ON_BLUETOOTH) { //se estiver ligado
            listDevices();
        } else if (verifyBluetoothAdapter() == ENABLE_BLUETOOTH) { //se estiver desativado

            new AlertDialog.Builder(this)
                    .setTitle(R.string.bluetooth_off)
                    .setMessage(R.string.bluetooth_off_text)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //ativa o bluetooth
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .show();

        }
    }

    //faz a busca pelos dispositivos bluetooth
    public void listDevices() {
        builderSingle = new AlertDialog.Builder(DefinirOponente.this);
        builderSingle.setTitle(R.string.title_bluetooth);
        builderSingle.setIcon(R.drawable.ic_bluetooth);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(DefinirOponente.this, android.R.layout.select_dialog_item);

        for (BluetoothAdapterInfo item : pairedDevices()) {
            arrayAdapter.add(item.getName());
        }

        builderSingle.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setPositiveButton(R.string.btn_search_name, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listDescoveredDevices = new ArrayList<>();
                descoveredDevices();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //faz a connection com o device
                connectOpponent(pairedDevices().get(which).getAddress());
                dialog.dismiss();
            }
        });
        builderSingle.show();
    }

    //retorna uma lista com os nomes dos dispostivos ja pareados
    public ArrayList<BluetoothAdapterInfo> pairedDevices() {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        ArrayList<BluetoothAdapterInfo> pairedList = new ArrayList<>();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                BluetoothAdapterInfo deviceInfo = new BluetoothAdapterInfo(device.getName(), device.getAddress());
                pairedList.add(deviceInfo);
            }
        }

        return pairedList;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                BluetoothAdapterInfo deviceInfo = new BluetoothAdapterInfo(device.getName(), device.getAddress());
                listDescoveredDevices.add(deviceInfo);
            }
        }
    };

    public void descoveredDevices() {

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        btAdapter.startDiscovery();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        builderSingle = new AlertDialog.Builder(DefinirOponente.this);
        builderSingle.setTitle(R.string.title_bluetooth);
        builderSingle.setIcon(R.drawable.ic_bluetooth);

        mArrayAdapter = new ArrayAdapter<>(DefinirOponente.this, android.R.layout.select_dialog_item);

        builderSingle.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(mArrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //faz a connection com o device
                connectOpponent(listDescoveredDevices.get(which).getAddress());
                dialog.dismiss();
            }
        });
        builderSingle.show();
    }




    //verificar como está o adaptador bluetooth do dispositivo
    public int verifyBluetoothAdapter() {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            return NOT_BLUETOOTH;
        } else {
            if (!btAdapter.isEnabled()) {
                return ENABLE_BLUETOOTH;
            } else {
                return ON_BLUETOOTH;
            }
        }
    }

    public static void showAlert(String status) {
        AlertDialog.Builder builderInner = new AlertDialog.Builder(DefinirOponente.getInstance());
        builderInner.setMessage(status);
        builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderInner.show();
    }

    //connecta ao endereco do device
    public void connectOpponent(String deviceAddress) {
        connect = new ConnectionThread(deviceAddress);
        connect.start();
    }

    //pegar a instancia para utilizar em metodos statics
    public static DefinirOponente getInstance() {
        return instance;
    }

    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            byte[] data = bundle.getByteArray("data");
            String dataString = new String(data);

            if (dataString.equals("---N")) {
                showAlert(getInstance().getResources().getString(R.string.bluetooth_error));
            } else if (dataString.equals("---S")) {
                showAlert(getInstance().getResources().getString(R.string.bluetooth_success));
                //Intent it = new Intent(getInstance(), CreateMapa.class);
                //getInstance().startActivity(it);
            } else {

                //textSpace.setText(new String(data));
            }
        }
    };


    @Override
    protected void onDestroy() {

        super.onDestroy();

        /*  Remove o filtro de descoberta de dispositivos do registro.
         */
      // unregisterReceiver(mReceiver);
    }


    public  boolean verificaConexao() {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conectado = true;
        } else {
            conectado = false;
        }
        return conectado;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {


                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Jogadores");

                Intent it = new Intent(DefinirOponente.this, CreateMapa.class);
                it.putExtra("temaEscolhido", temaEscolhido);
                it.putExtra("tipoBatalha", tipoBatalha);
                startActivity(it);

                Toast.makeText(this,
                        "Sucesso ao entrar!",
                        Toast.LENGTH_LONG)
                        .show();
            } else {
                Toast.makeText(this,
                        "Por favor tente mais tarde!",
                        Toast.LENGTH_LONG)
                        .show();

                // Close the app
                finish();
            }
        }

    }
}
