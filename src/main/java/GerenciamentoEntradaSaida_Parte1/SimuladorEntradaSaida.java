package GerenciamentoEntradaSaida_Parte1;

import java.util.*;

public class SimuladorEntradaSaida {
    private Disco disco;

    // Construtor:
    public SimuladorEntradaSaida(Disco disco) {
        this.disco = disco;
    }

    // Métodos:
    public void executarEscalonamento(int algoritmoSelecionado, int cabeca) {
        System.out.println("\n====== Iniciando a simulação ======");
        int tempoTotal;
        switch (algoritmoSelecionado) {
            case 1:
                tempoTotal = fcfs();
                System.out.println("Tempo total de seek: " + tempoTotal + " u.t.");
                break;
            case 2:
                tempoTotal = sstf();
                System.out.println("Tempo total de seek: " + tempoTotal + " u.t.");
                break;
            case 3:
                tempoTotal = scan();
                System.out.println("Tempo total de seek: " + tempoTotal + " u.t.");
                break;
            case 4:
                tempoTotal = look();
                System.out.println("Tempo total de seek: " + tempoTotal + " u.t.");
                break;
            case 5:
                tempoTotal = cscan();
                System.out.println("Tempo total de seek: " + tempoTotal + " u.t.");
                break;
            case 6:
                tempoTotal = clook();
                System.out.println("Tempo total de seek: " + tempoTotal + " u.t.");
            case 7:
                compararTodosAlgoritmos(cabeca);
        }
    }

    public int fcfs() {
        System.out.println("\n --- fcfs ---\n");
        return moverEntreBlocos(disco.getRequisicoes());
    }

    public int sstf() {
        System.out.println("\n --- sstf ---\n");
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
        System.out.println("\n --- scan ---\n");
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
        System.out.println("\n --- C-scan ---\n");
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
        System.out.println("\n --- look ---\n");
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
        System.out.println("\n --- c-look ---\n");
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

    private void compararTodosAlgoritmos(int cabeca){
        Map<String, Integer> resultados = new HashMap<>();

        // Executa todos os algoritmos e armazena os tempos
        resultados.put("FCFS", fcfs());
        disco.setPosicaoCabeca(cabeca);
        resultados.put("SSTF", sstf());
        disco.setPosicaoCabeca(cabeca);
        resultados.put("SCAN", scan());
        disco.setPosicaoCabeca(cabeca);
        resultados.put("LOOK", look());
        disco.setPosicaoCabeca(cabeca);
        resultados.put("C-SCAN", cscan());
        disco.setPosicaoCabeca(cabeca);
        resultados.put("C-LOOK", clook());

        // Ordena pelo tempo total de seek (do menor para o maior)
        List<Map.Entry<String, Integer>> ordenado = new ArrayList<>(resultados.entrySet());
        ordenado.sort(Map.Entry.comparingByValue());

        // Imprime os resultados ordenados
        System.out.println("\n====== Comparação dos Algoritmos ======");
        for (Map.Entry<String, Integer> entry : ordenado) {
            System.out.printf("%-7s -> Tempo total de seek: %3d u.t.\n", entry.getKey(), entry.getValue());
        }
    }
}