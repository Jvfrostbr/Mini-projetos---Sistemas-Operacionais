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
        }
        System.out.println("Tempo total de seek: " + tempoTotal + " u.t.");
    }

    public int fcfs() {
        System.out.println("\n[FCFS]");
        int tempoTotal = 0;
        int atual = disco.getPosicaoCabeca();

        for (int bloco : disco.getRequisicoes()) {
            int seek = Math.abs(atual - bloco);
            System.out.printf("Movendo da trilha %d para %d (seek: %d)\n", atual, bloco, seek);
            tempoTotal += seek;
            atual = bloco;
        }
        return tempoTotal;
    }

    public int sstf() {
        List<Integer> pendentes = new ArrayList<>(disco.getRequisicoes());
        int atual = disco.getPosicaoCabeca();
        int tempoTotal = 0;

        while (!pendentes.isEmpty()) {
            int maisPerto = pendentes.getFirst();
            int menorDist = Math.abs(atual - maisPerto);

            for (int bloco : pendentes) {
                int dist = Math.abs(atual - bloco);
                if (dist < menorDist) {
                    menorDist = dist;
                    maisPerto = bloco;
                }
            }

            System.out.printf("Movendo da trilha %d para %d (seek: %d)\n", atual, maisPerto, menorDist);
            tempoTotal += menorDist;
            atual = maisPerto;
            pendentes.remove(Integer.valueOf(maisPerto));
        }
        return tempoTotal;
    }

    public int scan() {
        int tempoTotal = 0;
        List<Integer> blocos = new ArrayList<>(disco.getRequisicoes());
        List<Integer> acimaCabeca = new ArrayList<>();
        List<Integer> abaixoCabeca = new ArrayList<>();

        organizarBlocosParaScan(blocos, disco.getPosicaoCabeca(), acimaCabeca, abaixoCabeca);

        if (!acimaCabeca.isEmpty()){
            tempoTotal += moverEntreBlocos(acimaCabeca);
            disco.setPosicaoCabeca(acimaCabeca.getLast());
        }

        // Movendo a cabeca do disco até a extremidade superior
        if (disco.getPosicaoCabeca() != disco.getBlocoMax()) {
            tempoTotal += moverParaExtremidade(disco.getPosicaoCabeca(), disco.getBlocoMax());
            disco.setPosicaoCabeca(disco.getBlocoMax());
        }
        tempoTotal += moverEntreBlocos(abaixoCabeca);
        return tempoTotal;
    }

    private void organizarBlocosParaScan(List<Integer> blocos, int cabeca, List<Integer> acimaCabeca, List<Integer> abaixoCabeca) {
        blocos.add(cabeca);
        Collections.sort(blocos);

        // Separa os blocos que estão acima da cabeça ou abaixo da cabeça
        for (int bloco : blocos) {
            (bloco >= cabeca ? acimaCabeca : abaixoCabeca).add(bloco);
        }
        Collections.reverse(abaixoCabeca); // para descer na volta
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

    private int moverParaExtremidade(int origem, int extremidade) {
        int seek = Math.abs(origem - extremidade);
        imprimirMovimento(origem, extremidade, seek, true);
        disco.setPosicaoCabeca(disco.getBlocoMax());
        return seek;
    }

    private void imprimirMovimento(int de, int para, int seek, boolean fimDoDisco) {
        if (fimDoDisco)
            System.out.printf("Movendo de [ %d ] -> [ %d ] | (seek: %d) [Fim do disco]\n", de, para, seek);
        else
            System.out.printf("Movendo [ %d ] -> [ %d ] | (seek: %d)\n", de, para, seek);
    }

    public int look() {
        List<Integer> blocos = new ArrayList<>(disco.getRequisicoes());
        int atual = disco.getPosicaoCabeca();
        int tempoTotal = 0;

        blocos.add(atual);
        Collections.sort(blocos);

        List<Integer> acima = new ArrayList<>();
        List<Integer> abaixo = new ArrayList<>();

        for (int bloco : blocos) {
            if (bloco >= atual) {
                acima.add(bloco);
            } else {
                abaixo.add(bloco);
            }
        }
        Collections.reverse(abaixo);

        int pos = atual;
        for (int bloco : acima) {
            int seek = Math.abs(pos - bloco);
            System.out.printf("Movendo da trilha %d para %d (seek: %d)\n", pos, bloco, seek);
            tempoTotal += seek;
            pos = bloco;
        }

        // Agora volta descendo, mas só até o menor bloco requisitado (não até o bloco mínimo do disco)
        for (int bloco : abaixo) {
            int seek = Math.abs(pos - bloco);
            System.out.printf("Movendo da trilha %d para %d (seek: %d)\n", pos, bloco, seek);
            tempoTotal += seek;
            pos = bloco;
        }
        return tempoTotal;
    }
}