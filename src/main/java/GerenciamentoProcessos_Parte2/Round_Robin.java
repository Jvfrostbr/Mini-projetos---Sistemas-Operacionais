package GerenciamentoProcessos_Parte2;

import java.util.*;

public class Round_Robin extends Escalonador {
    private int quantum;

    public Round_Robin(int quantum, List<Processo> todosProcessos) {
        super(todosProcessos);
        this.quantum = quantum;
        this.processosReady = new LinkedList<>();
    }

    @Override
    public void executarProcessos() throws InterruptedException {
            while (!processosNaoChegados.isEmpty() || !processosReady.isEmpty() || processoAtual != null) {

                // Mover processos que chegaram para a fila de prontos
                super.adicionarProcessosReady();

                if (processoAtual == null && !processosReady.isEmpty()) {
                    processoAtual = processosReady.poll();
                    processoAtual.permitirExecucao();
                    processoAtual.setStatus(StatusProcesso.EXECUTANDO);
                    mostrarInformacoes();
                }

                if (processoAtual != null) {
                    int tempoExecucao = Math.min(quantum, processoAtual.getTempoRestante());
                    tempoTotal += tempoExecucao;
                    processosReady.forEach(p -> p.setTempoEspera(p.getTempoEspera() + tempoExecucao));

                    processoAtual.setTempoRestante(processoAtual.getTempoRestante() - tempoExecucao);
                    processoAtual.setTempoCPU(processoAtual.getTempoCPU() + tempoExecucao);

                    Thread.sleep(tempoExecucao * 1000L); // Simula execução
                    processoAtual.pausarExecucao();

                    if (processoAtual.getTempoRestante() <= 0) {
                        processoAtual.setStatus(StatusProcesso.FINALIZADO);
                        processosTerminated.add(processoAtual);
                        processoAtual.setTempoTurnAround(tempoTotal);
                    } else {
                        processoAtual.setStatus(StatusProcesso.ESPERANDO);
                        processosReady.add(processoAtual);
                    }

                    processoAtual = null;
                }
            }
    }
    public int getQuantum() {
        return quantum;
    }

    public void setQuantum(int quantum) {
        this.quantum = quantum;
    }

}
