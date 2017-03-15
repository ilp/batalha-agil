package batalhaagil.ufrpe.iversonluis.batalhaagil;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

public class TelaInicial extends AppCompatActivity {

    private TextView btnIniciar;
    private ImageView navio1;
    private ImageView navio2;
    private ImageView navio3;
    private ImageView navio4;
    private ImageView navio5;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial);

        toolbar = (Toolbar) findViewById(R.id.toolbar_inicial);
        btnIniciar = (TextView) findViewById(R.id.btnIniciar);
        navio1 = (ImageView) findViewById(R.id.navio1);
        navio2 = (ImageView) findViewById(R.id.navio2);
        navio3 = (ImageView) findViewById(R.id.navio3);
        navio4 = (ImageView) findViewById(R.id.navio4);
        navio5 = (ImageView) findViewById(R.id.navio5);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.inflateMenu(R.menu.menu_tela_inicial);
        toolbar.setTitle("");

        VideoView videoview = (VideoView) findViewById(R.id.videoView);
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.back_ocean);
        videoview.setVideoURI(uri);
        videoview.start();
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });


        Typeface typeface=Typeface.createFromAsset(getAssets(), "fonts/Bungasai.ttf");
        btnIniciar.setTypeface(typeface);

        final MediaPlayer soundAbertura = MediaPlayer.create(this, R.raw.battleship_sound);
        final MediaPlayer soundButton = MediaPlayer.create(this, R.raw.btn_press);
        soundAbertura.start();
        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation a = AnimationUtils.loadAnimation(TelaInicial.this, R.anim.milkshake);
                btnIniciar.startAnimation(a);
                btnIniciar.setTextColor(ContextCompat.getColor(TelaInicial.this, R.color.colorPrimaryDark));

                TranslateAnimation animation = new TranslateAnimation(0,-1500,0,0);
                animation.setDuration(2000);
                animation.setFillAfter(false);

                TranslateAnimation animation2 = new TranslateAnimation(0,1500,0,0);
                animation2.setDuration(2000);
                animation2.setFillAfter(false);

                navio1.startAnimation(animation);
                navio2.startAnimation(animation2);
                navio3.startAnimation(animation);
                navio4.startAnimation(animation2);
                navio5.startAnimation(animation2);

                soundButton.start();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent it = new Intent(TelaInicial.this, DefinirOponente.class);
                        startActivity(it);
                        soundAbertura.stop();
                        finish();
                    }
                }, 900);

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tela_inicial, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {

            showAbout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void showAbout(){
        final Dialog dialog = new Dialog(TelaInicial.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_show_about);

        final MediaPlayer soundButton = MediaPlayer.create(this, R.raw.sucess);
        soundButton.start();

        Button btnOK = (Button) dialog.findViewById(R.id.btn_ok_close);


        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}
