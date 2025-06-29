package GerenciamentoArquivos;

public class AlocadorFAT implements Alocador {

    @Override
    public int alocarNoBloco(String nome, int tamanhoDadoKB, Object objetoAlocado) {
        return 0;
    }

    @Override
    public void desalocarBloco(String nome) {

    }

    @Override
    public void verificarFragmentacaoInterna(String nome, int tamanhoKB) {

    }

    @Override
    public void mostrarBlocos() {

    }

    @Override
    public int contarBlocosLivres() {
        return 0;
    }

    @Override
    public int calcularBlocosNecessarios(int tamanhoArquivoOuDiretorioKB) {
        return 0;
    }
}
