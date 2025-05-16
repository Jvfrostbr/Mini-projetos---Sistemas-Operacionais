package GerenciamentoProcessos_Parte2;

import java.util.*;

public class Preemption_Priority implements Escalonador {
    private PriorityQueue<Processo> processosReady;
    private List<Processo> processosNaoChegados;
    private List<Processo> processosTerminated;
    private Processo processoAtual;
    private int tempoTotal;

    public Preemption_Priority(List<Processo> todosProcessos) {
        this.processosReady = new PriorityQueue<>(new ComparadorPrioridade());
        this.processosNaoChegados = new ArrayList<>(todosProcessos);
        this.processosTerminated = new ArrayList<>();
        this.tempoTotal = 0;

        // Ordena por tempo de chegada
        processosNaoChegados.sort(Comparator.comparingInt(Processo::getTempoChegada));
    }

    @Override
    public void adicionarProcesso(Processo processo) {
        processosReady.add(processo);
    }

    @Override
    public void executarProcessos() {
        while (!processosReady.isEmpty() || !processosNaoChegados.isEmpty() || processoAtual != null) {
            // Mover processos que chegaram para a fila de prontos
            Iterator<Processo> iterator = processosNaoChegados.iterator();
            while (iterator.hasNext()) {
                Processo p = iterator.next();
                if (p.getTempoChegada() <= tempoTotal) {
                    adicionarProcesso(p);
                    iterator.remove();
                }
            }

            // Verifica se há preempção
            if (!processosReady.isEmpty()) {
                if (processoAtual == null) {
                    processoAtual = processosReady.poll();
                    processoAtual.setStatus(StatusProcesso.EXECUTANDO);
                } else {
                    if (processoAtual.getPrioridade() < processosReady.stream()
                            .mapToInt(Processo::getPrioridade)
                            .min()
                            .orElse(Integer.MAX_VALUE)) {
                        processosReady.add(processoAtual);
                        processoAtual.setStatus(StatusProcesso.ESPERANDO);
                        processoAtual = processosReady.poll();
                        processoAtual.setStatus(StatusProcesso.EXECUTANDO);
                    }
                }
            }

            if (processoAtual != null) {
                // Executa o processo atual por 1 unidade de tempo
                processoAtual.permitirExecucao();
                processoAtual.setTempoRestante(processoAtual.getTempoRestante() - 1);
                processoAtual.setTempoCPU(processoAtual.getTempoCPU() + 1);

                processosReady.stream().forEach(p -> p.setTempoEspera(p.getTempoEspera() + 1));

                mostrarInformacoes();

                // Simula a execução (pode ajustar ou remover se não quiser delays)
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Execução interrompida.");
                    break;
                }

                if (processoAtual.getTempoRestante() <= 0) {
                    processoAtual.setStatus(StatusProcesso.FINALIZADO);
                    processosTerminated.add(processoAtual);
                    processoAtual.setTempoTurnAround(tempoTotal);
                    processoAtual = null;
                }
            } else {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Execução interrompida.");
                    break;
                }
            }

            tempoTotal++;
        }
    }

    @Override
    public void mostrarInformacoes() {
        System.out.print("\033[H\033[2J"); // Limpa o terminal (melhor visualizar no CMD)
        System.out.flush();

        System.out.println("Simulador - Preemption Priority");
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

    public PriorityQueue<Processo> getProcessosReady() {
        return processosReady;
    }

    public void setProcessosReady(PriorityQueue<Processo> processosReady) {
        this.processosReady = processosReady;
    }

    public List<Processo> getProcessosNaoChegados() {
        return processosNaoChegados;
    }

    @Override
    public void setProcessosNaoChegados(List<Processo> processosNaoChegados) {
        this.processosNaoChegados = processosNaoChegados;
    }

    public List<Processo> getProcessosTerminated() {
        return processosTerminated;
    }

    public void setProcessosTerminated(List<Processo> processosTerminated) {
        this.processosTerminated = processosTerminated;
    }

    public Processo getProcessoAtual() {
        return processoAtual;
    }

    public void setProcessoAtual(Processo processoAtual) {
        this.processoAtual = processoAtual;
    }

    public int getTempoTotal() {
        return tempoTotal;
    }

    public void setTempoTotal(int tempoTotal) {
        this.tempoTotal = tempoTotal;
    }

    @Override
    public void run() {
        executarProcessos();
    }
}
