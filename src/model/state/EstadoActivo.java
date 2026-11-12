package model.state;

public class EstadoActivo implements EstadoPlan {

    @Override
    public String getNombreEstado() {
        return "Activo";
    }

    @Override
    public boolean puedeEditar() {
        return false;
    }

    @Override
    public void activar(PlanContext context) {
    }

    @Override
    public void completar(PlanContext context) {
        context.setEstado(new EstadoCompletado());
    }

    @Override
    public void cancelar(PlanContext context) {
        context.setEstado(new EstadoCancelado());
    }

    @Override
    public void reiniciarBorrador(PlanContext context) {
        context.setEstado(new EstadoBorrador());
    }
}
