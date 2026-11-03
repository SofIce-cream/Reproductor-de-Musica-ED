package Integrante1;

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

	public String reproducirSiguiente() {
		NodoDoble actual = fila.getCancionActual();
		if(actual == null) {
			throw new ListaVaciaException("No está sonando ninguna canción actualmente");
		}
		if(actual.getNext() == null && !fila.isModoBucle()) {
			throw new ReproductorException("Ya estás en el final de la lista");
		}
		//Guardar la actual en el historial
		pilaHistorial.push(actual);
		
		//Pasa a la siguiente cancion
		fila.pasarSiguiente();
		return "⏭ Está sonando: " + fila.getCancionActual().getDato().getTitulo();
	}
	
	public String reproducirAnteriorFila() {
		NodoDoble actual = fila.getCancionActual();
		
		//Casos de expceción
		if(actual == null) {
			throw new ListaVaciaException("La cola está vacía");
		}
		if(actual.getPrev() == null && !fila.isModoBucle()) {
			throw new ReproductorException("Ya estás en el inicio de la lista");
		}
		fila.volverAnterior();
		return "⏮ Está sonando: " + fila.getCancionActual().getDato().getTitulo();
	}
	
	public String volverHistorialReal() {
		if(pilaHistorial.isEmpty()) {
			throw new ReproductorException("El historial está vacío");
		}
		// Se busca la última canción que siga en la fila
		PilaEnlazada<NodoDoble> omitidas = new PilaEnlazada<>(); // Las que no están se guardan aparte para el Undo
		NodoDoble encontrada = null;
		while(encontrada == null && !pilaHistorial.isEmpty()) {
			NodoDoble candidata = pilaHistorial.pop();
			if(fila.contieneNodo(candidata)) {
				encontrada = candidata;
			} else {
				omitidas.push(candidata);
			}
		}
		// Se devuelven al historial en su orden original
		while(!omitidas.isEmpty()) {
			pilaHistorial.push(omitidas.pop());
		}
		if(encontrada == null) {
			throw new ReproductorException("Las canciones del historial ya no están en la fila");
		}
		fila.saltarACancion(encontrada);
		return "Volviendo a " + encontrada.getDato().getTitulo();
	}
	
	public String agregarAlFinal(int id, String titulo, String artista, int duracion) {
		validarDatos(id, titulo, artista, duracion);
		Cancion nueva = new Cancion(id, titulo, artista, duracion);
		fila.agregarAlFinal(nueva);
		return nueva.getTitulo() + " agregada con éxito";
	}
	
	public String playNext(int id, String titulo, String artista, int duracion) {
		validarDatos(id, titulo, artista, duracion);
		Cancion nueva = new Cancion(id, titulo, artista, duracion);
		if(fila.getHead() == null || fila.getCancionActual() == null) {
			fila.agregarAlFinal(nueva);
			return nueva.getTitulo() + " agregada con éxito";
		}
		if(fila.insertarDespuesDeActual(nueva) != 1) {
			throw new ReproductorException("No fue posible insertar la canción después de la actual");
		}
		return nueva.getTitulo() + " sonará después de la actual";
	}
	
	private void validarDatos(int id, String titulo, String artista, int duracion) {
		if(id <= 0) {
			throw new DatosCancionInvalidosException("El ID debe ser un número positivo");
		}
		if(existeId(id)) {
			throw new IdRepetidoException("Ya existe una canción con el ID " + id);
		}
		if(titulo == null || titulo.trim().isEmpty() || artista == null || artista.trim().isEmpty()) {
			throw new DatosCancionInvalidosException("El título y el artista no pueden estar vacíos");
		}
		if(duracion <= 0) {
			throw new DatosCancionInvalidosException("La duración debe ser mayor a 0 segundos");
		}
	}
	
	private NodoDoble eliminarYGuardar(NodoDoble nodo) {
		if(!fila.contieneNodo(nodo)) {
			throw new ReproductorException("La canción no está en la fila");
		}
		NodoDoble anterior = (nodo == fila.getHead()) ? null : nodo.getPrev();
		NodoDoble siguiente = (nodo == fila.getTail()) ? null : nodo.getNext();
		NodoDoble eliminado = fila.eliminarNodoEspecifico(nodo);
		pilaUndo.push(new RegistroEliminacion(eliminado, anterior, siguiente));
		return eliminado;
	}
	
	public String eliminarActual() {
		if(fila.getCancionActual() == null) {
			throw new ListaVaciaException("No hay ninguna canción para eliminar");
		}
		NodoDoble eliminada = eliminarYGuardar(fila.getCancionActual()); //Guardar la eliminada
		return "Eliminada " + eliminada.getDato().getTitulo() + " con éxito";
	}

	public String eliminarSeleccion(int id) {
		if(fila.getHead() == null) {
			throw new ListaVaciaException("La fila está vacía");
		}
		NodoDoble nodo = fila.buscarNodoPorId(id);
		if(nodo == null) {
			throw new ReproductorException("No se encontró una canción con el ID " + id);
		}
		NodoDoble eliminada = eliminarYGuardar(nodo);
		return "Eliminada " + eliminada.getDato().getTitulo() + " con éxito";
	}
	
	// La canción vuelve a su posición original solo si sus dos vecinos siguen juntos
	private boolean vecinosIntactos(RegistroEliminacion registro) {
		NodoDoble anterior = registro.getAnterior();
		NodoDoble siguiente = registro.getSiguiente();
		
		if(anterior == null && siguiente == null) {
			return fila.getHead() == null; // Era la única canción y la fila sigue vacía
		}
		if(anterior == null) {
			return fila.contieneNodo(siguiente) && fila.getHead() == siguiente; // Estaba al inicio
		}
		if(siguiente == null) {
			return fila.contieneNodo(anterior) && fila.getTail() == anterior; // Estaba al final
		}
		return fila.contieneNodo(anterior) && fila.contieneNodo(siguiente)
				&& anterior.getNext() == siguiente && siguiente.getPrev() == anterior;
	}
	
	public String deshacerEliminacion() {
		if(pilaUndo.isEmpty()) {
			throw new ReproductorException("No hay nada para deshacer");
		}
		RegistroEliminacion registro = pilaUndo.pop();
		NodoDoble eliminada = registro.getNodoEliminado();
		String titulo = eliminada.getDato().getTitulo();
		
		// Si mientras tanto se agregó otra canción con el mismo ID, no se puede restaurar
		if(existeId(eliminada.getDato().getId())) {
			throw new IdRepetidoException("No se puede restaurar " + titulo + ": ya existe una canción con ese ID (se descartó del Undo)");
		}
		
		//Si los vecinos ya no están juntos, va al final
		boolean posicionOriginal = vecinosIntactos(registro);
		NodoDoble anterior = posicionOriginal ? registro.getAnterior() : fila.getTail();
		
		//Se reinserta la canción
		if(fila.insertarNodoDespuesDe(eliminada, anterior) != 1) {
			throw new ReproductorException("No fue posible restaurar la canción");
		}
		return "Canción " + titulo + " restaurada con éxito" + (posicionOriginal ? " en su posición original" : " al final de la fila");
	}
	
	public String saltarCancion(int idDestino) {
		if(fila.getHead() == null) {
			throw new ListaVaciaException("La fila está vacía");
		}
		NodoDoble destino = fila.buscarNodoPorId(idDestino);
		if(destino == null) {
			throw new ReproductorException("No se encontró una canción con el ID " + idDestino);
		}
		NodoDoble actual = fila.getCancionActual();
		if(destino == actual) { // Evita meter la misma canción al historial sin haber cambiado
			throw new ReproductorException("Esa canción ya está sonando");
		}
		if(actual != null) {
			pilaHistorial.push(actual);
		}
		fila.saltarACancion(destino);
		return "⏭ Saltando a " + destino.getDato().getTitulo();
	}
	
	// idReferencia = 0 significa mover la canción al inicio de la fila
	public String moverCancion(int idMover, int idReferencia) {
		if(fila.getHead() == null) {
			throw new ListaVaciaException("La fila está vacía");
		}
		NodoDoble mover = fila.buscarNodoPorId(idMover);
		if(mover == null) {
			throw new ReproductorException("No se encontró una canción con el ID " + idMover);
		}
		NodoDoble destino = null;
		if(idReferencia != 0) {
			destino = fila.buscarNodoPorId(idReferencia);
			if(destino == null) {
				throw new ReproductorException("No se encontró una canción con el ID " + idReferencia);
			}
		}
		if(fila.moverCancion(mover, destino) != 1) {
			throw new ReproductorException("No es posible mover la canción a esa posición");
		}
		if(destino == null) {
			return "Canción " + mover.getDato().getTitulo() + " movida al inicio de la fila";
		}
		return "Canción " + mover.getDato().getTitulo() + " movida después de " + destino.getDato().getTitulo();
	}
	
	public String modoBucle() {
		// Se alterna usando el estado real de la lista
		if(fila.getHead() == null) {
			throw new ListaVaciaException("No hay canciones en la fila");
		}
		boolean estado = !fila.isModoBucle(); //En base al estado, se cambia
		fila.cambiarModoBucle(estado);
		return "↳↰ Modo bucle " + (estado ? "activado" : "desactivado");
	}
	
	public String detalles() {
		StringBuilder sb = new StringBuilder();
		sb.append("- - - - ˗ˏˋ 🕒 Historial de Reproducción ˎˊ˗ - - - -\n");
		if(pilaHistorial.isEmpty()) { //Revisar que no este vacio
			sb.append(" Vacío \n");
		}
		 
		//Mostrar el historial
		PilaEnlazada<NodoDoble> auxHis = new PilaEnlazada<>();
		while(!pilaHistorial.isEmpty()){
			NodoDoble nodo = pilaHistorial.pop();
			sb.append(" ").append(nodo.getDato()).append("\n");
			auxHis.push(nodo);
		}
		
		while(!auxHis.isEmpty()) { //Se restaura el orden original
			pilaHistorial.push(auxHis.pop());
		}
		
		sb.append("- - - - ˗ˏˋ 🗑 Canciones Eliminadas (Undo) ˎˊ˗ - - - -\n");
		if(pilaUndo.isEmpty()) {
			sb.append(" Vacío \n");
		}
		//Mostrar los eliminados
		PilaEnlazada<RegistroEliminacion> auxUnd = new PilaEnlazada<>();
		while(!pilaUndo.isEmpty()){
			RegistroEliminacion reg = pilaUndo.pop();
			sb.append(" ").append(reg.getNodoEliminado().getDato()).append("\n");
			auxUnd.push(reg);
		}
		
		while(!auxUnd.isEmpty()) {
			pilaUndo.push(auxUnd.pop());
		} //Se restaura el orden original
		
		return sb.toString();
	}
	
	public String mostrarDashboardConsola() {
		StringBuilder sb = new StringBuilder();
		sb.append("♪ 𝕽𝖊𝖕𝖗𝖔𝖉𝖚𝖈𝖙𝖔𝖗 𝕯𝖊 𝕸ú𝖘𝖎𝖈𝖆 ♪\n");
		NodoDoble actual = fila.getCancionActual();
		if(actual != null) {
			sb.append("ℝ𝕖𝕡𝕣𝕠𝕕𝕦𝕔𝕚𝕖𝕟𝕕𝕠\n");
			//Se muestra la información de la canción actual
			sb.append("⭑·•—–⏭ " + actual.getDato().getTitulo() + " - " + actual.getDato().getArtista() + " ⌛︎ " + actual.getDato().getDuracion() + " s ⏮—–•·⭑\n");
			//Se muestra la canción previa y siguiente de la cola
			String anterior = (actual.getPrev() !=null) ? actual.getPrev().getDato().getTitulo() : ".✦ ݁˖ Es el inicio de la cola";
			String siguiente = (actual.getNext() != null) ? actual.getNext().getDato().getTitulo() : "Es el fin de la cola .✦ ݁˖";

			sb.append("⏮ Anterior: " + anterior + "\n");
			sb.append("⏭ Siguiente: " + siguiente + "\n");
		}
		else {
			sb.append("✦. Actualmente no está sonando nada .✦ \n");
		}
		sb.append("- - - - ˗ˏˋ ★ ˎˊ˗ - - - -\n");
		sb.append("\n" + fila.mostrarLista() + "\n"); //Se muestra la fila de canciones
		
		//Peek() devuelve null si la pila está vacía
		NodoDoble ultimoHistorial = pilaHistorial.peek();
		String historial = (ultimoHistorial != null && ultimoHistorial.getDato() != null) ? ultimoHistorial.getDato().getTitulo() + " - " + ultimoHistorial.getDato().getArtista() : " ˖݁ La cima está vacía  ݁˖";
		RegistroEliminacion ultimoUndo = pilaUndo.peek();
		String undo = (ultimoUndo != null && ultimoUndo.getNodoEliminado().getDato() != null) ? ultimoUndo.getNodoEliminado().getDato().getTitulo() + " - " + ultimoUndo.getNodoEliminado().getDato().getArtista() : " ˖݁ No hay nada para deshacer ݁˖";
		
		//Mostrar la cima de cada pila
		sb.append("\n★ 🕒 Cima de Historial: " + historial + "\n");
		sb.append("★ 🗑 Cima de Undo: " + undo + "\n");
		sb.append("- - - - ˗ˏˋ ★ ˎˊ˗ - - - -");
		return sb.toString();
	}
	
	public String cargarMockData() {
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
			return "La mock data ya está cargada en la fila";
		}
		return "Mock data cargada (" + agregadas + " canciones)";
	}
}
