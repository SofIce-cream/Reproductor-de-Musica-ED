package Integrante1;

public class Cancion {

    // Atributos
    private final int id; 
    private String titulo;
    private String artista;
    private int duracion;
    

    // Constructor
    public Cancion(int id, String titulo, String artista, int duracion) {
        this.id = id;
        this.titulo = titulo;
        this.artista = artista;
        this.duracion = duracion;
    }

    // Getter y Setter del ID
    public int getId() {
        return id;
    }

    // Getter y Setter del título
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    // Getter y Setter del artista
    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    // Getter y Setter de la duración
    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    // Mostrar información de la canción
    @Override
    public String toString() {
        return "ID: " + id
                + " | Título: " + titulo
                + " | Artista: " + artista
                + " | Duración: " + duracion + " segundos";
    }
}
