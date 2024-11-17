/*FCFS.java */
/**
** Hecho por: Adriel Levi Argueta Caal
** Carnet: 24003171.
** Seccion: BN.
**/
package scheduler.scheduling.policies;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import scheduler.processing.*;

public class PP extends Policy implements Enqueable {
    private final ConcurrentLinkedQueue<SimpleProcess> colaPrioridad1; // IOProcess
    private final ConcurrentLinkedQueue<SimpleProcess> colaPrioridad2; // ArithmeticProcess
    private final ConcurrentLinkedQueue<SimpleProcess> colaPrioridad3; // ConditionalProcess y LoopProcess

    private final double arithTime;
    private final double ioTime;
    private final double condTime;
    private final double loopTime;
    private final double tiempoMinimo;
    private final double tiempoMaximo;
    private static int idGeneradoGlobal = 0;
    private volatile boolean running; // Controla la ejecución principal
    private int procesosAtendidos;
    private double totalTiempoAtencion = 0.0;

    public PP(double tiempoMinimo, double tiempoMaximo, double arithTime, double ioTime, double condTime, double loopTime) {
        super();
        this.colaPrioridad1 = new ConcurrentLinkedQueue<>();
        this.colaPrioridad2 = new ConcurrentLinkedQueue<>();
        this.colaPrioridad3 = new ConcurrentLinkedQueue<>();
        this.arithTime = arithTime;
        this.ioTime = ioTime;
        this.condTime = condTime;
        this.loopTime = loopTime;
        this.tiempoMinimo = tiempoMinimo;
        this.tiempoMaximo = tiempoMaximo;
        this.running = true;
        this.procesosAtendidos = 0;
    }

    @Override
    public void add(SimpleProcess process) {
        if (process instanceof IOProcess) {
            colaPrioridad1.add(process);
        } else if (process instanceof ArithmeticProcess) {
            colaPrioridad2.add(process);
        } else if (process instanceof ConditionalProcess || process instanceof LoopProcess) {
            colaPrioridad3.add(process);
        }
        size++;
        totalProcesses++;
    }

    @Override
    public void remove() {
        if (!colaPrioridad1.isEmpty()) {
            colaPrioridad1.poll();
        } else if (!colaPrioridad2.isEmpty()) {
            colaPrioridad2.poll();
        } else if (!colaPrioridad3.isEmpty()) {
            colaPrioridad3.poll();
        }
        size--;
    }

    @Override
    public SimpleProcess next() {
        if (!colaPrioridad1.isEmpty()) {
            return colaPrioridad1.peek();
        } else if (!colaPrioridad2.isEmpty()) {
            return colaPrioridad2.peek();
        } else {
            return colaPrioridad3.peek();
        }
    }

    public void ejecucion() {
        Thread generacionProcesos = new Thread(() -> {
            int idGenerado = 0;
            Random randTiempo = new Random();
            Random randProceso = new Random();

            while (running) {
                long tiempoRandomLong = (long) (tiempoMinimo * 1000) +
                                        randTiempo.nextLong() % (long) ((tiempoMaximo - tiempoMinimo) * 1000 + 1);

                try {
                    Thread.sleep(tiempoRandomLong);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                int procesoEleccion = randProceso.nextInt(4);
                idGenerado++;
                SimpleProcess procesoGenerado = generarProcesoAleatorio(idGenerado, procesoEleccion);
                add(procesoGenerado);

                System.out.println("Generado proceso ID: " + idGenerado + " Tipo: " + determinarTipo(procesoGenerado));
                imprimirCola();
            }
        });

        Thread atencionProcesos = new Thread(() -> {
            while (running) {
                SimpleProcess procesoAtender = next();
                if (procesoAtender == null) continue;

                double tiempoAtencion = obtenerTiempoDeServicio(procesoAtender);
                System.out.println("\nAtendiendo proceso ID: " + procesoAtender.getId() +
                                   " Tipo: " + determinarTipo(procesoAtender) +
                                   " Tiempo de atención: " + tiempoAtencion + " segundos.");

                try {
                    Thread.sleep((long) (tiempoAtencion * 1000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                totalTiempoAtencion += tiempoAtencion;
                procesosAtendidos++;
                remove();

                System.out.println("\nAtendido proceso ID: " + procesoAtender.getId() +
                                   " Tipo: " + determinarTipo(procesoAtender));
                imprimirCola();
            }
        });

        Thread recibirSalida = new Thread(() -> {
            Scanner teclado = new Scanner(System.in);
            try {
                while (running) {
                    System.out.println("Escribe 'q' para salir:");
                    String salida = teclado.nextLine();
                    if (salida.equals("q")) {
                        stopRunning();
                        break;
                    }
                }
            } finally {
                teclado.close();
            }
        });

        generacionProcesos.start();
        atencionProcesos.start();
        recibirSalida.start();

        try {
            generacionProcesos.join();
            atencionProcesos.join();
            recibirSalida.join();
        } catch (InterruptedException e) {
            System.out.println("Hubo un problema de sincronización.");
        }
    }

    public void ejecucionDoble(){
        Thread generacionProcesos = new Thread(() -> {
            int idGenerado = 0;
            Random randTiempo = new Random();
            Random randProceso = new Random();        

            while(running){
                long tiempoRandomLong = (long) ( tiempoMinimo * 1000) +
                                        randProceso.nextLong() % (long) ((tiempoMaximo - tiempoMinimo) * 1000 + 1);
                try{
                    Thread.sleep(tiempoRandomLong);
                } catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                    break;
                }
                int procesoEleccion = randProceso.nextInt(4);
                idGenerado++;
                SimpleProcess procesoGenerado = generarProcesoAleatorio(idGenerado, procesoEleccion);
                add(procesoGenerado);

                System.out.println("Generado proceso ID: " + idGenerado + "Tipo:" + determinarTipo(procesoGenerado));
                imprimirCola();
            }
        });

        Thread atencionProcesos = new Thread(() ->{
            while(running){
                SimpleProcess procesoAtender = next();
                if(procesoAtender == null)continue;
                double tiempoAtencion = obtenerTiempoDeServicio(procesoAtender);
                System.out.println("\nAtendiendo proceso ID:" + procesoAtender.getId() + 
                                   "Tipo: " + determinarTipo(procesoAtender) +
                                   "Tiempo de atencion: " + tiempoAtencion + "segundo.");
                try{
                    Thread.sleep((long) (tiempoAtencion * 1000));
                } catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                    break;
                }
                
                totalTiempoAtencion += tiempoAtencion;
                procesosAtendidos++;
                remove();

                System.out.println("\nAtendido proceso ID: " + procesoAtender.getId() + 
                                   "Tipo: " + determinarTipo(procesoAtender));
            }
        });
        Thread atencionProceso2 = new Thread(() -> {
            while(running){
                SimpleProcess procesoAtender = next();
                if(procesoAtender == null) continue;

                double tiempoAtencion = obtenerTiempoDeServicio(procesoAtender);
            System.out.println("\nAtendiendo proceso ID: " + procesoAtender.getId() + 
                               "Tipo : " + tiempoAtencion + "segundo.");
            
            try {
                Thread.sleep((long)(tiempoAtencion * 1000));
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
                break;
            }

            totalTiempoAtencion += tiempoAtencion;
            procesosAtendidos++;
            remove();

            System.out.println("\nAtendido proceso ID: " + procesoAtender.getId() + 
                               "Tipo: " + determinarTipo(procesoAtender));
            imprimirCola();                   
            }
        });  

        Thread recibirSalida = new Thread(() -> {
            Scanner teclado = new Scanner(System.in);
            try {
                while (running) {
                    System.out.println("Escribe 'q' para salir:");
                    String salida = teclado.nextLine();
                    if (salida.equals("q")) {
                        stopRunning();
                        break;
                    }
                }
            } finally {
                teclado.close();
            }
        });       
        
        generacionProcesos.start();
        atencionProcesos.start();
        atencionProceso2.start();
        recibirSalida.start();

        try {
            generacionProcesos.join();
            atencionProceso2.join();
            atencionProcesos.join();
            recibirSalida.join();
        } catch (InterruptedException e){
            System.out.println("Hubo im problema en la sincronizacion.");
        }
    }

    private SimpleProcess generarProcesoAleatorio(int id, int tipoProceso) {
        switch (tipoProceso) {
            case 0: return new ArithmeticProcess(id, arithTime);
            case 1: return new IOProcess(id, ioTime);
            case 2: return new ConditionalProcess(id, condTime);
            case 3: return new LoopProcess(id, loopTime);
            default: throw new IllegalStateException("Tipo de proceso inesperado: " + tipoProceso);
        }
    }

    private double obtenerTiempoDeServicio(SimpleProcess proceso) {
        if (proceso instanceof ArithmeticProcess) return arithTime;
        if (proceso instanceof IOProcess) return ioTime;
        if (proceso instanceof ConditionalProcess) return condTime;
        if (proceso instanceof LoopProcess) return loopTime;
        return 0.0;
    }

    private String determinarTipo(SimpleProcess proceso) {
        if (proceso instanceof ArithmeticProcess) return "ArithmeticProcess";
        if (proceso instanceof IOProcess) return "IOProcess";
        if (proceso instanceof ConditionalProcess) return "ConditionalProcess";
        if (proceso instanceof LoopProcess) return "LoopProcess";
        return "Desconocido";
    }

    private void imprimirCola() {
        System.out.println("Cola de prioridad 1 (IOProcess): " + colaPrioridad1);
        System.out.println("Cola de prioridad 2 (ArithmeticProcess): " + colaPrioridad2);
        System.out.println("Cola de prioridad 3 (ConditionalProcess y LoopProcess): " + colaPrioridad3);
    }

    private synchronized int generarNuevoID(){
        return ++idGeneradoGlobal;
    }

    public void stopRunning() {
        running = false;

        double tiempoPromedio = (procesosAtendidos > 0) ? (totalTiempoAtencion / procesosAtendidos) : 0;
        System.out.println("\n--------Datos finales--------");
        System.out.println("Procesos atendidos: " + procesosAtendidos);
        System.out.println("Tiempo promedio de atención por proceso: " + tiempoPromedio + " segundos");
        System.out.println("Política utilizada: Priority Policy (PP)");
        System.exit(0);
        
    }
}
