package br.com.somaxi.appsomaxi.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import br.com.somaxi.appsomaxi.R;
import br.com.somaxi.appsomaxi.config.ConfigiracaoFirebase;

public class CadastroUsuarioActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_cadastro_usuario );

        auth = ConfigiracaoFirebase.getFirebaseAtenticacao();


        //Imnplementação da Toobar
        toolbar = (Toolbar) findViewById( R.id.toolbar );
        toolbar.setTitle( "Perfil Usuário" );
        toolbar.setNavigationIcon( R.drawable.ic_arrow_back );
        setSupportActionBar( toolbar );


    }


}
