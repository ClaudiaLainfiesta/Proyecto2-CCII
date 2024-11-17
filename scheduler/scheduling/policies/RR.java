/*RR.java */
/**
** Hecho por: Maria Claudia Lainfiesta Herrera.
** Carnet: 24000149.
** Sección: BN.
**/
/*Descripción: Clase que realiza la política Round-Robin, los procesos son atendidos en el orden en que llegan a la cola de procesos con un tiempo determinado, si supera ese tiempo vuelve a encolarse.*/

package scheduler.scheduling.policies;

/*Librerías utilizadas dentro del programa */
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import scheduler.processing.*;

public class RR extends Policy implements Enqueable {

    //**************************** Campos ****************************

    protected ConcurrentLinkedQueue<SimpleProcess> cola;
    protected int size;
    protected int totalProcesses;
    protected Double minimoTiempo;
    protected Double maximoTiempo;
    protected Double arith;
    protected Double io;
    protected Double cond;
    protected Double loop;
    protected Double quantum;
    protected int procesosAtendidos;
    private boolean running;
    private double totalTiempoAtencion = 0.0;
    private static int idGeneradoGlobal = 0;

    //************************* Constructor *************************

    /**
     * Constructor que inicializa los parámetros para la política de RR.
     * @param minimoTiempo tiempo mínimo de agregar procesos.
     * @param maximoTiempo tiempo máximo de agregar procesos.
     * @param arith tiempo de atención de procesos aritméticos.
     * @param io tiempo de atención de procesos input/output.
     * @param cont tiempo de atención de procesos condicionales.
     * @param loop tiempo de atención de procesos iterativos.
     * @param quantum tiempo de atención fijo de cada procesador.
     */
    public RR(Double minimoTiempo, Double maximoTiempo, Double arith, Double io, Double cond, Double loop, Double quantum){
        super();
        this.cola = new ConcurrentLinkedQueue<>();
        this.minimoTiempo = minimoTiempo;
        this.maximoTiempo = maximoTiempo;
        this.arith = arith;
        this.io = io;
        this.cond = cond;
        this.loop = loop;
        this.quantum = quantum;
        this.procesosAtendidos = 0;
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
     * Nombre: unProcesador.
     * Método que realiza la ejecución con un solo procesador de la política RR, en donde se atiende y generan procesos al mismo tiempo.
     * @return ejecución del programa con política RR.
     */
    public void unProcesador(){
        Thread integracionProcesos = new Thread(() -> {
            while(running){
                long tiempoSleep = tiempoAleatorioRango();
                try{
                    Thread.sleep(tiempoSleep);
                }catch(Exception e){
                    System.out.println("Proceso interrumpido");
                }
                int idGenerado = generarNuevoID();
                SimpleProcess procesoGenerado = procesoAleatorio(idGenerado);
                String tipoProcesoGenerado = castingTipo(procesoGenerado);
                add(procesoGenerado);
                System.out.println("Generado proceso ID: " + idGenerado + " Tipo: " + tipoProcesoGenerado);
                imprimirCola();
            }
        });
        Thread atencionProcesos = new Thread(() -> {
            while(running){
                SimpleProcess procesoAtender = next();
                if (procesoAtender == null) continue;
                int idProceso = castingID(procesoAtender);
                Double tiempoAtencionProceso = castingTiempoAtencion(procesoAtender);
                String tipoProceso = castingTipo(procesoAtender);
                long tiempoAtencionProcesoMs = (long) (tiempoAtencionProceso * 1000);
                System.out.println("\nAtendiendo proceso ID: " + idProceso + " Tipo: " + tipoProceso + " Tiempo restante: " + tiempoAtencionProceso + " segundos.");

                double tiempoAtencion = Math.min(quantum, tiempoAtencionProceso);
                try {
                    Thread.sleep((long) (tiempoAtencion * 1000));
                } catch (InterruptedException e) {
                    System.out.println("Atención interrumpida.");
                }

                tiempoAtencionProceso -= tiempoAtencion;
                totalTiempoAtencion += tiempoAtencion;

                if (tiempoAtencionProceso > 0) {
                    System.out.println("Proceso ID: " + idProceso + " incompleto. Tiempo restante: " + tiempoAtencionProceso + " segundos.");
                    castingSetTiempoAtencion(procesoAtender , tiempoAtencionProceso);
                    remove();
                    add(procesoAtender);
                } else {
                    System.out.println("Proceso ID: " + idProceso + " completado.");
                    procesosAtendidos++;
                    remove();
                    System.out.println();
                    imprimirCola();
                    System.out.println();
                }
            }
        });
        Thread leerTeclado = new Thread(() -> {
            Scanner teclado = new Scanner(System.in);
            while (running) {
                String q = teclado.nextLine();
                if (q.equals("q")) {
                    stopRunning();
                    break;
                }
            }
            teclado.close();
        });

        integracionProcesos.start();
        atencionProcesos.start();
        leerTeclado.start();

        try {
            integracionProcesos.join();
            atencionProcesos.join();
            leerTeclado.join();
        } catch (InterruptedException e) {
            System.out.println("Hubo un problema de sincronización.");
        }
    }

    /**
     * Nombre: dosProcesadores.
     * Método que realiza la ejecución con dos procesadores de la política RR, en donde se atiende y generan procesos al mismo tiempo.
     * @return ejecución del programa con política RR con dos procesadores.
     */
    public void dosProcesadores(){
        Object lock = new Object();
        Thread integracionProcesos = new Thread(() -> {
            while(running){
                long tiempoSleep = tiempoAleatorioRango();
                try{
                    Thread.sleep(tiempoSleep);
                }catch(Exception e){
                    System.out.println("Proceso interrumpido");
                }
                int idGenerado = generarNuevoID();
                SimpleProcess procesoGenerado = procesoAleatorio(idGenerado);
                String tipoProcesoGenerado = castingTipo(procesoGenerado);
                add(procesoGenerado);
                System.out.println("Generado proceso ID: " + idGenerado + " Tipo: " + tipoProcesoGenerado);
                imprimirCola();
            }
        });
        Thread atencionProcesos1 = new Thread(() -> {
            while(running){
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
                System.out.println("\nProcesador 1: Atendiendo proceso ID: " + idProceso + " Tipo: " + tipoProceso + " Tiempo restante: " + tiempoAtencionProceso + " segundos.");

                double tiempoAtencion = Math.min(quantum, tiempoAtencionProceso);
                try {
                    Thread.sleep((long) (tiempoAtencion * 1000));
                } catch (InterruptedException e) {
                    System.out.println("Procesador 1: Atención interrumpida.");
                }

                tiempoAtencionProceso -= tiempoAtencion;
                totalTiempoAtencion += tiempoAtencion;

                if (tiempoAtencionProceso > 0) {
                    System.out.println("Procesador 1: Proceso ID: " + idProceso + " incompleto. Tiempo restante: " + tiempoAtencionProceso + " segundos.");
                    castingSetTiempoAtencion(procesoAtender , tiempoAtencionProceso);
                    add(procesoAtender);
                } else {
                    System.out.println("Procesador 1: Proceso ID: " + idProceso + " completado.");
                    procesosAtendidos++;
                    System.out.println();
                    imprimirCola();
                    System.out.println();
                }
            }
        });
        Thread atencionProcesos2 = new Thread(() -> {
            while(running){
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
                System.out.println("\nProcesador 2: Atendiendo proceso ID: " + idProceso + " Tipo: " + tipoProceso + " Tiempo restante: " + tiempoAtencionProceso + " segundos.");

                double tiempoAtencion = Math.min(quantum, tiempoAtencionProceso);
                try {
                    Thread.sleep((long) (tiempoAtencion * 1000));
                } catch (InterruptedException e) {
                    System.out.println("Procesador 2: Atención interrumpida.");
                }

                tiempoAtencionProceso -= tiempoAtencion;
                totalTiempoAtencion += tiempoAtencion;

                if (tiempoAtencionProceso > 0) {
                    System.out.println("Procesador 2: Proceso ID: " + idProceso + " incompleto. Tiempo restante: " + tiempoAtencionProceso + " segundos.");
                    castingSetTiempoAtencion(procesoAtender , tiempoAtencionProceso);
                    add(procesoAtender);
                } else {
                    System.out.println("Procesador 2: Proceso ID: " + idProceso + " completado.");
                    procesosAtendidos++;
                    System.out.println();
                    imprimirCola();
                    System.out.println();
                }
            }
        });
        Thread leerTeclado = new Thread(() -> {
            Scanner teclado = new Scanner(System.in);
            while (running) {
                String q = teclado.nextLine();
                if (q.equals("q")) {
                    stopRunning();
                    break;
                }
            }
            teclado.close();
        });

        integracionProcesos.start();
        atencionProcesos1.start();
        atencionProcesos2.start();
        leerTeclado.start();
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
        long minimoTiempoLong = (long) (this.minimoTiempo * 1000);
        long maximoTiempoLong = (long) (this.maximoTiempo * 1000);
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
     * Nombre: castingSetTiempoAtencion.
     * Método en el que castea en cada tipo de proceso y cambia su tiempo de atención.
     * @param proceso proceso al que se casteara.
     * @param tiempoNuevo tiempo de atención nuevo.
     */
    private void castingSetTiempoAtencion(SimpleProcess proceso, Double tiempoNuevo){
        if(proceso instanceof ArithmeticProcess){
            ((ArithmeticProcess) proceso).setTiempoServicio(tiempoNuevo);
        } else if(proceso instanceof IOProcess){
            ((IOProcess) proceso).setTiempoServicio(tiempoNuevo);
        } else if(proceso instanceof ConditionalProcess){
            ((ConditionalProcess) proceso).setTiempoServicio(tiempoNuevo);
        } else if(proceso instanceof LoopProcess){
            ((LoopProcess) proceso).setTiempoServicio(tiempoNuevo);
        }
    }

    /**
     * Nombre: stopRunning.
     * Método que detiene por completo el programa e imprime los datos finales.
     * @return mensaje en terminal de datos finales.
     */
    public void stopRunning() {
        this.running = false;
        int procesosEnCola = this.cola.size();

        double tiempoPromedio = (procesosAtendidos > 0) ? (totalTiempoAtencion / procesosAtendidos) : 0;
        System.out.println();
        System.out.println("--------Datos finales--------");
        System.out.println("Procesos atendidos: " + procesosAtendidos);
        System.out.println("Procesos en cola (sin atenderse): " + procesosEnCola);
        System.out.println("Tiempo promedio de atención por proceso: " + tiempoPromedio + " segundos");
        System.out.println("Política utilizada: Round-Robin (RR)");
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
            System.out.println("La cola está vacía.");
        } else {
            System.out.print("Procesos en la cola: ");
            for (SimpleProcess proceso : cola) {
                System.out.print(castingString(proceso) + " ");
            }
            System.out.println();
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
