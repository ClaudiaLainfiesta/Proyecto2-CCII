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
    private double tiempoMinimo;
    private double tiempoMaximo;
    private volatile boolean ejecutar; // Variable para controlar la ejecución de la simulación

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
        Scanner scanner = new Scanner(System.in);
        System.out.println("Iniciando simulación con política de Prioridad...");

        // Proceso para interrumpir con Q
        Thread listener = new Thread(() -> {
            System.out.println("Presione 'Q' para detener la simulación.");
            while (ejecutar) {
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("Q")) {
                    ejecutar = false;
                    System.out.println("Deteniendo la simulación...");
                }
            }
        });

        listener.start(); 

        long tiempoMaximoLong = (long) (this.tiempoMaximo);
        long tiempoMinimoLong = (long) (this.tiempoMinimo);

        int tiempoTranscurrido = 0;
        int id = 0;
        long tiempoParaNuevoProceso = tiempoMinimoLong + (long) (Math.random() * (tiempoMaximoLong - tiempoMinimoLong));

        while (ejecutar) {
            System.out.println("Tiempo transcurrido: " + tiempoTranscurrido + " ms");

            if (tiempoTranscurrido >= tiempoParaNuevoProceso) {
                id++;
                SimpleProcess nuevoProceso = generarProcesoAleatorio(id);
                add(nuevoProceso);
                System.out.println("Proceso agregado: ID " + nuevoProceso.getId() + ", tipo: " + nuevoProceso.getClass().getSimpleName());

                tiempoParaNuevoProceso = tiempoTranscurrido + tiempoMinimoLong + (long) (Math.random() * (tiempoMaximoLong - tiempoMinimoLong));
            }

            atenderProcesos();
            tiempoTranscurrido += 100;

            try {
                Thread.sleep(100); // Simular un intervalo de tiempo
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }

        try {
            listener.join(); // Esperar a que el hilo de escucha termine
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }

        System.out.println("Simulación finalizada.");
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

    private void atenderProcesos() {
        SimpleProcess procesoActual = next();
        if (procesoActual != null) {
            System.out.println("Atendiendo proceso ID " + procesoActual.getId() + " con prioridad " + determinarPrioridad(procesoActual));
            simularAtencionProceso(procesoActual);
            remove();
        }
    }

    private void simularAtencionProceso(SimpleProcess proceso) {
        double tiempoServicio;
        if (proceso instanceof ArithmeticProcess) {
            tiempoServicio = arithTime;
        } else if (proceso instanceof IOProcess) {
            tiempoServicio = ioTime;
        } else if (proceso instanceof ConditionalProcess) {
            tiempoServicio = condTime;
        } else if (proceso instanceof LoopProcess) {
            tiempoServicio = loopTime;
        } else {
            throw new IllegalArgumentException("Tipo de proceso desconocido");
        }

        long milisegundos = (long) (tiempoServicio * 1000);
        try {
            Thread.sleep(milisegundos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    private int determinarPrioridad(SimpleProcess proceso) {
        if (proceso instanceof IOProcess) {
            return 1;
        } else if (proceso instanceof ArithmeticProcess) {
            return 2;
        } else {
            return 3; // ConditionalProcess y LoopProcess
        }
    }
}
