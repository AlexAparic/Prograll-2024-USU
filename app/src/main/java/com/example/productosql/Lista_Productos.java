package com.example.productosql;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Lista_Productos extends AppCompatActivity {

    Bundle parametros = new Bundle();
    ListView lts;

    BDProducto dbProductos;
    Cursor cProductos;

    productos misProductos;

    final ArrayList<productos> alproductos=new ArrayList<productos>();
    final ArrayList<productos> alproductosCopy=new ArrayList<productos>();
    FloatingActionButton btn;

    JSONArray datosJSON;
    JSONObject jsonObject;
    obtenerDatosServidor datosServidor;
    detectarInternet di;

    int posicion=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_productos);


        dbProductos = new BDProducto(Lista_Productos.this, "" , null, 2);

        btn = findViewById(R.id.btnAbrirNuevosProductos);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parametros.putString("accion", "nuevo");
                abrirActividad(parametros);
            }
        });
        try {
            di = new detectarInternet(getApplicationContext());
            if (di.hayConexionInternet()){
                obtenerDatosProductosServidor();
            }else {
                obtenerProdctos();//offline

            }
        }catch (Exception e){
            mostrarMsg("error al detectar si hay conexion" + e.getMessage());
        }
        buscarProductos();
    }

    private void obtenerDatosProductosServidor(){
        try{
            datosServidor = new obtenerDatosServidor();
            String data = datosServidor.execute().get();
            jsonObject = new JSONObject(data);
            datosJSON = jsonObject.getJSONArray("rows");
            mostrarDatosProductos();
        }catch (Exception e){
            mostrarMsg("Error al obtener datos desde el servidor: "+ e.getMessage());
        }
    }


    private void mostrarDatosProductos(){
        try {
            if (datosJSON.length()>0 ){
                lts = findViewById(R.id.ltsProducto);

                alproductos.clear();
                alproductosCopy.clear();

                JSONObject misDatosJSONObject;
                for (int i=0; i<datosJSON.length(); i++) {
                    misDatosJSONObject = datosJSON.getJSONObject(i).getJSONObject("value");
                    misProductos = new productos(
                            misDatosJSONObject.getString("_id"),
                            misDatosJSONObject.getString("_rev"),
                            misDatosJSONObject.getString("idProducto"),
                            misDatosJSONObject.getString("Codigo"),
                            misDatosJSONObject.getString("detalle"),
                            misDatosJSONObject.getString("Marca"),
                            misDatosJSONObject.getString("Precio"),
                            misDatosJSONObject.getString("Costo"),
                            misDatosJSONObject.getString("Stock"),
                            misDatosJSONObject.getString("urlCompletaFoto")
                    );
                    alproductos.add(misProductos);

                }
                alproductosCopy.addAll(alproductos);

                adaptadorImagenes adImagenes = new adaptadorImagenes(getApplicationContext(), alproductos);
                lts.setAdapter(adImagenes);

                registerForContextMenu(lts);
            }else{
                mostrarMsg("No hay Datos que Mostrar");
            }

        }catch (Exception e){
            mostrarMsg("Error al mostrar los datos" +e.getMessage());
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);



        try {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.mimenu, menu);

            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            posicion = info.position;
            menu.setHeaderTitle(datosJSON.getJSONObject(posicion).getJSONObject("value").getString("Codigo"));
        }catch (Exception e){
            mostrarMsg("Erro al mostrar el menu: "+e.getMessage());
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        try {
            int prueba = item.getItemId();
            if (prueba == R.id.mnxAgregar) {
                parametros.putString("accion", "nuevo");
                abrirActividad(parametros);
            } else if (prueba == R.id.mnxModificar) {
                parametros.putString("accion", "modificar");
                parametros.putString("cristian", datosJSON.getJSONObject(posicion).toString());
                abrirActividad(parametros);

            }else if (prueba== R.id.mnxEliminar){
                eliminarProductos();
            }
            return true;
        } catch (Exception e) {
            mostrarMsg("Error al seleccionar el item: " + e.getMessage());
            return super.onContextItemSelected(item);


        }
    }
    private void eliminarProductos(){
        try {
            AlertDialog.Builder confirmacion = new AlertDialog.Builder(Lista_Productos.this);
            confirmacion.setTitle("Esta seguro de eliminar a: ");
            confirmacion.setMessage(datosJSON.getJSONObject(posicion).getJSONObject("value").getString("Codigo"));


            confirmacion.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String respuesta = dbProductos.administrar_productos("eliminar", new String[]{cProductos.getString(0)});
                    if (respuesta.equals("ok")){
                        mostrarMsg("Producto eliminado con exito.");
                        obtenerProdctos();
                    } else {
                        mostrarMsg("Error al eliminar Producto: " + respuesta);
                    }
                }
            });
            confirmacion.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            confirmacion.create().show();
        }catch (Exception e){
            mostrarMsg("Error al eliminar: "+ e.getMessage());

        }
    }

    private void abrirActividad(Bundle parametros ){
        Intent abrirVentana = new Intent(getApplicationContext(), MainActivity.class);
        abrirVentana.putExtras(parametros);
        startActivity(abrirVentana);
    }

    private void obtenerProdctos(){
        try {
            dbProductos = new BDProducto(getApplicationContext(), "", null, 2);
            cProductos = dbProductos.obtener_producto();

            if (cProductos.moveToFirst()){
                datosJSON = new JSONArray();

                do {
                    jsonObject = new JSONObject();
                    JSONObject jsonObjectValue = new JSONObject();

                    jsonObject.put("_id", cProductos.getString(0));
                    jsonObject.put("_rev", cProductos.getString(1));
                    jsonObject.put("idProducto", cProductos.getString(2));
                    jsonObject.put("Codigo", cProductos.getString(3));
                    jsonObject.put("detalle", cProductos.getString(4));
                    jsonObject.put("Marca", cProductos.getString(5));
                    jsonObject.put("Precio", cProductos.getString(6));
                    jsonObject.put("Costo", cProductos.getString(7));
                    jsonObject.put("Stock", cProductos.getString(8));
                    jsonObject.put("urlCompletaFoto", cProductos.getString(9));
                    jsonObjectValue.put("value", jsonObject);

                    datosJSON.put(jsonObjectValue);
                }while (cProductos.moveToNext());
                mostrarDatosProductos();
            }else{
                mostrarMsg("No hay Productos que mostrar");
            }
        }catch (Exception e){
            mostrarMsg("Error al obtener Productos:" + e.getMessage());
        }
    }
    private void mostrarMsg(String msg){

        Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_LONG).show();
    }
    private void buscarProductos(){
        TextView tempVal;
        tempVal = findViewById(R.id.txtBuscarProducto);
        tempVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    alproductos.clear();
                    String valor = tempVal.getText().toString().trim().toLowerCase();
                    if ( valor.length()<=0 ){
                        alproductos.addAll(alproductosCopy);
                    }else{
                        for (productos producto : alproductosCopy){
                            String Codigoo = producto.getCodigo();
                            String Deetalle = producto.getDetalle();
                            String Marcaa = producto.getMarca();
                            String Precioo = producto.getPrecio();

                            if( Codigoo.toLowerCase().trim().contains(valor) ||
                                    Deetalle.toLowerCase().trim().contains(valor) ||
                                    Marcaa.toLowerCase().trim().contains(valor) ||
                                    Precioo.trim().contains(valor) ) {
                                alproductos.add(producto);
                            }
                        }
                        adaptadorImagenes adImagenes = new adaptadorImagenes(getApplicationContext(), alproductos);
                        lts.setAdapter(adImagenes);
                    }

                }catch (Exception e){
                    mostrarMsg("Error al buscar: "+e.getMessage() );

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}