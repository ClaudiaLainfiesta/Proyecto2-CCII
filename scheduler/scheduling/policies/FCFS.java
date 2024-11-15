/*FCFS.java */
/**
** Hecho por: Maria Claudia Lainfiesta Herrera.
** Carnet: 24000149.
** Seccion: BN.
**/
/*Descripción: Clase que realiza la política First Come First Served, los procesos son atendidos en el orden en que llegan a la cola de procesos.*/

package scheduler.scheduling.policies;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

import scheduler.processing.*;

public class FCFS extends Policy implements Enqueable {
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

    /**
     * Constructor que inicializa los parámetros para la política de FCFS.
     * @param primeraParte tiempo mínimo de agregar procesos.
     * @param segundaParte tiempo máximo de agregar procesos.
     * @param arith tiempo de atención de procesos aritméticos.
     * @param io tiempo de atención de procesos input/output.
     * @param cont tiempo de atención de procesos condicionales.
     * @param loop tiempo de atención de procesos iterativos.
     */
    private static int idGeneradoGlobal = 0;
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

    @Override
    public void add(SimpleProcess P) {
        this.cola.add(P);
        this.size++;
        this.totalProcesses++;
    }

    @Override
    public void remove() {
        this.cola.poll();
        this.size--;
    }

    @Override
    public SimpleProcess next() {
        return this.cola.peek();
    }

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
                    System.out.println();
                    System.out.println("Saliendo del programa...");
                    System.out.println();
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
            e.printStackTrace();
        }
    }

    public void stopRunning() {
        this.running = false;
    }

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

    private synchronized int generarNuevoID() {
        return ++idGeneradoGlobal;
    }

    public void ejecucionDoble() {
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
                SimpleProcess procesoAtender = next();
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
                System.out.println("Atendiendo proceso ID: " + procesoAtender.getId() + " Tipo: " + tipoProceso + " Tiempo de atención: " + tiempoAtencion + " segundos.");
                System.out.println();
                try {
                    Thread.sleep(tiempoAtencionMs);
                } catch (Exception e) {
                    System.out.println("Proceso interrumpido");
                }
                System.out.println();
                System.out.println("Atendido proceso ID: " + procesoAtender.getId() + " Tipo: " + tipoProceso + " Tiempo de atención: " + tiempoAtencion + " segundos.");
                procesosAtendidos++;
                remove();
                System.out.println();
                imprimirCola();
                System.out.println();
            }
        });

        Thread atencionProcesos2 = new Thread(() -> {
            while (running) {
                SimpleProcess procesoAtender = next();
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
                System.out.println("Atendiendo proceso ID: " + procesoAtender.getId() + " Tipo: " + tipoProceso + " Tiempo de atención: " + tiempoAtencion + " segundos.");
                System.out.println();
                try {
                    Thread.sleep(tiempoAtencionMs);
                } catch (Exception e) {
                    System.out.println("Proceso interrumpido");
                }
                System.out.println();
                System.out.println("Atendido proceso ID: " + procesoAtender.getId() + " Tipo: " + tipoProceso + " Tiempo de atención: " + tiempoAtencion + " segundos.");
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
                    System.out.println();
                    System.out.println("Saliendo del programa...");
                    System.out.println();
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
            e.printStackTrace();
        }
    }

}