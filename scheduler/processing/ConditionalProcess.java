/*ConditionalProcess.java */
/**
** Hecho por:
** Carnet:
** Seccion: BN
**/
/*Descripcion: */

package scheduler.processing;

public class ConditionalProcess extends SimpleProcess{
    protected static Double tiempoServicio;
    public ConditionalProcess(int id, Double tiempoServicio) {
        super(id);
        this.tiempoServicio = tiempoServicio;
    }
    public Double getTiempoServicio(){
        return this.tiempoServicio;
    }

    
}
