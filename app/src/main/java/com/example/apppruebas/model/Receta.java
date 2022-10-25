package com.example.apppruebas.model;

public class Receta {
    String titulo, ingredientes, preparacion,imagen;

    //Constructor vac[io
    public Receta(){

    }

    //Constructor
    public Receta(String titulo, String ingredientes, String preparacion, String imagen) {
        this.titulo = titulo;
        this.ingredientes = ingredientes;
        this.preparacion = preparacion;
        this.imagen = imagen;
    }

    //Getters and Setters
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
}
