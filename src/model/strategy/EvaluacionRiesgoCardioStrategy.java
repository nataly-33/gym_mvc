package model.strategy;

public class EvaluacionRiesgoCardioStrategy implements EvaluacionStrategy {

    @Override
    public String getNombreMetodo() {
        return "Riesgo Cardiovascular (Índice Cintura/Cadera - ICC)";
    }

    @Override
    public String evaluar(double weight, double waist, double glutes, double bodyFat, double chest) {
        if (waist <= 0 || glutes <= 0) {
            return "Error: Cintura y Glúteos deben ser mayores a 0 cm para calcular el ICC.";
        }

        double icc = waist / glutes;
        String nivelRiesgo;
        String distribucionGrasa;

        if (icc < 0.85) {
            nivelRiesgo = "Bajo / Riesgo Coronario Mínimo";
            distribucionGrasa = "Distribución Ginoide";
        } else if (icc < 0.95) {
            nivelRiesgo = "Moderado";
            distribucionGrasa = "Distribución Mixta";
        } else {
            nivelRiesgo = "ELEVADO";
            distribucionGrasa = "Distribución Androide (Grasa visceral)";
        }

        return String.format(
            "[%s]\n• Ratio Cintura/Glúteos (ICC): %.2f\n• Nivel de Riesgo: %s\n• Patrón: %s",
            getNombreMetodo(), icc, nivelRiesgo, distribucionGrasa
        );
    }
}
