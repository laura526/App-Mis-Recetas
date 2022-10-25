package com.example.apppruebas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class CrearRecetaActivity extends AppCompatActivity {

    Button btnAgregar, btnEscoger;
    EditText edtTitulo, edtIngredientes, edtPreparacion;
    ImageButton btnRegresar;
    ImageView imagenReceta;
    private FirebaseFirestore miFireStore;
    FirebaseAuth fireAuth;

    StorageReference storageReference; // Referencia del Storage de Firebase
    String storagePath = "recetas/*"; // carpeta en Firebase storage donde se guardan las imagenes

    private static final int COD_SEL_IMAGE = 300; //Codigo de seleccion de imagenes

    private Uri imagenUrl; // esta variable tiene la url de la imagen
    String imagen = "imagen";
    String id; //id actual de la receta

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_receta);

        //ESTÉTICA
        colores();
        setTitle("Mis Recetas");

        //INSTANCIAS FIRESTORE Y FIREBASE
        miFireStore = FirebaseFirestore.getInstance(); //haciendo conexion con FireStore
        fireAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference(); //Instanciando el Storage

        //ID OBTENIDOS, BOTONES Y OTROS
        id = getIntent().getStringExtra("idReceta");// Aquí se pasa el id de la receta que selecciona el usuario
        edtTitulo = (EditText) findViewById(R.id.ediTxtTitulo);
        edtIngredientes = (EditText) findViewById(R.id.ediTxtIngredientes);
        edtPreparacion = (EditText) findViewById(R.id.editTxtPreparacion);
        btnAgregar = (Button) findViewById(R.id.btnAgregar);
        btnRegresar = (ImageButton) findViewById(R.id.btnFRegresar);
        imagenReceta = (ImageView) findViewById(R.id.imgViewFoto);
        btnEscoger = (Button) findViewById(R.id.btnEscogerImg);

        //CLICKS EN BOTONES
        btnEscoger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarImagen();
            }
        });

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CrearRecetaActivity.this,MainActivity.class);
                startActivity(i);
            }
        });

        //NUEVA RECETA
        if (id == null || id == ""){
            btnAgregar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String titulo = edtTitulo.getText().toString();
                    String ingredientes = edtIngredientes.getText().toString();
                    String preparacion = edtPreparacion.getText().toString();

                    if (titulo.isEmpty() || ingredientes.isEmpty() || preparacion.isEmpty()){
                        Toast.makeText(CrearRecetaActivity.this, "Debe ingresar todos los datos", Toast.LENGTH_LONG).show();
                    }else{
                        enviarDatos(titulo,ingredientes,preparacion);
                        Intent a = new Intent(CrearRecetaActivity.this,MainActivity.class);
                        startActivity(a);
                    }
                }
            });
        }else{
            //ACTUALIZAR RECETA
            btnAgregar.setText("Actualizar");
            obtenerReceta(id);
            btnAgregar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CrearRecetaActivity.this);
                    builder.setTitle(Html.fromHtml("<font color='#FF000000'>Confirmar</font>"));
                    builder.setMessage(Html.fromHtml("<font color='#FF000000'>Desea actualizar la receta?</font>"))
                            .setPositiveButton(Html.fromHtml("<font color='#FF1744'>Aceptar</font>"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String titulo = edtTitulo.getText().toString();
                                    String ingredientes = edtIngredientes.getText().toString();
                                    String preparacion = edtPreparacion.getText().toString();

                                    if (titulo.isEmpty() || ingredientes.isEmpty() || preparacion.isEmpty()){
                                        Toast.makeText(CrearRecetaActivity.this, "Debe ingresar todos los datos", Toast.LENGTH_LONG).show();
                                    }else{
                                        actualizarDatos(titulo,ingredientes,preparacion,id);
                                        Intent a = new Intent(CrearRecetaActivity.this,MainActivity.class);
                                        startActivity(a);
                                    }
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
        }
    }

    //FUNCIONES-----------------------------------

    //SELECCIONAR Y SUBIR IMAGEN
    private void seleccionarImagen() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, COD_SEL_IMAGE);
    }

    //OBTENIENDO DIRECCIÓN DE IMAGEN
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == COD_SEL_IMAGE){
                imagenUrl = data.getData();
                subirImagen(imagenUrl);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //SUBIENDO DIRECCION A FIRESTORE
    private void subirImagen(Uri imagenUrl) {
        String ruteStorageImage = storagePath + "" + imagen +""+ fireAuth.getUid() +"" + id;
        StorageReference reference = storageReference.child(ruteStorageImage);
        reference.putFile(imagenUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                if (uriTask.isSuccessful()){
                    uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String descargarUri = uri.toString();
                            HashMap<String,Object> map = new HashMap<>();
                            map.put("imagen",descargarUri); // se está agregando a la receta un campo "imagen" con la url de la imagen
                            miFireStore.collection("recetas").document(id).update(map);
                            obtenerReceta(id);
                            Toast.makeText(CrearRecetaActivity.this, "Imagen actualizada!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CrearRecetaActivity.this, "Error al cargar imagen", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //EDITANDO UNA RECETA
    private void actualizarDatos(String titulo, String ingredientes, String preparacion, String id) {
        Map<String,Object> map = new HashMap<>();
        map.put("titulo", titulo);
        map.put("ingredientes", ingredientes);
        map.put("preparacion", preparacion);

        miFireStore.collection("recetas").document(id).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(CrearRecetaActivity.this, "Receta actualizada exitosamente", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CrearRecetaActivity.this, "Error al actualizar la receta", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //CRENADO UNA NUEVA RECETA
    private void enviarDatos(String titulo, String ingredientes, String preparacion) {
        Map<String,Object> map = new HashMap<>();
        map.put("titulo", titulo);
        map.put("ingredientes", ingredientes);
        map.put("preparacion", preparacion);

        miFireStore.collection("recetas").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(CrearRecetaActivity.this, "Receta creada éxitosamente", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CrearRecetaActivity.this, "Error al intentar crear la receta", Toast.LENGTH_LONG).show();
            }
        });
    }

    //MOSTRANDO LAS RECETAS
    private void obtenerReceta(String id){
        miFireStore.collection("recetas").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String titulo = documentSnapshot.getString("titulo");
                String ingredientes = documentSnapshot.getString("ingredientes");
                String preparacion = documentSnapshot.getString("preparacion");
                String imagen = documentSnapshot.getString("imagen");

                edtTitulo.setText(titulo);
                edtIngredientes.setText(ingredientes);
                edtPreparacion.setText(preparacion);
                try {
                    if (!imagen.equals("")){
                        Picasso.with(CrearRecetaActivity.this).load(imagen).resize(150,150).into(imagenReceta);
                    }
                } catch (Exception e){
                    Log.v("Error","e: " + e);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CrearRecetaActivity.this, "Error al obtener información", Toast.LENGTH_SHORT).show();
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