/*FCFS.java */
/**
** Hecho por: María Claudia Lainfiesta Herrera
** Carnet: 24000149
** Seccion: BN
**/
//me dare de baja el otro año 
/*Descripcion: */
package scheduler.scheduling.policies;

import java.util.concurrent.ConcurrentLinkedQueue;
import scheduler.processing.SimpleProcess;


public class FCFS extends Policy implements Enqueable{
    //Policy
    protected ConcurrentLinkedQueue<SimpleProcess> cola;
    protected int size;
	protected int totalProcesses;

    public FCFS(){
        super();
        this.cola = new ConcurrentLinkedQueue<>();
    }

    //Enqueable
    @Override
    public void add(SimpleProcess P){
        this.cola.add(P);
        this.size++;
        this.totalProcesses++;
    }
    @Override
    public void remove(){
        this.cola.poll();
        this.size--;
    }
    @Override
    public SimpleProcess next(){
        return this.cola.peek();
    }
}
