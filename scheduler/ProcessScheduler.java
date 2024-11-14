

import scheduler.scheduling.policies.FCFS;
import scheduler.scheduling.policies.PP;

public class ProcessScheduler {
    public static void main(String[] args){
        if (args.length < 1) {
            System.out.println("No se proporcionaron suficientes argumentos. Usa -sintaxis o -help para más información.");
            return;
        }
        String politica = args[0];
        if (args.length == 6) {
            String rangoTiempoIngreso = args[1];
            String[] partes = rangoTiempoIngreso.split("-");
            Double primeraParte = Double.parseDouble(partes[0]);
            Double segundaParte = Double.parseDouble(partes[1]);

            Double arith = Double.parseDouble(args[2]);
            Double io = Double.parseDouble(args[3]);
            Double cond = Double.parseDouble(args[4]);
            Double loop = Double.parseDouble(args[5]);
            System.out.println("Rango de tiempo de ingreso: " + primeraParte + " - " + segundaParte);
            System.out.println("Tiempo de proceso aritmético: " + arith);
            System.out.println("Tiempo de proceso I/O: " + io);
            System.out.println("Tiempo de proceso condicional: " + cond);
            System.out.println("Tiempo de proceso iterativo (loop): " + loop);
            if (politica.equals("-fcfs")) {
                System.out.println("----------Iniciando Proceso First-Come First-Served----------");
                FCFS procesoRequerido = new FCFS(primeraParte, segundaParte, arith, io, cond, loop);
                procesoRequerido.ejecucionSimple();
                try {
                    while(true) {
                        String input = new java.util.Scanner(System.in).nextLine();
                        if (input.equals("q")) {
                            procesoRequerido.detenerSimulacion();
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (politica.equals("-lcfs")) {
                System.out.println("----------Iniciando Proceso Last-Come First-Served----------");
            } else if (politica.equals("-pp")) {
                System.out.println("----------Iniciando Proceso Priority Policy----------");
                PP procesoRequerido = new PP(arith, io, cond, loop);
                procesoRequerido.ejecucion();
            } else {
                System.out.println("Comando no válido. Usa -sintaxis o -help para ver los comandos válidos.");
            }
        } else if (args.length == 7) {
            if (politica.equals("-rr")) {
                System.out.println("----------Iniciando Proceso Round-Robin----------");
                //int rangoTiempoIngreso = Integer.parseInt(args[1]);
                int arith = Integer.parseInt(args[2]);
                int io = Integer.parseInt(args[3]);
                int cond = Integer.parseInt(args[4]);
                int loop = Integer.parseInt(args[5]);
                int quantum = Integer.parseInt(args[6]);
            } else {
                System.out.println("Comando no válido. Usa -sintaxis o -help para ver los comandos válidos.");
            }
        } else if (args.length == 1) {
            if (politica.equals("-sintaxis")) {
                System.out.println("Sintaxis válidas:");
                System.out.println("   java ProcessScheduler -fcfs rango_tiempo_ingreso arith io cond loop");
                System.out.println("   java ProcessScheduler -lcfs rango_tiempo_ingreso arith io cond loop");
                System.out.println("   java ProcessScheduler -pp   rango_tiempo_ingreso arith io cond loop");
                System.out.println("   java ProcessScheduler -rr   rango_tiempo_ingreso arith io cond loop quantum");
            } else if (politica.equals("-help")) {
                System.out.println("First-Come First-Served:\n   El primer proceso en llegar es el primero en ser atendido.");
                System.out.println("Last-Come First-Served:\n   El último proceso en llegar es el primero en ser atendido.");
                System.out.println("Priority Policy:\n   El proceso con mayor prioridad es atendido primero.");
                System.out.println("Round-Robin:\n   Cada proceso recibe una cantidad fija de tiempo (quantum) y se alternan en la ejecución.");
            } else {
                System.out.println("Comando no válido. Usa -sintaxis o -help para ver los comandos válidos.");
            }
        } else {
            System.out.println("Número de argumentos incorrecto. Usa -sintaxis o -help para ver los comandos válidos.");
        }
    }
}
