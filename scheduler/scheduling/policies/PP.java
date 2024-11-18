/*FCFS.java */
/**
** Hecho por: Adriel Levi Argueta Caal
** Carnet: 24003171.
** Seccion: BN.
/* Descripcion: clase que implementa la politica Priority Policy, donde los procesos son atendidos segun su nivel de priorida*/
package scheduler.scheduling.policies;

/*Librerias que se utilizan dentro del progreama */
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import scheduler.processing.*;


public class PP extends Policy implements Enqueable {
    
    /*Campos*/
    private final ConcurrentLinkedQueue<SimpleProcess> colaPrioridad1; // IOProcess
    private final ConcurrentLinkedQueue<SimpleProcess> colaPrioridad2; // ArithmeticProcess
    private final ConcurrentLinkedQueue<SimpleProcess> colaPrioridad3; // ConditionalProcess y LoopProcess
    private final double arithTime;
    private final double ioTime;
    private final double condTime;
    private final double loopTime;
    private final double tiempoMinimo;
    private final double tiempoMaximo;
    private static int idGeneradoGlobal = 0;
    private volatile boolean running; // Controla la ejecución principal
    private int procesosAtendidos;
    private double totalTiempoAtencion = 0.0;

    /*Constructor */
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
        this.running = true;
        this.procesosAtendidos = 0;
    }
    
    /******** Metodos implementados **********/

    /**
     * Nombre: add.
     * Metodo que agrega un proceso a la cola segun su nivel de prioridad.
     * @param process proceso a encolar 
     */
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
    /*
     * Nombre: remove
     * Metodo que remuve el primer proceso de la cola con mayor prioridad
     */

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
    /*
     * Nombre: next.
     * Método que obtiene el siguiente proceso a atender según la prioridad.
     * @return El siguiente proceso a atender, según la prioridad.
     */

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

    /*********** Metodos Principales **************/

    /**
     * nombre: Ejecucion
     *Método de ejecución de la política  con un solo procesador de la politica PP, Los procesos son encolados según su tipo.
     * @return ejecucion del programa con politicas PP.
     */
    public void ejecucion() {
        Object lock = new Object();
        Thread generacionProcesos = new Thread(() -> {
            int idGenerado = 0;
            Random randTiempo = new Random();
            Random randProceso = new Random();

            while (running) {
                long tiempoRandomLong = (long) (tiempoMinimo * 1000) + randTiempo.nextLong() % (long) ((tiempoMaximo - tiempoMinimo) * 1000 + 1);

                try {
                    Thread.sleep(tiempoRandomLong);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                synchronized (lock) {
                int procesoEleccion = randProceso.nextInt(4);
                idGenerado++;
                SimpleProcess procesoGenerado = generarProcesoAleatorio(idGenerado, procesoEleccion);
                add(procesoGenerado);
                System.out.println("Generado proceso ID: " + idGenerado + " Tipo: " + determinarTipo(procesoGenerado) + " Tiempo de atencion: " + obtenerTiempoDeServicio(procesoGenerado) + " segundos.");
                imprimirCola();
                }
            }
        });

        Thread atencionProcesos = new Thread(() -> {
            while (running) {
                SimpleProcess procesoAtender;
                synchronized (lock){
                    procesoAtender = next();
                    if(procesoAtender != null){
                        remove();
                    }
                }
                if (procesoAtender == null) continue;
                double tiempoAtencion = obtenerTiempoDeServicio(procesoAtender);
                synchronized (lock){
                    System.out.println("\nAtendiendo proceso ID: " + procesoAtender.getId() +
                                   " Tipo: " + determinarTipo(procesoAtender) +
                                   " Tiempo de atención: " + tiempoAtencion + " segundos.");
                }
                try {
                    Thread.sleep((long) (tiempoAtencion * 1000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                synchronized(lock) {
                    totalTiempoAtencion += tiempoAtencion;
                    procesosAtendidos++;
                    remove();

                    System.out.println("\nAtendido proceso ID: " + procesoAtender.getId() + " Tipo: " + determinarTipo(procesoAtender) + " Tiempo atendido: " + obtenerTiempoDeServicio(procesoAtender) + " segundos.");
                    System.out.println("Atendidos en total: " + procesosAtendidos);
                    imprimirCola();
                }
            }
        });

        Thread recibirSalida = new Thread(() -> {
            Scanner teclado = new Scanner(System.in);
            try {
                while (running) {
                    System.out.println("Escribe 'q' para salir:");
                    String salida = teclado.nextLine();
                    if (salida.equals("q")) {
                        stopRunning();
                        break;
                    }
                }
            } finally {
                teclado.close();
            }
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
     * nombre: ejecucionDoble
     *Método de ejecución de la política  con dos procesadorres de la politica PP, Los procesos son encolados según su tipo.
     * @return ejecucion del programa con politicas PP.
     */
    
    public void ejecucionDoble(){
        Object lock = new Object();
        Thread generacionProcesos = new Thread(() -> {
            int idGenerado = 0;
            Random randTiempo = new Random();
            Random randProceso = new Random();

            while(running){
                long tiempoRandomLong = (long) ( tiempoMinimo * 1000) + randProceso.nextLong() % (long) ((tiempoMaximo - tiempoMinimo) * 1000 + 1);
                try{
                    Thread.sleep(tiempoRandomLong);
                } catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                    break;
                }

                synchronized (lock){
                    int procesoEleccion = randProceso.nextInt(4);
                    idGenerado++;
                    SimpleProcess procesoGenerado = generarProcesoAleatorio(idGenerado, procesoEleccion);
                    add(procesoGenerado);
                    System.out.println("Generado proceso ID: " + idGenerado + " Tipo: " + determinarTipo(procesoGenerado) + " Tiempo de atencion: " + obtenerTiempoDeServicio(procesoGenerado) + " segundos.");
                    imprimirCola();
                }
            }
        });

        Thread atencionProcesos = new Thread(() ->{
            while(running){
                SimpleProcess procesoAtender;
                synchronized (lock) {
                    procesoAtender = next();
                    if(procesoAtender != null) {
                        remove();
                    }
                }
                if( procesoAtender == null) continue;
                double tiempoAtencion = obtenerTiempoDeServicio(procesoAtender);
                synchronized (lock){
                    System.out.println("\nAtendiendo proceso ID:" + procesoAtender.getId() + 
                                   " Tipo: " + determinarTipo(procesoAtender) +
                                   " Tiempo de atencion: " + tiempoAtencion + " segundos.");
                }
                try{
                    Thread.sleep((long) (tiempoAtencion * 1000));
                } catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                    break;
                }
                synchronized (lock){
                    totalTiempoAtencion += tiempoAtencion;
                    procesosAtendidos++;
                    remove();
                    System.out.println("\nAtendido proceso ID: " + procesoAtender.getId() + 
                                   " Tipo: " + determinarTipo(procesoAtender) + 
                                   " Tiempo de atencion: " + tiempoAtencion + " segundos.");
                    System.out.println("Atendidos en total: " + procesosAtendidos);
                    imprimirCola();
                    System.out.println();
                }
            }
        });

        Thread atencionProceso2 = new Thread(() -> {
            while(running){
                SimpleProcess procesoAtender;
                synchronized (lock){
                    procesoAtender = next();
                    if(procesoAtender != null){
                        remove();
                    }
                }

                if(procesoAtender == null) continue;
                double tiempoAtencion = obtenerTiempoDeServicio(procesoAtender);
                
                synchronized(lock){
                    System.out.println("\nAtendiendo proceso ID: " + procesoAtender.getId() + 
                                " Tipo: " + determinarTipo(procesoAtender) +
                                " Tiempo de atencion: " + tiempoAtencion + " segundos.");
                }
            
                try {

                    Thread.sleep((long)(tiempoAtencion * 1000));
                } catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                    break;
                }

                synchronized(lock){
                    totalTiempoAtencion +=tiempoAtencion;
                    procesosAtendidos++;
                    remove();
                    System.out.println("\nAtendido proceso ID: " + procesoAtender.getId() +
                               " Tipo: " + determinarTipo(procesoAtender) + " Tiempo atendido: " + obtenerTiempoDeServicio(procesoAtender) + " segundos.");
                               System.out.println("Procesos atendidos en total: " + procesosAtendidos);
                    imprimirCola();
                }
            }
        });  

        Thread recibirSalida = new Thread(() -> {
            Scanner teclado = new Scanner(System.in);
            try {
                while (running) {
                    System.out.println("Escribe 'q' para salir:");
                    String salida = teclado.nextLine();
                    if (salida.equals("q")) {
                        stopRunning();
                        break;
                    }
                }
            } finally {
                teclado.close();
            }
        });       
        
        generacionProcesos.start();
        atencionProcesos.start();
        atencionProceso2.start();
        recibirSalida.start();

        try {
            generacionProcesos.join();
            atencionProceso2.join();
            atencionProcesos.join();
            recibirSalida.join();
        } catch (InterruptedException e){
            System.out.println("Hubo im problema en la sincronizacion.");
        }
    }

    /******************** Metodos Secundarios *********************/
/**
 * Nombre: generarProcesoAleatorio.
 * Método que genera un proceso aleatorio basado en un tipo especificado.
 * @param id Identificador único del proceso generado.
 * @param tipoProceso Entero que representa el tipo de proceso a generar.
 * @return Una instancia de SimpleProcess correspondiente al tipo especificado.
 * */
    private SimpleProcess generarProcesoAleatorio(int id, int tipoProceso) {
        switch (tipoProceso) {
            case 0: return new ArithmeticProcess(id, arithTime);
            case 1: return new IOProcess(id, ioTime);
            case 2: return new ConditionalProcess(id, condTime);
            case 3: return new LoopProcess(id, loopTime);
            default: throw new IllegalStateException("Tipo de proceso inesperado: " + tipoProceso);
        }
    }
   /**
   * Nombre: obtenerTiempoDeServicio.
   * Método que devuelve el tiempo de atención requerido para un proceso, 
   * basado en su tipo específico.
   * @param proceso Instancia de SimpleProcess cuyo tiempo de atención será determinado.
   * @return Tiempo de atención en segundos según el tipo del proceso.
   *         Retorna 0.0 si el tipo del proceso no es reconocido.
   */
    private double obtenerTiempoDeServicio(SimpleProcess proceso) {
        if (proceso instanceof ArithmeticProcess) return arithTime;
        if (proceso instanceof IOProcess) return ioTime;
        if (proceso instanceof ConditionalProcess) return condTime;
        if (proceso instanceof LoopProcess) return loopTime;
        return 0.0;
    }


   /**
   * Nombre: determinarTipo.
   * Método que identifica y devuelve el nombre del tipo de un proceso específico.
   * @param proceso Instancia de SimpleProcess cuyo tipo se determinará.
   * @return Una cadena que representa el nombre del tipo del proceso. 
   */
    private String determinarTipo(SimpleProcess proceso) {
        if (proceso instanceof ArithmeticProcess) return "ArithmeticProcess";
        if (proceso instanceof IOProcess) return "IOProcess";
        if (proceso instanceof ConditionalProcess) return "ConditionalProcess";
        if (proceso instanceof LoopProcess) return "LoopProcess";
        return "Desconocido";
    }

   /**
   * Nombre: imprimirCola.
   * Método que imprime en consola el estado actual de las colas de prioridad.
   * Cada cola representa un conjunto de procesos clasificados según su tipo:
   * Los procesos se imprimen en el formato proporcionado por su método `toString`.
   */
    private void imprimirCola() {
        System.out.println("Cola de prioridad 1 (IOProcess): " + colaPrioridad1);
        System.out.println("Cola de prioridad 2 (ArithmeticProcess): " + colaPrioridad2);
        System.out.println("Cola de prioridad 3 (ConditionalProcess y LoopProcess): " + colaPrioridad3);
    }



    /**
     * Nombre: generarNuevoID.
     * Método que genera un nuevo ID único y global para cada proceso que se crea durante la ejecución.
     * @return aumento de no. ID.
     */    

    private synchronized int generarNuevoID(){
        return ++idGeneradoGlobal;
    }

    /*
     * Nombre: stopRunning
     * Metodo que detiene por completo el programa e imprime los datos finales.
     * @return mensahe en la terminal de datos finales
     */

    public void stopRunning() {
        running = false;
        int sizeCola1 = colaPrioridad1.size();
        int sizeCola2 = colaPrioridad2.size();
        int sizeCola3 = colaPrioridad3.size();
        int sizeTotal = sizeCola1 + sizeCola2 + sizeCola3;
        double tiempoPromedio = (procesosAtendidos > 0) ? (totalTiempoAtencion / procesosAtendidos) : 0;
        System.out.println("\n--------Datos finales--------");
        System.out.println("Procesos atendidos: " + procesosAtendidos);
        System.out.println("Procesos en cola (sin atenderse): " + sizeTotal);
        System.out.println("Tiempo promedio de atención por proceso: " + tiempoPromedio + " segundos");
        System.out.println("Política utilizada: Priority Policy (PP)");
        System.exit(0);
        
    }
}
