package model.state;

public class EstadoBorrador implements EstadoPlan {

    @Override
    public String getNombreEstado() {
        return "Borrador";
    }

    @Override
    public boolean puedeEditar() {
        return true;
    }

    @Override
    public void activar(PlanContext context) {
        context.setEstado(new EstadoActivo());
    }

    @Override
    public void completar(PlanContext context) {
    }

    @Override
    public void cancelar(PlanContext context) {
        context.setEstado(new EstadoCancelado());
    }

    @Override
    public void reiniciarBorrador(PlanContext context) {
    }
}
