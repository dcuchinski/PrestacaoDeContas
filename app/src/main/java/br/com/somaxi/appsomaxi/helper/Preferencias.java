package br.com.somaxi.appsomaxi.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferencias {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private String CHAVE_IDENTIFICADOR = "identificadorUsuariologado";



    public Preferencias (Context contextParametro){

        int MODE = 0;
        String NOME_ARQUIVO = "app_somaxi";
        preferences = contextParametro.getSharedPreferences(NOME_ARQUIVO, MODE);
        editor = preferences.edit();


    }

    public void salvarDados(String identificadorUsuariologado) {
        editor.putString(CHAVE_IDENTIFICADOR, identificadorUsuariologado);
        editor.commit();
    }

    public String getIdentificador(){

        return preferences.getString( CHAVE_IDENTIFICADOR, null );

    }


}
