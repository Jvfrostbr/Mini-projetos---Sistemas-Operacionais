package GerenciamentoEntradaSaida_Parte1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimuladorEntradaSaida {
    private Disco disco;

    // Construtor:
    public SimuladorEntradaSaida(Disco disco) {
        this.disco = disco;
    }

    // Métodos:
    public void executarEscalonamento(int algoritmoSelecionado) {
        System.out.println("\n====== Iniciando a simulação ======");
        int tempoTotal = 0;
        switch (algoritmoSelecionado) {
            case 1 -> tempoTotal = fcfs();
            case 2 -> tempoTotal = sstf();
            case 3 -> tempoTotal = scan();
            case 4 -> tempoTotal = look();
            case 5 -> tempoTotal = cscan();
            case 6 -> tempoTotal = clook();
        }
        System.out.println("Tempo total de seek: " + tempoTotal + " u.t.");
    }

    public int fcfs() {
        return moverEntreBlocos(disco.getRequisicoes());
    }

    public int sstf() {
        List<Integer> pendentes = new ArrayList<>(disco.getRequisicoes());
        int tempoTotal = 0;

        while (!pendentes.isEmpty()) {
            int blocoMaisProximo = procurarBlocoMaisProximo(pendentes);
            int seek = Math.abs(disco.getPosicaoCabeca() - blocoMaisProximo);
            imprimirMovimento(disco.getPosicaoCabeca(), blocoMaisProximo, seek, false);
            tempoTotal += seek;
            disco.setPosicaoCabeca(blocoMaisProximo); // Atualizando a cabeça
            pendentes.remove(Integer.valueOf(blocoMaisProximo));
        }
        return tempoTotal;
    }

    private int procurarBlocoMaisProximo(List<Integer> blocosPendentes){
        int blocoMaisProximo = blocosPendentes.getFirst();
        int menorDistancia = Math.abs(disco.getPosicaoCabeca() - blocoMaisProximo);

        for (int bloco : blocosPendentes) {
            int distancia = Math.abs(disco.getPosicaoCabeca() - bloco);
            if (distancia < menorDistancia) {
                menorDistancia = distancia;
                blocoMaisProximo = bloco;
            }
            // Em caso de empate, o bloco mais próximo escolhido vai ser oq o bloco que tiver o menor valor
            else if (distancia == menorDistancia) {
                blocoMaisProximo = Math.min(blocoMaisProximo, bloco);
            }
        }
        return blocoMaisProximo;
    }

    public int scan() {
        int tempoTotal = 0;
        List<Integer> blocos = new ArrayList<>(disco.getRequisicoes());
        List<Integer> acimaCabeca = new ArrayList<>();
        List<Integer> abaixoCabeca = new ArrayList<>();

        organizarBlocosScanLook(blocos, disco.getPosicaoCabeca(), acimaCabeca, abaixoCabeca);

        // Atendendo os blocos acima da cabeça (subida)
        if (!acimaCabeca.isEmpty()){
            tempoTotal += moverEntreBlocos(acimaCabeca);
            disco.setPosicaoCabeca(acimaCabeca.getLast());
        }

        // Movendo a cabeca do disco até a extremidade superior
        if (disco.getPosicaoCabeca() != disco.getBlocoMax()) {
            tempoTotal += moverParaExtremidade(disco.getPosicaoCabeca(), disco.getBlocoMax(), true);
            disco.setPosicaoCabeca(disco.getBlocoMax());
        }

        // Atendendo os blocos abaixo da cabeça (descida)
        if(!abaixoCabeca.isEmpty()){
            tempoTotal += moverEntreBlocos(abaixoCabeca);
        }
        return tempoTotal;
    }

    public int cscan() {
        int tempoTotal = 0;
        List<Integer> blocos = new ArrayList<>(disco.getRequisicoes());
        List<Integer> acimaCabeca = new ArrayList<>();
        List<Integer> abaixoCabeca = new ArrayList<>();

        organizarBlocosScanLook(blocos, disco.getPosicaoCabeca(), acimaCabeca, abaixoCabeca);

        // Atende blocos acima da cabeça (subida)
        if (!acimaCabeca.isEmpty()) {
            tempoTotal += moverEntreBlocos(acimaCabeca);
            disco.setPosicaoCabeca(acimaCabeca.getLast());
        }

        // Movendo a cabeca do disco até a extremidade superior
        if (disco.getPosicaoCabeca() != disco.getBlocoMax()) {
            tempoTotal += moverParaExtremidade(disco.getPosicaoCabeca(), disco.getBlocoMax(), true);
            disco.setPosicaoCabeca(disco.getBlocoMax());
        }

        // Retorno circular: volta para o bloco mínimo sem atender ninguém
        if (!abaixoCabeca.isEmpty()) {

            // Movendo a cabeca do disco até a extremidade Inferior
            tempoTotal += moverParaExtremidade(disco.getPosicaoCabeca(), disco.getBlocoMin(), false);
            disco.setPosicaoCabeca(disco.getBlocoMin()); //

            // Atendendo blocos abaixo da cabeça, agora do início para frente
            Collections.sort(abaixoCabeca);
            tempoTotal += moverEntreBlocos(abaixoCabeca);
        }

        return tempoTotal;
    }

    private int moverParaExtremidade(int origem, int extremidade, boolean extremidadeSuperior) {
        int seek = Math.abs(origem - extremidade);
        imprimirMovimento(origem, extremidade, seek, extremidadeSuperior);
        disco.setPosicaoCabeca(disco.getBlocoMax());
        return seek;
    }

    public int look() {
        int tempoTotal = 0;
        List<Integer> blocos = new ArrayList<>(disco.getRequisicoes());
        List<Integer> acimaCabeca = new ArrayList<>();
        List<Integer> abaixoCabeca = new ArrayList<>();

        organizarBlocosScanLook(blocos, disco.getPosicaoCabeca(), acimaCabeca, abaixoCabeca);
        if(!acimaCabeca.isEmpty()){
            tempoTotal += moverEntreBlocos(acimaCabeca);
        }
        if (!abaixoCabeca.isEmpty()) {
            tempoTotal += moverEntreBlocos(abaixoCabeca);
        }

        return tempoTotal;
    }

    public int clook() {
        int tempoTotal = 0;
        List<Integer> blocos = new ArrayList<>(disco.getRequisicoes());

        List<Integer> acimaCabeca = new ArrayList<>();
        List<Integer> abaixoCabeca = new ArrayList<>();

        organizarBlocosScanLook(blocos, disco.getPosicaoCabeca(), acimaCabeca, abaixoCabeca);

        if (!acimaCabeca.isEmpty()) {
            tempoTotal += moverEntreBlocos(acimaCabeca);
            disco.setPosicaoCabeca(acimaCabeca.getLast());
        }

        if (!abaixoCabeca.isEmpty()) {
            // Vai direto para o menor bloco requisitado
            int menor = Collections.min(abaixoCabeca);
            tempoTotal += moverParaExtremidade(disco.getPosicaoCabeca(), menor, false);
            disco.setPosicaoCabeca(menor);

            Collections.sort(abaixoCabeca);
            tempoTotal += moverEntreBlocos(abaixoCabeca);
        }

        return tempoTotal;
    }

    private int moverEntreBlocos(List<Integer> blocosDestino) {
        int tempo = 0;

        for (int blocoDestino : blocosDestino) {
            int seek = Math.abs(disco.getPosicaoCabeca() - blocoDestino);
            imprimirMovimento(disco.getPosicaoCabeca(), blocoDestino, seek, false);
            tempo += seek;
            disco.setPosicaoCabeca(blocoDestino); // Atualizando a cabeça do disco
        }
        return tempo;
    }

    private void imprimirMovimento(int de, int para, int seek, boolean fimDoDisco) {
        if (seek != 0){
            if (fimDoDisco)
                System.out.printf("Movendo de [ %d ] -> [ %d ] | (seek: %d) [Fim do disco]\n", de, para, seek);
            else
                System.out.printf("Movendo [ %d ] -> [ %d ] | (seek: %d)\n", de, para, seek);
        }
    }

    private void organizarBlocosScanLook(List<Integer> blocos, int cabeca, List<Integer> acimaCabeca, List<Integer> abaixoCabeca) {
        Collections.sort(blocos);

        // Separa os blocos que estão acima da cabeça ou abaixo da cabeça
        for (int bloco : blocos) {
            (bloco >= cabeca ? acimaCabeca : abaixoCabeca).add(bloco);
        }
        Collections.reverse(abaixoCabeca); // para descer na volta
    }
}