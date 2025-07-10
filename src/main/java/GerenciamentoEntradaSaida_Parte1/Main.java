package GerenciamentoEntradaSaida_Parte1;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Simulador de Gerenciamento de E/S ===");
        Disco disco = inicializarDisco();
        lerRequisicoes(disco);

        int opcaoAlgoritmo = escolherAlgoritmo();

        executarEscalonamento(disco, opcaoAlgoritmo);

        scanner.close();
    }

    private static Disco inicializarDisco() {
        System.out.print("Digite o bloco mínimo do disco: ");
        int blocoMin = scanner.nextInt();

        System.out.print("Digite o bloco máximo do disco: ");
        int blocoMax = scanner.nextInt();

        System.out.print("Digite a posição inicial da cabeça do disco: ");
        int posCabeca = scanner.nextInt();

        return new Disco(blocoMin, blocoMax, posCabeca);
    }

    private static void lerRequisicoes(Disco disco) {
        System.out.println("Deseja informar manualmente os blocos (M) ou gerar aleatoriamente (A)? ");
        char escolhaEntrada = scanner.next().toUpperCase().charAt(0);
        scanner.nextLine(); // consumir o fim da linha

        if (escolhaEntrada == 'M') {
            System.out.println("Digite os blocos a serem visitados (digite 'fim' para encerrar):");
            while (true) {
                System.out.print("Bloco: ");
                String entrada = scanner.nextLine();
                if (entrada.equalsIgnoreCase("fim")) break;
                try {
                    int bloco = Integer.parseInt(entrada);
                    disco.adicionarRequisicao(bloco);
                } catch (NumberFormatException e) {
                    System.out.println("Entrada inválida, digite um número válido ou 'fim'.");
                }
            }
        } else {
            System.out.print("Digite a quantidade de blocos a serem gerados aleatoriamente: ");
            int qtd = scanner.nextInt();
            Random random = new Random();
            for (int i = 0; i < qtd; i++) {
                int blocoAleatorio = disco.getBlocoMin() + random.nextInt(disco.getBlocoMax() - disco.getBlocoMin() + 1);
                disco.adicionarRequisicao(blocoAleatorio);
            }
            System.out.println("Blocos gerados aleatoriamente: " + disco.getRequisicoes());
        }
    }

    private static int escolherAlgoritmo() {
        int opcao;

        System.out.println("""
                Escolha o algoritmo de escalonamento:
                1 - FCFS
                2 - SSTF
                3 - SCAN
                4 - LOOK
                escolha uma opção:\t""");
        opcao = scanner.nextInt();

        if(opcao < 1 || opcao > 4){
            escolherAlgoritmo();
        }
        return opcao;
    }

    private static void executarEscalonamento(Disco disco, int opcaoAlgoritmo) {
        EscalonadorDisco escalonador = new EscalonadorDisco();

        List<Integer> ordemVisitada;
        int tempoTotalSeek;

        switch (opcaoAlgoritmo) {
            case 1 -> {
                ordemVisitada = escalonador.fcfs(disco);
                tempoTotalSeek = escalonador.calcularTempoSeek(disco.getPosicaoCabeca(), ordemVisitada);
                System.out.println("\nAlgoritmo FCFS selecionado.");
            }
            case 2 -> {
                ordemVisitada = escalonador.sstf(disco);
                tempoTotalSeek = escalonador.calcularTempoSeek(disco.getPosicaoCabeca(), ordemVisitada);
                System.out.println("\nAlgoritmo SSTF selecionado.");
            }
            case 3 -> {
                ordemVisitada = escalonador.scan(disco);
                tempoTotalSeek = escalonador.calcularTempoSeekScanLook(disco, ordemVisitada);
                System.out.println("\nAlgoritmo SCAN selecionado.");
            }
            case 4 -> {
                ordemVisitada = escalonador.look(disco);
                tempoTotalSeek = escalonador.calcularTempoSeekScanLook(disco, ordemVisitada);
                System.out.println("\nAlgoritmo LOOK selecionado.");
            }
            default -> {
                System.out.println("Opção inválida. Encerrando programa.");
                return;
            }
        }

        // Exibir resultado
        System.out.println("\nOrdem dos blocos visitados:");
        ordemVisitada.forEach(b -> System.out.print(b + " "));
        System.out.println("\nTempo total de seek: " + tempoTotalSeek + " unidades de tempo");
    }
}
