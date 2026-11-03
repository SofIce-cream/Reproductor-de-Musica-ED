package Integrante3;

public class NodoSimple <T>{
	private T dato;

    private NodoSimple<T> next;

    public NodoSimple(T dato) {

        this.dato = dato;

        this.next = null;

    }

    public T getDato() {

        return dato;

    }

    public void setDato(T dato) {

        this.dato = dato;

    }

    public NodoSimple<T> getNext() {

        return next;

    }

    public void setNext(NodoSimple<T> next) {

        this.next = next;

    }

}
