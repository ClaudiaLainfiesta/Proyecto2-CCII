/*IOProcess.java */
/**
** Hecho por:
** Carnet:
** Seccion: BN
**/
/*Descripcion: */

package scheduler.processing;

public class IOProcess extends SimpleProcess{
    protected static Double tiempoServicio;
    public IOProcess(int id, Double tiempoServicio) {
        super(id);
        this.tiempoServicio = tiempoServicio;
    }
    public Double getTiempoServicio(){
        return this.tiempoServicio;
    }

}
