package GerenciamentoArquivos;

public class Arquivo {
    private String nome;
    private int tamanho; // em KB
    private int blocoInicial;
    private int hashParidade; // Hash para simulação de paridade
    private String conteudo;
    private boolean protegido;
    private String senha;

    // Construtor:
    public Arquivo(String nome, int tamanho, int blocoInicial, String conteudo, boolean protegido, String senha) {
        this.nome = nome;
        this.tamanho = tamanho;
        this.blocoInicial = blocoInicial;
        this.hashParidade = calcularHashParidade();
        this.conteudo = conteudo;
        this.protegido = protegido;
        if (protegido){
            this.senha = senha;
        }
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

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setTamanho(int tamanho) {
        this.tamanho = tamanho;
    }

    public void setHashParidade(int hashParidade) {
        this.hashParidade = hashParidade;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public boolean isProtegido() {
        return protegido;
    }

    public void setProtegido(boolean protegido) {
        this.protegido = protegido;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}