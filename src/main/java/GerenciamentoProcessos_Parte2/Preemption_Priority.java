package GerenciamentoProcessos_Parte2;

import java.util.*;

public class Preemption_Priority extends Escalonador {

    public Preemption_Priority(List<Processo> todosProcessos) {
        super(todosProcessos);
        this.processosReady = new PriorityQueue<>(new ComparadorPrioridade());
    }

    @Override
    public void executarProcessos() throws InterruptedException {
        while (!processosReady.isEmpty() || !processosNaoChegados.isEmpty() || processoAtual != null) {
            // Mover processos que chegaram para a fila de prontos
            super.adicionarProcessosReady();

            // Verifica se há preempção
            if (!processosReady.isEmpty()) {
                if (processoAtual == null) {
                    processoAtual = processosReady.poll();
                } else {
                    Processo melhorProcesso = processosReady.peek();
                    if (processoAtual.getPrioridade() > melhorProcesso.getPrioridade() &&
                            processoAtual.getTempoChegada() <= this.tempoTotal) {
                        processosReady.add(processoAtual);
                        processoAtual.pausarExecucao();
                        processoAtual.setStatus(StatusProcesso.ESPERANDO);
                        processoAtual = processosReady.poll();
                    }
                }
            }

            if (processoAtual != null) {
                processoAtual.setStatus(StatusProcesso.EXECUTANDO);
                // Executa o processo atual por 1 unidade de tempo
                processoAtual.permitirExecucao();
                processoAtual.setTempoRestante(processoAtual.getTempoRestante() - 1);
                processoAtual.setTempoCPU(processoAtual.getTempoCPU() + 1);

                processosReady.forEach(p -> p.setTempoEspera(p.getTempoEspera() + 1));

                // Simula a execução (pode ajustar ou remover se não quiser delays)
                Thread.sleep(1000);

                mostrarInformacoes();

                if (processoAtual.getTempoRestante() <= 0) {
                    processoAtual.setStatus(StatusProcesso.FINALIZADO);
                    processosTerminated.add(processoAtual);
                    processoAtual.setTempoTurnAround(tempoTotal);
                    processoAtual.pausarExecucao();
                    processoAtual = null; // Processo finalizado, limpa a referência
                }
            }
            tempoTotal++;
        }
    }
}
