package GerenciamentoArquivos;

import GerenciamentoEntradaSaida_Parte2.RAID;

public interface Alocador {
    int alocarNoBloco(String nome, int tamanhoDadoKB, Object objetoAlocado);
    void desalocarBloco(String nome);
    void verificarFragmentacaoInterna(String nome, int tamanhoKB);
    void mostrarBlocos();
    int contarBlocosLivres();
    int calcularBlocosNecessarios(int tamanhoArquivoOuDiretorioKB);
    RAID getRaid();

}