package GerenciamentoMemoria_Parte1;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

class Main {
    public static void main(String[] args) {
        testarEstrategia("first");
        testarEstrategia("best");
        testarEstrategia("worst");
        /*AlocacaoMemoria alocacao = new AlocacaoMemoria(10);
        alocacao.processosAlocados = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            String nome = "P" + i;
            int tamanho = new Random().nextInt(7) + 1; // tamanho entre 1 e 8
            int tempoEspera = new Random().nextInt(11); // tempoEspera entre 0 e 10

            Processo processo = new Processo(nome, tamanho, tempoEspera);
            alocacao.adicionarProcessoFila(processo);
        }
        alocacao.processarFila();*/
    }
    public static void testarEstrategia(String estrategia) {
        System.out.println("\n=== Estrategia " + estrategia.toUpperCase() + " ===");
        AlocacaoMemoria alocacao = new AlocacaoMemoria(10, estrategia);

        alocacao.adicionarProcessoFila(new Processo("P1",3,2));
        alocacao.adicionarProcessoFila(new Processo("P2",4,1));
        alocacao.adicionarProcessoFila(new Processo("P3",2,3));

        alocacao.processarFila(); // Tenta alocar todos os processos

    }
}