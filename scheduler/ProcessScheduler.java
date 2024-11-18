/*ProcessScheduler.java */
/**
** Hecho por: Maria Claudia Lainfiesta Herrera.
** Carnet: 24000149.
** Seccion: BN.
**/
/*Descripción: Este es la clase principal que ejecuta el programa. Recibe los argumentos de la línea de comandos, valida los mismos y llama a diferentes métodos según el número y tipo de argumentos proporcionados. Se encarga de gestionar las políticas de planificación y las configuraciones de la simulación.*/

import scheduler.scheduling.policies.FCFS;
import scheduler.scheduling.policies.LCFS;
import scheduler.scheduling.policies.PP;
import scheduler.scheduling.policies.RR;

public class ProcessScheduler {

    //*************************************** Metodo Principal *************************************************
    /**
     * Nombre: main.
     * Método que realiza la ejecución principal del programa al ser compilado.
     * @return ejecución del proceso solicitado.
     */
    public static void main(String[] args){
        String politicaDual = args[0];
        try{
            if (politicaDual.equals("-dual") && args.length == 7){
                String politica = args[1];
                String rangoTiempo = args[2];
                String[] partes = rangoTiempo.split("-");
                Double primeraParte = Double.parseDouble(partes[0]);
                Double segundaParte = Double.parseDouble(partes[1]);
                if(validacionRangoTiempo(primeraParte, segundaParte)){
                    Double arith = Double.parseDouble(args[3]);
                    Double io = Double.parseDouble(args[4]);
                    Double cond = Double.parseDouble(args[5]);
                    Double loop = Double.parseDouble(args[6]);
                    llamarPoliticaDoble(politica, primeraParte, segundaParte, arith, io, cond, loop);
                } else {
                    System.err.println("Error en el rango de tiempo dado.");
                }
            } else if (politicaDual.equals("-dual") && args.length == 8){
                String politica = args[1];
                String rangoTiempo = args[2];
                String[] partes = rangoTiempo.split("-");
                Double primeraParte = Double.parseDouble(partes[0]);
                Double segundaParte = Double.parseDouble(partes[1]);
                if(validacionRangoTiempo(primeraParte, segundaParte)){
                    Double arith = Double.parseDouble(args[3]);
                    Double io = Double.parseDouble(args[4]);
                    Double cond = Double.parseDouble(args[5]);
                    Double loop = Double.parseDouble(args[6]);
                    Double quantium = Double.parseDouble(args[7]);
                    llamarPoliticaDoble2(politica, primeraParte, segundaParte, arith, io, cond, loop, quantium);
                } else {
                    System.err.println("Error en el rango de tiempo dado.");
                }
            } else if (args.length == 6){
                String rangoTiempo = args[1];
                String[] partes = rangoTiempo.split("-");
                Double primeraParte = Double.parseDouble(partes[0]);
                Double segundaParte = Double.parseDouble(partes[1]);
                if(validacionRangoTiempo(primeraParte, segundaParte)){
                    Double arith = Double.parseDouble(args[2]);
                    Double io = Double.parseDouble(args[3]);
                    Double cond = Double.parseDouble(args[4]);
                    Double loop = Double.parseDouble(args[5]);
                    llamarPoliticaSimple(politicaDual, primeraParte, segundaParte, arith, io, cond, loop);
                } else {
                    System.err.println("Error en el rango de tiempo dado.");
                }
            } else if (args.length == 7){
                String rangoTiempo = args[1];
                String[] partes = rangoTiempo.split("-");
                Double primeraParte = Double.parseDouble(partes[0]);
                Double segundaParte = Double.parseDouble(partes[1]);
                if(validacionRangoTiempo(primeraParte, segundaParte)){
                    Double arith = Double.parseDouble(args[2]);
                    Double io = Double.parseDouble(args[3]);
                    Double cond = Double.parseDouble(args[4]);
                    Double loop = Double.parseDouble(args[5]);
                    Double quantium = Double.parseDouble(args[6]);
                    llamarPoliticaSimple2(politicaDual, primeraParte, segundaParte, arith, io, cond, loop, quantium);
                } else {
                    System.err.println("Error en el rango de tiempo dado.");
                }
            } else if (args.length == 1){
                mensajes(politicaDual);
            } else {
                System.out.println("Comando no válido. Usa -sintaxis o -help para ver los comandos válidos.");
            }
        } catch(NumberFormatException e) {
            System.out.println("Por favor usa el sintaxis correcto. Usa -sintaxis o -help para ver los comandos válidos. ");
        }
    }

    //*************************************** Metodos Secundarios *************************************************

    /**
     * Nombre: impresionDatosIniciales.
     * Método que imprime los datos iniciales en cada ejecución.
     * @param primeraParte tiempo mínimo de agregar procesos.
     * @param segundaParte tiempo máximo de agregar procesos.
     * @param arith tiempo de atención de procesos aritméticos.
     * @param io tiempo de atención de procesos input/output.
     * @param cont tiempo de atención de procesos condicionales.
     * @param loop tiempo de atención de procesos iterativos.
     * @param quantum tiempo de atención de procesos iterativos.
     * @return mensaje en terminal.
     */
    public static void impresionDatosIniciales(String politica,int procesadores, Double primeraParte, Double segundaParte, Double arith, Double io, Double cond, Double loop, Double quantum){
        System.out.println("  ~Politica: " + politica + ".");
        System.out.println("  ~No. Procesadores: " + procesadores + ".");
        System.out.println("  ~Rango entrada de procesos: " + primeraParte + "seg - " + segundaParte + " seg.");
        System.out.println("  ~Tiempo procesos aritméticos: " + arith + " seg.");
        System.out.println("  ~Tiempo procesos input/output: " + io + " seg.");
        System.out.println("  ~Tiempo procesos condicionales: " + cond + " seg.");
        System.out.println("  ~Tiempo procesos iterativos: " + loop + " seg.");
        if(quantum != 0.0) {
            System.out.println("  ~Quantum: " + quantum + " seg.");
        }
        System.out.println(" Recordatorio: ingresa la letra \"q\" en cualquier momento para salir.");
        System.out.println("-----------------------------------------------------------------------");
    }

    /**
     * Nombre: validacionRangoTiempo.
     * Método que valida si el rango de tiempo ingresado es valido.
     * @param minimo tiempo mínimo de agregar procesos.
     * @param maximo tiempo máximo de agregar procesos.
     * @return true/false si cumple o no la condicion..
     */
    public static boolean validacionRangoTiempo(Double minimo, Double maximo){
        if (minimo < maximo) {
            return true;
        }
        return false;
    }

    /**
     * Nombre: llamarPoliticaSimple.
     * Método que manda a llamar la politica (FCFS, LCFS o PP) solicita con un solo procesador.
     * @param primeraParte tiempo mínimo de agregar procesos.
     * @param segundaParte tiempo máximo de agregar procesos.
     * @param arith tiempo de atención de procesos aritméticos.
     * @param io tiempo de atención de procesos input/output.
     * @param cont tiempo de atención de procesos condicionales.
     * @param loop tiempo de atención de procesos iterativos.
     * @return ejecución del proceso solicitado.
     */
    public static void llamarPoliticaSimple(String politica, Double primeraParte, Double segundaParte, Double arith, Double io, Double cond, Double loop){
        int procesador = 1;
        Double quantum = 0.0;
        if (politica.equals("-fcfs")) {
            System.out.println("-------------- Iniciando Politica First-Come First-Served --------------");
            FCFS procesoRequerido = new FCFS(primeraParte, segundaParte, arith, io, cond, loop);
            impresionDatosIniciales(politica, procesador, primeraParte, segundaParte, arith, io, cond, loop, quantum);
            procesoRequerido.ejecucionSimple();
        } else if (politica.equals("-lcfs")) {
            System.out.println("-------------- Iniciando Politica Last-Come First-Served --------------");
            LCFS procesoRequerido = new LCFS(primeraParte, segundaParte, arith, io, cond, loop);
            impresionDatosIniciales(politica, procesador, primeraParte, segundaParte, arith, io, cond, loop, quantum);
            procesoRequerido.ejecucionSimple();
        } else if (politica.equals("-pp")) {
            System.out.println("------------------ Iniciando Politica Priority Policy ------------------");
            PP procesoRequerido = new PP(primeraParte, segundaParte, arith, io, cond, loop);
            impresionDatosIniciales(politica, procesador, primeraParte, segundaParte, arith, io, cond, loop, quantum);
            procesoRequerido.ejecucion();
        } else {
            System.out.println("Comando no válido. Usa -sintaxis o -help para ver los comandos válidos.");
        }
    }

    /**
     * Nombre: llamarPoliticaSimple2.
     * Método que manda a llamar la politica (RR) solicita con un solo procesador.
     * @param primeraParte tiempo mínimo de agregar procesos.
     * @param segundaParte tiempo máximo de agregar procesos.
     * @param arith tiempo de atención de procesos aritméticos.
     * @param io tiempo de atención de procesos input/output.
     * @param cont tiempo de atención de procesos condicionales.
     * @param loop tiempo de atención de procesos iterativos.
     * @param quantium tiempo fijo de atención.
     * @return ejecución del proceso solicitado.
     */
    public static void llamarPoliticaSimple2(String politica, Double primeraParte, Double segundaParte, Double arith, Double io, Double cond, Double loop, Double quantum){
        int procesador = 1;
        if (politica.equals("-rr")) {
            System.out.println("----------------- Iniciando Politica Round-Robin -----------------");
            impresionDatosIniciales(politica, procesador, primeraParte, segundaParte, arith, io, cond, loop, quantum);
            RR procesoRequerido = new RR(primeraParte, segundaParte, arith, io, cond, loop, quantum);
            procesoRequerido.unProcesador();
        } else {
            System.out.println("Comando no válido. Usa -sintaxis o -help para ver los comandos válidos.");
        }
    }

    /**
     * Nombre: llamarPoliticaDoble.
     * Método que manda a llamar la politica (FCFS, LCFS o PP) solicita con dos procesadores.
     * @param primeraParte tiempo mínimo de agregar procesos.
     * @param segundaParte tiempo máximo de agregar procesos.
     * @param arith tiempo de atención de procesos aritméticos.
     * @param io tiempo de atención de procesos input/output.
     * @param cont tiempo de atención de procesos condicionales.
     * @param loop tiempo de atención de procesos iterativos.
     * @return ejecución del proceso solicitado.
     */
    public static void llamarPoliticaDoble(String politica, Double primeraParte, Double segundaParte, Double arith, Double io, Double cond, Double loop){
        int procesador = 2;
        Double quantum = 0.0;
        if (politica.equals("-fcfs")) {
            System.out.println("-------------- Iniciando Politica First-Come First-Served --------------");
            impresionDatosIniciales(politica, procesador, primeraParte, segundaParte, arith, io, cond, loop, quantum);
            FCFS procesoRequerido = new FCFS(primeraParte, segundaParte, arith, io, cond, loop);
            procesoRequerido.ejecucionDoble();
        } else if (politica.equals("-lcfs")) {
            System.out.println("-------------- Iniciando Politica Last-Come First-Served --------------");
            impresionDatosIniciales(politica, procesador, primeraParte, segundaParte, arith, io, cond, loop, quantum);
            LCFS procesoRequerido = new LCFS(primeraParte, segundaParte, arith, io, cond, loop);
            procesoRequerido.ejecucionDoble();
        } else if (politica.equals("-pp")) {
            System.out.println("------------------ Iniciando Politica Priority Policy ------------------");
            impresionDatosIniciales(politica, procesador, primeraParte, segundaParte, arith, io, cond, loop, quantum);
            PP procesoRequerido = new PP(primeraParte, segundaParte, arith, io, cond, loop);
            procesoRequerido.ejecucionDoble();
        } else {
            System.out.println("Comando no válido. Usa -sintaxis o -help para ver los comandos válidos.");
        }
    }

    /**
     * Nombre: llamarPoliticaDoble2.
     * Método que manda a llamar la politica (RR) solicita con un solo procesador.
     * @param primeraParte tiempo mínimo de agregar procesos.
     * @param segundaParte tiempo máximo de agregar procesos.
     * @param arith tiempo de atención de procesos aritméticos.
     * @param io tiempo de atención de procesos input/output.
     * @param cont tiempo de atención de procesos condicionales.
     * @param loop tiempo de atención de procesos iterativos.
     * @param quantium tiempo fijo de atención.
     * @return ejecución del proceso solicitado.
     */
    public static void llamarPoliticaDoble2(String politica, Double primeraParte, Double segundaParte, Double arith, Double io, Double cond, Double loop, Double quantum){
        int procesador = 2;
        if (politica.equals("-rr")) {
            System.out.println("----------------- Iniciando Politica Round-Robin -----------------");
            impresionDatosIniciales(politica, procesador, primeraParte, segundaParte, arith, io, cond, loop, quantum);
            RR procesoRequerido = new RR(primeraParte, segundaParte, arith, io, cond, loop, quantum);
            procesoRequerido.dosProcesadores();
        } else {
            System.out.println("Comando no válido. Usa -sintaxis o -help para ver los comandos válidos.");
        }
    }

    /**
     * Nombre: help.
     * Método que imprime en terminal cada una de las políticas disponibles y su funcionalidad.
     * @return mensajes en terminal.
     */
    public static void help(){
        System.out.println();
        System.out.println("COMANDOS/POLITICAS VALIDAS:");
        System.out.println();
        System.out.println(" -fsfc (First-Come First-Served):");
        System.out.println("   El primer proceso en llegar es el primero en ser atendido.");
        System.out.println();
        System.out.println(" -lsfc (Last-Come First-Served):");
        System.out.println("   El último proceso en llegar es el primero en ser atendido.");
        System.out.println();
        System.out.println(" -pp (Priority Policy):");
        System.out.println("   El proceso con mayor prioridad es atendido primero.");
        System.out.println();
        System.out.println(" -rr (Round-Robin):");
        System.out.println("   Cada proceso recibe una cantidad fija de tiempo (quantum) y se alternan en la ejecución.");
        System.out.println();
        System.out.println(" -help (ayuda):");
        System.out.println("   Despliegue de comando y politicas validas en el programa..");
        System.out.println();
        System.out.println(" -sintaxis (escritura):");
        System.out.println("   Despliegue de sintaxis validas dentro del programa.");
        System.out.println();
    }

    /**
     * Nombre: sintaxis.
     * Método que imprime en terminal cada una de las sintaxis validas.
     * @return mensajes en terminal.
     */
    public static void sintaxis(){
        System.out.println();
        System.out.println("SINTAXIS VALIDAS:");
        System.out.println();
        System.out.println(" ~1 procesador:");
        System.out.println("   java ProcessScheduler -fcfs rango_tiempo_ingreso arith io cond loop");
        System.out.println("   java ProcessScheduler -lcfs rango_tiempo_ingreso arith io cond loop");
        System.out.println("   java ProcessScheduler -pp   rango_tiempo_ingreso arith io cond loop");
        System.out.println("   java ProcessScheduler -rr   rango_tiempo_ingreso arith io cond loop quantum");
        System.out.println();
        System.out.println(" ~2 procesadores:");
        System.out.println("   java ProcessScheduler -dual -fcfs rango_tiempo_ingreso arith io cond loop");
        System.out.println("   java ProcessScheduler -dual -lcfs rango_tiempo_ingreso arith io cond loop");
        System.out.println("   java ProcessScheduler -dual -pp   rango_tiempo_ingreso arith io cond loop");
        System.out.println("   java ProcessScheduler -dual -rr   rango_tiempo_ingreso arith io cond loop quantum");
        System.out.println();
    }

    /**
     * Nombre: mensajes.
     * Método que determina si se manda a llamar el método help o sintaxis.
     * @return mensajes en terminal.
     */
    public static void mensajes(String comando){
        if (comando.equals("-help")){
            help();
        } else if (comando.equals("-sintaxis")){
            sintaxis();
        } else {
            System.out.println("Comando no válido. Usa -sintaxis o -help para ver los comandos válidos.");
        }
    }
}
