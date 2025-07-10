package GerenciamentoArquivos;

public class Arquivo {
    private String nome;
    private int tamanho; // em KB
    private int blocoInicial;
    private int hashParidade; // Hash para simulação de paridade

    // Construtor:
    public Arquivo(String nome, int tamanho, int blocoInicial) {
        this.nome = nome;
        this.tamanho = tamanho;
        this.blocoInicial = blocoInicial;
        this.hashParidade = calcularHashParidade();
    }

    private int calcularHashParidade() {
        // Usamos nome + tamanho para gerar um valor único para cálculos de paridade
        return (nome.hashCode() ^ tamanho);
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

    public void setBlocoInicial(int blocoInicial) {
        this.blocoInicial = blocoInicial;
    }

    public int getHashParidade() {
        return hashParidade;
    }
}