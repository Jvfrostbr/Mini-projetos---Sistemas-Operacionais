package GerenciamentoProcessos_Parte2;

import java.util.List;

public interface Escalonador extends Runnable {
    public void adicionarProcesso(Processo processo);
    public void executarProcessos() throws InterruptedException;
    public void mostrarInformacoes();
    void setProcessosNaoChegados(List<Processo> listaProcessosInicial);
    List<Processo> getProcessosTerminated();
}
