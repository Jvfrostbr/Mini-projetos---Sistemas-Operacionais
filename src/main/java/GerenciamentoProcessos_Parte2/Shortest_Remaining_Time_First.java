package GerenciamentoProcessos_Parte2;

import java.util.*;

public class Shortest_Remaining_Time_First implements Escalonador {
    private int tempoTotal;
    private Processo processoAtual;

    private List<Processo> processosNaoChegados;
    private PriorityQueue<Processo> processosReady;
    private List<Processo> processosTerminated;

    public Shortest_Remaining_Time_First(List<Processo> todosProcessos) {
        this.tempoTotal = 0;
        this.processoAtual = null;
        this.processosNaoChegados = new ArrayList<>(todosProcessos);
        this.processosNaoChegados.sort(Comparator.comparingInt(Processo::getTempoChegada));
        this.processosReady = new PriorityQueue<>(new ComparadorTempoRestante());
        this.processosTerminated = new ArrayList<>();
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

            // Selecionar o processo com menor tempo restante
            if (!processosReady.isEmpty() && (processoAtual == null || processoAtual.getTempoRestante() > processosReady.stream()
                    .mapToInt(Processo::getTempoRestante)
                    .min()
                    .orElse(Integer.MAX_VALUE))) {
                // Preemptar o processo atual se for diferente do próximo
                if (processoAtual != null) {
                    processoAtual.pausarExecucao();
                    processoAtual.setStatus(StatusProcesso.ESPERANDO);
                    processosReady.add(processoAtual);
                }
                processoAtual = processosReady.poll();
                processoAtual.permitirExecucao();
                processoAtual.setStatus(StatusProcesso.EXECUTANDO);
                mostrarInformacoes();
            }

            if (processoAtual != null) {
                // Executa por 1 unidade de tempo (quantum fixo para SRTF)
                tempoTotal += 1;
                processoAtual.setTempoRestante(processoAtual.getTempoRestante() - 1);
                processoAtual.setTempoCPU(processoAtual.getTempoCPU() + 1);
                processosReady.stream().forEach(p -> p.setTempoEspera(p.getTempoEspera() + 1));
                processoAtual.setTempoTurnAround(tempoTotal);

                Thread.sleep(1000); // Simula 1 segundo de execução

                if (processoAtual.getTempoRestante() <= 0) {
                    processoAtual.setStatus(StatusProcesso.FINALIZADO);
                    processoAtual.pausarExecucao();
                    processosTerminated.add(processoAtual);
                    processoAtual = null;
                }
            } else {
                // Nenhum processo para executar, avança o tempo
                tempoTotal++;
                Thread.sleep(100); // Pequena pausa para não consumir CPU inutilmente
            }
        }
    }

    @Override
    public void mostrarInformacoes() {
        System.out.print("\033[H\033[2J"); // Limpa o terminal (CMD)
        System.out.flush();

        System.out.println("Simulador - Shortest Remaining Time First");
        System.out.println("Tempo total de simulação: " + tempoTotal + "s\n");

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

    public Queue<Processo> getProcessosReady() {
        return processosReady;
    }

    public void setProcessosReady(Queue<Processo> processosReady) {
        this.processosReady = new PriorityQueue<>(processosReady);
    }

    public List<Processo> getProcessosTerminated() {
        return processosTerminated;
    }

    public void setProcessosTerminated(List<Processo> processosTerminated) {
        this.processosTerminated = processosTerminated;
    }

    public List<Processo> getProcessosNaoChegados() {
        return processosNaoChegados;
    }

    @Override
    public void setProcessosNaoChegados(List<Processo> processosNaoChegados) {
        this.processosNaoChegados = processosNaoChegados;
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
