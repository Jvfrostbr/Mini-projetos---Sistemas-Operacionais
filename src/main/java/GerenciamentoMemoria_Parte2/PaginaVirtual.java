package GerenciamentoMemoria_Parte2;

public class PaginaVirtual  implements Pagina{
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
    public String getNome() {
        return nome;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public int getIdProcesso() {
        return this.idProcesso;
    }
}
