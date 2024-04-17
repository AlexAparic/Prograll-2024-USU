package com.example.productosql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class BDProducto extends SQLiteOpenHelper {
    private static final String dbname = "db_alexia";
    private static final int v=2;
    private static final String SQldb = "CREATE TABLE PRODUCTO(id text, rev text, idProducto text, Codigo text, detalle text, Marca text, Precio text, Costo txt, Stock txt, Foto txt)";
    public BDProducto(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, dbname, factory, v);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQldb);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //para hacer la actualizacion de la BD
    }
    public String administrar_productos(String accion, String[] datos){
        try {
            SQLiteDatabase db = getWritableDatabase();
            if(accion.equals("nuevo")){
                db.execSQL("INSERT INTO PRODUCTO(id, rev, idProducto,Codigo, detalle, Marca, Precio, Costo, Stock, Foto ) VALUES('"+ datos[0] +"','"+ datos[1] +"','"+ datos[2] +"','"+ datos[3] +"','"+ datos[4] +"', '"+ datos[5] +"','"+ datos[6] +"', '"+ datos[7] +"', '"+ datos[8] +"' ,'"+ datos[9] +"')");
            } else if (accion.equals("modificar")) {
                db.execSQL("UPDATE PRODUCTO SET id='"+ datos[0] +"', rev='"+ datos[1] +"' ,Codigo='"+ datos[3] +"', detalle='"+ datos[4] +"', Marca='"+ datos[5] +"', Precio='"+ datos[6] +"', Costo='"+ datos[7] +"', Stock='"+ datos[8] +"' , Foto='"+ datos[9] +"' WHERE idProducto='"+ datos[2] +"'");
            } else if (accion.equals("eliminar")) {
                db.execSQL("DELETE FROM PRODUCTO WHERE idProducto='"+ datos[2] +"'");
            }
            return "ok";
        }catch (Exception e){
            return "Error: "+ e.getMessage();
        }
    }

    public Cursor obtener_producto(){
        Cursor cursor;
        SQLiteDatabase db = getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM PRODUCTO ORDER BY Codigo", null);
        return cursor;

    }
}