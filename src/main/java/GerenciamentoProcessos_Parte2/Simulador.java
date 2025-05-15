package GerenciamentoProcessos_Parte2;

import GerenciamentoProcessos_Parte1.RecursosCompartilhados;

public class Simulador extends Thread {
    Escalonador escalonador;

    @Override
    public void run() {
        try {
            escalonador.executarProcessos();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}

// A ideia é nessa main, você pode escolher o escalonador que deseja usar, e esse escalonador vai rodando como uma thread.
// Enquanto isso você pode adicionar processos a ele. Pela lista "Não chegados", os escalonadores vão pegando os processos
// dessa lista, adicionando na lista de Ready, e executando com base do seu algoritmo.
class Main {
    public static void main(String[] args) {
        Simulador simulador = new Simulador();
        simulador.escalonador = new Round_Robin(2);
        simulador.start();
    }
}
