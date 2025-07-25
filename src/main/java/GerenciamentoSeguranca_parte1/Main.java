package GerenciamentoSeguranca_parte1;

import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Criptografador criptografador = new Criptografador();
        QuebradorDeSenha quebrador = new QuebradorDeSenha();
        Senha senha = gerarSenha(scanner);
        menu(senha, scanner, criptografador, quebrador);

    }

    private static Senha gerarSenha(Scanner scanner) {
        System.out.print("Digite uma senha (até 8 caracteres): ");
        String entrada = scanner.nextLine();

        while (entrada.length() > 8) {
            System.out.print("Senha muito longa! Digite novamente (máx. 8 caracteres): ");
            entrada = scanner.nextLine();
        }

        return new Senha(entrada);
    }

    public static void menu(Senha senha,Scanner scanner, Criptografador criptografador, QuebradorDeSenha quebrador){
            System.out.println("""
                    \n========== MENU ==========
                    1. Cifrar com Cifra de César Expandida
                    2. Cifrar com Substituição
                    3. Cifrar com Hash SHA-256
                    5. Quebrar Cifra de César Expandida
                    6. Quebrar Substituição
                    7. Quebrar SHA-256
                    Escolha uma opção:\t""");
            int opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1 -> {
                    Random random = new Random();
                    int deslocamento = random.nextInt(26) + 1; // Gera valor aleatório entre 1 e 26
                    String cifrada = criptografador.cifraDeCesarExpandida(senha.getSenhaOriginal(), deslocamento);
                    System.out.println("Senha cifrada: " + cifrada);
                    senha.setCriptografada(cifrada, "crifra de cesar expandida");
                    menu(senha, scanner, criptografador, quebrador);
                }
                case 2 -> {
                    String cifrada = criptografador.substituicao(senha.getSenhaOriginal());
                    System.out.println("Senha cifrada: " + cifrada);
                    senha.setCriptografada(cifrada, "crifra substituição");
                    menu(senha, scanner, criptografador, quebrador);
                }
                case 3 -> {
                    String hash = criptografador.hashSHA256(senha.getSenhaOriginal());
                    System.out.println("Hash SHA-256: " + hash);
                    senha.setCriptografada(hash, "crifra sha-256");
                    menu(senha, scanner, criptografador, quebrador);
                }
                case 4 -> {
                    String senhaCifrada = senha.getSenhaCriptografada();
                    String original = quebrador.quebrarCifraDeCesarExpandida(senhaCifrada);
                    System.out.println("Senha provável: " + original);
                    System.out.println("Senha original: " + senha.getSenhaOriginal());
                    menu(senha, scanner, criptografador, quebrador);
                }
                case 5 -> {
                    String senhaCifrada = senha.getSenhaCriptografada();
                    String original = quebrador.quebrarSubstituicao(senhaCifrada);
                    System.out.println("Senha provável: " + original);
                    System.out.println("Senha original: " + senha.getSenhaOriginal());
                    menu(senha, scanner, criptografador, quebrador);
                }
                case 6 -> {
                    System.out.print("Digite o hash alvo: ");
                    String hashAlvo = scanner.nextLine();
                    String resultado = quebrador.quebrarSHA256(hashAlvo);
                    System.out.println("Senha encontrada: " + resultado);
                    menu(senha, scanner, criptografador, quebrador);
                }
                default -> {
                    System.out.println("Opção inválida!");
                    menu(senha, scanner, criptografador, quebrador);
                }
            }
        }

}
