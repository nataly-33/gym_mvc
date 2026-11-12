package model.state;

public class PlanContext {

    private EstadoPlan estadoActual;

    public PlanContext(EstadoPlan estadoInicial) {                                      
        if (estadoInicial == null) {                                                    
            this.estadoActual = new EstadoBorrador();                                   
        } else {
    this.estadoActual = estadoInicial;
        }
    }

    public void setEstado(EstadoPlan nuevoEstado) {
        this.estadoActual = nuevoEstado;
    }

    public EstadoPlan getEstado() {
        return this.estadoActual;
    }

    public String getNombreEstado() {
        return this.estadoActual.getNombreEstado();
    }

    public boolean puedeEditar() {
        return this.estadoActual.puedeEditar();
    }

    public void activar() {
        this.estadoActual.activar(this);
    }

    public void completar() {
        this.estadoActual.completar(this);
    }

    public void cancelar() {
        this.estadoActual.cancelar(this);
    }

    public void reiniciarBorrador() {
        this.estadoActual.reiniciarBorrador(this);
    }

    public void cargarEstado(String status) {
        if ("Activo".equals(status)) {
            this.estadoActual = new EstadoActivo();
        } else if ("Completado".equals(status)) {
            this.estadoActual = new EstadoCompletado();
        } else if ("Cancelado".equals(status)) {
            this.estadoActual = new EstadoCancelado();
        } else {
            this.estadoActual = new EstadoBorrador();
        }
    }
}
