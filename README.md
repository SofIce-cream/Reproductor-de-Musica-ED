# 🎶 Fila de Reproducción Dinámica
El proyecto consiste en el diseño del motor lógico detrás de una fila de reproducción musical interactiva en tiempo real ` (inspirada en la Cola/Queue de Spotify). `
A diferencia de un reproductor básico o una lista estática, este sistema resuelve el problema de la manipulación dinámica constante: permite navegar hacia adelante y hacia atrás, insertar canciones para que suenen inmediatamente después de la canción actual, eliminar o reordenar canciones en curso, saltar a canciones específicas sin perder el rastro real de lo escuchado y deshacer eliminaciones accidentales.
## 🛠️ Criterio de Optimalidad
El proyecto demuestra por qué el uso combinado de Listas Doblemente Enlazadas y Pilas es la arquitectura más óptima posible en complejidad temporal y espacial ` O(1) ` `(O(n) para eliminar, saltar, mover y agregar)`:
- Estructura Principal — Lista Doblemente Enlazada (Fila de Reproducción Activa): Controlada mediante tres punteros estratégicos `(head, tail y cancionActual)`. Al contar con enlaces bidireccionales `(next y prev)`, garantiza que avanzar y retroceder se ejecuten en tiempo constante `O(1)`, y que insertar, eliminar o mover un nodo ya conocido solo requiera reasignar punteros `O(1)`, sin desplazar elementos. Lo que cuesta `O(n)` es la búsqueda por ID y la validación de ID repetido. Esto supera a las Listas Simples, que requieren O(n) para retroceder o buscar el nodo previo al borrar, a los Arreglos Dinámicos como ArrayList, que requieren O(n) por desplazamiento de bloques de memoria al insertar o borrar en posiciones intermedias, y a las Colas FIFO tradicionales, que impiden modificar elementos intermedios o retroceder.
- Estructura Complementaria — Pila sobre Lista Simplemente Enlazada (Historial Real y Undo): Controlada por un único puntero top. Resuelve dos escenarios donde no hace falta una Lista Doble: registrar el historial cronológico real de reproducción cuando el usuario realiza saltos manuales entre canciones, y almacenar temporalmente los nodos eliminados para ofrecer la función Deshacer. Se implementa sobre una Lista Simplemente Enlazada, un solo puntero next por nodo, bajo un criterio estricto de optimización de memoria: dado que en una pila solo se opera en la cima `(push y pop en O(1))`, utilizar nodos dobles desperdiciaría memoria en punteros prev que no se recorrerían.

## 📁 Estructura
- `Main`: menú y lectura de datos. Es la única clase que imprime por consola.
- `ReproductorController`: lógica de la aplicación. Devuelve mensajes (`String`) o lanza excepciones.
- `ListaDobleReproduccion`, `NodoDoble`, `Cancion`: fila de reproducción.
- `PilaEnlazada`, `NodoSimple`, `RegistroEliminacion`: historial y Undo.

## ⚠️ Manejo de errores
El controller no imprime: los errores se lanzan como excepciones propias que `Main` captura y muestra. Todas heredan de `ReproductorException`:
- `ListaVaciaException`: operar sobre una fila vacía.
- `IdRepetidoException`: usar un ID que ya existe.
- `DatosCancionInvalidosException`: ID, título, artista o duración inválidos.

## ▶️ Cómo ejecutar
Compilar las clases del paquete `Reproductor` y ejecutar `Main`. Al iniciar se carga una mock data de 10 canciones.
