package br.com.somaxi.appsomaxi.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import br.com.somaxi.appsomaxi.R;
import br.com.somaxi.appsomaxi.config.ConfigiracaoFirebase;

public class MainActivity extends AppCompatActivity {


    private ImageView prestarContas;
    private ImageView racDigital;
    private ImageView dadosSomaxi;
    private ImageView portifolio;

    //Item para mensagem de alerta em breve
    private AlertDialog alerta;

    private FirebaseAuth auth;
    private Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        //item Prestar Conta
        prestarContas  = (ImageView) findViewById( R.id.image_prestar_contas );

        prestarContas.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (MainActivity.this, PrestarContasActivity.class);
                startActivity( i );
            }
        } );

        //item RAC Digital
        racDigital = (ImageView) findViewById( R.id.image_rac_digital );
        racDigital.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emBreve();
            }
        } );
        //item Dados Somaxi
        dadosSomaxi = (ImageView) findViewById( R.id.image_dados_somaxi );
        dadosSomaxi.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emBreve();
            }
        } );
        //item portifolio
        portifolio = (ImageView) findViewById( R.id.image_portifolio );
        portifolio.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emBreve();
            }
        } );


        auth = ConfigiracaoFirebase.getFirebaseAtenticacao();
        //Imnplementação da Toobar
        toolbar = (Toolbar) findViewById( R.id.toolbar );
        toolbar.setTitle( "" );
        setSupportActionBar( toolbar );
        getSupportActionBar().setIcon( R.drawable.ic_logo_s_novo);

    }

    public void deslogarUsuario(){
        auth.signOut();
        Intent i = new Intent( MainActivity.this, LoginActivity.class);
        startActivity( i );
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.menu_main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Executar o teste em cada item para a que sua ação seja chamada
        switch (item.getItemId()){
            case R.id.item_sair:
                deslogarUsuario();
                return true;
            case R.id.item_alterar_senha:
                alterarSenha();
                return true;
            case R.id.ajuda:
                Intent intentAjuda = new Intent( MainActivity.this, ajudaActivity.class);
                startActivity( intentAjuda );
                return true;
            case R.id.perfil_usuario:
                Intent intentCadastoUsuario = new Intent( MainActivity.this, CadastroUsuarioActivity.class );
                startActivity( intentCadastoUsuario );
                return true;
            default:
                return super.onOptionsItemSelected( item );
        }


    }

    private void alterarSenha() {
        auth.signOut();
        Intent i = new Intent( MainActivity.this, RedefinirSenhaActivity.class );
        startActivity( i );
        finish();
    }

    private void emBreve(){
        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setTitle( "Somaxi Solutions" );
        builder.setMessage( "Este item estará disponível em breve!!!" );
        builder.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText( MainActivity.this, "Equipe Somaxi de Desenvolvimento", Toast.LENGTH_LONG ).show();
            }
        } );
        alerta = builder.create();
        alerta.show();
    }

}
