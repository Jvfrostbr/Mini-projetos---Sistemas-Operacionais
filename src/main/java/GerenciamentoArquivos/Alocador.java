package GerenciamentoArquivos;

public interface Alocador {
    int alocarNoBloco(String nome, int tamanhoDadoKB);
    void desalocarBloco(String nome);
    boolean arquivoExiste(String nome);
    void verificarFragmentacaoInterna(String nome, int tamanhoKB);
    void mostrarBlocos();
    int contarBlocosLivres();
    int calcularBlocosNecessarios(int tamanhoArquivoOuDiretorioKB);
}