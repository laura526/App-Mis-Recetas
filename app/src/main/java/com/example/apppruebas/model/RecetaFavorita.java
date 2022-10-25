package com.example.apppruebas.model;

public class RecetaFavorita {
    String idReceta, titulo, ingredientes, preparacion,imagen,idUsuario;

    public RecetaFavorita(){

    }

    public RecetaFavorita(String idReceta, String titulo, String ingredientes, String preparacion, String imagen, String idUsuario) {
        this.idReceta = idReceta;
        this.titulo = titulo;
        this.ingredientes = ingredientes;
        this.preparacion = preparacion;
        this.imagen = imagen;
        this.idUsuario = idUsuario;
    }

    public String getIdReceta() {
        return idReceta;
    }

    public void setIdReceta(String idReceta) {
        this.idReceta = idReceta;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(String ingredientes) {
        this.ingredientes = ingredientes;
    }

    public String getPreparacion() {
        return preparacion;
    }

    public void setPreparacion(String preparacion) {
        this.preparacion = preparacion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }
}
