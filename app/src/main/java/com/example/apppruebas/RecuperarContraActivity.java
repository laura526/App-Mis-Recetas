package com.example.apppruebas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RecuperarContraActivity extends AppCompatActivity {

    EditText txtCorreo;
    Button btnCambiarContra, btnRegresar;
    String correo;
    FirebaseAuth aut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contra);

        //ESTÉTICA
        colores();
        setTitle("Mis Recetas");

        //INSTANCIA A FIREBASE
        aut = FirebaseAuth.getInstance();

        //BOTONES OBTENIDOS Y OTROS
        btnCambiarContra = findViewById(R.id.btnCambiarContra);
        btnRegresar = findViewById(R.id.btnRegresarLogin);
        txtCorreo = findViewById(R.id.txtVerificarCorreo);

        //CLICKS EN BOTONES
        btnCambiarContra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                correo = txtCorreo.getText().toString().trim();
                if(correo.isEmpty()){
                    Toast.makeText(RecuperarContraActivity.this, "Debe ingresar el correo", Toast.LENGTH_SHORT).show();
                }else if (!correoCorrecto(correo)){
                    Toast.makeText(RecuperarContraActivity.this, "Correo inválido", Toast.LENGTH_LONG).show();
                }else{
                    //CAMBIANDO CONTRASEÑA
                    cambiarContra(correo);
                }
            }
        });

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent irLogin = new Intent(RecuperarContraActivity.this, LoginActivity.class);
                startActivity(irLogin);
            }
        });
    }

    //FUNCIONES-----------------------------------

    //ENVIANDO CORREO PARA CAMBIAR CONTRASEÑA
    public void cambiarContra(String correo) {
        aut.sendPasswordResetEmail(correo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(RecuperarContraActivity.this, "Correo para reestablecer la contraseña enviado", Toast.LENGTH_SHORT).show();
                    Intent volver = new Intent(RecuperarContraActivity.this, LoginActivity.class);
                    startActivity(volver);
                }else{
                    Toast.makeText(RecuperarContraActivity.this, "Correo para reestablecer la contraseña inválido", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //VERIFICANDO FORMATO CORREO
    private boolean correoCorrecto(String correo){
        return correo.contains("@");
    }

    //COLORES ACTIVITY
    private void colores(){
        String lightColor = "#FF0040";
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red))); // Barra de Arriba
        getWindow().setStatusBarColor(Color.parseColor(lightColor)); // donde se ve la bater[ia y hora
    }
}