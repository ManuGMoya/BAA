package com.example.pc.bandsnarts.FragmentsPerfil;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pc.bandsnarts.BBDD.BDBAA;
import com.example.pc.bandsnarts.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import static com.facebook.FacebookSdk.getApplicationContext;



public class FragmentVerMiPerfil extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_verperfil_v_fragment_perfil, container, false);

        new BDBAA().cargarDatosPerfil(vista,"musico",getApplicationContext());
        new BDBAA().cargarDatosPerfil(vista,"grupo",getApplicationContext());
        //BOTON FLOTANTE PARA EDITAR EL PERFIL
        FloatingActionButton miFAB=(FloatingActionButton)vista.findViewById(R.id.floatingBPerfil);
        miFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //IMPLEMENTAR LA FUNCIONALIDAD DEL BOTON
                Toast.makeText(getActivity(), "HAS PULSADO", Toast.LENGTH_SHORT).show();
            }
        });
        return vista;
    }




}