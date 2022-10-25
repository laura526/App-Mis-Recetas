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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin,btnIrRegistro,btnOlvidoContra;
    EditText txtCorreo, txtContrasena;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //ESTÉTICA
        setTitle("Mis Recetas");
        colores();

        //INSTANCIA A FIREBASE
        auth = FirebaseAuth.getInstance();

        //BOTONES OBTENIDOS Y OTROS
        btnLogin = findViewById(R.id.btnLogin);
        btnIrRegistro = findViewById(R.id.btnIrRegistro);
        btnOlvidoContra = findViewById(R.id.btnOlvidoContra);
        txtCorreo = findViewById(R.id.txtCorreoLogin);
        txtContrasena = findViewById(R.id.txtContrasenaLogin);

        //CLICKS EN BOTONES
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String correo = txtCorreo.getText().toString().trim();
                String contrasena = txtContrasena.getText().toString().trim();

                if(correo.isEmpty() || contrasena.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Completar todos los campos", Toast.LENGTH_SHORT).show();
                }else{
                    iniciarSesion(correo, contrasena);
                }
            }
        });
        btnIrRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent irRegistro = new Intent(LoginActivity.this, RegistrarseActivity.class);
                startActivity(irRegistro);
            }
        });
        btnOlvidoContra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent irRecuperarContra = new Intent(LoginActivity.this, RecuperarContraActivity.class);
                startActivity(irRecuperarContra);
            }
        });
    }
    //FUNCIONES-----------------------------------

    //INICIANDO SESIÓN
    private void iniciarSesion(String correo, String contrasena) {
        auth.signInWithEmailAndPassword(correo,contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    Toast.makeText(LoginActivity.this, "Bienvenido a Mis Recetas", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(LoginActivity.this, "Error, correo o contraseña inválidos", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Error, datos inválidos", Toast.LENGTH_SHORT).show();
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