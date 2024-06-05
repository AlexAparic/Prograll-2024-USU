package com.example.redsocial;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BD extends SQLiteOpenHelper {
    private static final String DB_NAME = "db_usuarios";
    private static final int DB_VERSION = 2; // Incremented version
    private static final String TABLE_NAME = "usuarios";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_UUID = "uuid";
    private static final String COLUMN_USUARIO = "usuario";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_TELEFONO = "telefono";

    public BD(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_UUID + " TEXT,"
                + COLUMN_USUARIO + " TEXT,"
                + COLUMN_EMAIL + " TEXT,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_TELEFONO + " TEXT"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_TELEFONO + " TEXT");
        }
    }

    public boolean addUsuario(String usuario, String email, String password, String telefono) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_UUID, UUID.randomUUID().toString());
        values.put(COLUMN_USUARIO, usuario);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_TELEFONO, telefono);
        long result = db.insert(TABLE_NAME, null, values);
        Log.d("BD", "Usuario insertado en la base de datos local: " + usuario + ", " + email + ", " + password + ", " + telefono);
        db.close();
        return result != -1;
    }

    public List<Usuario> getAllUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int indexUuid = cursor.getColumnIndex(COLUMN_UUID);
                    int indexUsuario = cursor.getColumnIndex(COLUMN_USUARIO);
                    int indexEmail = cursor.getColumnIndex(COLUMN_EMAIL);
                    int indexPassword = cursor.getColumnIndex(COLUMN_PASSWORD);
                    int indexTelefono = cursor.getColumnIndex(COLUMN_TELEFONO);

                    if (indexUsuario != -1 && indexEmail != -1 && indexPassword != -1 && indexTelefono != -1) {
                        String uuid = cursor.getString(indexUuid);
                        String usuario = cursor.getString(indexUsuario);
                        String email = cursor.getString(indexEmail);
                        String password = cursor.getString(indexPassword);
                        String telefono = cursor.getString(indexTelefono);
                        usuarios.add(new Usuario(uuid, usuario, email, password, telefono));
                        Log.d("BD", "Usuario recuperado de la base de datos local: " + usuario + ", " + email + ", " + password + ", " + telefono);
                    } else {
                        Log.e("BD", "Error al obtener índices de columnas");
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            Log.e("BD", "Cursor nulo al ejecutar consulta SQL");
        }
        db.close();
        return usuarios;
    }
}
