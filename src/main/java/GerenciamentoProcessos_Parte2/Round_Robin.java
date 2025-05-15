package GerenciamentoProcessos_Parte2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static java.lang.Thread.sleep;

public class Round_Robin implements Escalonador {
    private int quantum;
    private Processo processoAtual;
    private Queue<Processo> processosReady;
    private List<Processo> processosTerminated;
    private List<Processo> processosNaoChegados; // Processos que ainda não chegaram
    private int tempoTotal;
    private int tempoExecutado; // Rastreia quanto tempo do quantum já foi usado

    // CONSTRUTOR
    public Round_Robin(int quantum, List<Processo> todosProcessos) {
        this.quantum = quantum;
        this.processosReady = new LinkedList<>();
        this.processosTerminated = new ArrayList<>();
        this.processosNaoChegados = new ArrayList<>(todosProcessos);
        this.processosNaoChegados.sort((p1, p2) -> p1.getTempoChegada() - p2.getTempoChegada()); // Ordena por tempo de chegada
        this.tempoTotal = 0;
        this.tempoExecutado = 0;
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

            // Seleciona o próximo processo se não houver um em execução
            if (processoAtual == null && !processosReady.isEmpty()) {
                processoAtual = processosReady.poll();
                tempoExecutado = 0; // Reinicia o contador do quantum
            }

            // Executa uma unidade de tempo do processo atual
            if (processoAtual != null) {
                processoAtual.setTempoRestante(processoAtual.getTempoRestante() - 1);
                tempoExecutado++;
                tempoTotal++;

                // Verifica se o processo terminou ou esgotou o quantum
                if (processoAtual.getTempoRestante() == 0) {
                    processosTerminated.add(processoAtual);
                    processoAtual = null;
                } else if (tempoExecutado >= quantum) {
                    processosReady.add(processoAtual); // Reinsere na fila
                    processoAtual = null;
                }
            } else {
                // Avança o tempo se não houver processos prontos
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
