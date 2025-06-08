package GerenciamentoMemoria_Parte2;

public class Pagina {
    private int idProcesso;
    private String idPagina;
    private boolean bitUso;
    private long ultimoTempoUsado;

    //CONSTRUTOR
    public Pagina(int idProcesso, String idPagina) {
        this.idProcesso = idProcesso;
        this.idPagina = idPagina;
        this.bitUso = false;
    }

    //MÉTODOS:
    public void atualizarTempoDeUso() {
        this.ultimoTempoUsado = System.nanoTime();
    }

    //GETS AND SETS
    public int getIdProcesso() {
        return idProcesso;
    }

    public String getIdPagina() {
        return idPagina;
    }

    public boolean getBitUso() {
        return bitUso;
    }

    public void setBitUso(boolean bitUso) {
        this.bitUso = bitUso;
    }
    public long getLastUsedTime() {
        return ultimoTempoUsado;
    }
}
