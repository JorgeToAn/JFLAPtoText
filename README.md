# Conversor JFLAP a archivo texto v.1.1
Descarga el archivo ejecutable .jar para empezar a convertir tus automatas de JFLAP en archivos de texto en siguiente formato:

1. Primer renglon sera el alfabeto, cada simbolo es separado por una coma.
2. Segundo renglon sera el numero de estados del automata, un numero entero.
3. Tercer renglon sera la lista de estados finales, numeros enteros separados por coma.
4. Cuarto renglon en adelante es la tabla de transiciones, en un arreglo de dos dimensiones, el renglon corresponde al estado y cada columna al simbolo correspondiente para la transicion, en el mismo orden que se presenta el alfabeto.

## Consideraciones

- El estado inicial debe de ser el estado q0.
- El diagrama debe contener todos los estados entre q0 hasta qn (por ejemplo un diagrama que empiece con q0 y termine en q3 pero no contiene q1), no realizara la conversion correctamente.
- El diagrama no debe de contener un estado trampa, ya que el programa lo añadira por ti.
- El archivo de texto sera guardado en el mismo directorio del archivo JFLAP que escogiste.
