package com.example.pc.bandsnarts.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.pc.bandsnarts.BBDD.BDBAA;
import com.example.pc.bandsnarts.Container.BandsnArts;
import com.example.pc.bandsnarts.Fragment_Visitar_Perfil.Visitar_Anuncios;
import com.example.pc.bandsnarts.Fragment_Visitar_Perfil.Visitar_Perfil;
import com.example.pc.bandsnarts.R;
import com.google.firebase.auth.FirebaseAuth;

public class VisitarPerfilDeseado extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    String pos;
    public static FragmentManager fragment;
    public static Activity a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragment = getSupportFragmentManager();
        a = this;
        setContentView(R.layout.activity_actvity_visitar_perfil);
        final Activity activity = this;
        pos = getIntent().getStringExtra("pos");
        FloatingActionButton fbChat = findViewById(R.id.fabEnviarMensaje);
        fbChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("MCHAT");
                BDBAA.comprobarConversacionExistente(getIntent().getStringExtra("tipo"), FirebaseAuth.getInstance().getCurrentUser().getUid(), getIntent().getStringExtra("pos"), activity);
            }
        });
        BottomNavigationView navigation = findViewById(R.id.navVisitarPerfil);
        navigation.setOnNavigationItemSelectedListener(this);
        //carga de inicio visitar perfil
        switch (getIntent().getExtras().getInt("op")) {
            case 0:
                cargarFragment(new Visitar_Perfil(getIntent().getStringExtra("pos"), getIntent().getStringExtra("tipo")));
                getSupportActionBar().setTitle("Perfil");
                break;
            case 1:
                cargarFragment(new Visitar_Anuncios(getIntent().getStringExtra("pos"), getIntent().getStringExtra("tipo")));
                getSupportActionBar().setTitle("Anuncio");
                break;
            default:
                break;
        }
    }

    private boolean cargarFragment(Fragment fragment) {
        if (fragment != null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contenedor_visitar_perfil, fragment)
                    .commit();
            return true;
        }
        return false;
    }


    //Este metodo se llama al tocar en el menu y cambiar de fragment
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment frg = null;
        //saber que opcion esta seleccionada
        switch (item.getItemId()) {
            case R.id.itemperfilvisitado:
                frg = new Visitar_Perfil(getIntent().getStringExtra("pos"), getIntent().getStringExtra("tipo"));
                getSupportActionBar().setTitle("Perfil");
                break;
            case R.id.itemanunciosvisitado:
                frg = new Visitar_Anuncios(getIntent().getStringExtra("pos"), getIntent().getStringExtra("tipo"));
                getSupportActionBar().setTitle("Anuncio");
                BandsnArts.paraHilo = true;
                if (BandsnArts.mediaPlayer != null)
                    BandsnArts.mediaPlayer.stop();
                break;
        }

        return cargarFragment(frg);
    }

    @Override
    public void onBackPressed() {

        BandsnArts.paraHilo = true;
        if (BandsnArts.mediaPlayer != null)
            BandsnArts.mediaPlayer.stop();
        finish();

    }
}
