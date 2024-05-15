package com.example.redsocial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.example.redsocial.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegistrarUsuario extends AppCompatActivity {

    TextInputEditText mTextInputUsuario;
    TextInputEditText mTextInputemail;
    TextInputEditText mTextInputpassword;
    TextInputEditText mTextInputConfirmarpassword;
    Button mButtonRegister;
    FirebaseAuth mAuth;
    FloatingActionButton btnRegresar;
    FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);

        mTextInputemail =findViewById(R.id.txtcorreoregis);
        mTextInputUsuario =findViewById(R.id.txtNombreuser);
        mTextInputpassword =findViewById(R.id.txtcontraseñaregis);
        mTextInputConfirmarpassword =findViewById(R.id.txtconfirmarContraseña);
        mButtonRegister =findViewById(R.id.btnresgistrar);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        btnRegresar = findViewById(R.id.Regresarlogin);

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Regresarlogin();
            }
        });
    }

    private void register(){
        String usuario = mTextInputUsuario.getText().toString();
        String email = mTextInputemail.getText().toString();
        String contraseña = mTextInputpassword.getText().toString();
        String confirmarcontraseña = mTextInputConfirmarpassword.getText().toString();

        if (!usuario.isEmpty() && !email.isEmpty() && !contraseña.isEmpty() && !confirmarcontraseña.isEmpty()) {
            if (isEmailValid(email)) {
                if (contraseña.equals(confirmarcontraseña)) {
                    if (contraseña.length() >= 6) {
                        createUser(usuario, email,contraseña);
                    }
                    else {
                        Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(this, "Las contraseña no coinciden", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(this, "Insertaste todos los campos pero el correo no es valido", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(this, "Para continuar inserta todos los campos", Toast.LENGTH_SHORT).show();
        }

    }

    private void createUser( final String usuario, final String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String id = mAuth.getCurrentUser().getUid();
                    Map<String, Object> map = new HashMap<>();
                    map.put("email", email);
                    map.put("username", usuario);
                    map.put("password", password);
                mFirestore.collection("User").document(id).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RegistrarUsuario.this, "El usuario se almaceno correctamente en la base de datos", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(RegistrarUsuario.this, "No pudo se almaceno correctamente en la base de datos", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                } else {
                    Toast.makeText(RegistrarUsuario.this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
        private void Regresarlogin() {
            Intent abrirVentana = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(abrirVentana);
        }

    public boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();

    }
}

