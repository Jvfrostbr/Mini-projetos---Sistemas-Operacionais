package GerenciamentoProcessos_Parte2;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class Shortest_Remaining_Time_First implements Escalonador {
    public int tempoTotal;
    public Processo processoAtual;
    public Queue<Processo> processosReady;
    public List<Processo> processosTerminated;
    private List<Processo> processosNaoChegados; // Lista de processos que ainda não chegaram

    public Shortest_Remaining_Time_First(List<Processo> todosProcessos) {
        this.tempoTotal = 0;
        this.processoAtual = null;
        this.processosReady = new PriorityQueue<>(new ComparadorTempoRestante());
        this.processosTerminated = new ArrayList<>();
        this.processosNaoChegados = new ArrayList<>(todosProcessos);
        this.processosNaoChegados.sort((p1, p2) -> p1.getTempoChegada() - p2.getTempoChegada()); // Ordena por tempo de chegada
    }

    @Override
    public void adicionarProcesso(Processo processo) {
        processosReady.add(processo);
    }

    @Override
    public void executarProcessos() {
        while (!processosReady.isEmpty() || !processosNaoChegados.isEmpty() || processoAtual != null) {
            // Adiciona processos que chegaram no tempo atual
            while (!processosNaoChegados.isEmpty() && processosNaoChegados.get(0).getTempoChegada() <= tempoTotal) {
                Processo p = processosNaoChegados.remove(0);
                processosReady.add(p);
            }

            // Encontra o processo com menor tempo restante
            Processo proximo = processosReady.peek();
            if (proximo != null && (processoAtual == null ||
                    proximo.getTempoRestante() < processoAtual.getTempoRestante())) {
                // Preempta o processo atual se houver um mais curto
                if (processoAtual != null) {
                    processosReady.add(processoAtual);
                }
                processoAtual = processosReady.poll();
            }

            // Executa o processo atual por 1 unidade de tempo
            if (processoAtual != null) {
                processoAtual.setTempoRestante(processoAtual.getTempoRestante() - 1);
                tempoTotal++;

                if (processoAtual.getTempoRestante() == 0) {
                    processosTerminated.add(processoAtual);
                    processoAtual = null;
                }
            } else {
                // Nenhum processo para executar, avança o tempo
                tempoTotal++;
            }
        }
    }


    @Override
    public void mostrarInformacoes() {
        System.out.println("Processos terminados:");
        for (Processo processo : processosTerminated) {
            System.out.println("ID: " + processo.getID() + ", Nome: " + processo.getNome() + ", Tempo total: " + processo.getTempoCPU());
        }
        System.out.println("Tempo total de execução: " + tempoTotal);
    }
}
