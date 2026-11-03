package Integrante1;
import java.util.*;

public class Main {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		
		//Instacia la estructura
		ListaDobleReproduccion fila = new ListaDobleReproduccion();
		PilaEnlazada<NodoDoble> pilaHistorial = new PilaEnlazada<>();
		PilaEnlazada<RegistroEliminacion> pilaUndo = new PilaEnlazada<>();
		
		ReproductorController controller = new ReproductorController(fila, pilaHistorial, pilaUndo);
		controller.cargarMockData(); //Se agrega la mock data
		int opcion = -1;
		while (opcion != 0) {
            System.out.println();
            System.out.println(controller.mostrarDashboardConsola());
            opcionesMenu();
            opcion = leerEntero(scanner, "\n.✦ ݁˖     Selecciona una opción     .✦ ݁˖\n ");
            try {
            	switch (opcion) {
                	case 1:
                    	System.out.println(controller.reproducirSiguiente());
                    	break;
                	case 2:
                    	System.out.println(controller.reproducirAnteriorFila());
                    	break;
                	case 3:
                    	System.out.println(controller.volverHistorialReal());
                    	break;
                	case 4:
                    	System.out.println("\n⚡ Play Next ⚡");
                    	int idNext = leerIdNuevo(scanner, controller);
                    	String tituloNext = leerTextoNoVacio(scanner, "   Título: ");
                    	String artistaNext = leerTextoNoVacio(scanner, "   Artista: ");
                    	int duracionNext = leerEnteroPositivo(scanner, "   Duración en segundos: ");
                    	System.out.println(controller.playNext(idNext, tituloNext, artistaNext, duracionNext));
                    	break;
                	case 5:
                    	System.out.println("\n˙⋆✮ Agregar al final de la fila ✮⋆˙");
                    	int idFinal = leerIdNuevo(scanner, controller);
                    	String tituloFinal = leerTextoNoVacio(scanner, "   Título: ");
                    	String artistaFinal = leerTextoNoVacio(scanner, "   Artista: ");
                    	int duracionFinal = leerEnteroPositivo(scanner, "   Duración en segundos: ");
                    	System.out.println(controller.agregarAlFinal(idFinal, tituloFinal, artistaFinal, duracionFinal));
                    	break;
                	case 6:
                    	System.out.println(controller.eliminarActual());
                    	break;
                	case 7:
                    	int idBorrar = leerEntero(scanner, "Ingresa el ID de la canción a eliminar: ");
                    	System.out.println(controller.eliminarSeleccion(idBorrar));
                    	break;
                	case 8:
                    	System.out.println(controller.deshacerEliminacion());
                    	break;
                	case 9:
                    	int idDestino = leerEntero(scanner, "Ingresa el ID de la canción que deseas saltar: ");
                    	System.out.println(controller.saltarCancion(idDestino));
                    	break;
                	case 10:
                		int idCancion = leerEntero(scanner, "Ingresa el ID de la canción que quieres mover: ");
                    	int idRef = leerEntero(scanner, "Ingresa el ID de la canción después de la cual quieres ponerla (0 para el inicio): ");
                    	System.out.println(controller.moverCancion(idCancion, idRef));
                    	break;
                	case 11:
                    	System.out.println(controller.modoBucle());
                    	break;
                	case 12:
                    	System.out.println(controller.detalles());
                    	break;
                	case 13:
                    	System.out.println(controller.cargarMockData());
                    	break;
                	case 0:
                    	System.out.println("Cerrando el reproductor...");
                    	break;
                	default:
                		System.out.println("Opción no válida. Intenta con un número del 0 al 13.");
            	}
            } catch (ReproductorException e) {
            	System.out.println("⚠ " + e.getMessage());
            }
        }

        scanner.close();
		
	}
	private static void opcionesMenu() {
		System.out.println("\n✦.               Menú de Controles             .✦");
        System.out.println("  1. ⏭  Siguiente canción");
        System.out.println("  2. ⏮  Canción anterior en fila");
        System.out.println("  3. 🕒 Volver por Historial Real");
        System.out.println("  4. ⚡ Play Next: Insertar después de la actual");
        System.out.println("  5. ➕ Agregar canción al final de la fila");
        System.out.println("  6. 🗑  Eliminar canción actual");
        System.out.println("  7. ❌ Eliminar canción por ID");
        System.out.println("  8. ↩  Deshacer eliminación / Undo");
        System.out.println("  9. 🎯 Saltar directamente a una canción por ID ");
        System.out.println(" 10. 🔀 Mover / Reordenar canción en la fila");
        System.out.println(" 11. 🔁 Activar / Desactivar Modo Bucle");
        System.out.println(" 12. 📚 Detalles de las Pilas (Historial y Undo)");
        System.out.println(" 13. 🔄 Recargar Mock Data");
        System.out.println("  0. 🚪 Salir\n");
	}
	// Validación para evitar InputMismatchException
    private static int leerEntero(Scanner scanner, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor ingresa un número entero.");
            }
        }
    }
    // Números mayores a 0 (duración)
    private static int leerEnteroPositivo(Scanner scanner, String mensaje) {
        while (true) {
            int valor = leerEntero(scanner, mensaje);
            if (valor > 0) {
                return valor;
            }
            System.out.println("El valor debe ser mayor a 0.");
        }
    }
    // Pide un ID positivo que no exista aún, así no se llenan los demás datos para nada
    private static int leerIdNuevo(Scanner scanner, ReproductorController controller) {
        while (true) {
            int id = leerEnteroPositivo(scanner, "   ID de la canción: ");
            if (!controller.existeId(id)) {
                return id;
            }
            System.out.println("Ya existe una canción con ese ID. Intenta con otro.");
        }
    }
    // Validación para que no queden títulos o artistas en blanco
    private static String leerTextoNoVacio(Scanner scanner, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = scanner.nextLine().trim();
            if (!texto.isEmpty()) {
                return texto;
            }
            System.out.println("El campo no puede estar vacío.");
        }
    }
}
