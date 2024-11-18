/*LCFS.java */
/**
** Hecho por: Kevin Jeancarlo De León Castillo.
** Carnet: 24002596.
** Sección: BN.
**/
/*Descripción: Clase que realiza la política Last Come First Served (LCFS), donde el último proceso en ingresar es el primero en ser atendido.*/

package scheduler.scheduling.policies;

/*Librerías utilizadas dentro del programa */
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;
import scheduler.processing.*;

public class LCFS extends Policy implements Enqueable {

    //************************* Campos ***********************************

    protected Stack<SimpleProcess> pila;
    protected int size;
    protected int totalProcesses;
    protected Double primeraParte;
    protected Double segundaParte;
    protected Double arith;
    protected Double io;
    protected Double cont;
    protected Double loop;
    protected int procesosAtendidos;
    protected int procesosAtendidos2;
    private boolean running;
    private double totalTiempoAtencion = 0.0;
    private double totalTiempoAtencion2 = 0.0;
    private static int idGeneradoGlobal = 0;

    //************************* Constructor ***********************************

    /**
     * Constructor que inicializa los parámetros para la política de LCFS.
     * @param primeraParte tiempo mínimo de agregar procesos.
     * @param segundaParte tiempo máximo de agregar procesos.
     * @param arith tiempo de atención de procesos aritméticos.
     * @param io tiempo de atención de procesos input/output.
     * @param cont tiempo de atención de procesos condicionales.
     * @param loop tiempo de atención de procesos iterativos.
     */
    public LCFS(Double primeraParte, Double segundaParte, Double arith, Double io, Double cont, Double loop) {
        super();
        this.pila = new Stack<>();
        this.primeraParte = primeraParte;
        this.segundaParte = segundaParte;
        this.arith = arith;
        this.io = io;
        this.cont = cont;
        this.loop = loop;
        this.procesosAtendidos = 0;
        this.procesosAtendidos2 = 0;
        this.running = true;
    }

    //************************* Métodos implementados ***********************************

    /**
     * Nombre: add.
     * Método que agrega un proceso a la pila.
     * @param proceso proceso a agregar a la pila.
     */
    @Override
    public synchronized void add(SimpleProcess proceso) {
        this.pila.push(proceso);
        this.size++;
        this.totalProcesses++;
    }

    /**
     * Nombre: remove.
     * Método que remueve de la pila el proceso en la cima.
     */
    @Override
    public synchronized void remove() {
        if (!this.pila.isEmpty()) {
            this.pila.pop();
            this.size--;
        }
    }

    /**
     * Nombre: next.
     * Método que obtiene el siguiente proceso en la pila (la cima).
     * @return proceso en la cima de la pila.
     */
    @Override
    public synchronized SimpleProcess next() {
        return this.pila.isEmpty() ? null : this.pila.peek();
    }

    //************************* Métodos principales ***********************************

    /**
     * Nombre: ejecucionDoble
     * Método que realiza la ejecución con dos procesadores de la política LCFS, en donde se atiende y generan procesos al mismo tiempo.
     * @return ejecución del programa con política LCFS con dos procesadores.
     */
    public void ejecucionSimple() {
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
                    synchronized (lock) {
                        System.out.println("Proceso interrumpido");
                    }
                }
                synchronized (lock) {
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
                    synchronized (lock) {
                        add(procesoGenerado);
                        System.out.println();
                        System.out.println("Generado proceso ID: " + idGenerado + " Tipo: " + tipoProceso);
                        imprimirPila();
                        System.out.println();
                    }
                }
            }
        });

        Thread atencionProcesos1 = new Thread(() -> atenderProceso(lock, "Procesador", 1));

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
        atencionProcesos1.start();
        recibirSalida.start();

        try {
            generacionProcesos.join();
            atencionProcesos1.join();
            recibirSalida.join();
        } catch (InterruptedException e) {
            synchronized (lock) {
                System.out.println("Hubo un problema de sincronización.");
            }
        }
    }

    /**
     * Nombre: ejecucionDoble
     * Método que realiza la ejecución con dos procesadores de la política LCFS, en donde se atiende y generan procesos al mismo tiempo.
     * @return ejecución del programa con política LCFS con dos procesadores.
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
                    synchronized (lock) {
                        System.out.println("Proceso interrumpido");
                    }
                }
                synchronized (lock) {
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
                    synchronized (lock) {
                        add(procesoGenerado);
                        System.out.println();
                        System.out.println("Generado proceso ID: " + idGenerado + " Tipo: " + tipoProceso);
                        imprimirPila();
                        System.out.println();
                    }
                }
            }
        });

        Thread atencionProcesos1 = new Thread(() -> atenderProceso(lock, "Procesador 1", 1));
        Thread atencionProcesos2 = new Thread(() -> atenderProceso(lock, "Procesador 2", 2));

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
            synchronized (lock) {
                System.out.println("Hubo un problema de sincronización.");
            }
        }
    }

    /**
     * Nombre: atenderProceso.
     * Método que simula la atención de un proceso por un procesador.
     * @param lock objeto de sincronización.
     * @param nombreProcesador nombre del procesador que atiende el proceso.
     */
    private void atenderProceso(Object lock, String nombreProcesador, int unoDos) {
        while (running) {
            SimpleProcess procesoAtender;
            synchronized (lock) {
                procesoAtender = next();
                if (procesoAtender != null) {
                    remove();
                }
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
            synchronized (lock) {
                System.out.println();
                System.out.println(nombreProcesador + ": Atendiendo proceso ID: " + procesoAtender.getId() + " Tipo: " + tipoProceso + " Tiempo de atención: " + tiempoAtencion + " segundos.");
                System.out.println();
            }
            try {
                Thread.sleep(tiempoAtencionMs);
            } catch (Exception e) {
                synchronized (lock) {
                    System.out.println("Proceso interrumpido");
                }
            }

            synchronized (lock) {
                System.out.println();
                System.out.println(nombreProcesador + ": Atendido proceso ID: " + procesoAtender.getId() + " Tipo: " + tipoProceso + " Tiempo de atención: " + tiempoAtencion + " segundos.");
                int totalProcesosUnido = 0;
                if(unoDos == 2){
                    totalTiempoAtencion2 += tiempoAtencion;
                    procesosAtendidos2++;
                    totalProcesosUnido = procesosAtendidos + procesosAtendidos2;
                } else {
                    totalTiempoAtencion += tiempoAtencion;
                    procesosAtendidos++;
                    totalProcesosUnido = procesosAtendidos + procesosAtendidos2;
                }
                System.out.println("Total de procesos atendidos hasta el momento: " + totalProcesosUnido + ".");
                imprimirPila();
                System.out.println();
            }
        }
    }

    //************************* Métodos secundarios ***********************************

    /**
     * Nombre: stopRunning.
     * Método que detiene la ejecución del programa e imprime los datos finales.
     * @return mensaje en la terminal con los datos finales.
     */
    public void stopRunning(int procesador) {
        this.running = false;
        int procesosPendientes = this.pila.size();
        System.out.println();
        System.out.println("------------------ Datos finales ------------------");
        if(procesador == 2){
            double tiempoPromedio1 = (procesosAtendidos > 0) ? (totalTiempoAtencion / procesosAtendidos) : 0;
            double tiempoPromedio2 = (procesosAtendidos > 0) ? (totalTiempoAtencion2 / procesosAtendidos2) : 0;
            int totalprocessAtend = procesosAtendidos + procesosAtendidos2;
            System.out.println("Procesos atendidos: " + totalprocessAtend + ".");
            System.out.println("Procesos en cola (sin atenderse): " + procesosPendientes + ".");
            System.out.println("Tiempo promedio de atencion por procesador 1: " + String.format("%.2f", tiempoPromedio1) + " seg.");
            System.out.println("Tiempo promedio de atencion por procesador 2: " + String.format("%.2f", tiempoPromedio2) + " seg.");
        } else {
            double tiempoPromedio1 = (procesosAtendidos > 0) ? (totalTiempoAtencion / procesosAtendidos) : 0;
            int totalprocessAtend = procesosAtendidos;
            System.out.println("Procesos atendidos: " + totalprocessAtend + ".");
            System.out.println("Procesos en cola (sin atenderse): " + procesosPendientes + ".");
            System.out.println("Tiempo promedio de atencion por procesador: " + String.format("%.2f", tiempoPromedio1) + " seg.");
        }
        System.out.println("Política utilizada: Last Come First Served (LCFS)");
        System.out.println();
        System.exit(0);
    }

    /**
     * Nombre: castingTipo.
     * Método que obtiene el tipo de proceso como una cadena.
     * @param proceso proceso del cual se quiere obtener el tipo.
     * @return tipo de proceso.
     */
    private String castingTipo(SimpleProcess proceso) {
        if (proceso instanceof ArithmeticProcess) {
            return ((ArithmeticProcess) proceso).getTipo();
        } else if (proceso instanceof IOProcess) {
            return ((IOProcess) proceso).getTipo();
        } else if (proceso instanceof ConditionalProcess) {
            return ((ConditionalProcess) proceso).getTipo();
        } else if (proceso instanceof LoopProcess) {
            return ((LoopProcess) proceso).getTipo();
        }
        return "";
    }

    /**
     * Nombre: imprimirPila.
     * Método que imprime el contenido de la pila de procesos.
     */
    public void imprimirPila() {
        if (pila.isEmpty()) {
            System.out.println("La pila está vacía.");
        } else {
            System.out.print("Procesos en la pila: ");
            for (SimpleProcess proceso : pila) {
                System.out.print(proceso.toString() + " ");
            }
            System.out.println();
        }
    }

    /**
     * Nombre: generarNuevoID.
     * Método que genera un nuevo ID único y global para cada proceso.
     * @return un nuevo ID incremental.
     */
    private synchronized int generarNuevoID() {
        return ++idGeneradoGlobal;
    }
}