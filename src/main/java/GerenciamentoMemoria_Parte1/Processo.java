package GerenciamentoMemoria_Parte1;

public class Processo {
    private static int proximoId = 1;
    private String nome;
    private int tamanho;
    private int espacoInicial;
    private int ID;

    //CONSTRUTOR
    public Processo(String nome, int tamanho) {
        this.nome = nome;
        this.tamanho = tamanho;
        this.espacoInicial = -1; // Inicialmente não alocado
        this.ID = proximoId++;
    }

    //GETS AND SETS:
    public String getNome() {
        return nome;
    }
    public int getTamanho() {
        return tamanho;
    }
    public int getEspacoInicial() {
        return espacoInicial;
    }
    public void setEspacoInicial(int espacoInicial) {
        this.espacoInicial = espacoInicial;
    }

    @Override
    public String toString() {
        return "Processo " + nome + " (ID: " + ID + "| Tamanho:" + tamanho + "MB )";
    }
}