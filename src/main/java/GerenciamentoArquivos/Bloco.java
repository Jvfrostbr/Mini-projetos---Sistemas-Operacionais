package GerenciamentoArquivos;

public class Bloco {
    private int id;
    private boolean ocupado;
    private String nome;
    private Integer proximoBloco;
    private Object objetoAlocado; // armazena a instância do objeto alocado (diretório ou arquivo)

    // Construtor:
    public Bloco(int id) {
        this.id = id;
        this.ocupado = false;
        this.nome = null;
        this.proximoBloco = null;
        this.objetoAlocado = null;
    }

    // Métodos:
    public void alocar(String nome, Object objetoAlocado) {
        this.ocupado = true;
        this.nome = nome;
        this.objetoAlocado = objetoAlocado;
    }

    public void desalocar() {
        this.ocupado = false;
        this.nome = null;
        this.proximoBloco = null;
        this.objetoAlocado = null;
    }

    // Getters e Setters:
    public int getId() {
        return id;
    }

    public Object getObjetoAlocado() {
        return objetoAlocado;
    }

    public boolean isOcupado() {
        return ocupado;
    }

    public void setOcupado(boolean ocupado) {
        this.ocupado = ocupado;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getProximoBloco() {
        return proximoBloco;
    }

    public void setProximoBloco(Integer proximoBloco) {
        this.proximoBloco = proximoBloco;
    }
}