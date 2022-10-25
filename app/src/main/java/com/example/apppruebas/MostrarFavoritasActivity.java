package com.example.apppruebas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppruebas.adapter.RecetaFavoritaAdapter;
import com.example.apppruebas.model.RecetaFavorita;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MostrarFavoritasActivity extends AppCompatActivity {
    RecyclerView fRecycler;
    RecetaFavoritaAdapter fAdapter;
    FirebaseAuth fireAuth;
    FirebaseFirestore firestore;
    ImageButton btnRegresar;
    String idUsuarioLogeado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_favoritas);

        //ESTÉTICA
        colores();
        setTitle("Mis Recetas");

        //BOTONES OBTENIDOS Y OTROS
        btnRegresar = (ImageButton) findViewById(R.id.btnFRegresar);

        //------------------------------

        //INSTANCIAS FIRESTORE Y FIREBASE
        fireAuth = FirebaseAuth.getInstance();
        idUsuarioLogeado = fireAuth.getUid();  //OBTENIENDO ID DE USUARIO LOGUEADO

        firestore = FirebaseFirestore.getInstance(); //INSTANCIANDO BD
        fRecycler = findViewById(R.id.rcyViewListaRecetasFavoritas);
        fRecycler.setLayoutManager(new LinearLayoutManager(this));    //CONEXIÓN ENTRE EL RECYCLER VIEW ( que está en mostrarFavoritasActivity)
                                                                            // Y LA VIEW RECETA_INDIVIDUAL

        //CONSULTA A LA BD -> = Select * from recetasFavoritas where idUsuario = idUsuarioLogeado
        Query query = firestore.collection("recetasFavoritas").whereEqualTo("idUsuario", idUsuarioLogeado);

        //OBTENIENDO DATOS DE CONSULTA
        FirestoreRecyclerOptions<RecetaFavorita> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<RecetaFavorita>().setQuery(query, RecetaFavorita.class).build();

        //REFERENCIANDO DATOS DENTRO DE ADAPTADOR
        fAdapter = new RecetaFavoritaAdapter(firestoreRecyclerOptions, this); //INSTANCIANDO ADAPTADOR
        fAdapter.notifyDataSetChanged(); // LEE LOS CAMBIOS QUE SUCEDEN
        fRecycler.setAdapter(fAdapter); //  LOS CAMBIOS SON ENVIADOS AL RECYCLER

        //------------------------------

        //CLICKS EN BOTONES
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MostrarFavoritasActivity.this, MainActivity.class));
            }
        });
    }

    //FUNCIONES-----------------------------------

    @Override
    protected void onStart() {
        super.onStart();
        fAdapter.startListening(); //cuando se inicie la app que inicie a escuchar
    }

    @Override
    protected void onStop() {
        super.onStop();
        fAdapter.stopListening(); //cuando se cierre la app que deje de escuchar
    }

    //COLORES ACTIVITY
    private void colores(){
        String lightColor = "#FF0040";
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red))); // Barra de Arriba
        getWindow().setStatusBarColor(Color.parseColor(lightColor)); // donde se ve la bater[ia y hora
    }
}