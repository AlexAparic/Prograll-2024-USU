package com.example.productosql;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    TextView tempVal;
    Button btn;

    FloatingActionButton btnRegresar;
    String accion="nuevo", id="", urlCompletaImg="", rev="", idProducto="";

    Intent tomarfotoIntent;

    ImageView img;

    utilidades utls;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        utls = new utilidades();
        img = findViewById(R.id.imgProductos);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tomarFotoProducto();
            }
        });

        btnRegresar = findViewById(R.id.btnRegresarListaProductos);

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegresarListaProductos();
            }
        });


        btn = findViewById(R.id.btnGuardarProucto);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                try{
                    tempVal = findViewById(R.id.txtCodigo);
                    String Codigo = tempVal.getText().toString();

                    tempVal = findViewById(R.id.txtDescripcion);
                    String detalle = tempVal.getText().toString();

                    tempVal = findViewById(R.id.txtMarca);
                    String Marca = tempVal.getText().toString();

                    tempVal = findViewById(R.id.txtPrecio);
                    String Precio = tempVal.getText().toString();

                    tempVal = findViewById(R.id.txtCosto);
                    String Costo = tempVal.getText().toString();


                    tempVal = findViewById(R.id.txtStock);
                    String Stock = tempVal.getText().toString();


                    double precioNumerico = Double.parseDouble(Precio);
                    double costoNumerico = Double.parseDouble(Costo);
                    double ganancia = ((precioNumerico - costoNumerico) / costoNumerico) * 100;


                    TextView lblGanancias = findViewById(R.id.ganancias);
                    lblGanancias.setText(String.format("Ganancia: %.2f%%", ganancia));

                    //guardar datos en el servidor
                    JSONObject datosProductos = new JSONObject();
                    if (accion.equals("modificar") && id.length()>0 && rev.length()>0 ){
                        datosProductos.put("_id", id);
                        datosProductos.put("_rev", rev);
                    }
                    datosProductos.put("idProducto", idProducto);
                    datosProductos.put("Codigo", Codigo);
                    datosProductos.put("detalle", detalle);
                    datosProductos.put("Marca", Marca);
                    datosProductos.put("Precio", Precio);
                    datosProductos.put("Costo", Costo);
                    datosProductos.put("Stock", Stock);
                    datosProductos.put("urlCompletaFoto", urlCompletaImg);
                    String respuesta = "";

                    enviarDatosServidor objGuardarDatosServidor = new enviarDatosServidor(getApplicationContext());
                    respuesta = objGuardarDatosServidor.execute(datosProductos.toString()).get();

                    JSONObject respuestaJSONObject = new JSONObject(respuesta);
                    if (respuestaJSONObject.getBoolean("ok") ){
                        id = respuestaJSONObject.getString("id");
                        rev = respuestaJSONObject.getString("rev");
                    }else{
                        respuesta = "Error al Guardar en Servidor: "+  respuesta;
                    }


                    BDProducto db = new BDProducto(getApplicationContext(), "", null, 2);
                    String[] datos = new String[]{id, rev, idProducto, Codigo, detalle, Marca, Precio, Costo, Stock, urlCompletaImg};
                    respuesta = db.administrar_productos(accion, datos);
                    if (respuesta.equals("ok")) {
                        Toast.makeText(getApplicationContext(), "Producto Registrado con Exito.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error: " + respuesta, Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                mostrarMsg("Error al Guardar: "+ e.getMessage());
                }
            }
        });
        mostrarDatosProductos();
    }

    private void tomarFotoProducto(){
        tomarfotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if ( tomarfotoIntent.resolveActivity(getPackageManager()) !=null){
            File fotoProducto = null;
            try {
                fotoProducto = crearImagenProducto();
                if(fotoProducto!=null){
                    Uri uriFotoProducto = FileProvider.getUriForFile(MainActivity.this, "com.example.productosql.fileprovider", fotoProducto);
                    tomarfotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriFotoProducto);
                    startActivityForResult(tomarfotoIntent, 1);
                }else{
                    mostrarMsg("No pude crear la foto");
                }

            }catch (Exception e){
                mostrarMsg("Error al tomar la foto: " + e.getMessage());

            }
        }else {
            mostrarMsg("No se selecciono una foto...toma foto");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if ( requestCode==1 && resultCode==RESULT_OK ){
                Bitmap imagenBitman = BitmapFactory.decodeFile(urlCompletaImg);
                img.setImageBitmap(imagenBitman);
            }else {
                mostrarMsg("Se cancelo la toma de foto");
            }
        }catch (Exception e){
            mostrarMsg("Error al mostrar la camara" + e.getMessage());

        }
    }

    private File crearImagenProducto() throws Exception{
        String FechaHoraMs = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String FileName = "Imagen_" + FechaHoraMs + "_";
        File dirAlmacenaminto = getExternalFilesDir(Environment.DIRECTORY_DCIM);
        if (!dirAlmacenaminto.exists() ){
            dirAlmacenaminto.mkdir();
        }
        File image = File.createTempFile(FileName, ".jpg", dirAlmacenaminto);
        urlCompletaImg = image.getAbsolutePath();
        return image;
    }
    private void mostrarMsg(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
    private void RegresarListaProductos(){
        Intent abrirVentana = new Intent(getApplicationContext(), Lista_Productos.class);
        startActivity(abrirVentana);
    }
    private void mostrarDatosProductos(){
        try {
            Bundle parametros = getIntent().getExtras();
            accion = parametros.getString("accion");
            if (accion.equals("modificar") ){
                JSONObject jsonObject = new JSONObject(parametros.getString("cristian")).getJSONObject("value");
                id = jsonObject.getString("_id");
                rev = jsonObject.getString("_rev");
                idProducto = jsonObject.getString("idProducto");

                tempVal = findViewById(R.id.txtCodigo);
                tempVal.setText(jsonObject.getString("Codigo"));

                tempVal = findViewById(R.id.txtDescripcion);
                tempVal.setText(jsonObject.getString("detalle"));

                tempVal = findViewById(R.id.txtMarca);
                tempVal.setText(jsonObject.getString("Marca"));

                tempVal = findViewById(R.id.txtPrecio);
                tempVal.setText(jsonObject.getString("Precio"));

                tempVal = findViewById(R.id.txtCosto);
                tempVal.setText(jsonObject.getString("Costo"));

                tempVal = findViewById(R.id.txtStock);
                tempVal.setText(jsonObject.getString("Stock"));

                urlCompletaImg = jsonObject.getString("urlCompletaFoto");
                Bitmap bitmap = BitmapFactory.decodeFile(urlCompletaImg);
                img.setImageBitmap(bitmap);
            }else { //nuevo registro
                idProducto = utls.generarIdUnico();
            }
        }catch (Exception e){
            mostrarMsg("Error al mostrar los datos: "+ e.getMessage());
        }
    }
}