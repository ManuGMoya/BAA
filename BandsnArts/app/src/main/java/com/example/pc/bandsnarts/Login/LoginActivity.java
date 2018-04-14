package com.example.pc.bandsnarts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private TextView titulo;
    private Typeface fuenteTitulo;
    private Activity ventanaPrincipal;
    private EditText edtUser, edtPass;
    private Button btnCancelarAlerta, btnAceptarAlerta;//, btnReg;
    private CheckBox grupo, musico;
    private AlertDialog.Builder alertaBuilder;
    private AlertDialog alerta;
    private LayoutInflater inflador;
    //Objeto para conectar con la api de facebook
    public static LoginResult loginResult;

    public static final int CODIGO_DE_INICIO = 777;

    // Objeto para conectar con la API del Cliente Google
    private GoogleApiClient clienteGoogle;

    // Objeto FirebaseAuth y su escuchador
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener escuchador;

    private Autentificacion auth;

    private LoginButton botonFaceBook;
    // Objeto de clase CallbackManager para detectar acciones en el boton FaceBook
    private CallbackManager callbackManager;

    private Activity estaVentana;
    private GoogleSignInResult result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        titulo = findViewById(R.id.tituloVLogin);
        //asignar nueva fuente
        fuenteTitulo = Typeface.createFromAsset(getAssets(), "fonts/VtksSimplizinha.ttf");
        titulo.setTypeface(fuenteTitulo);
        ventanaPrincipal = this;
        //btnReg = findViewById(R.id.btnRegistrarVLogin);
        edtUser = findViewById(R.id.edtUsuarioVLogin);
        edtPass = findViewById(R.id.edtPassVLogin);

        //Guardamos el objeto para no tener que hacer nuevas instancias.
        auth = new Autentificacion(this);

        estaVentana = this;

        //Opciones de inicio con google, obtenemos un token de usuario
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //Configuramos el cliente google, pasandole las opciones de inicio
        clienteGoogle = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Inicializamos el FireBaseAuth y su escuchador
        firebaseAuth = FirebaseAuth.getInstance();
        escuchador = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Este metodo se ejecuta cuando cambia el estado de la autenticacion
                // Verificamos si estamos autenticados en Firebase
                FirebaseUser usuario = firebaseAuth.getCurrentUser();

                if (usuario != null) {

                    Toast.makeText(LoginActivity.this, "Usuario Verificado", Toast.LENGTH_SHORT).show();
                    if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                        siguienteActivity();
                    }

                }
            }
        };


        // Comprobación de sesion iniciada en FaceBook
        if (AccessToken.getCurrentAccessToken() != null) {
            // Lanzamos la siguiente actividad
            siguienteActivity();
        }

        // Inicializamos CallbackManager
        callbackManager = CallbackManager.Factory.create();
        // Recogemos el Boton
        botonFaceBook = findViewById(R.id.btnFacebookVLogin);
        // Establecemos permisos para leer el correo electronico del usuario
        botonFaceBook.setReadPermissions(Arrays.asList("email"));
        botonFaceBook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                LoginActivity.loginResult = loginResult;
                // Cuando el login con Facebook sea exitoso, podemos acceder a los datos del usuario
                // Le pasamos al metodo el Token del usuario a traves del loginResult
                startActivityForResult(new Intent(LoginActivity.this, RegistarRedSocial.class), 111);
                //Apartamos este metodo de aqui ya que sino autentica directamente sin pasar por la actividad de registrar los datos
                // manejadorTokenFacebook(loginResult.getAccessToken());
                Toast.makeText(estaVentana, "ACCESO CON FACEBOOK CORRECTO", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancel() {
                // Cuando se cancele el inicio de sesion.
                Toast.makeText(estaVentana, "Inicio Cancelado.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                // Algun error como conexion u otros.
                Toast.makeText(estaVentana, "Ocurrió algun error al iniciar sesión.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void manejadorTokenFacebook(AccessToken accessToken, final Intent data) {
        //Creamos una credencial en base al Token recibido por parametro
        AuthCredential credencial = FacebookAuthProvider.getCredential(accessToken.getToken());
        // Autenticamos al usuario el Firebase con esa credencial obtenida
        firebaseAuth.signInWithCredential(credencial).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(estaVentana, "Error de login en Firebase con FaceBook", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("AUTENTICADO", "onComplete: Autenticado con facebook");
                    //siempre debemos guardar en la bd despues de autenticar pls
                    //  guardarBD(data);
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        // Aqui escucharemos los cambios de estado de la autenticacion
        firebaseAuth.addAuthStateListener(escuchador);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // En este metodo paramos el escuchador
        if (escuchador != null) {
            firebaseAuth.removeAuthStateListener(escuchador);
        }
    }

    public void onClickIngresarVLogin(View view) {
        if (edtPass.getText().toString().isEmpty() || edtUser.getText().toString().isEmpty()) {
            Toast.makeText(this, "DEBE INSERTAR AMBOS DATOS", Toast.LENGTH_SHORT).show();
        } else {
            auth.loginMailPass(this,edtUser.getText().toString(), edtPass.getText().toString());

        }

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // SI ALGO SALE MAL EN LA CONEXION...INFORMAR AL USUARIO
    }

    public void onClickIngresoGoogle(View view) {
        Intent g = Auth.GoogleSignInApi.getSignInIntent(clienteGoogle);
        startActivityForResult(g, CODIGO_DE_INICIO);
    }

    //!!!!!!!!!!!!!!!!!!!!!!!!!ON ACTIVITY FOR RESULT!!!!!!!!!!!!!!!!!
    /*
    !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!Aqui !!!!!!!!!!!!!!!!
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODIGO_DE_INICIO) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            compruebaResultado(result);
        }
        //Este if es para saber que ha rellenado todo lo necesario en el logueo
        if (requestCode == 000) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "He vuelto 000", Toast.LENGTH_SHORT).show();
            // Llamada al metodo para autenticar al usuario en Firebase y le mandamos la cuenta
            // autenticarEnFirebase(result.getSignInAccount(),data);
        }

        if (requestCode == 111) {
            Log.d("PRUEBA CON FACEBOOK", "onActivityResult: " + loginResult);
            manejadorTokenFacebook(loginResult.getAccessToken(), data);

            // Para reconocer las acciones del boton de Inicio de FaceBook
            try {
                callbackManager.onActivityResult(requestCode, resultCode, data);

            } catch (NullPointerException e) {
                // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            }
        }
    }

    private void compruebaResultado(GoogleSignInResult result) {
        this.result = result;
        if (result.isSuccess()) {
            autenticarEnFirebase(result.getSignInAccount(), this);
            //siguienteActivity();

        } else {
            Toast.makeText(this, "ERROR AL LOGAR", Toast.LENGTH_SHORT).show();
        }
    }

    private void autenticarEnFirebase(GoogleSignInAccount signInAccount, final Context context) {
        // Creamos una credencial y guardamos en ella el Token obtenido del objeto cuenta, el segundo
        // parametro es es access Token que no es necesario, le pasamos null
        AuthCredential credencial = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);

        // Autenticamos con firebase y agragamos un escuchador que nos dirá cuando termina
        firebaseAuth.signInWithCredential(credencial).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "No se pudo autenticar con Firebase", Toast.LENGTH_SHORT).show();
                } else {
                    new BDBAA().comprobarUID(context, FirebaseAuth.getInstance().getCurrentUser().getUid());
                    //startActivityForResult(new Intent(context, RegistarRedSocial.class), 000);
                    //siempre debemos guardar en la bd despues de autenticar pls
                    //  guardarBD(data);
                }
            }
        });
    }

    private void siguienteActivity() {
        Intent i = new Intent(this, VentanaInicialApp.class);
        startActivity(i);
    }

    public void onClickRegistrarVLogin(View view) {
        //sacar alert dialog para grupo o musico
        alertaBuilder = new AlertDialog.Builder(this);
        //si no, da error
        inflador = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View vista = inflador.inflate(R.layout.alertdialoggrupomusico, (ViewGroup) findViewById(R.id.alertaregistro));
        //hago aqui el find porque necesita la vista///////
        btnCancelarAlerta = vista.findViewById(R.id.btnCancelarVAlert);
        btnAceptarAlerta = vista.findViewById(R.id.btnAceptarVAlert);
        grupo = vista.findViewById(R.id.chkGrupoVAlert);
        musico = vista.findViewById(R.id.chkMusicoVAlert);
        ////////////////////////////////////////////////
        alerta = alertaBuilder.create();
        alerta.setView(vista);
        //para no poder usar el onbackpressed
        alerta.setCancelable(false);
        /*btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //muestra el alert*/
        alerta.show();
         /*   }
        });*/
        btnCancelarAlerta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cierra el alert
                alerta.cancel();
            }
        });
        //al pulsar aceptar se abrira una u otra ventana
        btnAceptarAlerta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenceManager.getDefaultSharedPreferences(estaVentana).edit().putInt("intentos", 0).commit();
                if (grupo.isChecked()) {
                    startActivityForResult(new Intent(ventanaPrincipal, RegistrarGrupo.class),000);
                    alerta.cancel();
                } else if (musico.isChecked()) {
                    startActivityForResult(new Intent(ventanaPrincipal, RegistarMusico.class),000);
                    alerta.cancel();
                } else {
                    Toast.makeText(LoginActivity.this, "POR FAVOR, ELIJA ALGUNA OPCIÓN PARA CONTINUAR EL REGISTRO", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //control de los checkbox
        musico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                grupo.setChecked(false);
            }
        });

        grupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musico.setChecked(false);
            }
        });
    }

    public void onclick(View view) {
        startActivity(new Intent(this, VentanaInicialApp.class));
    }
}