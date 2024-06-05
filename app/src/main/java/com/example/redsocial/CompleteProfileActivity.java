package com.example.redsocial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompleteProfileActivity extends AppCompatActivity {

    TextInputEditText mTextInputUsuario;

    Button mButtonRegister;
    FirebaseAuth mAuth;

    FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);


        mTextInputUsuario =findViewById(R.id.txtNombreuser);
        mButtonRegister =findViewById(R.id.btnresgistrar);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });


    }

    private void register(){
        String usuario = mTextInputUsuario.getText().toString();

        if (!usuario.isEmpty()) {
            updateUser(usuario);


        }
        else {
            Toast.makeText(this, "Para continuar inserta todos los campos", Toast.LENGTH_SHORT).show();
        }

    }

    private void updateUser( final String usuario) {
        String id = mAuth.getCurrentUser().getUid();
        Map<String, Object> map = new HashMap<>();

        map.put("username", usuario);

        mFirestore.collection("User").document(id).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                   Intent intent = new Intent(CompleteProfileActivity.this, HomeActivity.class);
                   startActivity(intent);
                }else {
                    Toast.makeText(CompleteProfileActivity.this, "No pudo se almaceno correctamente en la base de datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void Regresarlogin() {
        Intent abrirVentana = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(abrirVentana);
    }


}

