package com.example.productosql;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class adaptadorImagenes extends BaseAdapter {
    Context context;

    ArrayList<productos> datosProductosArrayList;
    productos misProductos;

    LayoutInflater layoutInflanter;

    public adaptadorImagenes(Context context, ArrayList<productos> datosProductosArrayList) {
        this.context = context;
        this.datosProductosArrayList = datosProductosArrayList;
    }

    @Override
    public int getCount() {
        return datosProductosArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return datosProductosArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position; //Long.parseLong(datosProductosArrayList.get(position).getIdProducto());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        layoutInflanter = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflanter.inflate(R.layout.listview_imagenes, parent, false);
        try{
            misProductos = datosProductosArrayList.get(position);

            TextView tempVal = view.findViewById(R.id.lblCodigo);
            tempVal.setText(misProductos.getCodigo());

            tempVal = view.findViewById(R.id.lblDescripcion);
            tempVal.setText(misProductos.getDetalle());

            tempVal = view.findViewById(R.id.lblStock);
            tempVal.setText(misProductos.getStock());

            ImageView imageView = view.findViewById(R.id.imgFoto);
            Bitmap bitmap = BitmapFactory.decodeFile(misProductos.getFoto());
            imageView.setImageBitmap(bitmap);



        }catch (Exception e){
            Toast.makeText(context, "Error al Mostrar Datos:"+ e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return view;
    }
}
