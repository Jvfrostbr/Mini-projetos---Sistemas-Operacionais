package GerenciamentoEntradaSaida_Parte1;

import java.util.Random;
import java.util.Scanner;

public class Main {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Simulador de Gerenciamento de E/S ===");
        Disco disco = inicializarDisco();
        int cabeca = disco.getPosicaoCabeca();
        gerarBlocosRequisitados(disco);

        while(true){
            int algoritmoSelecionado = escolherAlgoritmo();
            SimuladorEntradaSaida simulador = new SimuladorEntradaSaida(disco);
            simulador.executarEscalonamento(algoritmoSelecionado);
            disco.setPosicaoCabeca(cabeca); // Voltando a cabeça para a posição inicial
        }
    }

    private static Disco inicializarDisco() {
        System.out.print("Digite o bloco mínimo do disco: ");
        int blocoMin = scanner.nextInt();

        System.out.print("Digite o bloco máximo do disco: ");
        int blocoMax = scanner.nextInt();

        System.out.print("Digite a posição inicial da cabeça do disco: ");
        int posCabeca = scanner.nextInt();

        if(posCabeca > blocoMax || posCabeca < blocoMin){
            System.out.println("A posição da cabeça do disco tem que estar entre o bloco máximo e o bloco mínimo");
            inicializarDisco();
        }

        return new Disco(blocoMin, blocoMax, posCabeca);
    }

    private static void gerarBlocosRequisitados(Disco disco) {
        System.out.println("""
        Deseja informar manualmente os blocos a serem acessados:
        1 - Manualmente
        2 - Aleatoriamente
        Escolha uma opção:\t
        """);
        int opcao = scanner.nextInt();
        scanner.nextLine(); // consumir o fim da linha

        if (opcao == 1) {
            System.out.println("Digite os blocos a serem visitados (digite 'fim' para encerrar):");

            while (true) {
                System.out.print("Bloco: ");
                String entrada = scanner.nextLine();
                if (entrada.equalsIgnoreCase("fim")){
                    break;
                }
                int bloco = Integer.parseInt(entrada);
                if (bloco < 0) {
                    System.out.println("Entrada inválida, digite um número maior que 0");
                }
                try {
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
                \nEscolha o algoritmo de escalonamento:
                1 - FCFS
                2 - SSTF
                3 - SCAN
                4 - LOOK
                5 - C-SCAN
                6 - C-LOOK
                escolha uma opção:\t""");
        opcao = scanner.nextInt();

        if(opcao < 1 || opcao > 6){
            escolherAlgoritmo();
        }
        return opcao;
    }
}