package GerenciamentoEntradaSaida_Parte1;

import java.util.ArrayList;
import java.util.List;

public class Disco {
    private int blocoMin;
    private int blocoMax;
    private List<Integer> requisicoes;
    private int posicaoCabeca;

    // Construtor:
    public Disco(int blocoMin, int blocoMax, int posicaoInicial) {
        this.blocoMin = blocoMin;
        this.blocoMax = blocoMax;
        this.posicaoCabeca = posicaoInicial;
        this.requisicoes = new ArrayList<>();
    }

    // Métodos:
    public void adicionarRequisicao(int bloco) {
        if (bloco >= blocoMin && bloco <= blocoMax) {
            requisicoes.add(bloco);
        } else {
            System.out.println("Bloco " + bloco + " está fora do intervalo permitido.");
        }
    }

    // Gets and Sets:
    public List<Integer> getRequisicoes() {
        return requisicoes;
    }

    public int getPosicaoCabeca() {
        return posicaoCabeca;
    }

    public void setPosicaoCabeca(int novaPosicao) {
        this.posicaoCabeca = novaPosicao;
    }

    public int getBlocoMin() {
        return blocoMin;
    }

    public int getBlocoMax() {
        return blocoMax;
    }
}
