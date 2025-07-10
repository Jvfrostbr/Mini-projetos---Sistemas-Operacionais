package GerenciamentoArquivos;

import java.util.Scanner;
import GerenciamentoEntradaSaida_Parte2.AlocadorRAID;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int tipoAlocador = seletorTipoAlocacao(scanner);
        SistemaArquivos sistema = inicializarSistemaArquivos(scanner, tipoAlocador);
        menuPrincipal(scanner, sistema);
    }

    private static SistemaArquivos inicializarSistemaArquivos(Scanner scanner, int tipoAlocador) {
        System.out.println("Digite o tamanho total de memória do sistema de arquivos (em KB): ");
        int tamMemoria = scanner.nextInt();

        System.out.println("Digite o tamanho dos blocos (em KB): ");
        int tamanhoBlocos = scanner.nextInt();

        // Se for RAID, pedir número de discos
        int quantidadeDiscos = 1;
        if (tipoAlocador == 3) {
            System.out.println("Digite o número de discos para o RAID (mínimo 3): ");
            quantidadeDiscos = scanner.nextInt();
            if (quantidadeDiscos < 3) {
                System.out.println("Usando mínimo de 3 discos para RAID 5");
                quantidadeDiscos = 3;
            }
        }

        return new SistemaArquivos(tamMemoria, tamanhoBlocos, tipoAlocador, quantidadeDiscos);
    }

    private static int seletorTipoAlocacao(Scanner scanner) {
        System.out.println("""
                Selecione o tipo de alocação a ser realizada:
                1 - Encadeada
                2 - FAT
                3 - RAID 5
                Escolha uma opção:\t""");
        int opcao = scanner.nextInt();

        if (opcao < 1 || opcao > 3) {
            System.out.println("Opção inválida!");
            return seletorTipoAlocacao(scanner);
        }
        return opcao;
    }

    private static void menuPrincipal(Scanner scanner, SistemaArquivos sistema) {
        System.out.println(
                "\n========== MENU ==========\n" +
                        "1. Criar diretório\n" +
                        "2. Excluir diretório\n" +
                        "3. Criar arquivo\n" +
                        "4. Excluir arquivo\n" +
                        "5. Listar diretórios\n" +
                        "6. Listar arquivos de um diretório\n" +
                        "7. Mostrar estado dos blocos\n" +
                        (sistema.getAlocador() instanceof AlocadorFAT ? "8. Opção exclusiva da FAT\n" : "") +
                        (sistema.getAlocador() instanceof AlocadorRAID ? "9. Opções RAID\n" : "") +
                        "0. Sair\n" +
                        "Escolha uma opção:\t");

        int opcao = scanner.nextInt();
        scanner.nextLine();  // consome o enter digitado pelo usuario

        switch (opcao) {
            case 1 -> criarDiretorio(scanner, sistema);
            case 2 -> deletarDiretorio(scanner, sistema);
            case 3 -> criarArquivo(scanner, sistema);
            case 4 -> deletarArquivo(scanner, sistema);
            case 5 -> sistema.listarDiretorios();
            case 6 -> listarArquivosDiretorio(scanner, sistema);
            case 7 -> sistema.mostrarEstadoBlocos();
            case 8 -> {
                if (sistema.getAlocador() instanceof AlocadorFAT) {
                    sistema.mostrarFragmentacaoInternaTotal();
                } else {
                    System.out.println("Opção inválida!");
                }
            }
            case 9 -> {
                if (sistema.getAlocador() instanceof AlocadorRAID) {
                    menuRAID(scanner, (AlocadorRAID) sistema.getAlocador(), sistema);
                } else {
                    System.out.println("Opção inválida!");
                }
            }
            case 0 -> System.exit(0);
            default -> System.out.println("Opção inválida!");
        }

        menuPrincipal(scanner, sistema);
    }

    private static void menuRAID(Scanner scanner, AlocadorRAID alocadorRAID, SistemaArquivos sistema) {
        System.out.println(
                "\n========== MENU RAID ==========\n" +
                        "1. Simular falha de disco\n" +
                        "2. Mostrar detalhes do RAID\n" +
                        "3. Ver fragmentação do RAID\n" +
                        "4. Voltar\n" +
                        "Escolha uma opção:\t");

        int opcao = scanner.nextInt();
        scanner.nextLine();

        switch (opcao) {
            case 1 -> {
                System.out.println("Digite o número do disco a simular falha (0-" + (alocadorRAID.getQuantidadeDiscos() - 1) + "):");
                int discoFalho = scanner.nextInt();
                alocadorRAID.simularFalhaDisco(discoFalho);
                sistema.mostrarEstadoBlocos();
            }
            case 2 -> alocadorRAID.mostrarDetalhesRAID();
            case 3 -> alocadorRAID.verificarFragmentacaoRAID();
            case 4 -> { return; }
            default -> System.out.println("Opção inválida!");
        }

        menuRAID(scanner, alocadorRAID, sistema);
    }

    private static void criarDiretorio(Scanner scanner, SistemaArquivos sistema){
        System.out.print("Digite o nome do diretório a ser criado: ");
        String nomeDiretorio = scanner.nextLine();
        sistema.criarDiretorio(nomeDiretorio);
    }

    private static void deletarDiretorio(Scanner scanner, SistemaArquivos sistema){
        sistema.listarDiretorios();
        System.out.print("\nDigite o nome do diretório a ser excluido: ");
        String nomeDiretorio = scanner.nextLine();
        sistema.excluirDiretorio(nomeDiretorio, scanner);
    }

    private static void criarArquivo(Scanner scanner, SistemaArquivos sistema){
        sistema.listarDiretorios();
        System.out.print("\nDigite o nome do diretório destino: ");
        String nomeDir = scanner.nextLine();
        System.out.print("Digite o nome do arquivo: ");
        String nomeArq = scanner.nextLine();
        System.out.print("Digite o tamanho do arquivo (em KB): ");
        int tamanho = scanner.nextInt();

        sistema.criarArquivo(nomeDir, nomeArq, tamanho);
    }

    private static void deletarArquivo(Scanner scanner, SistemaArquivos sistema){
        sistema.listarDiretorios();
        System.out.print("\nDigite o nome do diretório: ");
        String nomeDir = scanner.nextLine();
        sistema.listarConteudoDiretorio(nomeDir);
        System.out.print("\nDigite o nome do arquivo: ");
        String nomeArq = scanner.nextLine();
        sistema.excluirArquivo(nomeDir, nomeArq);
    }

    private static void listarArquivosDiretorio(Scanner scanner, SistemaArquivos sistema){
        System.out.print("Digite o nome do diretório: ");
        String nomeDir = scanner.nextLine();
        sistema.listarConteudoDiretorio(nomeDir);
    }
}
