package com.example.apppruebas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class MostrarRecetaActivity extends AppCompatActivity {

    String idReceta,titulo,ingredientes,preparacion,imagen,idUsuario;
    TextView txtVTitulo;
    EditText edtIngredientes, edtPreparacion;
    ImageView imagenReceta;
    ImageButton btnRegresar, btnFavorita;
    private FirebaseFirestore mFireStore;
    FirebaseAuth fireAuth;
    Button btnVerFavoritas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_receta);

        //ESTÉTICA
        colores();
        setTitle("Mis Recetas");

        //INSTANCIAS FIRESTORE Y FIREBASE
        mFireStore = FirebaseFirestore.getInstance();
        fireAuth = FirebaseAuth.getInstance();


        //ID OBTENIDOS, BOTONES Y OTROS
        idReceta = getIntent().getStringExtra("idReceta"); //OBTENIENDO ID DE IMAGEN_RECETA SELECCIONADA POR USUARIO
        idUsuario = fireAuth.getUid(); //OBTENIENDO ID DE USARIO LOGUEADO PARA CREAR RECETA FAVORITA

        //BOTONES OBTENIDOS Y OTROS
        txtVTitulo = (TextView) findViewById(R.id.txtMTitulo);
        edtIngredientes = (EditText) findViewById(R.id.editTxtMIngredientes);
        edtPreparacion = (EditText) findViewById(R.id.editTxtMPreparacion);
        imagenReceta = (ImageView) findViewById(R.id.imgViewReceta);
        btnRegresar = (ImageButton) findViewById(R.id.btnFRegresar);
        btnFavorita= (ImageButton) findViewById(R.id.imgFavorita);
        btnVerFavoritas = (Button) findViewById(R.id.btnVerFavoritas);

        //MOSTRANDO TODAS LAS RECETAS CREADAS
        mostrarRecetas(idReceta);

        //CLICKS EN BOTONES
        btnVerFavoritas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent irFavoritas = new Intent(MostrarRecetaActivity.this, MostrarFavoritasActivity.class);
                startActivity(irFavoritas);
            }
        });
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent irInicio = new Intent(MostrarRecetaActivity.this, MainActivity.class);
                startActivity(irInicio);
            }
        });
        btnFavorita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarFavorita(idReceta,titulo,ingredientes,preparacion,imagen,idUsuario);
            }
        });
    }

    //FUNCIONES-----------------------------------

    //MOSTRANDO LAS RECETAS GUARDADAS
    private void mostrarRecetas(String idReceta) {
        mFireStore.collection("recetas").document(idReceta).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

               titulo = documentSnapshot.getString("titulo");
               ingredientes = documentSnapshot.getString("ingredientes");
               preparacion = documentSnapshot.getString("preparacion");
               imagen = documentSnapshot.getString("imagen");

               txtVTitulo.setText(titulo);
               edtIngredientes.setText(ingredientes);
               edtPreparacion.setText(preparacion);
                try {
                    if (!imagen.equals("")){
                        Picasso.with(MostrarRecetaActivity.this).load(imagen).resize(200,200).into(imagenReceta);
                    }
                } catch (Exception e){
                    Log.v("Error","e: " + e);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MostrarRecetaActivity.this, "Error al obtener información", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //AÑADIENDO UNA RECETA COMO FAVORITA SEGÚN EL USUARIO LOGUEADO
    private void crearFavorita(String idReceta,String titulo, String ingredientes, String preparacion, String imagen,String idUsuario) {
        Map<String,Object> map = new HashMap<>();
        map.put("idReceta", idReceta);
        map.put("titulo", titulo);
        map.put("ingredientes", ingredientes);
        map.put("preparacion", preparacion);
        map.put("imagen", imagen);
        map.put("idUsuario", idUsuario);

        mFireStore.collection("recetasFavoritas").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(MostrarRecetaActivity.this, "Receta añadida como Favorita", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MostrarRecetaActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    //VERIFICAR SI LA RECETA ESTÁ AÑADIDA COMO FAVORITA
    private void verificarFavorita(String idReceta,String titulo, String ingredientes, String preparacion, String imagen,String idUsuario){

        mFireStore.collection("recetasFavoritas").whereEqualTo("idReceta",idReceta).whereEqualTo("idUsuario", idUsuario).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int existe = task.getResult().size();
                if(existe == 0){
                    crearFavorita(idReceta,titulo,ingredientes,preparacion,imagen,idUsuario);
                } else{
                    Toast.makeText(MostrarRecetaActivity.this, "La receta ya fue agregada como favorita", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //COLORES ACTIVITY
    private void colores(){
        String lightColor = "#FF0040";
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red))); // Barra de Arriba
        getWindow().setStatusBarColor(Color.parseColor(lightColor)); // donde se ve la bater[ia y hora
    }
}