
package Integrante1;

public class NodoDoble {

    private Cancion dato;
    private NodoDoble next;
    private NodoDoble prev;
    private boolean enLista;

    // Constructor
    public NodoDoble(Cancion dato) {
        this.dato = dato;
        this.next = null;
        this.prev = null;
        this.enLista = false;
    }

    // Getter y setter de la cancion
    public Cancion getDato() {
        return dato;
    }

    public void setDato(Cancion dato) {
        this.dato = dato;
    }

    // Getter y setter del siguiente nodo
    public NodoDoble getNext() {
        return next;
    }

    public void setNext(NodoDoble next) {
        this.next = next;
    }

    // Getter y setter del nodo anterior
    public NodoDoble getPrev() {
        return prev;
    }

    public void setPrev(NodoDoble prev) {
        this.prev = prev;
    }

    // Getter y setter de enLista
    public boolean isEnLista() {
        return enLista;
    }

    public void setEnLista(boolean enLista) {
        this.enLista = enLista;
    }
}
