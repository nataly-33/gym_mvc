package model.strategy;

public interface EvaluacionStrategy {
    String getNombreMetodo();
    String evaluar(double weight, double waist, double glutes, double bodyFat, double chest);
}
