/*IOProcess.java */
/**
** Hecho por: Adriel Levi Argueta Caal.
** Carnet: 24003171.
** Seccion: BN.
**/
/*Descripción: Esta clase representa un proceso input/output que hereda de la clase SimpleProcess. Su función principal es almacenar el tiempo de servicio para un proceso específico y proporcionar información sobre su tipo y tiempo de atención.*/

package scheduler.processing;

public class IOProcess extends SimpleProcess{
    protected static Double tiempoServicio;

    /**
     * Constructor que crea un proceso input/output con un ID específico y un tiempo de servicio.
     * @param id El identificador único del proceso.
     * @param tiempoServicio El tiempo que este proceso requiere para ser atendido.
     */
    public IOProcess(int id, Double tiempoServicio) {
        super(id);
        this.tiempoServicio = tiempoServicio;
    }

    /**
     * Nombre: getTiempoServicio.
     * Método que devuelve el tiempo de servicio del proceso input/output.
     * @return El tiempo de servicio de este proceso.
     */
    public Double getTiempoServicio(){
        return this.tiempoServicio;
    }

    /**
     * Nombre: getTipo.
     * Método que devuelve el tipo de proceso como una cadena de texto.
     * @return El tipo de proceso, que en este caso siempre es "IO" para input/output.
     */
    public String getTipo(){
        return "IO";
    }

    /**
     * Nombre: setTiempoServicio.
     * Método que cambio el tiempo de servicio de proceso.
     * @param tiempoNuevo La nueva cantidad de tiempo.
     */
    public void setTiempoServicio(Double tiempoNuevo){
        this.tiempoServicio = tiempoNuevo;
    }

    /**
     * Nombre: toString.
     * Método que devuelve una representación en cadena de texto del proceso input/output.
     * @return Una cadena que representa el ID, tiempo de atención y tipo del proceso.
     */
    @Override
    public String toString(){
        return "[ID:" + this.id + " | Tiempo de Atencion: " + this.tiempoServicio + " | Tipo: " + getTipo() + "]";
    }
}
