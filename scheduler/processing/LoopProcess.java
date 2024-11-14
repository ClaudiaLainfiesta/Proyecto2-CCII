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

}
