package Integrante3;

import Integrante1.Cancion;
import Integrante1.ListaDobleReproduccion;
import Integrante1.NodoDoble;

public class GestorPilas {
	
	private PilaEnlazada<RegistroEliminacion> pilaUndo;
    private PilaEnlazada<Cancion> historial;

    public GestorPilas() {
        pilaUndo = new PilaEnlazada<>();
        historial = new PilaEnlazada<>();
    }
    
    // Elimina la canción actual y guarda su información en Undo

    public boolean registrarEliminacion(ListaDobleReproduccion lista) {

        boolean eliminado = false;
        NodoDoble actual = lista.getCancionActual();

        if (actual != null) {
            NodoDoble anterior = actual.getPrev();
            NodoDoble siguiente = actual.getNext();

            // Ajustamos las referencias si la lista es circular

            if (lista.isModoBucle()) {
                if (actual == lista.getHead()) {
                    anterior = null;
                }
               
				if (actual == lista.getTail()) {
                    siguiente = null;
                }
            }

            NodoDoble nodoEliminado = lista.eliminarCancionActual();

            if (nodoEliminado != null) {
                RegistroEliminacion registro =  new RegistroEliminacion(nodoEliminado, anterior, siguiente);
                pilaUndo.push(registro);
                eliminado = true;
            }
        }
        return eliminado;
    }

    // Comprueba si la canción puede regresar a su posición original

    private boolean puedeRestaurar( ListaDobleReproduccion lista, RegistroEliminacion registro) {
		
        NodoDoble anterior = registro.getAnterior();
        NodoDoble siguiente = registro.getSiguiente();
        boolean valido = false;

        if (anterior == null && siguiente == null) {
            // La canción era la única de la lista
            valido = lista.getHead() == null;

        } else if (anterior == null) {

            // La canción estaba al inicio
            valido = siguiente.isEnLista() && lista.getHead() == siguiente;

        } else if (siguiente == null) {

            // La canción estaba al final
            valido = anterior.isEnLista()  && lista.getTail() == anterior;
        } else {

            // La canción estaba entre dos nodos
            valido = anterior.isEnLista()&& siguiente.isEnLista() 
            		&& anterior.getNext() == siguiente
                    && siguiente.getPrev() == anterior;
        }
        return valido;
    }

    // Deshace la última eliminación

    // 0: no hay eliminaciones

    // 1: restaurada en su posición original

    // 2: restaurada al final

    public int deshacerEliminacion(ListaDobleReproduccion lista) {

        int resultado = 0;

        if (!pilaUndo.isEmpty()) {

            RegistroEliminacion registro = pilaUndo.peek();
            NodoDoble nodo = registro.getNodoEliminado();
            NodoDoble anterior = registro.getAnterior();

            if (puedeRestaurar(lista, registro)) {
                int insercion =lista.insertarNodoDespuesDe(nodo, anterior);

                if (insercion == 1) {

                    pilaUndo.pop();
                    resultado = 1;
                }
            } else {
                int insercion = lista.insertarNodoDespuesDe(nodo, lista.getTail());

                if (insercion == 1) {

                    pilaUndo.pop();
                    resultado = 2;
                }
            }

        }

        return resultado;

    }

    // Guarda una canción en el historial

    public void registrarReproduccion(Cancion cancion) {

        if (cancion != null) {
            historial.push(cancion);
        }
    }

    // Consulta la última canción registrada sin retirarla

    public Cancion consultarUltimaReproduccion() {
        return historial.peek();
    }

    // Recupera la última canción registrada y la retira

    public Cancion recuperarUltimaReproduccion() {
        return historial.pop();
    }

    // Comprueba si existen eliminaciones por deshacer

    public boolean hayEliminacionesPendientes() {
        return !pilaUndo.isEmpty();
    }

    // Comprueba si existe historial de reproducción

    public boolean hayHistorial() {
        return !historial.isEmpty();
    }
}
