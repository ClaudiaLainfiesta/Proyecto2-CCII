package scheduler.scheduling.policies;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import scheduler.processing.*;

public class FCFS extends Policy implements Enqueable {
    // Cola de procesos
    protected ConcurrentLinkedQueue<SimpleProcess> cola;
    protected int size;
    protected int totalProcesses;
    protected Double primeraParte;
    protected Double segundaParte;
    protected Double arith;
    protected Double io;
    protected Double cont;
    protected Double loop;

    // Contadores de procesos
    protected int procesosAtendidos;

    public FCFS(Double primeraParte, Double segundaParte, Double arith, Double io, Double cont, Double loop) {
        super();
        this.cola = new ConcurrentLinkedQueue<>();
        this.primeraParte = primeraParte;
        this.segundaParte = segundaParte;
        this.arith = arith;
        this.io = io;
        this.cont = cont;
        this.loop = loop;
        this.procesosAtendidos = 0;
    }

    // Enqueable: Agregar proceso
    @Override
    public void add(SimpleProcess P) {
        this.cola.add(P);
        this.size++;
        this.totalProcesses++;
    }

    // Enqueable: Eliminar proceso
    @Override
    public void remove() {
        this.cola.poll();
        this.size--;
    }

    // Enqueable: Obtener siguiente proceso
    @Override
    public SimpleProcess next() {
        return this.cola.peek();
    }

    // Método para ejecutar la simulación
    public void ejecucionSimple() {
        Random rand = new Random();
        
        // Hilo para la generación de procesos
        Thread generacionThread = new Thread(() -> {
            int idProceso = 0;
            try {
                while (true) {
                    // Escoger un rango de tiempo aleatorio entre 1 y 3 segundos para la simulación
                    int tiempoEspera = rand.nextInt(3) + 1;

                    // Pausa entre las iteraciones para simular el tiempo de espera
                    TimeUnit.SECONDS.sleep(tiempoEspera);

                    // Crear un proceso aleatorio con un ID correlativo
                    idProceso += 1;
                    SimpleProcess proceso = generarProcesoAleatorio(idProceso);
                    add(proceso); // Agregar el proceso a la cola

                    // Mostrar la información de la cola y los procesos
                    mostrarInformacion();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Hilo para la atención de los procesos
        Thread atencionThread = new Thread(() -> {
            try {
                while (true) {
                    // Si hay procesos en la cola, atenderlos
                    if (!cola.isEmpty()) {
                        SimpleProcess procesoAtendido = next();
                        atenderProceso(procesoAtendido);
                    } else {
                        // Si no hay procesos en la cola, esperar un poco antes de volver a intentar
                        TimeUnit.SECONDS.sleep(1);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Iniciar ambos hilos
        generacionThread.start();
        atencionThread.start();
    }

    // Generar proceso aleatorio de uno de los 4 tipos con ID correlativo
    private SimpleProcess generarProcesoAleatorio(int id) {
        Random rand = new Random();
        int tipo = rand.nextInt(4); // 4 tipos de procesos: Aritmetico, IO, Condicional, Iterativo

        switch (tipo) {
            case 0:
                SimpleProcess proceso = new ArithmeticProcess(id, arith);
                break;
            case 1:
                 SimpleProcess proceso = new IOProcess(id, io);
                break;
            case 2:
                SimpleProcess proceso = new ConditionalProcess(id, cont);
                break;
            default:
                SimpleProcess proceso = new LoopProcess(id, loop);
                break;
        }
        return proceso;
    }

    // Atender proceso
    private void atenderProceso(SimpleProcess proceso) throws InterruptedException {
        // Simulamos el tiempo de atención del proceso
        Double tiempoAtencion = casting(proceso);

        System.out.println("Atendiendo proceso ID: " + proceso.getId() + " Tipo: PENDIENTE" + " Tiempo de atención: " + tiempoAtencion + " segundos.");

        // El proceso se atiende durante su tiempo de atención
        long tiempoAtencionFinal = (long)(tiempoAtencion * 1000);
        Thread.sleep(tiempoAtencionFinal);

        // Se ha terminado de atender el proceso
        remove(); // Se elimina el proceso de la cola
        procesosAtendidos++;

        // Mostrar la información después de atender un proceso
        mostrarInformacion();
    }

    // Mostrar la información en la consola
    private void mostrarInformacion() {
        System.out.println("\n--- Estado Actual ---");
        // Mostrar la cola
        System.out.println("Cola de procesos: ");
        cola.forEach(proceso -> {
            Double tiempoAtencion = casting(proceso);
            System.out.println("ID: " + proceso.getId() + " Tipo: PENDIENTE" + " Tiempo de atención: " + tiempoAtencion);
        });

        // Mostrar los procesos atendidos
        System.out.println("\nProcesos atendidos: " + procesosAtendidos);
        System.out.println("Total de procesos: " + totalProcesses);
    }

    // Método para finalizar la simulación
    public void detenerSimulacion() {
        System.out.println("\nSimulación detenida.");
        System.out.println("Procesos atendidos: " + procesosAtendidos);
        System.out.println("Procesos en cola: " + size);
        System.out.println("Promedio de atención por proceso: " + calcularPromedioAtencion());
        System.out.println("Política utilizada: FCFS");
    }

    // Calcular el promedio de tiempo de atención por proceso
    private double calcularPromedioAtencion() {
        return (double) procesosAtendidos / totalProcesses;
    }

    private double casting(SimpleProcess proceso){
        Double tiempoServicio = 0.0;
        if(proceso instanceof ArithmeticProcess){
            tiempoServicio = ((ArithmeticProcess) proceso).getTiempoServicio();
        } else if(proceso instanceof IOProcess){
            tiempoServicio = ((IOProcess) proceso).getTiempoServicio();
        } else if(proceso instanceof ConditionalProcess){
            tiempoServicio = ((ConditionalProcess) proceso).getTiempoServicio();
        } else if(proceso instanceof LoopProcess){
            tiempoServicio = ((LoopProcess) proceso).getTiempoServicio();
        }
        return tiempoServicio;
    }
}
