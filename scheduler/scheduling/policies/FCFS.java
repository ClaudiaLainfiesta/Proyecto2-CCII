/*FCFS.java */
/**
** Hecho por: María Claudia Lainfiesta Herrera
** Carnet: 24000149
** Seccion: BN
**/
/*Descripcion: */
package scheduler.scheduling.policies;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import scheduler.processing.*;



public class FCFS extends Policy implements Enqueable{
    //Policy
    protected ConcurrentLinkedQueue<SimpleProcess> cola;
    protected int size;
	protected int totalProcesses;
    protected Double primeraParte;
    protected Double segundaParte;
    protected Double arith;
    protected Double io;
    protected Double cont;
    protected Double loop;

    public FCFS(Double primeraParte, Double segundaParte, Double arith, Double io, Double cont, Double loop){
        super();
        this.cola = new ConcurrentLinkedQueue<>();
        this.primeraParte = primeraParte;
        this.segundaParte = segundaParte;
        this.arith = arith;
        this.io = io;
        this.cont = cont;
        this.loop = loop;

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
    // Ejecucion
    public void ejecucion() {
    System.out.println("Iniciando ciclo de ejecución...");
    Random random = new Random();
    int tiempoTranscurrido = 0;
    int id = 0;
    
    Double tiempoParaNuevoProceso = random.nextDouble(this.segundaParte - this.primeraParte + 1) + this.primeraParte;

    while (true) {
        System.out.println("Tiempo transcurrido: " + tiempoTranscurrido);
        
        if (tiempoTranscurrido >= tiempoParaNuevoProceso) {
            id++;
            SimpleProcess nuevoProceso = generarProcesoAleatorio(id);
            add(nuevoProceso);
            System.out.println("Proceso agregado: " + nuevoProceso.getId());

            tiempoParaNuevoProceso = tiempoTranscurrido + random.nextDouble(this.segundaParte - this.primeraParte + 1) + this.primeraParte;
        }

        atencionProcesos();
        tiempoTranscurrido++;
    }
}


    private SimpleProcess generarProcesoAleatorio(int id) {
        Random random = new Random();
        int tipoProceso = random.nextInt(4);
        SimpleProcess proceso;
        
        switch (tipoProceso) {
            case 0:
                proceso = new ArithmeticProcess(id, arith);
                break;
            case 1:
                proceso = new ConditionalProcess(id, cont);
                break;
            case 2:
                proceso = new IOProcess(id, io);
                break;
            case 3:
                proceso = new LoopProcess(id, loop);
                break;
            default:
                throw new IllegalStateException("Tipo de proceso inesperado: " + tipoProceso);
        }
        return proceso;
    }

    public void atencionProcesos() {
        SimpleProcess procesoActual = next();
        if (procesoActual != null) {
            System.out.println("Atendiendo proceso No." + procesoActual.getId());
            simuladorProceso(procesoActual);
            remove();
        }
    }

    private void simuladorProceso(SimpleProcess proceso) {
        Double tiempoEspera;
        if (proceso instanceof ArithmeticProcess) {
            tiempoEspera = arith;
        } else if (proceso instanceof ConditionalProcess) {
            tiempoEspera = cont;
        } else if (proceso instanceof IOProcess) {
            tiempoEspera = io;
        } else if (proceso instanceof LoopProcess) {
            tiempoEspera = loop;
        } else {
            throw new IllegalArgumentException("Tipo de proceso desconocido");
        }
        long milisegundos = (long) (tiempoEspera * 1000);
        try {
            Thread.sleep(milisegundos);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
