/*LoopProcess.java */
/**
** Hecho por:
** Carnet:
** Seccion: BN
**/
/*Descripcion: */

package scheduler.processing;

public class LoopProcess extends SimpleProcess{
    protected static Double tiempoServicio;
    public LoopProcess(int id, Double tiempoServicio) {
        super(id);
        this.tiempoServicio = tiempoServicio;
    }
    public Double getTiempoServicio(){
        return this.tiempoServicio;
    }
    public String getTipo(){
        return "L";
    }
    @Override
    public String toString(){
        return "[ID:" + this.id + " | Tiempo de Atencion: " + this.tiempoServicio + " | Tipo: " + getTipo() + "]";
    }
}
