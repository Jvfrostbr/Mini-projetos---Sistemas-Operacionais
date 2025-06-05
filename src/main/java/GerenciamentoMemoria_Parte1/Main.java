package GerenciamentoMemoria_Parte1;

import java.util.ArrayList;
import java.util.Random;

class Main {
    public static void main(String[] args) {
        AlocacaoMemoria alocacao = new AlocacaoMemoria(10);
        alocacao.processosAlocados = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            String nome = "P" + i;
            int tamanho = new Random().nextInt(7) + 1; // tamanho entre 1 e 8
            int tempoEspera = new Random().nextInt(11); // tempoEspera entre 0 e 10

            Processo processo = new Processo(nome, tamanho, tempoEspera);
            alocacao.adicionarProcessoFila(processo);
        }
        alocacao.processarFila();
    }
}