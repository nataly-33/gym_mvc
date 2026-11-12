package model.state;

public interface EstadoPlan {
    String getNombreEstado();
    boolean puedeEditar();
    void activar(PlanContext context);
    void completar(PlanContext context);
    void cancelar(PlanContext context);
    void reiniciarBorrador(PlanContext context);
}
