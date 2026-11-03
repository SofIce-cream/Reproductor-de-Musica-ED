package Integrante1;
import java.util.*;

public class ListaDobleReproduccion {

    private NodoDoble head;
    private NodoDoble tail;
    private NodoDoble cancionActual;
    private boolean modoBucle;
    private int cantidad;
    private Map<Integer, NodoDoble> indicePorId;

    // Constructor
    public ListaDobleReproduccion() {
        head = null;
        tail = null;
        cancionActual = null;
        modoBucle = false;
        cantidad = 0;
        indicePorId = new HashMap<>(); 
    }

    // Buscar una cancion por ID || O(1) || promedio
    public NodoDoble buscarNodoPorId(int id) {
        return indicePorId.get(id);
    }

    // Comprobar si un nodo pertenece a esta lista||  O(1) ||
    public boolean contieneNodo(NodoDoble nodo) {
        return nodo != null && nodo.isEnLista();
    }

    // Agregar una cancion al final
    public void agregarAlFinal(Cancion cancion) {
        if (cancion == null) {
            throw new IllegalArgumentException(
                "La cancion no puede ser nula.");
        }

        if (buscarNodoPorId(cancion.getId()) != null) {
            throw new IllegalArgumentException(
                "Ya existe una cancion con ese ID.");
        }

        NodoDoble nuevo = new NodoDoble(cancion);

        if (head == null) {
            head = nuevo;
            tail = nuevo;
            cancionActual = nuevo;
        } else {
            nuevo.setPrev(tail);
            tail.setNext(nuevo);
            tail = nuevo;
        }

        nuevo.setEnLista(true);
        indicePorId.put(cancion.getId(), nuevo);
        cantidad++;
        cerrarCirculo();
    }

    // Avanzar a la siguiente cancion O(1)
    public boolean pasarSiguiente() {
        if (cancionActual == null) {
            throw new IllegalStateException(
                "La lista esta vacia.");
        }

        if (cancionActual == tail && !modoBucle) {
            return false;
        }

        if (cancionActual == tail) {
            cancionActual = head;
        } else {
            cancionActual = cancionActual.getNext();
        }

        return true;
    }

    // Volver a la cancion anterior O(1)
    public boolean volverAnterior() {
        if (cancionActual == null) {
            throw new IllegalStateException(
                "La lista esta vacia.");
        }

        if (cancionActual == head && !modoBucle) {
            return false;
        }

        if (cancionActual == head) {
            cancionActual = tail;
        } else {
            cancionActual = cancionActual.getPrev();
        }

        return true;
    }

    // Activar el modo bucle
    public void activarModoBucle() {
        if (head == null) {
            throw new IllegalStateException(
                "No hay canciones en la lista.");
        }

        modoBucle = true;
        cerrarCirculo();
    }

    // Desactivar el modo bucle
    public void desactivarModoBucle() {
        if (!modoBucle) {
            return;
        }

        romperCirculo();
        modoBucle = false;
    }

    public void cambiarModoBucle(boolean activar) {
        if (activar) {
            activarModoBucle();
        } else {
            desactivarModoBucle();
        }
    }

    // Desconectar temporalmente los extremos
    private void romperCirculo() {
        if (modoBucle && head != null) {
            tail.setNext(null);
            head.setPrev(null);
        }
    }

    // Restablecer conexiones de los extremos
    private void cerrarCirculo() {
        if (head == null) {
            return;
        }

        if (modoBucle) {
            tail.setNext(head);
            head.setPrev(tail);
        } else {
            head.setPrev(null);
            tail.setNext(null);
        }
    }

    // Desconectar un nodo de sus vecinos
    private void desconectar(NodoDoble nodo) {
        NodoDoble ant = nodo.getPrev();
        NodoDoble sig = nodo.getNext();

        if (ant != null) {
            ant.setNext(sig);
        } else {
            head = sig;
        }

        if (sig != null) {
            sig.setPrev(ant);
        } else {
            tail = ant;
        }

        nodo.setPrev(null);
        nodo.setNext(null);
    }

    // Conectar un nodo despues de otro
    // Si ant es null, insertar al inicio
    private void conectarDespuesDe(
            NodoDoble nodo, NodoDoble ant) {

        if (ant == null) {
            nodo.setPrev(null);
            nodo.setNext(head);

            if (head != null) {
                head.setPrev(nodo);
            } else {
                tail = nodo;
            }

            head = nodo;
        } else {
            NodoDoble sig = ant.getNext();

            nodo.setPrev(ant);
            nodo.setNext(sig);
            ant.setNext(nodo);

            if (sig != null) {
                sig.setPrev(nodo);
            } else {
                tail = nodo;
            }
        }
    }

    // Insertar un nodo existente
    // Puede usarse para restaurar una eliminacion
    public int insertarNodoDespuesDe(
            NodoDoble nodo, NodoDoble ant) {

        if (nodo == null || nodo.getDato() == null) {
            return -1;
        }

        if (nodo.isEnLista()) {
            return -1;
        }

        int id = nodo.getDato().getId();

        if (buscarNodoPorId(id) != null) {
            return -1;
        }

        // Si el vecino original ya no existe,
        // insertar al final como respaldo para Undo
        if (ant != null && !contieneNodo(ant)) {
            ant = tail;
        }

        romperCirculo();
        conectarDespuesDe(nodo, ant);

        nodo.setEnLista(true);
        indicePorId.put(id, nodo);
        cantidad++;

        if (cancionActual == null) {
            cancionActual = nodo;
        }

        cerrarCirculo();
        return 1;
    }

    // Play Next: insertar despues de la actual
    public int insertarDespuesDeActual(Cancion cancion) {
        if (cancion == null) {
            return -1;
        }

        if (buscarNodoPorId(cancion.getId()) != null) {
            return -1;
        }

        if (head != null && cancionActual == null) {
            return -1;
        }

        NodoDoble nuevo = new NodoDoble(cancion);

        return insertarNodoDespuesDe(
            nuevo, cancionActual);
    }

    // Eliminar un nodo especifico
    public NodoDoble eliminarNodoEspecifico(
            NodoDoble nodo) {

        if (!contieneNodo(nodo)) {
            return null;
        }

        romperCirculo();

        NodoDoble ant = nodo.getPrev();
        NodoDoble sig = nodo.getNext();

        desconectar(nodo);
        cantidad--;

        nodo.setEnLista(false);
        indicePorId.remove(nodo.getDato().getId());

        if (nodo == cancionActual) {
            if (sig != null) {
                cancionActual = sig;
            } else if (modoBucle && head != null) {
                cancionActual = head;
            } else {
                cancionActual = ant;
            }
        }

        if (cantidad == 0) {
            head = null;
            tail = null;
            cancionActual = null;
            modoBucle = false;
        }

        cerrarCirculo();
        return nodo;
    }

    // Eliminar la cancion actual
    public NodoDoble eliminarCancionActual() {
        return eliminarNodoEspecifico(cancionActual);
    }

    // Mover un nodo despues de otro
    // destino null significa mover al inicio
    public int moverCancion(
            NodoDoble nodo, NodoDoble destino) {

        if (!contieneNodo(nodo)) {
            return -1;
        }

        if (destino != null
                && !contieneNodo(destino)) {
            return -1;
        }

        if (nodo == destino) {
            return -1;
        }

        romperCirculo();

        // No mover si ya esta en esa posicion
        if (nodo.getPrev() == destino) {
            cerrarCirculo();
            return 1;
        }

        desconectar(nodo);
        conectarDespuesDe(nodo, destino);

        cerrarCirculo();
        return 1;
    }

    // Saltar directamente a un nodo conocido
    public int saltarACancion(NodoDoble nodo) {
        if (!contieneNodo(nodo)) {
            return -1;
        }

        cancionActual = nodo;
        return 1;
    }

    // Obtener informacion de la cancion actual
    public String mostrarCancionActual() {
        if (cancionActual == null) {
            return "No hay cancion seleccionada.";
        }

        return "Reproduciendo: "
            + cancionActual.getDato().toString();
    }

    // Obtener toda la lista en texto
    public String mostrarLista() {
        if (head == null) {
            return "La lista esta vacia.";
        }

        StringBuilder resultado = new StringBuilder();
        NodoDoble auxiliar = head;

        resultado.append(
            "\n===== LISTA DE REPRODUCCION =====\n");

        for (int i = 0; i < cantidad; i++) {
            if (auxiliar == null) {
                throw new IllegalStateException(
                    "Error en las conexiones de la lista.");
            }

            if (auxiliar == cancionActual) {
                resultado.append(">> ");
            } else {
                resultado.append("   ");
            }

            resultado.append(auxiliar.getDato());
            resultado.append("\n");

            auxiliar = auxiliar.getNext();
        }

        resultado.append("Total de canciones: ")
                 .append(cantidad)
                 .append("\n");

        resultado.append("Modo bucle: ")
                 .append(modoBucle
                     ? "Activado" : "Desactivado");

        return resultado.toString();
    }

    // Comprobar la integridad de los enlaces y del índice || O(n) || método de diagnóstico
    public boolean validarEstructura() {
        if (cantidad == 0) {
            return head == null
                && tail == null
                && cancionActual == null
                && !modoBucle
                && indicePorId.isEmpty();
        }

        if (head == null || tail == null
                || cancionActual == null) {
            return false;
        }

        if (indicePorId.size() != cantidad) {
            return false;
        }

        NodoDoble auxiliar = head;
        NodoDoble anterior = modoBucle ? tail : null;

        boolean actualEncontrada = false;
        int i = 0;

        while (i < cantidad) {
            if (auxiliar == null
                    || !auxiliar.isEnLista()) {
                return false;
            }

            if (indicePorId.get(auxiliar.getDato().getId()) != auxiliar) {
                return false;
            }

            if (auxiliar.getPrev() != anterior) {
                return false;
            }

            if (auxiliar == cancionActual) {
                actualEncontrada = true;
            }

            anterior = auxiliar;
            auxiliar = auxiliar.getNext();

            if (i < cantidad - 1
                    && (auxiliar == null
                    || auxiliar == head)) {
                return false;
            }

            i++;
        }

        if (anterior != tail || !actualEncontrada) {
            return false;
        }

        if (modoBucle) {
            return auxiliar == head
                && tail.getNext() == head
                && head.getPrev() == tail;
        }

        return auxiliar == null
            && head.getPrev() == null
            && tail.getNext() == null;
    }
    // Getters
    public NodoDoble getHead() {
        return head;
    }

    public NodoDoble getTail() {
        return tail;
    }

    public NodoDoble getCancionActual() {
        return cancionActual;
    }

    public boolean isModoBucle() {
        return modoBucle;
    }

    public int getCantidad() {
        return cantidad;
    }
}
