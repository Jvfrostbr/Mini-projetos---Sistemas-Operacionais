package GerenciamentoProcessos_Parte2;

import java.util.*;

public class Shortest_Remaining_Time_First extends Escalonador {

    public Shortest_Remaining_Time_First(List<Processo> todosProcessos) {
        super(todosProcessos);
        this.processosReady = new PriorityQueue<>(new ComparadorTempoRestante());
    }

    @Override
    public void adicionarProcesso(Processo processo) {
        processosNaoChegados.add(processo);
        processosNaoChegados.sort(Comparator.comparingInt(Processo::getTempoChegada));
    }

    @Override
    public void executarProcessos() throws InterruptedException {
        while (!processosNaoChegados.isEmpty() || !processosReady.isEmpty() || processoAtual != null) {
            // Mover processos que chegaram para fila de prontos
            super.adicionarProcessosReady();

            // Selecionar o processo com menor tempo restante
            if (!processosReady.isEmpty() && (processoAtual == null || processoAtual.getTempoRestante() > processosReady.peek().getTempoRestante()
                    || processoAtual.getTempoChegada() <= this.tempoTotal)) {
                // Preemptar o processo atual se for diferente do próximo
                if (processoAtual != null) {
                    processoAtual.pausarExecucao();
                    processoAtual.setStatus(StatusProcesso.ESPERANDO);
                    processosReady.add(processoAtual);
                }
                processoAtual = processosReady.poll();
            }

            if (processoAtual != null) {
                processoAtual.permitirExecucao();
                processoAtual.setStatus(StatusProcesso.EXECUTANDO);
                // Executa por 1 unidade de tempo (quantum fixo para SRTF)
                processoAtual.setTempoRestante(processoAtual.getTempoRestante() - 1);
                processoAtual.setTempoCPU(processoAtual.getTempoCPU() + 1);
                processosReady.forEach(p -> p.setTempoEspera(p.getTempoEspera() + 1));

                Thread.sleep(1000); // Simula 1 segundo de execução

                mostrarInformacoes();

                if (processoAtual.getTempoRestante() <= 0) {
                    processoAtual.setTempoTurnAround(tempoTotal);
                    processoAtual.setStatus(StatusProcesso.FINALIZADO);
                    processoAtual.pausarExecucao();
                    processosTerminated.add(processoAtual);
                    processoAtual = null; // Libera o slot para o próximo processo
                }
            }
            tempoTotal++;
        }
    }

}
