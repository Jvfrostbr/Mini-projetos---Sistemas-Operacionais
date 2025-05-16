package GerenciamentoProcessos_Parte1;

public class Mutex {
    private boolean bloqueado = false;

    public synchronized void lock() {
        while (bloqueado) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        bloqueado = true;
    }

    public synchronized void unlock() {
        if(bloqueado){
            bloqueado = false;
            notifyAll();
        }
    }
}