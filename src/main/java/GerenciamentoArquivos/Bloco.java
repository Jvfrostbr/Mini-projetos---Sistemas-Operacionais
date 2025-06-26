package GerenciamentoArquivos;

public class Bloco {
    private int id;
    private boolean ocupado;
    private String nomeArquivo;
    private Integer proximoBloco;

    // Construtor:
    public Bloco(int id) {
        this.id = id;
        this.ocupado = false;
        this.nomeArquivo = null;
        this.proximoBloco = null;
    }

    // Métodos:
    public void alocar(String nomeArquivo) {
        this.ocupado = true;
        this.nomeArquivo = nomeArquivo;
    }

    public void desalocar() {
        this.ocupado = false;
        this.nomeArquivo = null;
        this.proximoBloco = null;
    }

    // Getters e Setters:


    public int getId() {
        return id;
    }

    public boolean isOcupado() {
        return ocupado;
    }

    public void setOcupado(boolean ocupado) {
        this.ocupado = ocupado;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public Integer getProximoBloco() {
        return proximoBloco;
    }

    public void setProximoBloco(Integer proximoBloco) {
        this.proximoBloco = proximoBloco;
    }
}