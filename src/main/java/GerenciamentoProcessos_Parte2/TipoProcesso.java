package GerenciamentoProcessos_Parte2;

public enum TipoProcesso     {
    CPU_BOUND("CPU_BOUND"),
    IO_BOUND("IO_BOUND");

    private final String descricaoEnum;

    TipoProcesso(String descricaoEnum) {
        this.descricaoEnum = descricaoEnum;
    }

    public String getDescricaoEnum() {
        return descricaoEnum;
    }
}

