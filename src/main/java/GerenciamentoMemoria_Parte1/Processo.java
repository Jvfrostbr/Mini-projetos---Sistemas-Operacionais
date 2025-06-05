package GerenciamentoMemoria_Parte1;

public class Processo {
    private String nome;
    private int tamanho;
    private int tempoEspera;
    private int espacoInicial;

    //CONSTRUTOR
    public Processo(String nome, int tamanho, int tempoEspera) {
        this.nome = nome;
        this.tamanho = tamanho;
        this.tempoEspera = tempoEspera;
    }

    //GETS AND SETS:
    public String getNome() {
        return nome;
    }
    public int getTamanho() {
        return tamanho;
    }
    public int getTempoEspera() {
        return tempoEspera;
    }
    public void setTempoEspera(int tempoEspera) {
        this.tempoEspera = tempoEspera;
    }
    public int getEspacoInicial() {
        return espacoInicial;
    }
    public void setEspacoInicial(int espacoInicial) {
        this.espacoInicial = espacoInicial;
    }

    @Override
    public String toString() {
        return "Processo{" + "nome=" + nome + ", tamanho=" + tamanho + '}';
    }
}