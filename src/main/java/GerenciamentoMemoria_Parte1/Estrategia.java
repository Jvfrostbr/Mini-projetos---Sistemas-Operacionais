package GerenciamentoMemoria_Parte1;

public enum Estrategia {
    FIRST_FIT("First Fit"),
    BEST_FIT("Best Fit"),
    WORST_FIT("Worst Fit");

    private final String descricao;

    Estrategia(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
