/*FCFS.java */
/**
** Hecho por: Adriel Levi Argueta Caal
** Carnet: 24003171.
** Seccion: BN.
**/

package scheduler.scheduling.policies;

import scheduler.processing.*;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

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
    private volatile boolean ejecutar; // Controla la ejecución del bucle principal

    private int procesosAtendidos;

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
        this.ejecutar = true;
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
            Random random = new Random();
            int id = 0;

            while (ejecutar) {
                try {
                    long tiempoRandom = (long) (tiempoMinimo * 1000 + random.nextDouble() * (tiempoMaximo - tiempoMinimo) * 1000);
                    Thread.sleep(tiempoRandom);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                id++;
                SimpleProcess nuevoProceso = generarProcesoAleatorio(id);
                add(nuevoProceso);
                System.out.println("Proceso generado: ID " + id + ", Tipo: " + determinarTipo(nuevoProceso));
                imprimirColas();
            }
        });

        Thread atencionProcesos = new Thread(() -> {
            while (ejecutar) {
                SimpleProcess procesoActual = next();
                if (procesoActual != null) {
                    System.out.println("Atendiendo proceso: ID " + procesoActual.getId() + ", Tipo: " + determinarTipo(procesoActual));
                    simularAtencionProceso(procesoActual);
                    procesosAtendidos++;
                    remove();
                    System.out.println("Proceso atendido: ID " + procesoActual.getId() + ", Tipo: " + determinarTipo(procesoActual));
                }
                imprimirColas();
            }
        });

        Thread detenerEjecucion = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (ejecutar) {
                String entrada = scanner.nextLine();
                if ("q".equalsIgnoreCase(entrada)) {
                    System.out.println("Finalizando la simulación...");
                    detener();
                    break;
                }
            }
            scanner.close();
        });

        generacionProcesos.start();
        atencionProcesos.start();
        detenerEjecucion.start();

        try {
            generacionProcesos.join();
            atencionProcesos.join();
            detenerEjecucion.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private SimpleProcess generarProcesoAleatorio(int id) {
        Random random = new Random();
        int tipoProceso = random.nextInt(4);

        switch (tipoProceso) {
            case 0:
                return new ArithmeticProcess(id, arithTime);
            case 1:
                return new IOProcess(id, ioTime);
            case 2:
                return new ConditionalProcess(id, condTime);
            case 3:
                return new LoopProcess(id, loopTime);
            default:
                throw new IllegalStateException("Tipo de proceso inesperado: " + tipoProceso);
        }
    }

    private void simularAtencionProceso(SimpleProcess proceso) {
        double tiempoServicio = 0.0;
        if (proceso instanceof ArithmeticProcess) {
            tiempoServicio = arithTime;
        } else if (proceso instanceof IOProcess) {
            tiempoServicio = ioTime;
        } else if (proceso instanceof ConditionalProcess) {
            tiempoServicio = condTime;
        } else if (proceso instanceof LoopProcess) {
            tiempoServicio = loopTime;
        }

        try {
            Thread.sleep((long) (tiempoServicio * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String determinarTipo(SimpleProcess proceso) {
        if (proceso instanceof ArithmeticProcess) {
            return "ArithmeticProcess";
        } else if (proceso instanceof IOProcess) {
            return "IOProcess";
        } else if (proceso instanceof ConditionalProcess) {
            return "ConditionalProcess";
        } else if (proceso instanceof LoopProcess) {
            return "LoopProcess";
        }
        return "Desconocido";
    }

    private void imprimirColas() {
        System.out.println("Cola de prioridad 1 (IOProcess): " + colaPrioridad1);
        System.out.println("Cola de prioridad 2 (ArithmeticProcess): " + colaPrioridad2);
        System.out.println("Cola de prioridad 3 (ConditionalProcess y LoopProcess): " + colaPrioridad3);
    }

    public void detener() {
        ejecutar = false;
    }
}
