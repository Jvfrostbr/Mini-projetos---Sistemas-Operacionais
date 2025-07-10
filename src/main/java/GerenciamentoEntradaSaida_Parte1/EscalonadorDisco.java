package GerenciamentoEntradaSaida_Parte1;

import java.util.*;

public class EscalonadorDisco {

    // FCFS
    public static void fcfs(Disco disco) {
        System.out.println("\n[FCFS]");
        int tempoTotal = 0;
        int atual = disco.getPosicaoCabeca();

        for (int bloco : disco.getRequisicoes()) {
            int seek = Math.abs(atual - bloco);
            System.out.printf("Movendo da trilha %d para %d (seek: %d)\n", atual, bloco, seek);
            tempoTotal += seek;
            atual = bloco;
        }
        System.out.println("Tempo total de seek: " + tempoTotal + " u.t.");
    }

    public static void sstf(Disco disco) {
        System.out.println("\n[SSTF]");
        List<Integer> pendentes = new ArrayList<>(disco.getRequisicoes());
        int atual = disco.getPosicaoCabeca();
        int tempoTotal = 0;

        while (!pendentes.isEmpty()) {
            int maisPerto = pendentes.get(0);
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
        System.out.println("Tempo total de seek: " + tempoTotal + " u.t.");
    }

    public static void scan(Disco disco) {
        System.out.println("\n[SCAN]");
        List<Integer> blocos = new ArrayList<>(disco.getRequisicoes());
        int atual = disco.getPosicaoCabeca();
        int min = disco.getBlocoMin();
        int max = disco.getBlocoMax();
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

        // Movimenta subindo até o máximo
        int pos = atual;
        for (int bloco : acima) {
            int seek = Math.abs(pos - bloco);
            System.out.printf("Movendo da trilha %d para %d (seek: %d)\n", pos, bloco, seek);
            tempoTotal += seek;
            pos = bloco;
        }

        // Move da última posição até o bloco máximo (extremidade)
        if (pos != max) {
            int seek = Math.abs(pos - max);
            System.out.printf("Movendo da trilha %d para %d (seek: %d) [Fim do disco]\n", pos, max, seek);
            tempoTotal += seek;
            pos = max;
        }

        // Agora volta descendo até o menor bloco requisitado
        for (int bloco : abaixo) {
            int seek = Math.abs(pos - bloco);
            System.out.printf("Movendo da trilha %d para %d (seek: %d)\n", pos, bloco, seek);
            tempoTotal += seek;
            pos = bloco;
        }

        System.out.println("Tempo total de seek: " + tempoTotal + " u.t.");
    }

    public static void look(Disco disco) {
        System.out.println("\n[LOOK]");
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

        System.out.println("Tempo total de seek: " + tempoTotal + " u.t.");
    }

}
