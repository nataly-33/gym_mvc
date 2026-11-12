package model.strategy;

public class EvaluacionContext {

    private EvaluacionStrategy strategy;

    public EvaluacionContext(EvaluacionStrategy strategy) {
        if (strategy == null) {
            this.strategy = new EvaluacionOMSStrategy();
        } else {
            this.strategy = strategy;
        }
    }

    public void setStrategy(EvaluacionStrategy strategy) {
        this.strategy = (strategy != null) ? strategy : new EvaluacionOMSStrategy();
    }

    public EvaluacionStrategy getStrategy() {
        return this.strategy;
    }

    public String ejecutarEvaluacion(double weight, double waist, double glutes, double bodyFat, double chest) {
        return this.strategy.evaluar(weight, waist, glutes, bodyFat, chest);
    }
}
