package GerenciamentoProcessos_Parte2;

public interface Escalonador {
    public void adicionarProcesso(Processo processo);
    public void executarProcessos() throws InterruptedException;
    public void mostrarInformacoes();

}
