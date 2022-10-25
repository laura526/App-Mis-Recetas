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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class RegistrarseActivity extends AppCompatActivity {

    Button btnRegistrarse,btnIrLogin;
    EditText txtCorreo, txtContrasena, txtConfirmarContrasena;
    FirebaseFirestore firestore;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

        //ESTÉTICA
        colores();
        setTitle("Mis Recetas");

        //INSTANCIA A FIRESTORE Y FIREBASE
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        //BOTONES OBTENIDOS Y OTROS
        btnRegistrarse = findViewById(R.id.btnCambiarContra);
        btnIrLogin = findViewById(R.id.btnIrLogin);
        txtCorreo = findViewById(R.id.txtVerificarCorreo);
        txtContrasena = findViewById(R.id.txtVerificarContra);
        txtConfirmarContrasena = findViewById(R.id.txtNuevaContra);

        //CLICKS EN BOTONES
        btnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String correo = txtCorreo.getText().toString().trim();
                String contrasena = txtContrasena.getText().toString().trim();
                String confirmarContrasena = txtConfirmarContrasena.getText().toString().trim();

                if(correo.isEmpty() || contrasena.isEmpty() || confirmarContrasena.isEmpty()){
                    Toast.makeText(RegistrarseActivity.this, "Se deben llenar todos los campos!", Toast.LENGTH_SHORT).show();
                }else if (!correoCorrecto(correo)){
                    Toast.makeText(RegistrarseActivity.this, "Correo inválido", Toast.LENGTH_LONG).show();
                }else if (!contraCorrecta(contrasena)){
                    Toast.makeText(RegistrarseActivity.this, "Contraseña inválida", Toast.LENGTH_LONG).show();
                }else if(!contrasena.equals(confirmarContrasena)){
                    Toast.makeText(RegistrarseActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                }else{
                    //VERIFICANDO EXISTENCIA DE CUENTA
                    existeCuenta(correo, contrasena);
                }
            }
        });
        btnIrLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent irLogin = new Intent(RegistrarseActivity.this, LoginActivity.class);
                startActivity(irLogin);
            }
        });
    }

    //FUNCIONES-----------------------------------

    //REGISTRANDO USUARIO NUEVO
    private void registrarUsuario(String correo, String contrasena) {
        auth.createUserWithEmailAndPassword(correo,contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                String idUsuario = auth.getCurrentUser().getUid();
                Map<String, Object> map = new HashMap<>();
                map.put("idUsuario", idUsuario);
                map.put("correo", correo);
                map.put("contrasena", contrasena);

                firestore.collection("usuarios").document(idUsuario).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        startActivity(new Intent(RegistrarseActivity.this,LoginActivity.class));
                        Toast.makeText(RegistrarseActivity.this, "Usuario registrado con exito", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegistrarseActivity.this, "Error al guardar usuario", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegistrarseActivity.this, "Error al registrar usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //VERIFICA FORMATO DEL CORREO
    private boolean correoCorrecto(String correo){
        return correo.contains("@");
    }

    //VERIFICA TAMAÑO DE CONTRASEÑA
    private boolean contraCorrecta(String contra){
        return contra.length() > 5;
    }

    //VERIFICANDO SI USUARIO YA TIENE CUENTA VINCULADO CON CORREO
    private void existeCuenta(String correo, String contrasena){
        auth.fetchSignInMethodsForEmail(correo).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                int existe = task.getResult().getSignInMethods().size();
                if (existe == 0){
                    //SI NO EXISTE EL CORREO EN LA BS
                    registrarUsuario(correo, contrasena);
                }else {
                    //SI YA EXISTE
                    Toast.makeText(RegistrarseActivity.this, "Usuario ya registrado", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegistrarseActivity.this, "Error al intentar verificar correo", Toast.LENGTH_SHORT).show();
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