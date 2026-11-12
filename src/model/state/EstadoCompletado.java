package model.state;

public class EstadoCompletado implements EstadoPlan {

    @Override
    public String getNombreEstado() {
        return "Completado";
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
    }

    @Override
    public void cancelar(PlanContext context) {
    }

    @Override
    public void reiniciarBorrador(PlanContext context) {
    }
}
