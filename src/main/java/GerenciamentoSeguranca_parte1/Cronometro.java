package GerenciamentoSeguranca_parte1;

public class Cronometro extends Thread {

    private volatile boolean executando = true;
    private long inicio;

    public void run() {
        inicio = System.currentTimeMillis();

        while (executando) {
            long agora = System.currentTimeMillis();
            long decorrido = (agora - inicio) / 1000;

            System.out.print("\rTempo decorrido: " + decorrido + " segundos");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
        System.out.println(); // para quebrar linha após fim do cronômetro
    }

    public void parar() {
        executando = false;
    }
}
