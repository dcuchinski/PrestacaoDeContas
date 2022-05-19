package br.com.somaxi.appsomaxi.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public final class ConfigiracaoFirebase {

    private static DatabaseReference referenciaFirebase;
    private static FirebaseAuth autenticacao;

    public static DatabaseReference getFirebase(){

        if (referenciaFirebase == null) {
            referenciaFirebase = FirebaseDatabase.getInstance().getReference();
        }
        return  referenciaFirebase;
    }

    public static FirebaseAuth getFirebaseAtenticacao(){

        if (autenticacao == null){
            autenticacao = FirebaseAuth.getInstance();

        }
        return autenticacao;


    }

}
