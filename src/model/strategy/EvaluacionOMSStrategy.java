package model.strategy;

public class EvaluacionOMSStrategy implements EvaluacionStrategy {

    @Override
    public String getNombreMetodo() {
        return "Diagnóstico Clínico OMS (IMC y Cintura)";
    }

    @Override
    public String evaluar(double weight, double waist, double glutes, double bodyFat, double chest) {
        if (weight <= 0) {
            return "Error: El peso debe ser mayor a 0 kg para calcular el IMC.";
        }

        double height = 1.60;
        double imc = weight / (height * height);

        String clasificacionIMC;
        if (imc < 18.5) {
            clasificacionIMC = "Bajo peso";
        } else if (imc < 25.0) {
            clasificacionIMC = "Normopeso / Peso Saludable";
        } else if (imc < 30.0) {
            clasificacionIMC = "Sobrepeso";
        } else if (imc < 35.0) {
            clasificacionIMC = "Obesidad Grado I";
        } else {
            clasificacionIMC = "Obesidad Grado II";
        }

        String alertaCintura = (waist > 88.0) 
            ? "ALERTA: Perímetro de cintura elevado (>88 cm)." 
            : "Perímetro de cintura aceptable.";

        return String.format(
            "[%s]\n• IMC: %.2f kg/m²\n• Diagnóstico: %s\n• Cintura: %.1f cm — %s",
            getNombreMetodo(), imc, clasificacionIMC, waist, alertaCintura
        );
    }
}
