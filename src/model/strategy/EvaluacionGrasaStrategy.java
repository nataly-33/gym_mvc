package model.strategy;

public class EvaluacionGrasaStrategy implements EvaluacionStrategy {

    @Override
    public String getNombreMetodo() {
        return "Composición Corporal (Masa Grasa vs Masa Magra)";
    }

    @Override
    public String evaluar(double weight, double waist, double glutes, double bodyFat, double chest) {
        if (weight <= 0 || bodyFat <= 0) {
            return "Error: Peso y Porcentaje de Grasa deben ser mayores a 0.";
        }

        double grasaKg = weight * (bodyFat / 100.0);
        double masaMagraKg = weight - grasaKg;

        String estadoFitness;
        if (bodyFat < 14.0) {
            estadoFitness = "Atleta de Alto Rendimiento";
        } else if (bodyFat < 20.0) {
            estadoFitness = "Condición Fitness Óptima";
        } else if (bodyFat < 25.0) {
            estadoFitness = "Rango Aceptable";
        } else {
            estadoFitness = "Exceso de Adiposidad";
        }

        return String.format(
            "[%s]\n• Grasa Corporal: %.1f%%\n• Masa Grasa: %.2f kg\n• Masa Magra: %.2f kg\n• Clasificación: %s",
            getNombreMetodo(), bodyFat, grasaKg, masaMagraKg, estadoFitness
        );
    }
}
