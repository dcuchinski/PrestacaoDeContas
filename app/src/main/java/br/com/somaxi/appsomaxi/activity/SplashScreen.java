package br.com.somaxi.appsomaxi.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import br.com.somaxi.appsomaxi.R;

public class SplashScreen extends AppCompatActivity implements Runnable{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_splash_screen );

        Handler handler = new Handler(  );
        handler.postDelayed( this, 3000 );

    }
    @Override
    public void run(){
        startActivity( new Intent( this, LoginActivity.class) );
    }
}
