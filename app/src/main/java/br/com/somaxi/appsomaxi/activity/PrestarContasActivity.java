package br.com.somaxi.appsomaxi.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.somaxi.appsomaxi.R;
import br.com.somaxi.appsomaxi.config.ConfigiracaoFirebase;
import br.com.somaxi.appsomaxi.helper.Base64Custom;
import br.com.somaxi.appsomaxi.helper.PermissionUtils;
import br.com.somaxi.appsomaxi.helper.Preferencias;
import br.com.somaxi.appsomaxi.model.PrestarContas;
import br.com.somaxi.appsomaxi.validation.ValidaCpfCnpj;

public class PrestarContasActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private StorageReference mStorageRef;
    private Toolbar toolbar;
    boolean formularioValidado;
    private Bitmap imageBitmap;

    //Botões aplicação
    private Button enviarPestacao;
    private ImageView btnCapturaComprovante, fotoComprovante;

    //id usuário logado e nome usuário
    private String idUser;
    private String emailUsuario, usuario;

    //itens do formulário
    private TextView dataDespesa, nomeArquivo, idDespesa;
    private EditText filialLoja, numeroChamado, placaCarro, descricao, valor, numeroComprovante, cpf, cnpj;

    //itens do formulário Dropbox (Spinner)
    private Spinner classificar, tipoDespesa, cliente, tipoComprovante, formaPagamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_prestar_contas );

        inicializarFirebase();

        //Imnplementação da Toobar
        toolbar = (Toolbar) findViewById( R.id.toolbar );
        toolbar.setTitle( "Prestar Contas" );
        toolbar.setNavigationIcon( R.drawable.ic_arrow_back );
        setSupportActionBar( toolbar );

        //Inicializar itens formulário
        dataDespesa = (TextView) findViewById( R.id.textViewData );
        idDespesa = (TextView) findViewById( R.id.textViewIdDespesa );
        filialLoja = (EditText) findViewById( R.id.editFilialLoja );
        numeroChamado = (EditText) findViewById( R.id.editNumeroChamado );
        placaCarro = (EditText) findViewById( R.id.editPlacaCarro );
        descricao = (EditText) findViewById( R.id.editDescricao );
        valor = (EditText) findViewById( R.id.editValorComprovante );
        numeroComprovante = (EditText) findViewById( R.id.editNumeroComprovante );
        cpf = (EditText) findViewById( R.id.editCpf );
        cnpj = (EditText) findViewById( R.id.editCnpj );
        btnCapturaComprovante = (ImageView) findViewById( R.id.imageCapturaComprovante );
        nomeArquivo = (TextView) findViewById( R.id.textViewNovaImagem );
        fotoComprovante = (ImageView) findViewById( R.id.imageViewComprovante );
        enviarPestacao = (Button) findViewById( R.id.btnEnviar );
        enviarPestacao.setEnabled( false );


        //Inicilair itens do Spinner
        classificar = (Spinner) findViewById( R.id.spinnerClassificarDespesa );
        ArrayAdapter adapterClassificar = ArrayAdapter.createFromResource( this, R.array.classificacao, android.R.layout.simple_spinner_dropdown_item );
        classificar.setAdapter( adapterClassificar );

        tipoDespesa = (Spinner) findViewById( R.id.spinnerTipoDespesa );
        ArrayAdapter adapterTipoDespesa = ArrayAdapter.createFromResource( this, R.array.tipo_despesa, android.R.layout.simple_spinner_dropdown_item );
        tipoDespesa.setAdapter( adapterTipoDespesa );

        cliente = (Spinner) findViewById( R.id.spinnerCliente );
        ArrayAdapter adapterCliente = ArrayAdapter.createFromResource( this, R.array.clientes, android.R.layout.simple_spinner_dropdown_item );
        cliente.setAdapter( adapterCliente );

        tipoComprovante = (Spinner) findViewById( R.id.spinnerComprovanteTipo );
        ArrayAdapter adapterComprovante = ArrayAdapter.createFromResource( this, R.array.comprovante, android.R.layout.simple_spinner_dropdown_item );
        tipoComprovante.setAdapter( adapterComprovante );

        formaPagamento = (Spinner) findViewById( R.id.spinnerFormaPagamento );
        ArrayAdapter adapterFormaPagamento = ArrayAdapter.createFromResource( this, R.array.forma_pagamento, android.R.layout.simple_spinner_dropdown_item );
        formaPagamento.setAdapter( adapterFormaPagamento );

        //e-mail do usuário logado
        Preferencias preferencias = new Preferencias( PrestarContasActivity.this );
        idUser = preferencias.getIdentificador();
        emailUsuario = Base64Custom.decodificarBase64( idUser );


        //Criando nome usuário
        String str = emailUsuario.substring( 0, emailUsuario.indexOf( "@" )).replace( ".", " " );
        usuario = str;
        //Toast.makeText( PrestarContasActivity.this, usuario, Toast.LENGTH_LONG ).show();


        //Mascara campo CPF
        SimpleMaskFormatter cpfMask = new SimpleMaskFormatter( "NNN.NNN.NNN-NN" );
        MaskTextWatcher cpfMtw = new MaskTextWatcher( cpf, cpfMask );
        cpf.addTextChangedListener( cpfMtw );

        //Mascara campo CNPJ
        SimpleMaskFormatter cnpjMask = new SimpleMaskFormatter( "NN.NNN.NNN/NNNN-NN" );
        MaskTextWatcher cnpjMtw = new MaskTextWatcher( cnpj, cnpjMask );
        cnpj.addTextChangedListener( cnpjMtw );

        //Valida Compo CPF
        valor.setOnFocusChangeListener( new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ValidaCpfCnpj validaCpfCnpj = new ValidaCpfCnpj();
                String selecComprovante = tipoComprovante.getSelectedItem().toString();
                if (hasFocus == true) {
                    switch (selecComprovante) {

                        case "Cupom Fiscal":
                            if (validaCpfCnpj.isCNPJ( cnpj.getText().toString() ) == true) {
                                Toast.makeText( PrestarContasActivity.this, "CNPJ validado!!!", Toast.LENGTH_LONG ).show();
                            } else {
                                cnpj.setError( "Digite um CNPJ válido!!!" );
                                cnpj.requestFocus();
                            }

                            break;
                        case "Nota Fiscal":
                            if (validaCpfCnpj.isCNPJ( cnpj.getText().toString() ) == true) {
                                Toast.makeText( PrestarContasActivity.this, "CNPJ validado!!!", Toast.LENGTH_LONG ).show();
                            } else {
                                cnpj.setError( "Digite um CNPJ válido!!!" );
                                cnpj.requestFocus();
                            }

                            break;
                        case "Recibo":
                            if (validaCpfCnpj.isCPF( cpf.getText().toString() ) == true) {
                                Toast.makeText( PrestarContasActivity.this, "CPF validado!!!", Toast.LENGTH_LONG ).show();
                            } else {
                                cpf.setError( "Digite um CPF válido!!!" );
                                cpf.requestFocus();

                            }

                        default:

                    }
                }
            }
        } );
        numeroComprovante.setOnFocusChangeListener( new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ValidaCpfCnpj validaCpfCnpj = new ValidaCpfCnpj();
                if (hasFocus == true) {
                    if (!TextUtils.isEmpty( cnpj.getText().toString() )) {
                        if (validaCpfCnpj.isCNPJ( cnpj.getText().toString() ) == true) {
                            Toast.makeText( PrestarContasActivity.this, "CNPJ validado!!!", Toast.LENGTH_LONG ).show();
                        } else {
                            cnpj.setError( "Digite um CNPJ válido!!!" );
                            cnpj.requestFocus();
                        }
                    }
                    if (TextUtils.isEmpty( cnpj.getText().toString() )){
                        cnpj.setError( "Digite um CNPJ válido!!!" );
                        cnpj.requestFocus();
                    }
                }
            }
        } );

        //Trazer data atual no campo dataDespesa
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat( "dd/MM/yyyy" );
        String dataCorrente = sdf.format( date );
        dataDespesa.setText( dataCorrente );

        //Regra do Spinner "Classificar Despesa"
        classificar.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (!TextUtils.equals( "Classificar" , classificar.getSelectedItem().toString())) {
                    identificadorDespesa();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        } );
        //Regra do Spinner "Tipo Despesa"
        tipoDespesa.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                validaCampoPlacaCarro();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        } );
        //Regra do Spinner "Tipo Comprovante"
        tipoComprovante.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                validaCampoComprovante();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        } );

        //Iniciar captura imagem
        btnCapturaComprovante.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checar se app possui permisão para utilização da camera
                String[] permissoes = new String[]{
                        Manifest.permission_group.CAMERA
                };
                PermissionUtils.validate( PrestarContasActivity.this, 0, permissoes );
                abrirCamera();
                Toast.makeText( PrestarContasActivity.this, "Capture seu comprovante!!!", Toast.LENGTH_LONG ).show();
                //ativaBotaoEnviar();
                enviarPestacao.setEnabled( true );
                enviarPestacao.requestFocus();
            }
        } );

        //Ação do botão Enviar prestação
        enviarPestacao.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean dadosValidadosSpinner = validarCamposSpinner();
                boolean dadosValidadosTexto = validarCamposTextos();

                if (!dadosValidadosSpinner) {
                    Toast.makeText( PrestarContasActivity.this, "Verifique campos obrigatórios!!!", Toast.LENGTH_LONG ).show();
                    if (!dadosValidadosTexto) {
                        Toast.makeText( PrestarContasActivity.this, "Verifique campos obrigatórios!!!", Toast.LENGTH_LONG ).show();
                    }
                } else {
                    addBancoDados();
                    addImagem();
                    finish();
                    Toast.makeText( PrestarContasActivity.this, "Enviado com Sucesso!!!", Toast.LENGTH_LONG ).show();
                }
            }
        } );
    }
    //Validações do formulário ---- Inicio ---
    private boolean validarCamposSpinner() {
        boolean retorno = false;

        if (TextUtils.equals( "Forma de Pagamento", formaPagamento.getSelectedItem().toString() )) {
            ((TextView) formaPagamento.getChildAt( 0 )).setError( "" );
            if (TextUtils.equals( "Tipo Comprovante", tipoComprovante.getSelectedItem().toString() )) {
                ((TextView) tipoComprovante.getChildAt( 0 )).setError( "" );
                if (TextUtils.equals( "Tipo Despesa", tipoDespesa.getSelectedItem().toString() )) {
                    ((TextView) tipoDespesa.getChildAt( 0 )).setError( "" );
                    if (TextUtils.equals( "Cliente", cliente.getSelectedItem().toString() )) {
                        ((TextView) cliente.getChildAt( 0 )).setError( "" );
                        if (TextUtils.equals( "Classificar", classificar.getSelectedItem().toString() )) {
                            ((TextView) classificar.getChildAt( 0 )).setError( "" );
                        }
                    }
                }
            }
        } else {
            retorno = true;
        }
        return retorno;
    }

    private boolean validarCamposTextos() {
        boolean retorno = false;

        if (TextUtils.isEmpty( filialLoja.getText().toString() )) {
            filialLoja.setError( "Descreva qual Filal / Loja!!!" );
            filialLoja.requestFocus();
            if (numeroChamado.getText().toString().length() < 5) {
                numeroChamado.setError( "Chamado Inválido!!!" );
                numeroChamado.requestFocus();
                if (placaCarro.getText().toString().length() < 7) {
                    placaCarro.setError( "Chamado Inválido!!!" );
                    placaCarro.requestFocus();
                    if (TextUtils.isEmpty( descricao.getText().toString() )) {
                        descricao.setError( "Descreva a despesa de forma breve!!!" );
                        descricao.requestFocus();
                        if (TextUtils.isEmpty( valor.getText().toString() )) {
                            valor.setError( "Informe valor!!!" );
                            valor.requestFocus();
                            if (TextUtils.isEmpty( numeroComprovante.getText().toString() )) {
                                numeroComprovante.setError( "Informe número Comprovante!!!" );
                                numeroComprovante.requestFocus();
                                if (TextUtils.isEmpty( cpf.getText().toString() )) {
                                    cpf.setError( "Informe CPF!!!" );
                                    cpf.requestFocus();
                                    if (TextUtils.isEmpty( cnpj.getText().toString() )) {
                                        cnpj.setError( "Informe CNPJ!!!" );
                                        cnpj.requestFocus();
                                    }
                                }
                            }
                        }
                    }

                }

            }
        } else {
            retorno = true;
        }
        return retorno;
    }

    private void validaCampoPlacaCarro() {
        //verificar campo "Tipo Despesa" para validar entrada da placa do carro
        placaCarro.setText( "" );

        if (TextUtils.equals( "Abastecimento", tipoDespesa.getSelectedItem().toString() )) {
            placaCarro.setEnabled( true );
            Toast.makeText( PrestarContasActivity.this, "É obrigatório informar a placa do carro!!!", Toast.LENGTH_LONG ).show();
        } else {
            placaCarro.setEnabled( false );
        }
    }

    private void validaCampoComprovante() {
        numeroComprovante.setEnabled( false );
        numeroComprovante.setText( "" );
        cpf.setEnabled( false );
        cpf.setText( "" );
        cpf.setError( null );
        cnpj.setEnabled( false );
        cnpj.setText( "" );
        cnpj.setError( null );

        String opcaoComprovante = tipoComprovante.getSelectedItem().toString();

        switch (opcaoComprovante) {

            case "Cupom Fiscal":
                numeroComprovante.setEnabled( true );
                cnpj.setEnabled( true );
                cnpj.requestFocus();
                Toast.makeText( PrestarContasActivity.this, "É obrigatório informar o número e CNPJ!!!", Toast.LENGTH_LONG ).show();
                break;
            case "Nota Fiscal":
                numeroComprovante.setEnabled( true );
                cnpj.setEnabled( true );
                cnpj.requestFocus();
                Toast.makeText( PrestarContasActivity.this, "É obrigatório informar o número e CNPJ!!!", Toast.LENGTH_LONG ).show();
                break;
            case "Recibo":
                cpf.setEnabled( true );
                cpf.requestFocus();
                Toast.makeText( PrestarContasActivity.this, "É obrigatório informar o CPF do fornecedor!!!", Toast.LENGTH_LONG ).show();
            default:
        }
    }
    //Validação formulário --- Fim ----

    private void inicializarFirebase() {
        FirebaseApp.initializeApp( PrestarContasActivity.this );
        auth = ConfigiracaoFirebase.getFirebaseAtenticacao();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

    }

    private void abrirCamera() {
        Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
        startActivityForResult( intent, 0 );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        /*if (data != null){
            Bundle bundle = data.getExtras();
            if (bundle != null){
                Bitmap img = (Bitmap) bundle.get( "data" );
                fotoComprovante.setImageBitmap( img );
            }

        /*InputStream stream = null;
        if (requestCode == 0 && resultCode == RESULT_OK) {

            try {
                if (bitmap != null){
                    bitmap.recycle();
                }
                stream = getContentResolver().openInputStream( data.getData() );
                bitmap = BitmapFactory.decodeStream(stream);
                fotoComprovante.setImageBitmap( resizeImage(this, bitmap,700,600) );
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }finally {
                if (stream != null)
                    try {
                        stream.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
            }*/
        if (requestCode == 0 && resultCode == RESULT_OK){

            imageBitmap = (Bitmap) data.getExtras().get( "data" );
            
            try {
                SimpleDateFormat formatoData = new SimpleDateFormat( "dd-MM-yyy_hh-MM-ss" );
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imageBitmap.compress( Bitmap.CompressFormat.PNG, 100, stream );

                byte[] imageBytes = stream.toByteArray();

                String nomeArquivoComprovante = formatoData.format( new Date() ) + ".png";
                //nomeArquivo.setText( nomeArquivoComprovante );
                fotoComprovante.setImageBitmap( imageBitmap );
                Toast.makeText( getApplicationContext(), "Imagem Capturada com Sucesso", Toast.LENGTH_LONG ).show();

                FileOutputStream fos = new FileOutputStream( nomeArquivoComprovante );
                fos.write( imageBytes );
                fos.flush();
                fos.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (requestCode == RESULT_CANCELED) {
            Toast.makeText( getBaseContext(), "A captura foi cancelada!!!", Toast.LENGTH_SHORT ).show();
        }

    }
    private void identificadorDespesa(){
        SimpleDateFormat formatoData = new SimpleDateFormat( "yyy-MM-dd_hh-MM-ss" );
        String codigodespesa = formatoData.format( new Date(  ) );
        idDespesa.setText( codigodespesa );

    }
    private static Bitmap resizeImage (Context context, Bitmap bmpOriginal, float newWidth, float newHeith){
        Bitmap novoBmp = null;
        int w = bmpOriginal.getWidth();
        int h = bmpOriginal.getHeight();

        float densitFactory = context.getResources().getDisplayMetrics().density;
        float novoW = newWidth * densitFactory;
        float novoH = newHeith * densitFactory;

        //captura a escala em percentual do tamanho orifinal para o novo tamanho
        float scalaW = novoW / w;
        float scalaH = novoH / h;

        //criando uma nova matrix para a manipulação da imagem Bitmap
        Matrix matrix = new Matrix(  );

        //Definindo a proporção da scala para a matrix
        matrix.postScale( scalaW, scalaH );

        //ciando um novo Bitmap com novo tamnho
        novoBmp = Bitmap.createBitmap( bmpOriginal,0,0,w,h,matrix,true );

        return novoBmp;
    }

    private boolean addBancoDados() {
        String identificador = idDespesa.getText().toString();
        try {
            databaseReference = ConfigiracaoFirebase.getFirebase().child( "prestacao" );
            PrestarContas prestarContas = new PrestarContas();
            //String uId = UUID.randomUUID().toString();
            prestarContas.setDataDespesa( dataDespesa.getText().toString() );
            prestarContas.setClassificar( classificar.getSelectedItem().toString() );
            prestarContas.setTipoDespesa( tipoDespesa.getSelectedItem().toString() );
            prestarContas.setCliente( cliente.getSelectedItem().toString() );
            prestarContas.setFilialLoja( filialLoja.getText().toString() );
            prestarContas.setNumeroChamado( numeroChamado.getText().toString() );
            prestarContas.setPlacaCarro( placaCarro.getText().toString() );
            prestarContas.setDescricao( descricao.getText().toString() );
            prestarContas.setTipoComprovante( tipoComprovante.getSelectedItem().toString() );
            prestarContas.setValor( valor.getText().toString() );
            prestarContas.setNumeroComprovante( numeroComprovante.getText().toString() );
            prestarContas.setCpf( cpf.getText().toString() );
            prestarContas.setCnpj( cnpj.getText().toString() );
            prestarContas.setFormaPagamento( formaPagamento.getSelectedItem().toString() );
            databaseReference.child( usuario ).child( identificador ).setValue( prestarContas );
            Toast.makeText( PrestarContasActivity.this, "Enviado com sucesso!!!", Toast.LENGTH_LONG ).show();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText( PrestarContasActivity.this, "Erro no envio, contate Suporte Técnico!!!", Toast.LENGTH_LONG ).show();
            return false;
        }
    }

    private boolean addImagem() {
        String identificador = idDespesa.getText().toString();
        if (nomeArquivo != null) {

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference imgageRef = storageRef.child( "images" ).child( usuario ).child( identificador );
            fotoComprovante.setDrawingCacheEnabled(true);
            fotoComprovante.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) fotoComprovante.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = imgageRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                }
            });

        } else {
            Toast.makeText( PrestarContasActivity.this, "Imagem não capturada!!!", Toast.LENGTH_LONG ).show();
        }
        return true;
    }


}


