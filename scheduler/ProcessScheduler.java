

import scheduler.scheduling.policies.PP;
import scheduler.scheduling.policies.FCFS;
//import scheduler.scheduling.policies.LCFS;
//import scheduler.scheduling.policies.RR;

public class ProcessScheduler {
    public static void main(String[] args){
        String politicaDual = args[0];
        try{
            if (politicaDual.equals("-dual") && args.length == 7){
                String politica = args[1];
                String rangoTiempo = args[2];
                String[] partes = rangoTiempo.split("-");
                Double primeraParte = Double.parseDouble(partes[0]);
                Double segundaParte = Double.parseDouble(partes[1]);
                Double arith = Double.parseDouble(args[3]);
                Double io = Double.parseDouble(args[4]);
                Double cond = Double.parseDouble(args[5]);
                Double loop = Double.parseDouble(args[6]);
                llamarPoliticaDoble(politica, primeraParte, segundaParte, arith, io, cond, loop);
            } else if (politicaDual.equals("-dual") && args.length == 8){
                String politica = args[1];
                String rangoTiempo = args[2];
                String[] partes = rangoTiempo.split("-");
                Double primeraParte = Double.parseDouble(partes[0]);
                Double segundaParte = Double.parseDouble(partes[1]);
                Double arith = Double.parseDouble(args[3]);
                Double io = Double.parseDouble(args[4]);
                Double cond = Double.parseDouble(args[5]);
                Double loop = Double.parseDouble(args[6]);
                Double quantium = Double.parseDouble(args[7]);
                llamarPoliticaDoble2(politica, primeraParte, segundaParte, arith, io, cond, loop, quantium);
            } else if (args.length == 6){
                String rangoTiempo = args[1];
                String[] partes = rangoTiempo.split("-");
                Double primeraParte = Double.parseDouble(partes[0]);
                Double segundaParte = Double.parseDouble(partes[1]);
                Double arith = Double.parseDouble(args[2]);
                Double io = Double.parseDouble(args[3]);
                Double cond = Double.parseDouble(args[4]);
                Double loop = Double.parseDouble(args[5]);
                llamarPoliticaSimple(politicaDual, primeraParte, segundaParte, arith, io, cond, loop);
            } else if (args.length == 7){
                String rangoTiempo = args[1];
                String[] partes = rangoTiempo.split("-");
                Double primeraParte = Double.parseDouble(partes[0]);
                Double segundaParte = Double.parseDouble(partes[1]);
                Double arith = Double.parseDouble(args[2]);
                Double io = Double.parseDouble(args[3]);
                Double cond = Double.parseDouble(args[4]);
                Double loop = Double.parseDouble(args[5]);
                Double quantium = Double.parseDouble(args[6]);
                llamarPoliticaSimple2(politicaDual, primeraParte, segundaParte, arith, io, cond, loop, quantium);
            } else if (args.length == 1){
                mensajes(politicaDual);
            } else {
                System.out.println("Comando no válido. Usa -sintaxis o -help para ver los comandos válidos.");
            }
        } catch(NumberFormatException e) {
            System.out.println("Por favor usa el sintaxis correcto. Usa -sintaxis o -help para ver los comandos válidos. ");
        }
    }

    /*Metodo que segun la politica crea el contructor de la clase correspondiente y llama la ejecucion simple de un solo procesador */
    public static void llamarPoliticaSimple(String politica, Double primeraParte, Double segundaParte, Double arith, Double io, Double cond, Double loop){
        if (politica.equals("-fcfs")) {
            System.out.println("----------Iniciando Proceso First-Come First-Served----------");
            System.out.println("Proceso: Una cola y un procesador");
            FCFS procesoRequerido = new FCFS(primeraParte, segundaParte, arith, io, cond, loop);
            procesoRequerido.ejecucionSimple();
        } else if (politica.equals("-lcfs")) {
            System.out.println("----------Iniciando Proceso Last-Come First-Served----------");
        } else if (politica.equals("-pp")) {
            System.out.println("----------Iniciando Proceso Priority Policy----------");
            PP procesoRequerido = new PP(primeraParte, segundaParte, arith, io, cond, loop);
            procesoRequerido.ejecucion();
        } else {
            System.out.println("Comando no válido. Usa -sintaxis o -help para ver los comandos válidos.");
        }
    }

    /*Metodo que segun la politica crea el constructor de la clase correspondiente y llama la ejecucion simple de dos procesadores */
    public static void llamarPoliticaSimple2(String politica, Double primeraParte, Double segundaParte, Double arith, Double io, Double cond, Double loopm, Double quantium){
        if (politica.equals("-rr")) {
            System.out.println("----------Iniciando Proceso Round-Robin----------");
        } else {
            System.out.println("Comando no válido. Usa -sintaxis o -help para ver los comandos válidos.");
        }
    }

    public static void llamarPoliticaDoble(String politica, Double primeraParte, Double segundaParte, Double arith, Double io, Double cond, Double loop){
        if (politica.equals("-fcfs")) {
            System.out.println("----------Iniciando Proceso First-Come First-Served----------");
            System.out.println("Proceso: Una cola y dos procesadores");
            FCFS procesoRequerido = new FCFS(primeraParte, segundaParte, arith, io, cond, loop);
            procesoRequerido.ejecucionDoble();
        } else if (politica.equals("-lcfs")) {
            System.out.println("----------Iniciando Proceso Last-Come First-Served----------");
        } else if (politica.equals("-pp")) {
            System.out.println("----------Iniciando Proceso Priority Policy----------");
            PP procesoRequerido = new PP(primeraParte, segundaParte, arith, io, cond, loop);
            procesoRequerido.ejecucion();
        } else {
            System.out.println("Comando no válido. Usa -sintaxis o -help para ver los comandos válidos.");
        }
    }

    public static void llamarPoliticaDoble2(String politica, Double primeraParte, Double segundaParte, Double arith, Double io, Double cond, Double loopm, Double quantium){
        if (politica.equals("-rr")) {
            System.out.println("----------Iniciando Proceso Round-Robin----------");
        } else {
            System.out.println("Comando no válido. Usa -sintaxis o -help para ver los comandos válidos.");
        }
    }

    public static void help(){
        System.out.println("First-Come First-Served:\n   El primer proceso en llegar es el primero en ser atendido.");
        System.out.println("Last-Come First-Served:\n   El último proceso en llegar es el primero en ser atendido.");
        System.out.println("Priority Policy:\n   El proceso con mayor prioridad es atendido primero.");
        System.out.println("Round-Robin:\n   Cada proceso recibe una cantidad fija de tiempo (quantum) y se alternan en la ejecución.");
    }

    public static void sintaxis(){
        System.out.println("Sintaxis válidas:");
        System.out.println("   java ProcessScheduler -fcfs rango_tiempo_ingreso arith io cond loop");
        System.out.println("   java ProcessScheduler -lcfs rango_tiempo_ingreso arith io cond loop");
        System.out.println("   java ProcessScheduler -pp   rango_tiempo_ingreso arith io cond loop");
        System.out.println("   java ProcessScheduler -rr   rango_tiempo_ingreso arith io cond loop quantum");
    }
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
