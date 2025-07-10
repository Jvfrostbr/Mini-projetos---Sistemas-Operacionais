package GerenciamentoArquivos;

public class Bloco {
    private int id;
    private boolean ocupado;
    private String nome;
    private Integer proximoBloco;
    private Object objetoAlocado; // armazena a instância do objeto alocado (diretório ou arquivo)
    private boolean isParidade;  // Novo campo para indicar se o bloco é paridade
    private Integer valorParidade; // Novo campo para armazenar o valor de paridade

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
        this.isParidade = false;  // Resetando o estado de paridade ao desalocar
        this.valorParidade = null; // Resetando o valor de paridade ao desalocar
    }

    public void alocarComoParidade(int valorParidade) {
        this.ocupado = true;
        this.isParidade = true;
        this.valorParidade = valorParidade;
        this.nome = "PARIDADE_" + valorParidade;
        this.objetoAlocado = null;
        this.proximoBloco = null;
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

    public boolean isParidade() {
        return isParidade;
    }
    public Integer getValorParidade() {
        return valorParidade;
    }
    public void setValorParidade(Integer valorParidade) {
        this.valorParidade = valorParidade;
    }
}