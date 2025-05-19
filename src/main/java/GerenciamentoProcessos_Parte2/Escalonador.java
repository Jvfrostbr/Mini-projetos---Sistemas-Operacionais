package GerenciamentoProcessos_Parte2;

import java.util.*;

public abstract class Escalonador {
    protected int tempoTotal = 0;
    protected Processo processoAtual = null;
    protected List<Processo> processosNaoChegados;
    protected Queue<Processo> processosReady;
    protected List<Processo> processosTerminated = new ArrayList<>();

    public Escalonador(List<Processo> listaProcessosInicial) {
        this.processosNaoChegados = new ArrayList<>(listaProcessosInicial);
        this.processosNaoChegados.sort(Comparator.comparingInt(Processo::getTempoChegada));
    }

    public void adicionarProcesso(Processo processo){processosNaoChegados.add(processo);}

    public abstract void executarProcessos() throws InterruptedException;

    public void adicionarProcessosReady() {
        Iterator<Processo> iterator = processosNaoChegados.iterator();
        if (iterator.hasNext()) {
            mostrarInformacoes();
            try {
                Thread.sleep(1000); // Simula 1 segundo de espera
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        while (iterator.hasNext()) {
            Processo p = iterator.next();
            if (p.getTempoChegada() <= tempoTotal) {
                p.start();
                p.setStatus(StatusProcesso.PRONTO);
                processosReady.add(p);
                iterator.remove();
            }
        }
    }

    public void mostrarInformacoes() {
        System.out.print("\033[H\033[2J"); // Limpa o terminal (melhor visualizar no CMD)
        System.out.flush();
        System.out.println("Simulador - " + this.getClass().getSimpleName());
        if (this instanceof Round_Robin) {
            System.out.println("Quantum: " + ((Round_Robin) this).getQuantum());
        }
        System.out.println("Tempo total de simulação: " + tempoTotal + "s");
        System.out.println("Processo atual: " + (processoAtual != null ? processoAtual.getNome() : "Nenhum"));
        System.out.println();

        System.out.printf("%-5s %-12s %-10s %-12s %-15s %-15s %-15s %-10s%n",
                "PID", "NOME", "PRIORIDADE", "STATUS", "TEMPO RESTANTE", "TEMPO CPU", "CHEGADA", "TIPO");

        List<Processo> todos = new ArrayList<>();
        if (processoAtual != null) {
            todos.add(processoAtual);
        }
        todos.addAll(processosNaoChegados);
        todos.addAll(processosReady);
        todos.addAll(processosTerminated);

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

    public List<Processo> getProcessosNaoChegados() {
        return processosNaoChegados;
    }

    public Queue<Processo> getProcessosReady() {
        return processosReady;
    }

    public void setProcessosReady(Queue<Processo> processosReady) {
        this.processosReady = processosReady;
    }

    public void setProcessosTerminated(List<Processo> processosTerminated) {
        this.processosTerminated = processosTerminated;
    }

    public void setProcessosNaoChegados(List<Processo> processosNaoChegados) {
        this.processosNaoChegados = processosNaoChegados;
    }

    public List<Processo> getProcessosTerminated() {
        return processosTerminated;
    }
}
