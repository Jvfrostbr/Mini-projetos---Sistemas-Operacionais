package GerenciamentoProcessos_Parte2;

public class Processo extends Thread {
    private int ID;
    private String nome;
    private int prioridade;
    private boolean ioBound = false;
    private StatusProcesso status;
    private int tempoCPU = 0;
    private int tempoRestante;
    private int tempoChegada;
    private final Object lock = new Object();
    private boolean podeExecutar = false;
    private int tempoEspera = 0;
    private int tempoTurnAround = 0;

    public Processo(int ID, String nome, int prioridade, int tempoRestante, int tempoChegada, boolean ioBound) {
        this.ID = ID;
        this.nome = nome;
        this.prioridade = prioridade;
        this.status = StatusProcesso.NOVO;
        this.tempoRestante = tempoRestante;
        this.tempoChegada = tempoChegada;
        this.ioBound = ioBound;
    }

    public void permitirExecucao() {
        synchronized (lock) {
            podeExecutar = true;
            lock.notify();
        }
    }

    public void pausarExecucao() {
        synchronized (lock) {
            podeExecutar = false;
        }
    }

    @Override
    public void run() {
        while (tempoRestante > 0) {
            synchronized (lock) {
                while (!podeExecutar) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Processo{" +
                "ID=" + ID +
                ", nome='" + nome + '\'' +
                ", prioridade=" + prioridade +
                ", tempoCPU=" + tempoCPU +
                ", tempoRestante=" + tempoRestante +
                ", tempoChegada=" + tempoChegada +
                '}';
    }

    // GETTERS E SETTERS

    public int getID() { return ID; }

    public void setID(int ID) { this.ID = ID; }

    public String getNome() { return nome; }

    public void setNome(String nome) { this.nome = nome; }

    public int getPrioridade() { return prioridade; }

    public void setPrioridade(int prioridade) { this.prioridade = prioridade; }

    public int getTempoCPU() { return tempoCPU; }

    public void setTempoCPU(int tempoCPU) { this.tempoCPU = tempoCPU; }

    public int getTempoRestante() { return tempoRestante; }

    public void setTempoRestante(int tempoRestante) { this.tempoRestante = tempoRestante; }

    public int getTempoChegada() { return tempoChegada; }

    public void setTempoChegada(int tempoChegada) { this.tempoChegada = tempoChegada; }

    public StatusProcesso getStatus() { return status; }

    public void setStatus(StatusProcesso status) { this.status = status; }

    public int getTempoEspera() {
        return tempoEspera;
    }

    public void setTempoEspera(int tempoEspera) {
        this.tempoEspera = tempoEspera;
    }

    public int getTempoTurnAround() {
        return tempoTurnAround;
    }

    public void setTempoTurnAround(int tempoTurnAround) {
        this.tempoTurnAround = tempoTurnAround;
    }

    public boolean isIoBound() {
        return ioBound;
    }

    public void setIoBound(boolean ioBound) {
        this.ioBound = ioBound;
    }
}
