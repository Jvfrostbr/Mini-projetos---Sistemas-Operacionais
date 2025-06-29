package GerenciamentoMemoria_Parte2;

public class PaginaVirtual {
    private int idProcesso;
    private String nome;
    private int id;

    //CONSTRUTOR
    public PaginaVirtual(String nome,int idProcesso, int id) {
        this.idProcesso = idProcesso;
        this.nome = nome;
        this.id = id;
    }

    //MÉTODOS:

    //GETS AND SETS
    public int getIdProcesso() {
        return idProcesso;
    }

    public String getNome() {
        return nome;
    }
}
