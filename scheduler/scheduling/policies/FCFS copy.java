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

    //************************* Campos ***********************************

    protected ConcurrentLinkedQueue<SimpleProcess> cola;
    protected int size;
    protected int totalProcesses;
    protected Double primeraParte;
    protected Double segundaParte;
    protected Double arith;
    protected Double io;
    protected Double cont;
    protected Double loop;
    protected int procesosAtendidos;
    private boolean running;
    private double totalTiempoAtencion = 0.0;
    private static int idGeneradoGlobal = 0;

    //************************* Constructor ***********************************

    /**
     * Constructor que inicializa los parámetros para la política de FCFS.
     * @param primeraParte tiempo mínimo de agregar procesos.
     * @param segundaParte tiempo máximo de agregar procesos.
     * @param arith tiempo de atención de procesos aritméticos.
     * @param io tiempo de atención de procesos input/output.
     * @param cont tiempo de atención de procesos condicionales.
     * @param loop tiempo de atención de procesos iterativos.
     */
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

        this.running = true;
    }

    //************************* Métodos implementados ***********************************

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

    //************************* Métodos principales ***********************************

    /**
     * Nombre: ejecucionSimple
     * Método que realiza la ejecución con un solo procesador de la política FCFS, en donde se atiende y generan procesos al mismo tiempo.
     * @return ejecución del programa con política FCFS.
     */
    public void ejecucionSimple() {
        Thread generacionProcesos = new Thread(() -> {
            int idGenerado = 0;
            while (running) {
                Random randTiempo = new Random();
                Random randProceso = new Random();
                long primeraParteLong = (long) (primeraParte * 1000);
                long segundaParteLong = (long) (segundaParte * 1000);
                long tiempoRandomLong = primeraParteLong + randTiempo.nextLong() % (segundaParteLong - primeraParteLong + 1);
                try {
                    Thread.sleep(tiempoRandomLong);
                } catch (Exception e) {
                    System.out.println("Proceso interrumpido");
                }
                int procesoEleccion = randProceso.nextInt(4);
                idGenerado++;
                SimpleProcess procesoGenerado = null;
                if (procesoEleccion == 0) {
                    procesoGenerado = new ArithmeticProcess(idGenerado, this.arith);
                } else if (procesoEleccion == 1) {
                    procesoGenerado = new IOProcess(idGenerado, this.io);
                } else if (procesoEleccion == 2) {
                    procesoGenerado = new ConditionalProcess(idGenerado, this.cont);
                } else if (procesoEleccion == 3) {
                    procesoGenerado = new LoopProcess(idGenerado, this.loop);
                }
                String tipoProceso = castingTipo(procesoGenerado);
                add(procesoGenerado);
                System.out.println("Generado proceso ID: " + idGenerado + " Tipo: " + tipoProceso);
                imprimirCola();
            }
        });

        Thread atencionProcesos = new Thread(() -> {
            int idAtendido = 0;
            while (running) {
                SimpleProcess procesoAtender = next();
                if (procesoAtender == null) continue;
                String tipoProceso = castingTipo(procesoAtender);
                Double tiempoAtencion = 0.0;

                if (procesoAtender instanceof ArithmeticProcess) {
                    tiempoAtencion = ((ArithmeticProcess) procesoAtender).getTiempoServicio();
                    idAtendido = ((ArithmeticProcess) procesoAtender).getId();
                } else if (procesoAtender instanceof IOProcess) {
                    tiempoAtencion = ((IOProcess) procesoAtender).getTiempoServicio();
                    idAtendido = ((IOProcess) procesoAtender).getId();
                } else if (procesoAtender instanceof ConditionalProcess) {
                    tiempoAtencion = ((ConditionalProcess) procesoAtender).getTiempoServicio();
                    idAtendido = ((ConditionalProcess) procesoAtender).getId();
                } else if (procesoAtender instanceof LoopProcess) {
                    tiempoAtencion = ((LoopProcess) procesoAtender).getTiempoServicio();
                    idAtendido = ((LoopProcess) procesoAtender).getId();
                }

                long tiempoAtencionMs = (long) (tiempoAtencion * 1000);
                System.out.println();
                System.out.println("Atendiendo proceso ID: " + procesoAtender.getId() + " Tipo: " + tipoProceso + " Tiempo de atención: " + tiempoAtencion + " segundos.");
                System.out.println();
                try {
                    Thread.sleep(tiempoAtencionMs);
                } catch (Exception e) {
                    System.out.println("Proceso interrumpido");
                }
                System.out.println();
                System.out.println("Atendido proceso ID: " + procesoAtender.getId() + " Tipo: " + tipoProceso + " Tiempo de atención: " + tiempoAtencion + " segundos.");
                totalTiempoAtencion += tiempoAtencion;
                procesosAtendidos++;
                remove();
                System.out.println();
                imprimirCola();
                System.out.println();
            }
        });

        Thread recibirSalida = new Thread(() -> {
            Scanner teclado = new Scanner(System.in);
            while (running) {
                String salida = teclado.nextLine();
                if (salida.equals("q")) {
                    stopRunning();
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
            System.out.println("Hubo un problema de sincronización.");
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
            while (running) {
                Random randTiempo = new Random();
                Random randProceso = new Random();
                long primeraParteLong = (long) (primeraParte * 1000);
                long segundaParteLong = (long) (segundaParte * 1000);
                long tiempoRandomLong = primeraParteLong + randTiempo.nextLong() % (segundaParteLong - primeraParteLong + 1);

                try {
                    Thread.sleep(tiempoRandomLong);
                } catch (Exception e) {
                    System.out.println("Proceso interrumpido");
                }

                int procesoEleccion = randProceso.nextInt(4);
                int idGenerado = generarNuevoID();
                SimpleProcess procesoGenerado = null;

                if (procesoEleccion == 0) {
                    procesoGenerado = new ArithmeticProcess(idGenerado, this.arith);
                } else if (procesoEleccion == 1) {
                    procesoGenerado = new IOProcess(idGenerado, this.io);
                } else if (procesoEleccion == 2) {
                    procesoGenerado = new ConditionalProcess(idGenerado, this.cont);
                } else if (procesoEleccion == 3) {
                    procesoGenerado = new LoopProcess(idGenerado, this.loop);
                }

                String tipoProceso = castingTipo(procesoGenerado);
                add(procesoGenerado);
                System.out.println();
                System.out.println("Generado proceso ID: " + idGenerado + " Tipo: " + tipoProceso);
                imprimirCola();
                System.out.println();
            }
        });

        Thread atencionProcesos1 = new Thread(() -> {
            while (running) {
                SimpleProcess procesoAtender;
                synchronized (lock) {
                    procesoAtender = next();
                    remove();
                }
                if (procesoAtender == null) continue;
                String tipoProceso = castingTipo(procesoAtender);
                Double tiempoAtencion = 0.0;

                if (procesoAtender instanceof ArithmeticProcess) {
                    tiempoAtencion = ((ArithmeticProcess) procesoAtender).getTiempoServicio();
                } else if (procesoAtender instanceof IOProcess) {
                    tiempoAtencion = ((IOProcess) procesoAtender).getTiempoServicio();
                } else if (procesoAtender instanceof ConditionalProcess) {
                    tiempoAtencion = ((ConditionalProcess) procesoAtender).getTiempoServicio();
                } else if (procesoAtender instanceof LoopProcess) {
                    tiempoAtencion = ((LoopProcess) procesoAtender).getTiempoServicio();
                }

                long tiempoAtencionMs = (long) (tiempoAtencion * 1000);
                System.out.println();
                System.out.println("Procesador 1: Atendiendo proceso ID: " + procesoAtender.getId() + " Tipo: " + tipoProceso + " Tiempo de atención: " + tiempoAtencion + " segundos.");
                System.out.println();
                try {
                    Thread.sleep(tiempoAtencionMs);
                } catch (Exception e) {
                    System.out.println("Proceso interrumpido");
                }
                System.out.println();
                System.out.println("Procesador 1: Atendido proceso ID: " + procesoAtender.getId() + " Tipo: " + tipoProceso + " Tiempo de atención: " + tiempoAtencion + " segundos.");
                totalTiempoAtencion += tiempoAtencion;
                procesosAtendidos++;
                System.out.println();
                imprimirCola();
                System.out.println();
            }
        });

        Thread atencionProcesos2 = new Thread(() -> {
            while (running) {
                SimpleProcess procesoAtender;
                synchronized (lock) {
                    procesoAtender = next();
                    remove();
                }
                if (procesoAtender == null) continue;
                String tipoProceso = castingTipo(procesoAtender);
                Double tiempoAtencion = 0.0;

                if (procesoAtender instanceof ArithmeticProcess) {
                    tiempoAtencion = ((ArithmeticProcess) procesoAtender).getTiempoServicio();
                } else if (procesoAtender instanceof IOProcess) {
                    tiempoAtencion = ((IOProcess) procesoAtender).getTiempoServicio();
                } else if (procesoAtender instanceof ConditionalProcess) {
                    tiempoAtencion = ((ConditionalProcess) procesoAtender).getTiempoServicio();
                } else if (procesoAtender instanceof LoopProcess) {
                    tiempoAtencion = ((LoopProcess) procesoAtender).getTiempoServicio();
                }

                long tiempoAtencionMs = (long) (tiempoAtencion * 1000);
                System.out.println();
                System.out.println("Procesador 2: Atendiendo proceso ID: " + procesoAtender.getId() + " Tipo: " + tipoProceso + " Tiempo de atención: " + tiempoAtencion + " segundos.");
                System.out.println();
                try {
                    Thread.sleep(tiempoAtencionMs);
                } catch (Exception e) {
                    System.out.println("Proceso interrumpido");
                }
                System.out.println();
                System.out.println("Procesador 2: Atendido proceso ID: " + procesoAtender.getId() + " Tipo: " + tipoProceso + " Tiempo de atención: " + tiempoAtencion + " segundos.");
                totalTiempoAtencion += tiempoAtencion;
                procesosAtendidos++;
                System.out.println();
                imprimirCola();
                System.out.println();
            }
        });

        Thread recibirSalida = new Thread(() -> {
            Scanner teclado = new Scanner(System.in);
            while (running) {
                String salida = teclado.nextLine();
                if (salida.equals("q")) {
                    stopRunning();
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
            System.out.println("Hubo un problema de sincronización.");
        }
    }

    //************************* Métodos secundarios ***********************************

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
        System.out.println("Política utilizada: First Come First Served (FCFS)");
        System.out.println();

        System.exit(0);
    }

    /**
     * Nombre: castingTipo.
     * Método en el que castea en cada tipo de proceso y obtiene el tipo de proceso que es.
     * @param proceso proceso al que se casteara
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
     * @param proceso proceso al que se casteara
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