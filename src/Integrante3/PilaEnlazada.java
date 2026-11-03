package Integrante3;

public class PilaEnlazada<T>{
	private NodoSimple<T> top;

    public PilaEnlazada() {
        this.top = null;

    }

    // Verificar si la pila está vacía

    public boolean isEmpty() {
        return top == null;

    }

    // Agregar un elemento a la pila

    public void push(T dato) {

        NodoSimple<T> nuevo = new NodoSimple<>(dato);
        nuevo.setNext(top);
        top = nuevo;
    }

    // Eliminar y devolver el elemento superior

    public T pop() {
        T dato = null;
        if (!isEmpty()) {
            dato = top.getDato();
            top = top.getNext();

        }

        return dato;
    }

    // Consultar el elemento superior sin eliminarlo

    public T peek() {
        T dato = null;
        if (!isEmpty()) {
            dato = top.getDato();
            
        }

        return dato;

    }

}
