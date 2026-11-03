package Integrante3;

import Integrante1.NodoDoble;

public class RegistroEliminacion {
	
	private NodoDoble nodoEliminado;
    private NodoDoble anterior;
    private NodoDoble siguiente;

    public RegistroEliminacion(NodoDoble nodoEliminado, NodoDoble anterior, NodoDoble siguiente) {

        this.nodoEliminado = nodoEliminado;
        this.anterior = anterior;
        this.siguiente = siguiente;

    }

    public NodoDoble getNodoEliminado() {
        return nodoEliminado;

    }

    public NodoDoble getAnterior() {
        return anterior;

    }

    public NodoDoble getSiguiente() {
        return siguiente;

    }

}
