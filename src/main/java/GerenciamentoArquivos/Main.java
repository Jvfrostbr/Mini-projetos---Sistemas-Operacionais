package GerenciamentoArquivos;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SistemaArquivos sistema = new SistemaArquivos(64 * 1024, 4096); // 64KB de memória total, blocos de 4KB

        while (true) {
            System.out.println("""
========== MENU ==========
1. Criar diretório
2. Excluir diretório
3. Criar arquivo
4. Excluir arquivo
5. Listar diretórios
6. Listar arquivos de um diretório
7. Mostrar estado dos blocos
0. Sair
Escolha uma opção: 
""");

            int opcao = scanner.nextInt();
            scanner.nextLine(); // consumir quebra de linha

            switch (opcao) {
                case 1 -> {
                    System.out.print("Nome do diretório: ");
                    String nomeDir = scanner.nextLine();
                    sistema.criarDiretorio(nomeDir);
                }
                case 2 -> {
                    System.out.print("Nome do diretório: ");
                    String nomeDir = scanner.nextLine();
                    sistema.excluirDiretorio(nomeDir);
                }
                case 3 -> {
                    System.out.print("Nome do diretório destino: ");
                    String nomeDir = scanner.nextLine();
                    System.out.print("Nome do arquivo: ");
                    String nomeArq = scanner.nextLine();
                    System.out.print("Tamanho do arquivo (em KB): ");
                    int tamanho = scanner.nextInt();
                    scanner.nextLine();
                    sistema.criarArquivo(nomeDir, nomeArq, tamanho);
                }
                case 4 -> {
                    System.out.print("Nome do diretório: ");
                    String nomeDir = scanner.nextLine();
                    System.out.print("Nome do arquivo: ");
                    String nomeArq = scanner.nextLine();
                    sistema.excluirArquivo(nomeDir, nomeArq);
                }
                case 5 -> sistema.listarDiretorios();
                case 6 -> {
                    System.out.print("Nome do diretório: ");
                    String nomeDir = scanner.nextLine();
                    sistema.listarConteudoDiretorio(nomeDir);
                }
                case 7 -> sistema.mostrarEstadoBlocos();
                case 0 -> {
                    System.out.println("Saindo do sistema.");
                    return;
                }
                default -> System.out.println("Opção inválida!");
            }
        }
    }
}
