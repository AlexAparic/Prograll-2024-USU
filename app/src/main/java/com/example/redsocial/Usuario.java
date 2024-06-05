package com.example.redsocial;



public class Usuario {
    private String uuid;
    private String usuario;
    private String email;
    private String password;

    private String Phone;

    public Usuario(String uuid, String usuario, String email, String password, String Phone) {
        this.uuid = uuid;
        this.usuario = usuario;
        this.email = email;
        this.password = password;
        this.Phone = Phone;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }
}
