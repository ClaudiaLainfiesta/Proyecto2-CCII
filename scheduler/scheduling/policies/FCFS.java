/*FCFS.java */
/**
** Hecho por: Maria Claudia Lainfiesta Herrera.
** Carnet: 24000149.
** Seccion: BN.
**/
/*Descripción: Clase que realiza la política First Come First Served, los procesos son atendidos en el orden en que llegan a la cola de procesos.*/

package scheduler.scheduling.policies;

/*Librerías utilizadas dentro del programa */
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import scheduler.processing.*;

public class FCFS extends Policy implements Enqueable {

    //**************************** Campos ****************************

    protected ConcurrentLinkedQueue<SimpleProcess> cola;
    protected Double primeraParte;
    protected Double segundaParte;
    protected Double arith;
    protected Double io;
    protected Double cond;
    protected Double loop;
    protected int procesosAtendidos = 0;
    protected int procesosAtendidos2 = 0;
    private boolean running;
    private double totalTiempoAtencion = 0.0;
    private double totalTiempoAtencion2 = 0.0;
    private static int idGeneradoGlobal = 0;

    //************************* Constructor *************************

    /**
     * Constructor que inicializa los parámetros para la política de FCFS.
     * @param primeraParte tiempo mínimo de agregar procesos.
     * @param segundaParte tiempo máximo de agregar procesos.
     * @param arith tiempo de atención de procesos aritméticos.
     * @param io tiempo de atención de procesos input/output.
     * @param cond tiempo de atención de procesos condicionales.
     * @param loop tiempo de atención de procesos iterativos.
     */
    public FCFS(Double primeraParte, Double segundaParte, Double arith, Double io, Double cond, Double loop) {
        super();
        this.cola = new ConcurrentLinkedQueue<>();
        this.primeraParte = primeraParte;
        this.segundaParte = segundaParte;
        this.arith = arith;
        this.io = io;
        this.cond = cond;
        this.loop = loop;
        this.running = true;
    }

    //******************** Métodos implementados ********************

    /**
     * Nombre: add.
     * Método que agrega un proceso a la cola.
     * @param P proceso a encolar.
     */
    @Override
    public void add(SimpleProcess P) {
        this.cola.add(P);
        this.size++;
        this.totalProcesses++;
    }

    /**
     * Nombre: remove.
     * Método que remueve de la cola el que sigue.
     */
    @Override
    public void remove() {
        this.cola.poll();
        this.size--;
    }

    /**
     * Nombre:next.
     * Método que obtiene el siguiente proceso en la cola.
     * @return proceso que sigue.
     */
    @Override
    public SimpleProcess next() {
        return this.cola.peek();
    }

    //********************* Métodos principales *********************

    /**
     * Nombre: ejecucionSimple
     * Método que realiza la ejecución con un solo procesador de la política FCFS, en donde se atiende y generan procesos al mismo tiempo.
     * @return ejecución del programa con política FCFS.
     */
    public void ejecucionSimple() {
        Object lock = new Object();
        Thread generacionProcesos = new Thread(() -> {
            while(running){
                long tiempoSleep = tiempoAleatorioRango();
                try{
                    Thread.sleep(tiempoSleep);
                }catch(InterruptedException e){
                    System.out.println("Proceso interrumpido");
                }
                synchronized (lock) {
                    int idGenerado = generarNuevoID();
                    SimpleProcess procesoGenerado = procesoAleatorio(idGenerado);
                    String tipoProcesoGenerado = castingTipo(procesoGenerado);
                    Double tiempoAtencionProcesoGenerado = castingTiempoAtencion(procesoGenerado);
                    add(procesoGenerado);
                    System.out.println();
                    System.out.println("Generado proceso -> ID: " + idGenerado + " | Tiempo de Atencion: " + tiempoAtencionProcesoGenerado + " | Tipo: " + tipoProcesoGenerado);
                    imprimirCola();
                    System.out.println();
                }
            }
        });

        Thread atencionProcesos = new Thread(() -> {
            while (running) {
                SimpleProcess procesoAtender;
                synchronized (lock) {
                    procesoAtender = next();
                    if (procesoAtender != null) {
                        remove();
                    }
                }
                if (procesoAtender == null) continue;
                int idProceso = castingID(procesoAtender);
                Double tiempoAtencionProceso = castingTiempoAtencion(procesoAtender);
                String tipoProceso = castingTipo(procesoAtender);
                long tiempoAtencionProcesoMs = (long) (tiempoAtencionProceso * 1000);
                synchronized (lock) {
                    System.out.println();
                    System.out.println("\nProcesador: Atendiendo proceso -> ID: " + idProceso + " | Tipo: " + tipoProceso + " | Tiempo restante: " + tiempoAtencionProceso + " seg.");
                    imprimirCola();
                    System.out.println();
                }
                try {
                    Thread.sleep(tiempoAtencionProcesoMs);
                } catch (InterruptedException e) {
                    System.out.println("Procesador: Proceso interrumpido");
                }
                synchronized (lock) {
                    System.out.println();
                    System.out.println("Procesador: Atendido proceso -> ID: " + idProceso + " | Tipo: " + tipoProceso + " | Tiempo de Atencion: " + tiempoAtencionProceso + " seg.");
                    totalTiempoAtencion += tiempoAtencionProceso;
                    procesosAtendidos++;
                    System.out.println("Total de procesos atendidos hasta el momento: " + this.procesosAtendidos + ".");
                    imprimirCola();
                    System.out.println();
                }
            }
        });

        Thread recibirSalida = new Thread(() -> {
            Scanner teclado = new Scanner(System.in);
            while (running) {
                String salida = teclado.nextLine();
                if (salida.equals("q")) {
                    stopRunning(1);
                    break;
                }
            }
            teclado.close();
        });

        generacionProcesos.start();
        atencionProcesos.start();
        recibirSalida.start();
        try {
            generacionProcesos.join();
            atencionProcesos.join();
            recibirSalida.join();
        } catch (InterruptedException e) {
            System.out.println("Hubo un problema de sincronizacion.");
        }
    }

    /**
     * Nombre: ejecucionDoble
     * Método que realiza la ejecución con dos procesadores de la política FCFS, en donde se atiende y generan procesos al mismo tiempo.
     * @return ejecución del programa con política FCFS con dos procesadores.
     */
    public void ejecucionDoble() {
        Object lock = new Object();
        Thread generacionProcesos = new Thread(() -> {
            while(running){
                long tiempoSleep = tiempoAleatorioRango();
                try{
                    Thread.sleep(tiempoSleep);
                }catch(InterruptedException e){
                    System.out.println("Proceso interrumpido");
                }
                synchronized (lock) {
                int idGenerado = generarNuevoID();
                SimpleProcess procesoGenerado = procesoAleatorio(idGenerado);
                String tipoProcesoGenerado = castingTipo(procesoGenerado);
                Double tiempoAtencionProcesoGenerado = castingTiempoAtencion(procesoGenerado);
                add(procesoGenerado);
                    System.out.println();
                    System.out.println("Generado proceso -> ID: " + idGenerado + " | Tiempo de Atencion: " + tiempoAtencionProcesoGenerado + " | Tipo: " + tipoProcesoGenerado);
                    imprimirCola();
                    System.out.println();
                }
            }
        });

        Thread atencionProcesos1 = new Thread(() -> {
            while (running) {
                SimpleProcess procesoAtender;
                synchronized (lock) {
                    procesoAtender = next();
                    if (procesoAtender != null) {
                        remove();
                    }
                }
                if (procesoAtender == null) continue;
                int idProceso = castingID(procesoAtender);
                Double tiempoAtencionProceso = castingTiempoAtencion(procesoAtender);
                String tipoProceso = castingTipo(procesoAtender);
                long tiempoAtencionProcesoMs = (long) (tiempoAtencionProceso * 1000);
                synchronized (lock) {
                    System.out.println();
                    System.out.println("\nProcesador 1: Atendiendo proceso -> ID: " + idProceso + " | Tipo: " + tipoProceso + " | Tiempo restante: " + tiempoAtencionProceso + " seg.");
                    imprimirCola();
                    System.out.println();
                }
                try {
                    Thread.sleep(tiempoAtencionProcesoMs);
                } catch (InterruptedException e) {
                    System.out.println("Procesador 1: Proceso interrumpido");
                }
                synchronized (lock) {
                    System.out.println();
                    System.out.println("Procesador 1: Atendido proceso -> ID: " + procesoAtender.getId() + " | Tipo: " + tipoProceso + " | Tiempo de Atencion: " + tiempoAtencionProceso + " seg.");
                    totalTiempoAtencion += tiempoAtencionProceso;
                    procesosAtendidos++;
                    int totalDosProcesosAtendidos = procesosAtendidos + procesosAtendidos2;
                    System.out.println("Total de procesos atendidos hasta el momento: " + totalDosProcesosAtendidos + ".");
                    imprimirCola();
                    System.out.println();
                }
            }
        });

        Thread atencionProcesos2 = new Thread(() -> {
            while (running) {
                SimpleProcess procesoAtender;
                synchronized (lock) {
                    procesoAtender = next();
                    if (procesoAtender != null) {
                        remove();
                    }
                }
                if (procesoAtender == null) continue;
                int idProceso = castingID(procesoAtender);
                Double tiempoAtencionProceso = castingTiempoAtencion(procesoAtender);
                String tipoProceso = castingTipo(procesoAtender);
                long tiempoAtencionProcesoMs = (long) (tiempoAtencionProceso * 1000);
                synchronized (lock) {
                    System.out.println();
                    System.out.println("\nProcesador 2: Atendiendo proceso -> ID: " + idProceso + " | Tipo: " + tipoProceso + " | Tiempo restante: " + tiempoAtencionProceso + " seg.");
                    imprimirCola();
                    System.out.println();
                }
                try {
                    Thread.sleep(tiempoAtencionProcesoMs);
                } catch (InterruptedException e) {
                    System.out.println("Procesador 2: Proceso interrumpido");
                }
                synchronized (lock) {
                    System.out.println();
                    System.out.println("Procesador 2: Atendido proceso -> ID: " + procesoAtender.getId() + " | Tipo: " + tipoProceso + " | Tiempo de Atencion: " + tiempoAtencionProceso + " seg.");
                    totalTiempoAtencion2 += tiempoAtencionProceso;
                    procesosAtendidos2++;
                    int totalDosProcesosAtendidos = procesosAtendidos + procesosAtendidos2;
                    System.out.println("Total de procesos atendidos hasta el momento: " + totalDosProcesosAtendidos + ".");
                    imprimirCola();
                    System.out.println();
                }
            }
        });

        Thread recibirSalida = new Thread(() -> {
            Scanner teclado = new Scanner(System.in);
            while (running) {
                String salida = teclado.nextLine();
                if (salida.equals("q")) {
                    stopRunning(2);
                    break;
                }
            }
            teclado.close();
        });

        generacionProcesos.start();
        atencionProcesos1.start();
        atencionProcesos2.start();
        recibirSalida.start();
        try {
            generacionProcesos.join();
            atencionProcesos1.join();
            atencionProcesos2.join();
            recibirSalida.join();
        } catch (InterruptedException e) {
            System.out.println("Hubo un problema de sincronizacion.");
        }
    }

    //********************* Métodos secundarios *********************

    /**
     * Nombre: procesoAleatorio.
     * Método que generara un tipo de proceso aleatorio.
     * @param idGenerado id del nuevo proceso generado.
     * @return proceso generado.
     */
    public SimpleProcess procesoAleatorio(int idGenerado){
        Random randProceso = new Random();
        int procesoEleccion = randProceso.nextInt(4);
        SimpleProcess procesoGenerado = null;
        if (procesoEleccion == 0) {
            procesoGenerado = new ArithmeticProcess(idGenerado, this.arith);
        } else if (procesoEleccion == 1) {
            procesoGenerado = new IOProcess(idGenerado, this.io);
        } else if (procesoEleccion == 2) {
            procesoGenerado = new ConditionalProcess(idGenerado, this.cond);
        } else if (procesoEleccion == 3) {
            procesoGenerado = new LoopProcess(idGenerado, this.loop);
        }
        return procesoGenerado;
    }

    /**
     * Nombre: tiempoAleatorioRango.
     * Método que generara un tiempo de proceso aleatorio dentro del rango.
     * @return tiempo aleatorio generado.
     */
    public long tiempoAleatorioRango(){
        Random randTiempo = new Random();
        long minimoTiempoLong = (long) (this.primeraParte * 1000);
        long maximoTiempoLong = (long) (this.segundaParte * 1000);
        long tiempoRandomLong = minimoTiempoLong + randTiempo.nextLong() % (maximoTiempoLong - minimoTiempoLong + 1);
        return tiempoRandomLong;
    }

    /**
     * Nombre: castingTipo.
     * Método en el que castea en cada tipo de proceso y obtiene el tipo de proceso que es.
     * @param proceso proceso al que se casteara.
     * @return tipo de proceso.
     */
    private String castingTipo(SimpleProcess proceso){
        String tipo = "";
        if(proceso instanceof ArithmeticProcess){
            tipo = ((ArithmeticProcess) proceso).getTipo();
        } else if(proceso instanceof IOProcess){
            tipo = ((IOProcess) proceso).getTipo();
        } else if(proceso instanceof ConditionalProcess){
            tipo = ((ConditionalProcess) proceso).getTipo();
        } else if(proceso instanceof LoopProcess){
            tipo = ((LoopProcess) proceso).getTipo();
        }
        return tipo;
    }

    /**
     * Nombre: castingString.
     * Método en el que castea en cada tipo de proceso y su modo de impresión (toString).
     * @param proceso proceso al que se casteara.
     * @return string del proceso.
     */
    private String castingString(SimpleProcess proceso){
        String texto = "";
        if(proceso instanceof ArithmeticProcess){
            texto = ((ArithmeticProcess) proceso).toString();
        } else if(proceso instanceof IOProcess){
            texto = ((IOProcess) proceso).toString();
        } else if(proceso instanceof ConditionalProcess){
            texto = ((ConditionalProcess) proceso).toString();
        } else if(proceso instanceof LoopProcess){
            texto = ((LoopProcess) proceso).toString();
        }
        return texto;
    }

    /**
     * Nombre: castingTiempoAtencion.
     * Método en el que castea en cada tipo de proceso y su tiempo de atención.
     * @param proceso proceso al que se casteara.
     * @return int del tiempo de atención de cada proceso.
     */
    private Double castingTiempoAtencion(SimpleProcess proceso){
        Double tiempoAtencion = 0.0;
        if(proceso instanceof ArithmeticProcess){
            tiempoAtencion = ((ArithmeticProcess) proceso).getTiempoServicio();
        } else if(proceso instanceof IOProcess){
            tiempoAtencion = ((IOProcess) proceso).getTiempoServicio();
        } else if(proceso instanceof ConditionalProcess){
            tiempoAtencion = ((ConditionalProcess) proceso).getTiempoServicio();
        } else if(proceso instanceof LoopProcess){
            tiempoAtencion = ((LoopProcess) proceso).getTiempoServicio();
        }
        return tiempoAtencion;
    }

    /**
     * Nombre: castingID.
     * Método en el que castea en cada tipo de proceso y su ID.
     * @param proceso proceso al que se casteara.
     * @return int del ID de cada proceso.
     */
    private int castingID(SimpleProcess proceso){
        int id = 0;
        if(proceso instanceof ArithmeticProcess){
            id = ((ArithmeticProcess) proceso).getId();
        } else if(proceso instanceof IOProcess){
            id = ((IOProcess) proceso).getId();
        } else if(proceso instanceof ConditionalProcess){
            id = ((ConditionalProcess) proceso).getId();
        } else if(proceso instanceof LoopProcess){
            id = ((LoopProcess) proceso).getId();
        }
        return id;
    }

    /**
     * Nombre: stopRunning.
     * Método que detiene por completo el programa e imprime los datos finales.
     * @return mensaje en terminal de datos finales.
     */
    public void stopRunning(int procesador) {
        this.running = false;
        int procesosEnCola = this.cola.size();
        System.out.println();
        System.out.println("------------------ Datos finales ------------------");
        if(procesador == 2){
            double tiempoPromedio1 = (procesosAtendidos > 0) ? (totalTiempoAtencion / procesosAtendidos) : 0;
            double tiempoPromedio2 = (procesosAtendidos > 0) ? (totalTiempoAtencion2 / procesosAtendidos) : 0;
            int totalprocessAtend = procesosAtendidos + procesosAtendidos2;
            System.out.println("Procesos atendidos: " + totalprocessAtend + ".");
            System.out.println("Procesos en cola (sin atenderse): " + procesosEnCola + ".");
            System.out.println("Tiempo promedio de atencion por procesador 1: " + String.format("%.2f", tiempoPromedio1) + " seg.");
            System.out.println("Tiempo promedio de atencion por procesador 2: " + String.format("%.2f", tiempoPromedio2) + " seg.");
        } else {
            double tiempoPromedio1 = (procesosAtendidos > 0) ? (totalTiempoAtencion / procesosAtendidos) : 0;
            int totalprocessAtend = procesosAtendidos;
            System.out.println("Procesos atendidos: " + totalprocessAtend + ".");
            System.out.println("Procesos en cola (sin atenderse): " + procesosEnCola + ".");
            System.out.println("Tiempo promedio de atencion por procesador: " + String.format("%.2f", tiempoPromedio1) + " seg.");
        }
        System.out.println("Politica utilizada: First-Come First-Served (FCFS).");
        System.out.println();

        System.exit(0);
    }

    /**
     * Nombre: imprimirCola.
     * Método que imprime la cola actualizada cada vez que se le mande a llamar.
     * @return cola completa.
     */
    public void imprimirCola() {
        if (cola.isEmpty()) {
            System.out.println("Procesos en la cola: [ VACIO ]");
        } else {
            System.out.print("Procesos en la cola: ");
            for (SimpleProcess proceso : cola) {
                System.out.print(castingString(proceso) + " ");
            }
        }
    }

    /**
     * Nombre: generarNuevoID.
     * Método que genera un nuevo ID único y global para cada proceso que se crea durante la ejecución.
     * @return aumento de no. ID.
     */
    private synchronized int generarNuevoID() {
        return ++idGeneradoGlobal;
    }
}