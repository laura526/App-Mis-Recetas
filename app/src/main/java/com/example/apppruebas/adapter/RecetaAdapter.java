package com.example.apppruebas.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppruebas.R;
import com.example.apppruebas.CrearRecetaActivity;
import com.example.apppruebas.model.Receta;
import com.example.apppruebas.MostrarRecetaActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Collections;

public class RecetaAdapter extends FirestoreRecyclerAdapter<Receta, RecetaAdapter.ViewHolder> {

    private FirebaseFirestore firestore;
    Activity activity;
    int existe;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public RecetaAdapter(@NonNull FirestoreRecyclerOptions<Receta> options, Activity activity) {

        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Receta receta) {

        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String idReceta = documentSnapshot.getId(); // obteniendo ID de FireBase
        firestore = FirebaseFirestore.getInstance();

        //MOSTRANDO TÍTULO E IMAGEN
        holder.titulo.setText(receta.getTitulo());
        String imagenReceta = receta.getImagen();
        try {
            if (!imagenReceta.equals("")){
                Picasso.with(activity.getApplicationContext()).load(imagenReceta).resize(150,150).into(holder.imagenReceta);
            }
        } catch (Exception e){
            Log.d("Exception", "e: "+e);
        }
        //--------------------------------

        //CLICKS EN BOTONES, IMÁGENES U OTROS
        holder.btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, CrearRecetaActivity.class);
                i.putExtra("idReceta", idReceta);
                activity.startActivity(i);
            }
        });
        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(Html.fromHtml("<font color='#FF000000'>Confirmar</font>"));
                builder.setMessage(Html.fromHtml("<font color='#FF000000'>Desea eliminar la receta?</font>"))
                .setPositiveButton(Html.fromHtml("<font color='#FF1744'>Aceptar</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eliminarReceta(idReceta);
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
        holder.imagenReceta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent irMostrarReceta = new Intent(activity, MostrarRecetaActivity.class);
                irMostrarReceta.putExtra("idReceta", idReceta);
                activity.startActivity(irMostrarReceta);
            }
        });
    }

    //FUNCIONES-----------------------------------

    //ELIMINANDO RECETA
    private void eliminarReceta(String idReceta) {

        //BORRANDO PRIMERO DE FAVORITAS
        borrarDeFavorita(idReceta);

        //BORRANDO LUEGO DE RECETAS
        firestore.collection("recetas").document(idReceta).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(activity, "Receta eliminada exitosamente", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, "Error al eliminar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ELIMINANDO RECETA DE FAVORITAS
    private void borrarDeFavorita(String idReceta){
        existe = 0;
        do {
            //PRIMERO SE VERIFICA SI ESTÁ AÑADIDA COMO FAVORITA
            firestore.collection("recetasFavoritas").whereEqualTo("idReceta", idReceta).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    //VERIFICANDO QUE ESTÉ YA AÑADIDA COMO FAVORITA PARA ELIMINARLA TAMBIÉN DE AHÍ SINO NO HACE NADA
                    existe = task.getResult().size();
                    if(existe > 0){
                        for (QueryDocumentSnapshot document: task.getResult()){
                            String id = document.getId();
                            firestore.collection("recetasFavoritas").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    existe = existe - 1;
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(activity, "Error" + e, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            });
        }while(existe>0);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_receta_individual,parent,false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo;
        ImageView btnEliminar, btnEditar, imagenReceta;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.txtTitulo);
            btnEliminar = itemView.findViewById(R.id.btnEliminarImg);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            imagenReceta = itemView.findViewById(R.id.imagenRecetaCard);
        }
    }
}
