/*FCFS.java */
/**
** Hecho por: María Claudia Lainfiesta Herrera
** Carnet: 24000149
** Seccion: BN
**/
/*Descripcion: */
package scheduler.scheduling.policies;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import scheduler.processing.*;


public class FCFS extends Policy implements Enqueable {
    protected ConcurrentLinkedQueue<SimpleProcess> cola;
    protected int size;
    protected int totalProcesses;
    protected Double primeraParte;
    protected Double segundaParte;
    protected Double arith;
    protected Double io;
    protected Double cont;
    protected Double loop;
    private static int idCounter = 1;
    
    private int totalAtendidos = 0; // Contador de procesos atendidos
    private long totalTime = 0; // Suma total de los tiempos de atención

    public FCFS(Double primeraParte, Double segundaParte, Double arith, Double io, Double cont, Double loop) {
        super();
        this.cola = new ConcurrentLinkedQueue<>();
        this.primeraParte = primeraParte;
        this.segundaParte = segundaParte;
        this.arith = arith;
        this.io = io;
        this.cont = cont;
        this.loop = loop;
    }

    @Override
    public void add(SimpleProcess P) {
        this.cola.add(P);
        this.size++;
        this.totalProcesses++;
        imprimirEstado();
    }

    @Override
    public void remove() {
        this.cola.poll();
        this.size--;
        imprimirEstado();
    }

    @Override
    public SimpleProcess next() {
        return this.cola.peek();
    }

    public void ejecucionSimple() {
        Scanner scanner = new Scanner(System.in);
        AtomicBoolean continuar = new AtomicBoolean(true); // Usamos AtomicBoolean para cambiar su valor

        // Hilo para agregar procesos aleatorios
        Thread generador = new Thread(() -> {
            Random random = new Random();
            while (continuar.get()) { // Mientras continuar sea true
                try {
                    long tiempoEspera = (long) (primeraParte * 1000 + random.nextDouble() * (segundaParte - primeraParte) * 1000);
                    Thread.sleep(tiempoEspera);

                    // Crear y agregar un nuevo proceso
                    SimpleProcess nuevoProceso = generarProcesoAleatorioConId();
                    add(nuevoProceso);
                    imprimirEstado(); // Mostrar estado después de agregar un proceso
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // Hilo para procesar los procesos
        Thread procesador = new Thread(() -> {
            while (continuar.get()) { // Mientras continuar sea true
                SimpleProcess proceso = cola.poll();
                if (proceso != null) {
                    Double tiempoAtencion = 0.0;

                    if (proceso instanceof ArithmeticProcess) {
                        tiempoAtencion = ((ArithmeticProcess) proceso).getTiempoServicio();
                    } else if (proceso instanceof IOProcess) {
                        tiempoAtencion = ((IOProcess) proceso).getTiempoServicio();
                    } else if (proceso instanceof ConditionalProcess) {
                        tiempoAtencion = ((ConditionalProcess) proceso).getTiempoServicio();
                    } else if (proceso instanceof LoopProcess) {
                        tiempoAtencion = ((LoopProcess) proceso).getTiempoServicio();
                    }

                    long tiempoAtencionMs = tiempoAtencion.longValue();

                    try {
                        System.out.println("Atendiendo proceso con ID " + proceso.getId() + " por " + tiempoAtencionMs + " ms");
                        Thread.sleep(tiempoAtencionMs);
                        totalAtendidos++;
                        totalTime += tiempoAtencionMs;
                        System.out.println("Proceso con ID " + proceso.getId() + " terminado.");
                        imprimirEstado(); // Mostrar el estado después de procesar un proceso
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // Hilo para escuchar la entrada del usuario
        Thread listener = new Thread(() -> {
            while (continuar.get()) {
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("q")) {
                    System.out.println("Finalizando programa...");
                    continuar.set(false); // Cambiar el valor de continuar a false
                    generador.interrupt();
                    procesador.interrupt();
                }
            }
            scanner.close();
        });

        // Iniciar los hilos
        generador.start();
        procesador.start();
        listener.start();
    }

    private SimpleProcess generarProcesoAleatorioConId() {
        Random random = new Random();
        int tipo = random.nextInt(4);
        SimpleProcess proceso;

        synchronized (FCFS.class) {
            int currentId = idCounter++;
            switch (tipo) {
                case 0:
                    proceso = new ArithmeticProcess(currentId, arith);
                    break;
                case 1:
                    proceso = new IOProcess(currentId, io);
                    break;
                case 2:
                    proceso = new ConditionalProcess(currentId, cont);
                    break;
                case 3:
                    proceso = new LoopProcess(currentId, loop);
                    break;
                default:
                    throw new IllegalStateException("Tipo de proceso no válido");
            }
        }

        return proceso;
    }

    // Imprimir estado de la cola y procesos
    private void imprimirEstado() {
        System.out.println("La cola de procesos:");
        for (SimpleProcess proceso : cola) {
            String tipo = "";
            Double tiempoAtencion = 0.0;
            if (proceso instanceof ArithmeticProcess) {
                tiempoAtencion = ((ArithmeticProcess) proceso).getTiempoServicio();
                tipo = "A";
            } else if (proceso instanceof IOProcess) {
                tiempoAtencion = ((IOProcess) proceso).getTiempoServicio();
                tipo = "IO";
            } else if (proceso instanceof ConditionalProcess) {
                tiempoAtencion = ((ConditionalProcess) proceso).getTiempoServicio();
                tipo = "C";
            } else if (proceso instanceof LoopProcess) {
                tiempoAtencion = ((LoopProcess) proceso).getTiempoServicio();
                tipo = "L";
            }
            System.out.println("ID: " + proceso.getId() + ", Tipo: " + tipo + ", Tiempo de atención: " + tiempoAtencion + " ms");
        }

        // Mostrar proceso en ejecución
        SimpleProcess procesoEnEjecucion = next();
        if (procesoEnEjecucion != null) {
            System.out.println("Proceso en ejecución: " + procesoEnEjecucion);
        }

        // Mostrar política y estadísticas
        System.out.println("Política utilizada: FCFS");
        System.out.println("Número de procesos atendidos: " + totalAtendidos);
        System.out.println("----------------------------");
    }

    private void terminarSimulacion() {
        // Información final
        System.out.println("\nSimulación terminada.");
        System.out.println("Procesos atendidos: " + totalAtendidos);
        System.out.println("Procesos restantes en cola: " + cola.size());
        if (totalAtendidos > 0) {
            System.out.println("Tiempo promedio de atención por proceso: " + (totalTime / totalAtendidos) + " ms");
        }
        System.out.println("Política utilizada: FCFS");
        System.exit(0); // Salir del programa
    }
}


