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
    public String getTipo(){
        return "IO";
    }
    @Override
    public String toString(){
        return "[ID:" + this.id + " | Tiempo de Atencion: " + this.tiempoServicio + " | Tipo: " + getTipo() + "]";
    }
}
