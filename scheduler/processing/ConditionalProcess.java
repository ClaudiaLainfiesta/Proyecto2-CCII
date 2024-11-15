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
    public String getTipo(){
        return "C";
    }
    @Override
    public String toString(){
        return "[ID:" + this.id + " | Tiempo de Atencion: " + this.tiempoServicio + " | Tipo: " + getTipo() + "]";
    }
}
