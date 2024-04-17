package com.example.productosql;

public class productos {
    String _id;
    String _rev;
    String idProducto;
    String  Codigo;
    String detalle;
    String Marca;
    String Precio;

    String Costo;

    String Stock;
    String Foto;

    public productos(String _id, String _rev, String idProducto, String codigo, String detalle, String marca, String precio, String costo, String stock, String foto) {
        this._id = _id;
        this._rev = _rev;
        this.idProducto = idProducto;
        Codigo = codigo;
        this.detalle = detalle;
        Marca = marca;
        Precio = precio;
        Costo = costo;
        Stock = stock;
        this.Foto = foto;
    }

    public String getCosto() {
        return Costo;
    }

    public void setCosto(String costo) {
        Costo = costo;
    }

    public String getStock() {
        return Stock;
    }

    public void setStock(String stock) {
        Stock = stock;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_rev() {
        return _rev;
    }

    public void set_rev(String _rev) {
        this._rev = _rev;
    }

    public String getFoto() {
        return Foto;
    }

    public void setFoto(String foto) {
        Foto = foto;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public String getCodigo() {
        return Codigo;
    }

    public void setCodigo(String codigo) {
        Codigo = codigo;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getMarca() {
        return Marca;
    }

    public void setMarca(String marca) {
        Marca = marca;
    }

    public String getPrecio() {
        return Precio;
    }

    public void setPrecio(String precio) {
        Precio = precio;
    }
}
