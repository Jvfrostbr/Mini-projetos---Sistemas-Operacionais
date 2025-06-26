package GerenciamentoArquivos;

public class Arquivo {
    private String nome;
    private int tamanho; // em KB
    private int blocoInicial;

    // Construtor:
    public Arquivo(String nome, int tamanho, int blocoInicial) {
        this.nome = nome;
        this.tamanho = tamanho;
        this.blocoInicial = blocoInicial;
    }

    // Getters e Setters:
    public String getNome() {
        return nome;
    }

    public int getTamanho() {
        return tamanho;
    }

    public int getBlocoInicial() {
        return blocoInicial;
    }

    public String toString() {
        return String.format("Arquivo: %s | Tamanho: %dKB | Bloco Inicial: %d", nome, tamanho, blocoInicial);
    }
}