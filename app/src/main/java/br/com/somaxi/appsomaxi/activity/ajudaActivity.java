package br.com.somaxi.appsomaxi.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.somaxi.appsomaxi.R;

public class ajudaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView botaoAcao1, botaoAcao2;
    private TextView textoRecolheOrientacaoFinanceira, textoRecolheConferenciaAprovacao;

    private boolean orientacaoFinanceira = false;
    private boolean conferenciaAprova = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_ajuda );

        //textoRecolheOrientacaoFinanceira.setVisibility( View.GONE );

        botaoAcao1 = (ImageView) findViewById( R.id.imageViewMaisOriFin );
        botaoAcao2 = (ImageView) findViewById( R.id.imageViewMaisConfAprovGastos );
        textoRecolheOrientacaoFinanceira = (TextView) findViewById( R.id.textViewRecolheOriFin );
        textoRecolheConferenciaAprovacao = (TextView) findViewById( R.id.textViewRecolheConferenciaAprova );

        //Imnplementação da Toobar
        toolbar = (Toolbar) findViewById( R.id.toolbar );
        toolbar.setTitle( "Ajuda" );
        toolbar.setNavigationIcon( R.drawable.ic_arrow_back );
        setSupportActionBar( toolbar );

        botaoAcao1.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (orientacaoFinanceira == false) {
                    botaoAcao1.setImageResource( R.drawable.ic_expandi );
                    textoRecolheOrientacaoFinanceira.setVisibility( View.GONE );
                    orientacaoFinanceira = true;
                } else {
                    botaoAcao1.setImageResource( R.drawable.ic_recolhe );
                    textoRecolheOrientacaoFinanceira.setVisibility( View.VISIBLE );
                    orientacaoFinanceira = false;
                }
            }
        } );
        botaoAcao2.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (conferenciaAprova == false) {
                    botaoAcao2.setImageResource( R.drawable.ic_expandi );
                    textoRecolheConferenciaAprovacao.setVisibility( View.GONE );
                    conferenciaAprova = true;
                } else {
                    botaoAcao2.setImageResource( R.drawable.ic_recolhe );
                    textoRecolheConferenciaAprovacao.setVisibility( View.VISIBLE );
                    conferenciaAprova = false;
                }
            }
        } );
    }
}
