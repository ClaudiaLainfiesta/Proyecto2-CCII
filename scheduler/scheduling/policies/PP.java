/*PP.java */
/**
** Hecho por: Adriel Levi Argueta Caal
**Carnet: 24003171
**Seccion: BN
**/
/*Descripcion */
package scheduler.scheduling.policies;

import java.util.concurrent.ConcurrentLinkedQueue;
import scheduler.processing.SimpleProcess;
import scheduler.scheduling.Policy;
import scheduler.scheduling.Enqueable;
import java.util.Random;
import scheduler.processing.*;

public class PP extends Policy implements Enqueable {
    private final ConcurrentLinkedQueue<SimpleProcess> colaPrioridad1; // IOProcess
    private final ConcurrentLinkedQueue<SimpleProcess> colaPrioridad2; // ArithmeticProcess
    private final ConcurrentLinkedQueue<SimpleProcess> colaPrioridad3; // ConditionalProcess y LoopProcess
    
    private final double arithTime;
    private final double ioTime;
    private final double condTime;
    private final double loopTime;

    public PP(double arithTime, double ioTime, double condTime, double loopTime) {
        super();
        this.colaPrioridad1 = new ConcurrentLinkedQueue<>();
        this.colaPrioridad2 = new ConcurrentLinkedQueue<>();
        this.colaPrioridad3 = new ConcurrentLinkedQueue<>();
        this.arithTime = arithTime;
        this.ioTime = ioTime;
        this.condTime = condTime;
        this.loopTime = loopTime;
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
        System.out.println("Iniciando simulación con política de Prioridad...");
        Random random = new Random();
        int tiempoTranscurrido = 0;
        int id = 0;

        double tiempoParaNuevoProceso = random.nextDouble(500) + 500;

        while (true) {
            System.out.println("Tiempo transcurrido: " + tiempoTranscurrido + " ms");

            if (tiempoTranscurrido >= tiempoParaNuevoProceso) {
                id++;
                SimpleProcess nuevoProceso = generarProcesoAleatorio(id);
                add(nuevoProceso);
                System.out.println("Proceso agregado: ID " + nuevoProceso.getId() + ", tipo: " + nuevoProceso.getClass().getSimpleName());

                tiempoParaNuevoProceso = tiempoTranscurrido + random.nextDouble(500) + 500;
            }

            atenderProcesos();
            tiempoTranscurrido += 100;
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
