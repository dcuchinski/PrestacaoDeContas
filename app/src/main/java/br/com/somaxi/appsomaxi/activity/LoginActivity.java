package br.com.somaxi.appsomaxi.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import br.com.somaxi.appsomaxi.R;
import br.com.somaxi.appsomaxi.config.ConfigiracaoFirebase;
import br.com.somaxi.appsomaxi.helper.Base64Custom;
import br.com.somaxi.appsomaxi.helper.Preferencias;
import br.com.somaxi.appsomaxi.model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private EditText email, senha;
    private TextView esqueciSenha;
    private Button btnAcessar;
    private Usuario usuario;
    private FirebaseAuth autenticacao;
    private String identificadorUsuariologado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        verificaUsuarioLogado();

        email = (EditText) findViewById( R.id.editEmailResetSenha );
        senha = (EditText) findViewById( R.id.editSenha );
        btnAcessar = (Button) findViewById( R.id.btAcessar );
        esqueciSenha = (TextView) findViewById( R.id.textEsqueciSenha );

        btnAcessar.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usuario = new Usuario();
                usuario.setEmail( email.getText().toString() );
                usuario.setSenha( senha.getText().toString() );
                if (email.getText().length() == 0){
                    email.setError( "Digite seu e-mail" );

                if (senha.getText().length() ==0) {
                    senha.setError( "Digite sua senha" );

                }
                }else {
                    validarLogin();

                }

                }
            } );
        esqueciSenha.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( LoginActivity.this, RedefinirSenhaActivity.class );
                startActivity( i );
            }
        } );

    }



    private void verificaUsuarioLogado(){
    autenticacao = ConfigiracaoFirebase.getFirebaseAtenticacao();
        if (autenticacao.getCurrentUser() != null){

        abrirTelaPrincipal();

    }
}

    private void validarLogin(){
        autenticacao = ConfigiracaoFirebase.getFirebaseAtenticacao();
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    Preferencias preferencias = new Preferencias( LoginActivity.this );
                    identificadorUsuariologado = Base64Custom.codificarBase64( usuario.getEmail() );
                    preferencias.salvarDados( identificadorUsuariologado);

                    abrirTelaPrincipal();
                    Toast.makeText(LoginActivity.this, "Login efetudo com sucesso!!!", Toast.LENGTH_LONG).show();



                }else {
                    String erroEcecao = "";

                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        erroEcecao  = "E-mail não cadastrado!";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erroEcecao  = "Senha não confere!";
                    } catch (Exception e) {
                        erroEcecao = "Ao efetuar login!";
                        e.printStackTrace();
                    }

                    Toast.makeText(LoginActivity.this, "Erro: " + erroEcecao, Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private void abrirTelaPrincipal(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        Toast.makeText(LoginActivity.this, "Bem vindo de volta!!!", Toast.LENGTH_LONG).show();
        finish();
    }

}