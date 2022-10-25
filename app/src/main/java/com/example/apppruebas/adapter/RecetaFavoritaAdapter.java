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

import com.example.apppruebas.MostrarRecetaActivity;
import com.example.apppruebas.R;
import com.example.apppruebas.model.RecetaFavorita;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class RecetaFavoritaAdapter extends FirestoreRecyclerAdapter<RecetaFavorita, RecetaFavoritaAdapter.ViewHolder> {

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    Activity activity;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public RecetaFavoritaAdapter(@NonNull FirestoreRecyclerOptions<RecetaFavorita> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int position, @NonNull RecetaFavorita RecetaFavorita) {

        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(viewHolder.getAdapterPosition());
        final String idRecetaPorEliminar = documentSnapshot.getId(); // OBTENIENDO ID DE FIREASE

        //OBTENIENDO ID DE RECETA FAVORITA PARA PODER MOSTRARLAS DESPUÉS
        String idRecetaFavorita = RecetaFavorita.getIdReceta();

        //MOSTRANDO TÍTULO E IMAGEN
        viewHolder.titulo.setText(RecetaFavorita.getTitulo());
        String imagenReceta = RecetaFavorita.getImagen();
        try {
            if (!imagenReceta.equals("")){
                Picasso.with(activity.getApplicationContext()).load(imagenReceta).resize(160,160).into(viewHolder.imagenReceta);
            }
        } catch (Exception e){
            Log.d("Exception", "e: "+e);
        }
        //--------------------------------

        //CLICKS EN BOTONES, IMÁGENES U OTROS
        viewHolder.imagenReceta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent irMostrarReceta = new Intent(activity, MostrarRecetaActivity.class);
                irMostrarReceta.putExtra("idReceta", idRecetaFavorita);
                activity.startActivity(irMostrarReceta);
            }
        });
        viewHolder.eliminarFavorita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(Html.fromHtml("<font color='#FF000000'>Confirmar</font>"));
                builder.setMessage(Html.fromHtml("<font color='#FF000000'>Desea eliminar la receta de Favoritas?</font>"))
                        .setPositiveButton(Html.fromHtml("<font color='#FF1744'>Aceptar</font>"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                eliminarRecetaFavorita(idRecetaPorEliminar);
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

    //FUNCIONES-----------------------------------

    private void eliminarRecetaFavorita(String idReceta) {
        firestore.collection("recetasFavoritas").document(idReceta).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(activity, "Receta favorita eliminada exitosamente", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, "Error al eliminar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_receta_favorita_individual, parent, false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo;
        ImageView imagenReceta, eliminarFavorita;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.txtTituloRecetaFavorita);
            imagenReceta = itemView.findViewById(R.id.imagenRecetaFavoritaCard);
            eliminarFavorita = itemView.findViewById(R.id.btnEliminarFavorita);
        }
    }
}