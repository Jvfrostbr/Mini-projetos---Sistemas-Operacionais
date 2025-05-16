package GerenciamentoProcessos_Parte2;

import java.util.*;

public class Round_Robin implements Escalonador {
    private int quantum;
    private int tempoTotal;
    private Processo processoAtual;
    private List<Processo> processosNaoChegados;
    private Queue<Processo> processosReady;
    private List<Processo> processosTerminated;

    public Round_Robin(int quantum, List<Processo> todosProcessos) {
        this.quantum = quantum;
        this.tempoTotal = 0;
        this.processosNaoChegados = new ArrayList<>(todosProcessos);
        this.processosReady = new LinkedList<>();
        this.processosTerminated = new ArrayList<>();
    }

    @Override
    public void adicionarProcesso(Processo processo) {
        processosNaoChegados.add(processo);
    }

    @Override
    public void executarProcessos() throws InterruptedException {
        while (!processosNaoChegados.isEmpty() || !processosReady.isEmpty() || processoAtual != null) {
            // Mover processos que chegaram para a fila de prontos
            Iterator<Processo> iterator = processosNaoChegados.iterator();
            while (iterator.hasNext()) {
                Processo p = iterator.next();
                if (p.getTempoChegada() <= tempoTotal) {
                    p.start();
                    p.setStatus(StatusProcesso.PRONTO);
                    processosReady.add(p);
                    iterator.remove();
                }
            }

            if (processoAtual == null && !processosReady.isEmpty()) {
                processoAtual = processosReady.poll();
                processoAtual.permitirExecucao();
                processoAtual.setStatus(StatusProcesso.EXECUTANDO);
                mostrarInformacoes();
            }

            if (processoAtual != null) {
                int tempoExecucao = Math.min(quantum, processoAtual.getTempoRestante());
                tempoTotal += tempoExecucao;
                processosReady.stream().forEach(p -> p.setTempoEspera(p.getTempoEspera() + 1));

                processoAtual.setTempoRestante(processoAtual.getTempoRestante() - tempoExecucao);
                processoAtual.setTempoCPU(processoAtual.getTempoCPU() + tempoExecucao);

                Thread.sleep(tempoExecucao * 1000); // Simula execução
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
            } else {
                Thread.sleep(100); // Espera se não há processo pronto
                tempoTotal += 1;
            }
        }
    }

    @Override
    public void mostrarInformacoes() {
        System.out.print("\033[H\033[2J"); // Limpa o terminal (melhor visualizar no CMD)
        System.out.flush();

        System.out.println("Simulador - Round Robin | Quantum: " + quantum);
        System.out.println("Tempo total de simulação: " + tempoTotal + "s");
        System.out.println();

        System.out.printf("%-5s %-12s %-10s %-12s %-15s %-15s %-15s %-10s%n",
                "PID", "NOME", "PRIORIDADE", "STATUS", "TEMPO RESTANTE", "TEMPO CPU", "CHEGADA", "TIPO");

        List<Processo> todos = new ArrayList<>();
        todos.addAll(processosNaoChegados);
        todos.addAll(processosReady);
        todos.addAll(processosTerminated);
        if (processoAtual != null && !todos.contains(processoAtual)) {
            todos.add(processoAtual);
        }

        for (Processo p : todos) {
            System.out.printf("%-5d %-12s %-10d %-12s %-15d %-15d %-15d %-10s%n",
                    p.getID(),
                    p.getNome(),
                    p.getPrioridade(),
                    p.getStatus().name(),
                    p.getTempoRestante(),
                    p.getTempoCPU(),
                    p.getTempoChegada(),
                    p.isIoBound() ? "I/O Bound" : "CPU Bound"
            );
        }
    }


    public int getQuantum() {
        return quantum;
    }

    public void setQuantum(int quantum) {
        this.quantum = quantum;
    }

    public int getTempoTotal() {
        return tempoTotal;
    }

    public void setTempoTotal(int tempoTotal) {
        this.tempoTotal = tempoTotal;
    }

    public Processo getProcessoAtual() {
        return processoAtual;
    }

    public void setProcessoAtual(Processo processoAtual) {
        this.processoAtual = processoAtual;
    }

    public List<Processo> getProcessosNaoChegados() {
        return processosNaoChegados;
    }
    @Override
    public void setProcessosNaoChegados(List<Processo> processosNaoChegados) {
        this.processosNaoChegados = processosNaoChegados;
    }

    public Queue<Processo> getProcessosReady() {
        return processosReady;
    }

    public void setProcessosReady(Queue<Processo> processosReady) {
        this.processosReady = processosReady;
    }

    public List<Processo> getProcessosTerminated() {
        return processosTerminated;
    }

    public void setProcessosTerminated(List<Processo> processosTerminated) {
        this.processosTerminated = processosTerminated;
    }

    @Override
    public void run() {
        try {
            executarProcessos();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Execução interrompida.");
        }
    }
}
