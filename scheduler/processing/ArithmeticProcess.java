/*ArithmeticProcess.java */
/**
** Hecho por:
** Carnet:
** Seccion: BN
**/
/*Descripcion: */

package scheduler.processing;

public class ArithmeticProcess extends SimpleProcess{
    protected static Double tiempoServicio;
    public ArithmeticProcess(int id, Double tiempoServicio) {
        super(id);
        this.tiempoServicio = tiempoServicio;
    }
    public Double getTiempoServicio(){
        return this.tiempoServicio;
    }

}
