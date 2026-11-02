package Integrante1;
import java.util.*;

public class ReproductorController {
	private ListaDobleReproduccion fila; 
	private PilaEnlazada<NodoDoble> pilaHistorial; //Historial de canciones sonadas
	private PilaEnlazada<RegistroEliminacion> pilaUndo; //Para deshacer las canciones eliminadas
	
	public ReproductorController(ListaDobleReproduccion fila, PilaEnlazada<NodoDoble> pilaHistorial, PilaEnlazada<RegistroEliminacion> pilaUndo) {
		super();
		this.fila = fila;
		this.pilaHistorial = pilaHistorial;
		this.pilaUndo = pilaUndo;
	}

	// Permite al Main validar el ID antes de pedir el resto de datos
	public boolean existeId(int id) {
		return fila.buscarNodoPorId(id) != null;
	}

	public void reproducirSiguiente() {
		NodoDoble actual = fila.getCancionActual();
		if(actual == null) {
			System.out.println("No está sonando ninguna canción actualmente");
			return;
		}
		if(actual.getNext() == null && !fila.isModoBucle()) {
			System.out.println("Ya estás en el final de la lista");
			return;
		}
		//Guardar la actual en el historial
		pilaHistorial.push(actual);
		
		//Pasa a la siguiente cancion
		fila.pasarSiguiente();
		System.out.println("⏭ Está sonando: " + fila.getCancionActual().getDato().getTitulo());
	}
	
	public void reproducirAnteriorFila() {
		NodoDoble actual = fila.getCancionActual();
		
		if(actual == null) {
			System.out.println("La cola está vacía");
			return;
		}
		if(actual.getPrev() == null && !fila.isModoBucle()) {
			System.out.println("Ya estás en el inicio de la lista"); 
			return;
		}
		fila.volverAnterior();
		NodoDoble nuevaActual = fila.getCancionActual();
		if(nuevaActual != null) {
			System.out.println("⏮ Está sonando: " + nuevaActual.getDato().getTitulo());
		}
	}
	
	public void volverHistorialReal() {
		if(pilaHistorial.isEmpty()) {
			System.out.println("El historial está vacío");
			return;
		}
		// Se descartan del historial los nodos que ya no existen en la fila.
		while(!pilaHistorial.isEmpty()) {
			NodoDoble ultimaEscucha = pilaHistorial.pop();
			if(ultimaEscucha == null || ultimaEscucha.getDato() == null) {
				continue;
			}
			NodoDoble enLista = fila.buscarNodoPorId(ultimaEscucha.getDato().getId());
			if(enLista == ultimaEscucha) {
				fila.saltarACancion(ultimaEscucha);
				System.out.println("Volviendo a " + ultimaEscucha.getDato().getTitulo());
				return;
			}
		}
		System.out.println("Las canciones del historial ya no están en la fila");
	}
	
	public void agregarAlFinal(int id, String titulo, String artista, int duracion) {
		if(!datosValidos(id, titulo, artista, duracion)) {
			return;
		}
		Cancion nueva = new Cancion(id, titulo, artista, duracion);
		fila.agregarAlFinal(nueva);
		System.out.println(nueva.getTitulo() + " agregada con éxito");
	}
	
	public void playNext(int id, String titulo, String artista, int duracion) {
		if(!datosValidos(id, titulo, artista, duracion)) {
			return;
		}
		Cancion nueva = new Cancion(id, titulo, artista, duracion);
		if(fila.getHead() == null|| fila.getCancionActual() == null) {
			fila.agregarAlFinal(nueva);
			System.out.println(nueva.getTitulo() + " agregada con éxito");
		}
		else {
			if(fila.insertarDespuesDeActual(nueva) == 1) {
				System.out.println(nueva.getTitulo() + " sonará después de la actual");
			}
			else {
				System.out.println("No fue posible hacer esta acción");
			}
		}
	}
	
	private boolean datosValidos(int id, String titulo, String artista, int duracion) {
		if(id <= 0) {
			System.out.println("El ID debe ser un número positivo");
			return false;
		}
		if(existeId(id)) {
			System.out.println("Ya existe una canción con el ID " + id);
			return false;
		}
		if(titulo == null || titulo.trim().isEmpty() || artista == null || artista.trim().isEmpty()) {
			System.out.println("El título y el artista no pueden estar vacíos");
			return false;
		}
		if(duracion <= 0) {
			System.out.println("La duración debe ser mayor a 0 segundos");
			return false;
		}
		return true;
	}
	
	private NodoDoble eliminarYGuardar(NodoDoble nodo) {
		if(nodo == null || !fila.contieneNodo(nodo))
			return null;
		NodoDoble anterior = (nodo == fila.getHead()) ? null : nodo.getPrev();
		NodoDoble siguiente = (nodo == fila.getTail()) ? null : nodo.getNext();
		NodoDoble eliminado = fila.eliminarNodoEspecifico(nodo);
		if(eliminado != null) {
			pilaUndo.push(new RegistroEliminacion(eliminado, anterior, siguiente));
		}
		return eliminado;
	}
	
	public void eliminarActual() {
		NodoDoble eliminada = eliminarYGuardar(fila.getCancionActual()); //Guardar la eliminada
		if(eliminada ==  null) { //Casos de excepción
			System.out.println("No es posible hacer esta acción"); 
			return;
		}
		System.out.println("Eliminada " + eliminada.getDato().getTitulo() + " con éxito");	
	}

	public void eliminarSeleccion(int id) {
		if(fila.getHead() == null) {
			System.out.println("La fila está vacía");
			return;
		}
		NodoDoble nodo = fila.buscarNodoPorId(id);
		NodoDoble nodoEliminado = eliminarYGuardar(nodo);
		if(nodoEliminado != null) {
			System.out.println("Eliminada " + nodoEliminado.getDato().getTitulo() + " con éxito");
		}
		else {
			System.out.println("No fue posible encontrar esta canción");
		}
	}
	
	public void deshacerEliminacion() {
		if(pilaUndo.isEmpty()) {
			System.out.println("No hay nada para deshacer");
			return;
		}
		RegistroEliminacion registro = pilaUndo.pop();
		NodoDoble eliminada = registro.getNodoEliminado();
		if(eliminada == null || eliminada.getDato() == null) { //Casos de excepción
			System.out.println("No es posible hacer esta acción");
			return;
		}
		
		// Si mientras se agregó otra canción con el mismo ID, no se puede restaurar
		if(existeId(eliminada.getDato().getId())) {
			System.out.println("No se puede restaurar " + eliminada.getDato().getTitulo() + ": ya existe una canción con ese ID");
			return;
		}
		NodoDoble anterior = registro.getAnterior();
		boolean posicionOriginal = true;
		
		//Si el anterior no está en la fila, va al final
		if(anterior != null && !fila.contieneNodo(anterior)) {
			anterior = fila.getTail();
			posicionOriginal = false;
		}
		//Se reinserta la canción
		if(fila.insertarNodoDespuesDe(eliminada, anterior) == 1) {
			System.out.println("Canción " + eliminada.getDato().getTitulo() + " restaurada con éxito" + (posicionOriginal ? " en su posicion original" : " al final de la fila"));
		}else {
			System.out.println("No fue posible hacer esta acción");
		}
	}
	
	public void saltarCancion(int idDestino) {
		if(fila.getHead() == null) {
			System.out.println("No es posible hacer esta acción");
			return;
		}
		NodoDoble destino = fila.buscarNodoPorId(idDestino);
		if(destino == null) {
			System.out.println("No es posible hacer esta acción");
			return;
		}
		NodoDoble actual = fila.getCancionActual();
		if(destino == actual) { // Evita meter la misma canción al historial sin haber cambiado
			System.out.println("Esa canción ya está sonando");
			return;
		}
		if(actual != null) {
			pilaHistorial.push(actual);
		}
		fila.saltarACancion(destino);
		System.out.println("⏭ Saltando a " + destino.getDato().getTitulo());
	}
	
	public void moverCancion(int actual, int id) {
		if(fila.getHead() == null) {
			System.out.println("No es posible hacer esta acción");
			return;
		}
		NodoDoble mover = fila.buscarNodoPorId(actual);
		NodoDoble destino = fila.buscarNodoPorId(id);
		
		if(mover == null || destino == null) {
			System.out.println("No se encontró alguna de las canciones indicadas");
			return;
		}
		int resultado = fila.moverCancion(mover, destino);
		if(resultado == 1){
			System.out.println("Canción " + mover.getDato().getTitulo() + " movida después de " + destino.getDato().getTitulo());
		} else {
			System.out.println("No es posible mover la canción a esa posición");
		}
	}
	
	public void modeBucle() {
		// Se alterna usando el estado real de la lista
		if(fila.getHead() == null) {
			System.out.println("No hay canciones en la fila");
			return;
		}
		boolean estado = !fila.isModoBucle(); //En base al estado, se cambia
		fila.cambiarModoBucle(estado);
		System.out.println("↳↰ Modo bucle " + (estado ? "activado" : "desactivado"));
	}
	
	public void detalles() {
		System.out.println("- - - - ˗ˏˋ 🕒 Historial de Reproducción ˎˊ˗ - - - -");
		if(pilaHistorial.isEmpty()) { //Revisar que no este vacio
			System.out.println(" Vacío ");
		}
		 
		//Mostrar el historial
		PilaEnlazada<NodoDoble> auxHis = new PilaEnlazada<>();
		while(!pilaHistorial.isEmpty()){
			NodoDoble nodo = pilaHistorial.pop();
			System.out.println(" " + nodo.getDato());
			auxHis.push(nodo);
		}
		
		while(!auxHis.isEmpty()) { //Se restaura el orden original
			pilaHistorial.push(auxHis.pop());
		}
		
		System.out.println("- - - - ˗ˏˋ 🗑 Canciones Eliminadas (Undo) ˎˊ˗ - - - -");
		if(pilaUndo.isEmpty()) {
			System.out.println(" Vacío ");
		}
		//Mostrar el historial
		PilaEnlazada<RegistroEliminacion> auxUnd = new PilaEnlazada<>();
		while(!pilaUndo.isEmpty()){
			RegistroEliminacion reg = pilaUndo.pop();
			System.out.println(" " + reg.getNodoEliminado().getDato());
			auxUnd.push(reg);
		}
		
		while(!auxUnd.isEmpty()) {
			pilaUndo.push(auxUnd.pop());
		} //Se restaura el orden original
	}
	
	public void mostrarDashboardConsola() {
		System.out.println("♪ 𝕽𝖊𝖕𝖗𝖔𝖉𝖚𝖈𝖙𝖔𝖗 𝕯𝖊 𝕸ú𝖘𝖎𝖈𝖆 ♪");
		NodoDoble actual = fila.getCancionActual();
		if(actual != null) {
			System.out.println("ℝ𝕖𝕡𝕣𝕠𝕕𝕦𝕔𝕚𝕖𝕟𝕕𝕠");
			//Se muestra la información de la canción actual
			System.out.println("⭑·•—–⏭ " + actual.getDato().getTitulo() + " - " + actual.getDato().getArtista() + " ⌛︎ " + actual.getDato().getDuracion() + " s ⏮—–•·⭑");
			//Se muestra la canción previa y siguiente de la cola
			String anterior = (actual.getPrev() !=null) ? actual.getPrev().getDato().getTitulo() : ".✦ ݁˖ Es el inicio de la cola";
			String siguiente = (actual.getNext() != null) ? actual.getNext().getDato().getTitulo() : "Es el fin de la cola .✦ ݁˖";

			System.out.println("⏮ Anterior: " + anterior);
			System.out.println("⏭ Siguiente: " + siguiente);
		}
		else {
			System.out.println("✦. Actualmente no está sonando nada .✦ ");
		}
		System.out.println("- - - - ˗ˏˋ ★ ˎˊ˗ - - - -");
		System.out.println("\n" + fila.mostrarLista()); //Se muestra la fila de canciones
		
		//Peek() devuelve null si la pila está vacía
		NodoDoble ultimoHistorial = pilaHistorial.peek();
		String historial = (ultimoHistorial != null && ultimoHistorial.getDato() != null) ? ultimoHistorial.getDato().getTitulo() + " - " + ultimoHistorial.getDato().getArtista() : " ˖݁ La cima está vacía  ݁˖";
		RegistroEliminacion ultimoUndo = pilaUndo.peek();
		String undo = (ultimoUndo != null && ultimoUndo.getNodoEliminado().getDato() != null) ? ultimoUndo.getNodoEliminado().getDato().getTitulo() + " - " + ultimoUndo.getNodoEliminado().getDato().getArtista() : " ˖݁ No hay nada para deshacer ݁˖";
		
		//Mostrar el historial
		System.out.println("\n★ 🕒 Cima de Historial: " + historial);
        System.out.println("★ 🗑 Cima de Undo: " + undo);
        System.out.println("- - - - ˗ˏˋ ★ ˎˊ˗ - - - -");
		
	}
	
	public void cargarMockData() {
		Cancion[] mock = {
			new Cancion(1, "Beat It",  "Michael Jackson", 218),
			new Cancion(2, "Bohemian Rhapsody",  "Queen", 355),
			new Cancion(3, "Livin' On A Prayer",  "Bon Jovi", 251),
			new Cancion(4, "Let Down",  "Radiohead", 299),
			new Cancion(5, "Somebody's Watching Me",  "RockWell", 299),
			new Cancion(6, "De Música Ligera",  "Soda Stereo", 213),
			new Cancion(7, "Every Breath You Take",  "The Police", 253),
			new Cancion(8, "Still Loving You",  "Scorpions", 386),
			new Cancion(9, "Don't Cry",  "Gun N Roses", 284),
			new Cancion(10, "Tren Al Sur",  "Los Prisioneros", 336)
		};
		int agregadas = 0;
		for(Cancion c : mock) {
			if(!existeId(c.getId())) {
				fila.agregarAlFinal(c);
				agregadas++;
			}
		}
		if(agregadas == 0) {
			System.out.println("La mock data ya está cargada en la fila");
		}
	}
}
