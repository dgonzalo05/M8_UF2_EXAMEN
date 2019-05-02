package org.dgonzalo.m8_uf2_exam_dgonzalo;

public class Incidencia {
    private String imagen;
    private String descripcion;
    private String aula;
    private String realizada;

    public Incidencia() {
    }

    public Incidencia(String imagen, String descripcion, String aula, String realizada) {
        this.imagen = imagen;
        this.descripcion = descripcion;
        this.aula = aula;
        this.realizada = realizada;
    }


    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getAula() {
        return aula;
    }

    public void setAula(String aula) {
        this.aula = aula;
    }

    public String getRealizada() {
        return realizada;
    }

    public void setRealizada(String realizada) {
        this.realizada = realizada;
    }
}
