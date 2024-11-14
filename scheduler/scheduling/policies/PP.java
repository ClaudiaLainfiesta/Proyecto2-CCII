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

public class PP extends Policy implements Enqueable {

    private ConcurrentLinkedQueue<SimpleProcess> priority1Queue; 
    private ConcurrentLinkedQueue<SimpleProcess> priority2Queue; 
    private ConcurrentLinkedQueue<SimpleProcess> priority3Queue; 

    public PP() {
        priority1Queue = new ConcurrentLinkedQueue<>();
        priority2Queue = new ConcurrentLinkedQueue<>();
        priority3Queue = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void enqueueProcess(SimpleProcess process) {
        switch (process.getProcessType()) {
            case "IOProcess":
                priority1Queue.offer(process);
                break;
            case "ArithmeticProcess":
                priority2Queue.offer(process);
                break;
            case "ConditionalProcess":
            case "LoopProcess":
                priority3Queue.offer(process);
                break;
            default:
                throw new IllegalArgumentException("Tipo de proceso desconocido");
        }
    }

    @Override
    public SimpleProcess dequeueProcess() {
        if (!priority1Queue.isEmpty()) {
            return priority1Queue.poll();
        } else if (!priority2Queue.isEmpty()) {
            return priority2Queue.poll();
        } else {
            return priority3Queue.poll();
        }
    }

    @Override
    public void executePolicy() {
        SimpleProcess process;
        while ((process = dequeueProcess()) != null) {
            System.out.println("Atendiendo proceso: " + process.getId() + " - Tipo: " + process.getProcessType());
            try {
                Thread.sleep(process.getProcessingTime());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Error en la simulación de proceso");
            }
        }
    }
}

