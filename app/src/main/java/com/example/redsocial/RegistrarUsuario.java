package com.example.redsocial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.redsocial.models.User;
import com.example.redsocial.providers.AuthProvider;
import com.example.redsocial.providers.UsersProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.example.redsocial.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class RegistrarUsuario extends AppCompatActivity {

    TextInputEditText mTextInputUsuario;
    TextInputEditText mTextInputemail;
    TextInputEditText mTextInputpassword;
    TextInputEditText mTextInputConfirmarpassword;
    TextInputEditText mTextInputPhone;
    Button mButtonRegister;
    Button BtnSinc;

    FloatingActionButton btnRegresar;

    AuthProvider mAuthProvider;
    UsersProvider mUsersProvider;

    detectarInternet di;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);

        mTextInputemail = findViewById(R.id.txtcorreoregis);
        mTextInputUsuario = findViewById(R.id.txtNombreuser);
        mTextInputpassword = findViewById(R.id.txtcontraseñaregis);
        mTextInputConfirmarpassword = findViewById(R.id.txtconfirmarContraseña);
        mTextInputPhone = findViewById(R.id.txtInputPhone);
        mButtonRegister = findViewById(R.id.btnresgistrar);



        mAuthProvider = new AuthProvider();
        mUsersProvider = new UsersProvider();

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

        BtnSinc = findViewById(R.id.btnSinc);
        BtnSinc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncDataToFirebase();
            }
        });
    }

    private void register() {
        String usuario = mTextInputUsuario.getText().toString();
        String email = mTextInputemail.getText().toString();
        String contraseña = mTextInputpassword.getText().toString();
        String confirmarcontraseña = mTextInputConfirmarpassword.getText().toString();
        String phone = mTextInputPhone.getText().toString();

        try {
            di = new detectarInternet(getApplicationContext());
            if (di.hayConexionInternet()) {
                if (!usuario.isEmpty() && !email.isEmpty() && !contraseña.isEmpty() && !confirmarcontraseña.isEmpty() && !phone.isEmpty()) {
                    if (isEmailValid(email)) {
                        if (contraseña.equals(confirmarcontraseña)) {
                            if (contraseña.length() >= 6) {
                                createUser(usuario, email, contraseña, phone);
                            } else {
                                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "El correo electrónico no es válido", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Para continuar, inserta todos los campos", Toast.LENGTH_SHORT).show();
                }
            } else {
                GuardarLocal(usuario, email, contraseña, phone);
            }
        } catch (Exception e) {
            mostrarMsg("error al detectar si hay conexion" + e.getMessage());

        }
    }

        private void createUser ( final String usuario, final String email, final String password,
        final String phone){
            mAuthProvider.register(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String UUID = mAuthProvider.getUid();
                        Map<String, Object> map = new HashMap<>();
                        map.put("email", email);
                        map.put("username", usuario);
                        map.put("password", password);
                        User user = new User();
                        user.setUuid(UUID);
                        user.setEmail(email);
                        user.setUsername(usuario);
                        user.setPhone(phone);
                        user.setTimestamp(new Date().getTime());

                        mUsersProvider.create(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(RegistrarUsuario.this, "El usuario se almacenó correctamente en la base de datos", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(RegistrarUsuario.this, "No se pudo almacenar correctamente en la base de datos", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(RegistrarUsuario.this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        private void Regresarlogin () {
            Intent abrirVentana = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(abrirVentana);
        }

        public boolean isEmailValid (String email){
            String expression = "^[\\w\\.-]+@gmail\\.com$";
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(email);
            return matcher.matches();
        }
    private void GuardarLocal (String usuario, String email, String password, String telefono){
        BD userDB = new BD(this);
        boolean insertado = userDB.addUsuario(usuario, email, password, telefono);
        if (insertado) {
            Toast.makeText(this, "Los datos se guardaron correctamente en la base local", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al guardar los datos en la base local", Toast.LENGTH_SHORT).show();
        }
    }

        private void syncDataToFirebase () {
            BD localDB = new BD(this);
            List<Usuario> usuarios = localDB.getAllUsuarios();

            for (Usuario usuario : usuarios) {
                if (usuario.getUuid() == null) {
                    syncUsuarioToFirebase(usuario);
                } else {
                    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
                    DocumentReference userRef = mFirestore.collection("Users").document(usuario.getUuid());
                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // Toast.makeText(RegistrarUsuario.this, "El usuario ya existe en Firebase", Toast.LENGTH_SHORT).show();
                                } else {
                                    syncUsuarioToFirebase(usuario);
                                }
                            } else {
                                Toast.makeText(RegistrarUsuario.this, "Error al verificar la existencia del usuario en Firebase", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }

        private void syncUsuarioToFirebase ( final Usuario usuario){
            FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

            DocumentReference userRef = mFirestore.collection("Users").document(usuario.getUuid());
            userRef.set(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegistrarUsuario.this, "Datos de usuario sincronizados con Firebase correctamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegistrarUsuario.this, "Error al sincronizar datos de usuario con Firebase", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    private void mostrarMsg(String msg){
        Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_LONG).show();
    }

}



