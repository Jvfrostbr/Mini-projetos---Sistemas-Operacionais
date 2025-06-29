package GerenciamentoMemoria_Parte2;

public class PaginaFisica {
    private int idProcesso;
    private String nome;
    private boolean bitUso;
    private long ultimoTempoUsado;
    private int id;

    //CONSTRUTOR
    public PaginaFisica(String nome ,int idProcesso, int id) {
        this.idProcesso = idProcesso;
        this.nome = nome;
        this.bitUso = false;
        this.id = id;
    }

    //MÉTODOS:
    public void atualizarTempoDeUso() {
        this.ultimoTempoUsado = System.nanoTime();
    }

    //GETS AND SETS
    public int getIdProcesso() {
        return idProcesso;
    }

    public String getNome() {
        return nome;
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
