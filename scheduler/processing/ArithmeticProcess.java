/*ArithmeticProcess.java */
/**
** Hecho por: Maria Claudia Lainfiesta Herrera.
** Carnet: 24000149.
** Seccion: BN.
**/
/*Descripción: Esta clase representa un proceso aritmético que hereda de la clase SimpleProcess. Su función principal es almacenar el tiempo de servicio para un proceso específico y proporcionar información sobre su tipo y tiempo de atención.*/

package scheduler.processing;

public class ArithmeticProcess extends SimpleProcess{
    protected static Double tiempoServicio;

    /**
     * Constructor que crea un proceso aritmético con un ID específico y un tiempo de servicio.
     * @param id El identificador único del proceso.
     * @param tiempoServicio El tiempo que este proceso requiere para ser atendido.
     */
    public ArithmeticProcess(int id, Double tiempoServicio) {
        super(id);
        this.tiempoServicio = tiempoServicio;
    }

    /**
     * Nombre: getTiempoServicio.
     * Método que devuelve el tiempo de servicio del proceso aritmético.
     * @return El tiempo de servicio de este proceso.
     */
    public Double getTiempoServicio(){
        return this.tiempoServicio;
    }

    /**
     * Nombre: getTipo.
     * Método que devuelve el tipo de proceso como una cadena de texto.
     * @return El tipo de proceso, que en este caso siempre es "A" para aritmético.
     */
    public String getTipo(){
        return "A";
    }

    /**
    * Nombre: toString.
    * Método que devuelve una representación en cadena de texto del proceso aritmético.
    * @return Una cadena que representa el ID, tiempo de atención y tipo del proceso.
    */
    @Override
    public String toString(){
        return "[ID:" + this.id + " | Tiempo de Atencion: " + this.tiempoServicio + " | Tipo: " + getTipo() + "]";
    }
}
