package com.example.pc.bandsnarts.Login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import com.example.pc.bandsnarts.Activities.VentanaInicialApp;
import com.example.pc.bandsnarts.BBDD.BDBAA;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class Autentificacion extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Activity vLog;

    public Autentificacion(Activity loginActivity) {
        vLog = loginActivity;
    }
    // Paso de void a Firebase user para devolver el usuario
    public void registroMailPass(String user, String password) {

        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(user.trim(), password.trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success");
                            // FirebaseUser user = mAuth.getCurrentUser();
                            // pruebas..
                            FirebaseUser usuario   = mAuth.getInstance().getCurrentUser();
                            // Name, email address, and profile photo Url
                            String name = usuario.getDisplayName();
                            String email = usuario.getEmail();
                            Uri photoUrl = usuario.getPhotoUrl();

                            // Check if user's email is verified
                            boolean emailVerified = usuario.isEmailVerified();
                            System.out.println("nombre: "+name+"\ncorreo: "+email+"\nURL de la foto: "+photoUrl+"\nemail verificado: "+emailVerified);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                        }
                    }
                });

    }


    public void loginMailPass(final Context cont, String user, String password) {

        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(user.trim(), password.trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "loginUserWithEmail:success ");
                        //    Toast.makeText(vLog, "Todo bien.", Toast.LENGTH_SHORT).show();
                            //  FirebaseUser user = mAuth.getCurrentUser();
                            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                if (!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                                    if (PreferenceManager.getDefaultSharedPreferences(cont).getInt("intentos", 1) != 3) {
                                        Toast.makeText(cont, "Debe verificar su correo antes de usar la app " + PreferenceManager.getDefaultSharedPreferences(cont).getInt("intentos", 0), Toast.LENGTH_SHORT).show();
                                        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
                                        int pos = PreferenceManager.getDefaultSharedPreferences(cont).getInt("intentos", 1) + 1;
                                        PreferenceManager.getDefaultSharedPreferences(cont).edit().putInt("intentos", pos).commit();
                                        FirebaseAuth.getInstance().signOut();
                                    } else {
                                        PreferenceManager.getDefaultSharedPreferences(cont).edit().putInt("intentos", 1);
                                         BDBAA.borrarPerfil(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                         BDBAA.eliminarNodo("musico",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                         BDBAA.eliminarNodo("grupo",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        Toast.makeText(cont, "Eliminado", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                Toast.makeText(cont, "Debe crear antes una cuenta", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            deslogueo();
                            Toast.makeText(vLog, "Credenciales no correctas", Toast.LENGTH_SHORT).show();
                            Log.w("TAG", "loginUserWithEmail:failure", task.getException());
                        }
                    }
                });

    }

    public void deslogueo() {
        mAuth = FirebaseAuth.getInstance();
        // PARA CONTROL DEL DESLOGUEO
        try {
            // COMPROBACION INTERNA DE DESLOGUEO
            Log.w("TAG", "" + mAuth.getCurrentUser().getUid());
        } catch (NullPointerException e) {
            Log.w("TAG", "USUARIO YA DESLOGUEADO");
        }
        mAuth.signOut();
        // Toast.makeText(this, ""+mAuth.getCurrentUser().getProviderId(), Toast.LENGTH_SHORT).show();
    }

    public boolean compruebaLogueado() {
        mAuth = FirebaseAuth.getInstance();
        if (mAuth != null) {
            deslogueo();
            return true;
        } else {
            return false;
        }
    }


    public boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }


    public boolean comprobarPass(String pass) {
        Pattern p = Pattern.compile("(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20}");
        return p.matcher(pass).matches();
    }


}
