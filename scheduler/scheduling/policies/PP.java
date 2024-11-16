/*FCFS.java */
/**
** Hecho por: Adriel Levi Argueta Caal
** Carnet: 24003171.
** Seccion: BN.
**/

package scheduler.scheduling.policies;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import scheduler.processing.*;


public class PP extends Policy implements Enqueable {
    private final ConcurrentLinkedQueue<SimpleProcess> colaPrioridad1; // IOProcess
    private final ConcurrentLinkedQueue<SimpleProcess> colaPrioridad2; // ArithmeticProcess
    private final ConcurrentLinkedQueue<SimpleProcess> colaPrioridad3; // ConditionalProcess y LoopProcess

    private final double arithTime;
    private final double ioTime;
    private final double condTime;
    private final double loopTime;
    private final double tiempoMinimo;
    private final double tiempoMaximo;
    private volatile boolean ejecutar; // Controla la ejecución del bucle principal
    private boolean running;
    private Double totalTiempoAtencion = 0.0;

    private int procesosAtendidos;

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
        this.ejecutar = true;
        this.procesosAtendidos = 0;
        this.running = true;
    }

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

    public void ejecucion() {
        Thread generacionProcesos = new Thread(() -> {
            int idGenerado = 0;
            while (running) {
                Random randTiempo = new Random();
                Random randProceso = new Random();
                long primeraParteLong = (long) (tiempoMinimo * 1000);
                long segundaParteLong = (long) (tiempoMaximo * 1000);
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
                    procesoGenerado = new ArithmeticProcess(idGenerado, this.arithTime);
                } else if (procesoEleccion == 1) {
                    procesoGenerado = new IOProcess(idGenerado, this.ioTime);
                } else if (procesoEleccion == 2) {
                    procesoGenerado = new ConditionalProcess(idGenerado, this.condTime);
                } else if (procesoEleccion == 3) {
                    procesoGenerado = new LoopProcess(idGenerado, this.loopTime);
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
    private SimpleProcess generarProcesoAleatorio(int id) {
        Random random = new Random();
        int tipoProceso = random.nextInt(4);

        switch (tipoProceso) {
            case 0:
                return new ArithmeticProcess(id, arithTime);
            case 1:
                return new IOProcess(id, ioTime);
            case 2:
                return new ConditionalProcess(id, condTime);
            case 3:
                return new LoopProcess(id, loopTime);
            default:
                throw new IllegalStateException("Tipo de proceso inesperado: " + tipoProceso);
        }
    }

    private void simularAtencionProceso(SimpleProcess proceso) {
        double tiempoServicio = 0.0;
        if (proceso instanceof ArithmeticProcess) {
            tiempoServicio = arithTime;
        } else if (proceso instanceof IOProcess) {
            tiempoServicio = ioTime;
        } else if (proceso instanceof ConditionalProcess) {
            tiempoServicio = condTime;
        } else if (proceso instanceof LoopProcess) {
            tiempoServicio = loopTime;
        }

        try {
            Thread.sleep((long) (tiempoServicio * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String determinarTipo(SimpleProcess proceso) {
        if (proceso instanceof ArithmeticProcess) {
            return "ArithmeticProcess";
        } else if (proceso instanceof IOProcess) {
            return "IOProcess";
        } else if (proceso instanceof ConditionalProcess) {
            return "ConditionalProcess";
        } else if (proceso instanceof LoopProcess) {
            return "LoopProcess";
        }
        return "Desconocido";
    }

    private void imprimirCola() {
        System.out.println("Cola de prioridad 1 (IOProcess): " + colaPrioridad1);
        System.out.println("Cola de prioridad 2 (ArithmeticProcess): " + colaPrioridad2);
        System.out.println("Cola de prioridad 3 (ConditionalProcess y LoopProcess): " + colaPrioridad3);
    }

    public void detener() {
        ejecutar = false;
    }
    public void stopRunning() {
        this.running = false;
        //int procesosEnCola = this.cola.size();

        double tiempoPromedio = (procesosAtendidos > 0) ? (totalTiempoAtencion / procesosAtendidos) : 0;
        System.out.println();
        System.out.println("--------Datos finales--------");
        System.out.println("Procesos atendidos: " + procesosAtendidos);
        //System.out.println("Procesos en cola (sin atenderse): " + procesosEnCola);
        System.out.println("Tiempo promedio de atención por proceso: " + tiempoPromedio + " segundos");
        System.out.println("Política utilizada: First Come First Served (FCFS)");
        System.out.println();

        System.exit(0);
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
}
