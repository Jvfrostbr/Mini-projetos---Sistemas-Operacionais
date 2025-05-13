package Parte2;

public class Processo extends Thread{
    private int ID;
    private String nome;
    private int prioridade;
    private TipoProcesso tipoProcesso; // Tipo de processo (CPU-bound ou I/O-bound)
    private int tempoCPU; // Tempo total de CPU
    private int tempoRestante; // Tempo restante de CPU

    public enum TipoProcesso {
        CPU_BOUND,
        IO_BOUND
    }

    public Processo(int ID, String nome, int prioridade, TipoProcesso tipoProcesso, int tempoCPU) {
        this.ID = ID;
        this.nome = nome;
        this.prioridade = prioridade;
        this.tipoProcesso = tipoProcesso;
        this.tempoCPU = tempoCPU;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(int prioridade) {
        this.prioridade = prioridade;
    }

    public TipoProcesso getTipoProcesso() {
        return tipoProcesso;
    }

    public void setTipoProcesso(TipoProcesso tipoProcesso) {
        this.tipoProcesso = tipoProcesso;
    }

    public int getTempoCPU() {
        return tempoCPU;
    }

    public void setTempoCPU(int tempoCPU) {
        this.tempoCPU = tempoCPU;
    }

    public int getTempoRestante() {
        return tempoRestante;
    }

    public void setTempoRestante(int tempoRestante) {
        this.tempoRestante = tempoRestante;
    }
}
