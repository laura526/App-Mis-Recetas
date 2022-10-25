package com.example.apppruebas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.apppruebas.adapter.RecetaAdapter;
import com.example.apppruebas.model.Receta;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class MainActivity extends AppCompatActivity {

    Button btnCrear, btnSalir;
    SearchView btnBuscar;
    RecyclerView recycler;
    RecetaAdapter adapter;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    Query query;
    String idUsuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ESTÉTICA
        setTitle("Inicio");
        colores();

        //INSTANCIAS FIRESTORE Y FIREBASE
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        recycler = findViewById(R.id.rcyViewListaRecetas);
        recycler.setLayoutManager(new LinearLayoutManager(this)); //conexiónn entre nuestro recyclerView y el la view receta individual
        query = firestore.collection("recetas");

        //OBTENIENDO DATOS DE LA CONSULTA
        FirestoreRecyclerOptions<Receta> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Receta>().setQuery(query, Receta.class).build();
        adapter = new RecetaAdapter(firestoreRecyclerOptions, this); //Referenciando los datos dentro del adaptador
        adapter.notifyDataSetChanged(); //lee los cambios que suceden
        recycler.setAdapter(adapter); //y esos cambios se los envían al recycler

        //BOTONES Y ID OBTENIDOS
        idUsuario = getIntent().getStringExtra("idUsuario");
        btnCrear = findViewById(R.id.btnIrCrearReceta);
        btnSalir = findViewById(R.id.btnSalir);
        btnBuscar = findViewById(R.id.btnBuscar);

        //CLICKS EN BOTONES
        btnCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, idUsuario, Toast.LENGTH_SHORT).show();
                Intent Ircrear = new Intent(MainActivity.this, CrearRecetaActivity.class);
                startActivity(Ircrear);
            }
        });
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(Html.fromHtml("<font color='#FF000000'>Confirmar</font>"));
                builder.setMessage(Html.fromHtml("<font color='#FF000000'>Desea cerrar sesión?</font>"))
                        .setPositiveButton(Html.fromHtml("<font color='#FF1744'>Aceptar</font>"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "Cerrando la Sesión", Toast.LENGTH_SHORT).show();
                                auth.signOut(); // Cerrando sesión
                                //Enviandolo al login
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            }
                        }).setNegativeButton(Html.fromHtml("<font color='#FF1744'>Cancelar</font>"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.show();
            }
        });

        //INVOCANDO FUNCION QUE BUSCA UNA RECETA
        buscarReceta();
    }

    //FUNCIONES-----------------------------------

    //BUSCANDO RECETA-------------------------------------
    private void buscarReceta() {
        btnBuscar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String texto) {
                buscarTexto(texto);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String texto) {
                buscarTexto(texto);
                return false;
            }
        });
    }

    //BUSCANDO Y ORDENANDO RECETAS POR TITULO
    private void buscarTexto(String texto) {
        FirestoreRecyclerOptions<Receta>firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Receta>().setQuery(query.orderBy("titulo").startAt(texto).endAt(texto+"~"),Receta.class).build();
        adapter = new RecetaAdapter(firestoreRecyclerOptions, this);
        adapter.startListening();
        recycler.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
    //-------------------------------------

    //COLORES ACTIVITY
    private void colores(){
        String lightColor = "#FF0040";
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red))); // Barra de Arriba
        getWindow().setStatusBarColor(Color.parseColor(lightColor)); // donde se ve la bater[ia y hora
    }
}