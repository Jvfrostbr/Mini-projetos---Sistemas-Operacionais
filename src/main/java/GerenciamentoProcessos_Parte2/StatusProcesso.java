package GerenciamentoProcessos_Parte2;

public enum StatusProcesso {
    NOVO,
    PRONTO,
    EXECUTANDO,
    ESPERANDO   ,
    FINALIZADO,
    DESCONHECIDO;

    public static StatusProcesso fromString(String status) {
        try {
            return StatusProcesso.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return DESCONHECIDO;
        }
    }
}
