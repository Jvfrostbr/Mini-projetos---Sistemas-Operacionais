package GerenciamentoMemoria_Parte1;

import java.util.*;

public class Main {

    static List<Processo> processos = new ArrayList<>();
    static int tamanhoMemoria = 0; // Tamanho da memória mb

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=== Alocação de Memória ===");
            System.out.println("1. Adicionar processo manualmente");
            System.out.println("2. Carregar processos teste");
            System.out.println("3. Visualizar processos adicionados");
            System.out.println("4. Definir tamanho da memória");
            System.out.println("5. Testar estratégia de alocação");
            System.out.println("6. Sair");

            Scanner scanner = new Scanner(System.in);
            int escolha = scanner.nextInt();
            scanner.nextLine(); // Limpar o buffer
            switch (escolha) {
                case 1:
                    adicionarProcessoManual(scanner);
                    break;
                case 2:
                    if (tamanhoMemoria == 0) {
                        System.out.println("Você precisa definir o tamanho da memória antes de carregar processos de teste.");
                        System.out.print("Digite o tamanho da memória (em MB): ");
                        tamanhoMemoria = scanner.nextInt();
                        scanner.nextLine(); // Limpar o buffer
                    }
                    carregarProcessosTeste();
                    break;
                case 3:
                    visualizarProcessos();
                    break;
                case 4:
                    Scanner tamanhoScanner = new Scanner(System.in);
                    System.out.println("\n=== Definir Tamanho da Memória ===");
                    if (tamanhoMemoria > 0) {
                        System.out.println("Tamanho atual da memória: " + tamanhoMemoria + " MB");
                    } else {
                        System.out.println("Nenhum tamanho de memória definido ainda.");
                    }
                    System.out.print("Digite o tamanho da memória (em MB): ");
                    tamanhoMemoria = tamanhoScanner.nextInt();
                    System.out.println("Tamanho da memória definido para " + tamanhoMemoria + " MB.");
                    break;
                case 5:
                    escolherEstrategia();
                    break;
                case 6:
                    System.out.println("Saindo...");
                    return;
                default:
                    System.out.println("Opção inválida, tente novamente.");
            }


        }
    }
    public static void testarEstrategia(Estrategia estrategia) {
        System.out.println("\n=== Estrategia " + estrategia.getDescricao() + " ===");
        AlocacaoMemoria alocacao = new AlocacaoMemoria(tamanhoMemoria, estrategia);
        for (Processo processo : processos) {
            alocacao.adicionarProcessoFila(processo);
        }
        alocacao.processarFila(); // Tenta alocar todos os processos

    }

    public static void carregarProcessosTeste() {
        System.out.println("\n=== Carregando processos de teste ===");
        int numeroProcessos = tamanhoMemoria/2; // Número de processos de teste
        for (int i = 1; i <= numeroProcessos; i++) {
            String nome = "P" + i;
            int tamanho = new Random().nextInt(tamanhoMemoria/3) + 1; // Tamanho entre 1 e 5 MB
            Processo processo = new Processo(nome, tamanho);
            processos.add(processo);
        }
    }

    public static void adicionarProcessoManual(Scanner scanner) {
        System.out.println("\n=== Adicionar Processo Manual ===");
        System.out.print("Nome do processo: ");
        String nome = scanner.nextLine();
        System.out.print("Tamanho do processo: ");
        int tamanho = scanner.nextInt();

        processos.add(new Processo(nome, tamanho));
    }

    public static void visualizarProcessos() {
        System.out.println("\n=== Processos Adicionados ===");
        if (processos.isEmpty()) {
            System.out.println("Nenhum processo adicionado.");
        } else {
            for (Processo p : processos) {
                String conteudo = p == null ? "   " : String.format("%-3s", p);
                System.out.print("[" + conteudo + "]");
            }
        }
    }

    public static void escolherEstrategia() {
        System.out.println("\n=== Escolher Estratégia de Alocação para Iniciar Simulação ===");
        System.out.println("1. First Fit");
        System.out.println("2. Best Fit");
        System.out.println("3. Worst Fit");

        Scanner scanner = new Scanner(System.in);
        int escolha = scanner.nextInt();

        switch (escolha) {
            case 1:
                testarEstrategia(Estrategia.FIRST_FIT);
                break;
            case 2:
                testarEstrategia(Estrategia.BEST_FIT);
                break;
            case 3:
                testarEstrategia(Estrategia.WORST_FIT);
                break;
            default:
                System.out.println("Opção inválida, tente novamente.");
        }
    }
}

