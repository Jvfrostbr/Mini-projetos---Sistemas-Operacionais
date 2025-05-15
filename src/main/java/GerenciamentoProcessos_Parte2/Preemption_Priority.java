package GerenciamentoProcessos_Parte2;

import java.util.*;

public class Preemption_Priority implements Escalonador {
    private Queue<Processo> processosReady;
    private List<Processo> processosTerminated;
    private List<Processo> processosNaoChegados;
    private Processo processoAtual;
    private int tempoTotal;

    // Construtor
    public Preemption_Priority(List<Processo> todosProcessos) {
        this.processosReady = new PriorityQueue<>(new ComparadorPrioridade());
        this.processosTerminated = new ArrayList<>();
        this.processosNaoChegados = new ArrayList<>(todosProcessos);
        this.processosNaoChegados.sort(Comparator.comparingInt(Processo::getTempoChegada)); // Ordena por tempo de chegada
        this.tempoTotal = 0;
    }

    @Override
    public void adicionarProcesso(Processo processo) {
        processosReady.add(processo);
        verificarPreempcao(); // Verifica se o novo processo deve preemptar o atual
    }

    @Override
    public void executarProcessos() {
        while (!processosReady.isEmpty() || !processosNaoChegados.isEmpty() || processoAtual != null) {
            // Adiciona processos que chegaram no tempo atual
            while (!processosNaoChegados.isEmpty() && processosNaoChegados.get(0).getTempoChegada() <= tempoTotal) {
                Processo p = processosNaoChegados.remove(0);
                adicionarProcesso(p);
            }

            // Seleciona o processo de maior prioridade se não houver um em execução
            if (processoAtual == null && !processosReady.isEmpty()) {
                processoAtual = processosReady.poll();
            }

            // Executa o processo atual por 1 unidade de tempo
            if (processoAtual != null) {
                processoAtual.setTempoRestante(processoAtual.getTempoRestante() - 1);
                tempoTotal++;

                // Verifica se o processo terminou
                if (processoAtual.getTempoRestante() == 0) {
                    processosTerminated.add(processoAtual);
                    processoAtual = null;
                } else {
                    verificarPreempcao(); // Verifica preempção após cada unidade de tempo
                }
            } else {
                tempoTotal++; // Avança o tempo se não houver processos prontos
            }
        }
    }

    private void verificarPreempcao() {
        if (!processosReady.isEmpty()) {
            Processo proximo = processosReady.peek();
            // Preempta se o próximo processo tiver prioridade mais alta que o atual
            if (processoAtual != null && proximo.getPrioridade() < processoAtual.getPrioridade()) {
                processosReady.add(processoAtual);
                processoAtual = processosReady.poll();
            }
        }
    }

    @Override
    public void mostrarInformacoes() {
        System.out.println("Processos terminados:");
        for (Processo processo : processosTerminated) {
            System.out.println("ID: " + processo.getID() +
                    ", Nome: " + processo.getNome() +
                    ", Prioridade: " + processo.getPrioridade() +
                    ", Tempo total: " + processo.getTempoCPU());
        }
        System.out.println("Tempo total de execução: " + tempoTotal);
    }
}
