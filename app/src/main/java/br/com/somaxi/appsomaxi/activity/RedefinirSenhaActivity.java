package br.com.somaxi.appsomaxi.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import br.com.somaxi.appsomaxi.R;
import br.com.somaxi.appsomaxi.config.ConfigiracaoFirebase;

public class RedefinirSenhaActivity extends AppCompatActivity {

    private EditText emailResetSenha;
    private Button botaoRedefineSenha;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_redefinir_senha );

        emailResetSenha = (EditText) findViewById( R.id.editEmailResetSenha );
        botaoRedefineSenha = (Button) findViewById( R.id.btRedefiniSenha );

        botaoRedefineSenha.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailResetSenha.getText().toString().trim();
                resetSenha( email );
            }
        } );


        }
        private void resetSenha(String email){
        auth.sendPasswordResetEmail( email )
                .addOnCompleteListener( RedefinirSenhaActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            alert ("Um e-mail foi enviado para redefinir sua senha");
                            Intent i = new Intent( RedefinirSenhaActivity.this, LoginActivity.class );
                            startActivity( i );
                            finish();
                        }else {
                            alert( "E-mail não cadastrado" );
                        }
                    }
                } );
            }

    private void alert(String s) {
        Toast.makeText( RedefinirSenhaActivity.this, s, Toast.LENGTH_LONG ).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth = ConfigiracaoFirebase.getFirebaseAtenticacao();
    }
}

