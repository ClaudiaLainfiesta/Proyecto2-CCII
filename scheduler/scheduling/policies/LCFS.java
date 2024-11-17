/*LCFS.java */
/**
** Hecho por: Kevin Jeancarlo De León Castillo.
** Carnet: 24002596.
** Sección: BN.
**/
/*Descripción: Clase que implementa la política Last Come First Served (LCFS), donde el último proceso en ingresar es el primero en ser atendido, con soporte para ejecución con dos procesadores.*/

package scheduler.scheduling.policies;

import java.util.Random;
import java.util.Scanner;
import java.util.Stack;
import scheduler.processing.*;

public class LCFS extends Policy implements Enqueable {

    // Campos de la clase
    protected Stack<SimpleProcess> stackProcesos;
    protected int cantidadProcesos;
    protected int totalGenerados;
    protected Double minIntervalo;
    protected Double maxIntervalo;
    protected Double tiempoArith;
    protected Double tiempoIO;
    protected Double tiempoCond;
    protected Double tiempoLoop;
    protected int procesosCompletados;
    private boolean activo;
    private double tiempoTotalProcesado = 0.0;
    private static int contadorGlobalId = 0;

    //-------------------------------CONSTRUCTOR-------------------------------
    /**
    * Constructor que inicializa los parámetros de tiempo y tipos de procesos.
    * @param minIntervalo Tiempo mínimo de intervalo para la generación de procesos.
    * @param maxIntervalo Tiempo máximo de intervalo para la generación de procesos.
    * @param tiempoArith Tiempo de atención de procesos aritméticos.
    * @param tiempoIO Tiempo de atención de procesos de entrada/salida.
    * @param tiempoCond Tiempo de atención de procesos condicionales.
    * @param tiempoLoop Tiempo de atención de procesos iterativos.
    */
    public LCFS(Double minIntervalo, Double maxIntervalo, Double tiempoArith, Double tiempoIO, Double tiempoCond, Double tiempoLoop) {
        super();
        this.stackProcesos = new Stack<>();
        this.minIntervalo = minIntervalo;
        this.maxIntervalo = maxIntervalo;
        this.tiempoArith = tiempoArith;
        this.tiempoIO = tiempoIO;
        this.tiempoCond = tiempoCond;
        this.tiempoLoop = tiempoLoop;
        this.procesosCompletados = 0;
        this.activo = true;
    }

    //-------------------------------METODOS----------------------------------
    /**
    * Nombre: add.
    * Método que agrega un proceso a la pila.
    * @param proceso Proceso a agregar a la pila.
    */
    @Override
    public void add(SimpleProcess proceso) {
        stackProcesos.push(proceso);
        cantidadProcesos++;
        totalGenerados++;
    }

    /**
    * Nombre: remove.
    * Método que elimina el proceso que se encuentra en la cima de la pila.
    */
    @Override
    public void remove() {
        if (!stackProcesos.isEmpty()) {
            stackProcesos.pop();
            cantidadProcesos--;
        }
    }

    /**
    * Nombre: next.
    * Método que devuelve el próximo proceso en la pila.
    * @return El proceso en la cima de la pila.
    */
    @Override
    public SimpleProcess next() {
        return stackProcesos.isEmpty() ? null : stackProcesos.peek();
    }

    /**
    * Nombre: ejecucionSimple.
    * Método que ejecuta la simulación con un solo procesador, generando y atendiendo procesos.
    */
    public void ejecucionSimple() {
        Thread hiloGeneracion = new Thread(() -> {
            int idProceso = 0;
            while (activo) {
                Random randomTiempo = new Random();
                Random randomTipo = new Random();
                long intervaloMinimo = (long) (minIntervalo * 1000);
                long intervaloMaximo = (long) (maxIntervalo * 1000);
                long intervaloAleatorio = intervaloMinimo + randomTiempo.nextLong() % (intervaloMaximo - intervaloMinimo + 1);

                try {
                    Thread.sleep(intervaloAleatorio);
                } catch (Exception e) {
                    System.out.println("Proceso interrumpido");
                }

                int tipoProceso = randomTipo.nextInt(4);
                idProceso++;
                SimpleProcess nuevoProceso = null;
                if (tipoProceso == 0) {
                    nuevoProceso = new ArithmeticProcess(idProceso, tiempoArith);
                } else if (tipoProceso == 1) {
                    nuevoProceso = new IOProcess(idProceso, tiempoIO);
                } else if (tipoProceso == 2) {
                    nuevoProceso = new ConditionalProcess(idProceso, tiempoCond);
                } else if (tipoProceso == 3) {
                    nuevoProceso = new LoopProcess(idProceso, tiempoLoop);
                }

                String tipo = obtenerTipoProceso(nuevoProceso);
                add(nuevoProceso);
                System.out.println("Generado proceso ID: " + idProceso + " Tipo: " + tipo);
                imprimirPila();
            }
        });

        Thread hiloAtencion = new Thread(() -> {
            while (activo) {
                SimpleProcess procesoActual = next();
                if (procesoActual == null) continue;
                String tipo = obtenerTipoProceso(procesoActual);
                Double duracion = 0.0;

                if (procesoActual instanceof ArithmeticProcess) {
                    duracion = ((ArithmeticProcess) procesoActual).getTiempoServicio();
                } else if (procesoActual instanceof IOProcess) {
                    duracion = ((IOProcess) procesoActual).getTiempoServicio();
                } else if (procesoActual instanceof ConditionalProcess) {
                    duracion = ((ConditionalProcess) procesoActual).getTiempoServicio();
                } else if (procesoActual instanceof LoopProcess) {
                    duracion = ((LoopProcess) procesoActual).getTiempoServicio();
                }

                long duracionMs = (long) (duracion * 1000);
                System.out.println();
                System.out.println("Atendiendo proceso ID: " + procesoActual.getId() + " Tipo: " + tipo + " Tiempo de atención: " + duracion + " segundos.");
                System.out.println();
                try {
                    Thread.sleep(duracionMs);
                } catch (Exception e) {
                    System.out.println("Proceso interrumpido");
                }

                System.out.println();
                System.out.println("Atendido proceso ID: " + procesoActual.getId() + " Tipo: " + tipo + " Tiempo de atención: " + duracion + " segundos.");
                tiempoTotalProcesado += duracion;
                procesosCompletados++;
                remove();
                System.out.println();
                imprimirPila();
                System.out.println();
            }
        });

        Thread hiloEntrada = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (activo) {
                String entrada = scanner.nextLine();
                if (entrada.equals("q")) {
                    detenerEjecucion();
                    break;
                }
            }
            scanner.close();
        });

        hiloGeneracion.start();
        hiloAtencion.start();
        hiloEntrada.start();

        try {
            hiloGeneracion.join();
            hiloAtencion.join();
            hiloEntrada.join();
        } catch (InterruptedException e) {
            System.out.println("Hubo un problema de sincronización.");
        }
    }

    /**
     * Nombre: ejecucionDoble.
     * Método que ejecuta la simulación con dos procesadores, generando y atendiendo procesos.
     */
    public void ejecucionDoble() {
        Object lock = new Object();
        Thread hiloGeneracion = new Thread(() -> {
            while (activo) {
                Random randomTiempo = new Random();
                Random randomTipo = new Random();
                long intervaloMinimo = (long) (minIntervalo * 1000);
                long intervaloMaximo = (long) (maxIntervalo * 1000);
                long intervaloAleatorio = intervaloMinimo + randomTiempo.nextLong() % (intervaloMaximo - intervaloMinimo + 1);

                try {
                    Thread.sleep(intervaloAleatorio);
                } catch (Exception e) {
                    System.out.println("Proceso interrumpido");
                }

                int tipoProceso = randomTipo.nextInt(4);
                int idProceso = generarNuevoId();
                SimpleProcess nuevoProceso = null;

                if (tipoProceso == 0) {
                    nuevoProceso = new ArithmeticProcess(idProceso, tiempoArith);
                } else if (tipoProceso == 1) {
                    nuevoProceso = new IOProcess(idProceso, tiempoIO);
                } else if (tipoProceso == 2) {
                    nuevoProceso = new ConditionalProcess(idProceso, tiempoCond);
                } else if (tipoProceso == 3) {
                    nuevoProceso = new LoopProcess(idProceso, tiempoLoop);
                }

                String tipo = obtenerTipoProceso(nuevoProceso);
                add(nuevoProceso);
                System.out.println();
                System.out.println("Generado proceso ID: " + idProceso + " Tipo: " + tipo);
                imprimirPila();
                System.out.println();
            }
        });

        Thread hiloAtencion1 = new Thread(() -> atenderProceso(lock, "Procesador 1"));
        Thread hiloAtencion2 = new Thread(() -> atenderProceso(lock, "Procesador 2"));

        Thread hiloEntrada = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (activo) {
                String entrada = scanner.nextLine();
                if (entrada.equals("q")) {
                    detenerEjecucion();
                    break;
                }
            }
            scanner.close();
        });

        hiloGeneracion.start();
        hiloAtencion1.start();
        hiloAtencion2.start();
        hiloEntrada.start();

        try {
            hiloGeneracion.join();
            hiloAtencion1.join();
            hiloAtencion2.join();
            hiloEntrada.join();
        } catch (InterruptedException e) {
            System.out.println("Hubo un problema de sincronización.");
        }
    }

    /**
     * Nombre: atenderProceso.
     * Método que atiende un proceso desde la pila y simula el tiempo de atención.
     * @param lock Objeto de bloqueo para sincronización.
     * @param nombreProcesador Nombre del procesador que está atendiendo el proceso.
     */
    private void atenderProceso(Object lock, String nombreProcesador) {
        while (activo) {
            SimpleProcess procesoAtender;
            synchronized (lock) {
                procesoAtender = next();
                if (procesoAtender != null) {
                    remove();
                }
            }
            if (procesoAtender == null) continue;
            String tipo = obtenerTipoProceso(procesoAtender);
            Double duracion = 0.0;

            if (procesoAtender instanceof ArithmeticProcess) {
                duracion = ((ArithmeticProcess) procesoAtender).getTiempoServicio();
            } else if (procesoAtender instanceof IOProcess) {
                duracion = ((IOProcess) procesoAtender).getTiempoServicio();
            } else if (procesoAtender instanceof ConditionalProcess) {
                duracion = ((ConditionalProcess) procesoAtender).getTiempoServicio();
            } else if (procesoAtender instanceof LoopProcess) {
                duracion = ((LoopProcess) procesoAtender).getTiempoServicio();
            }

            long duracionMs = (long) (duracion * 1000);
            System.out.println();
            System.out.println(nombreProcesador + ": Atendiendo proceso ID: " + procesoAtender.getId() + " Tipo: " + tipo + " Tiempo de atención: " + duracion + " segundos.");
            System.out.println();
            try {
                Thread.sleep(duracionMs);
            } catch (Exception e) {
                System.out.println("Proceso interrumpido");
            }

            System.out.println();
            System.out.println(nombreProcesador + ": Atendido proceso ID: " + procesoAtender.getId() + " Tipo: " + tipo + " Tiempo de atención: " + duracion + " segundos.");
            tiempoTotalProcesado += duracion;
            procesosCompletados++;
            System.out.println();
            imprimirPila();
            System.out.println();
        }
    }

    /**
    * Nombre: detenerEjecucion.
    * Método que detiene la ejecución de la simulación y muestra los datos finales.
    */
    public void detenerEjecucion() {
        this.activo = false;
        int procesosPendientes = this.stackProcesos.size();
        double tiempoPromedio = (procesosCompletados > 0) ? (tiempoTotalProcesado / procesosCompletados) : 0;
        System.out.println();
        System.out.println("--------Datos finales--------");
        System.out.println("Procesos atendidos: " + procesosCompletados);
        System.out.println("Procesos en pila (sin atenderse): " + procesosPendientes);
        System.out.println("Tiempo promedio de atención por proceso: " + tiempoPromedio + " segundos");
        System.out.println("Política utilizada: Last Come First Served (LCFS)");
        System.out.println();
        System.exit(0);
    }

    /**
    * Nombre: obtenerTipoProceso.
    * Método que obtiene el tipo de proceso como una cadena de texto.
    * @param proceso Proceso del cual se quiere obtener el tipo.
    * @return Tipo del proceso en formato de cadena.
    */
    private String obtenerTipoProceso(SimpleProcess proceso) {
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
    * Método que imprime el contenido actual de la pila de procesos.
    */
    private void imprimirPila() {
        synchronized (stackProcesos) {
            if (stackProcesos.isEmpty()) {
                System.out.println("La pila está vacía.");
            } else {
                System.out.print("Procesos en la pila: ");
                for (SimpleProcess proceso : stackProcesos) {
                    System.out.print(proceso.toString() + " ");
                }
                System.out.println();
            }
        }
    }

    /**
    * Nombre: generarNuevoId.
    * Método que genera un nuevo ID único y global para cada proceso.
    * @return Un nuevo ID incremental.
    */
    private synchronized int generarNuevoId() {
        return ++contadorGlobalId;
    }
}