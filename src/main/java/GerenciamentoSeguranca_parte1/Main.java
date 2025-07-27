package GerenciamentoSeguranca_parte1;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Criptografador criptografador = new Criptografador();
        QuebradorDeSenha quebrador = new QuebradorDeSenha();
        Senha senha = gerarSenha(scanner);
        menuCifra(senha, scanner, criptografador, quebrador);
    }

    private static Senha gerarSenha(Scanner scanner) {
        System.out.print("Digite uma senha (Min: 4 e Max: 8): ");
        String entrada = scanner.nextLine();

        if (entrada.length() > 8){
            System.out.print("Senha muito longa! Digite novamente");
            gerarSenha(scanner);
        } else if (entrada.length() < 4) {
            System.out.print("Senha muito curta! Digite novamente");
            gerarSenha(scanner);
        }

        return new Senha(entrada);
    }

    public static void menuCifra(Senha senha, Scanner scanner, Criptografador criptografador, QuebradorDeSenha quebrador){
            System.out.println("""
                    \n========== MENU ==========
                    1. Cifrar com Cifra de César Expandida
                    2. Cifrar com permutação + deslocamento
                    3. Cifrar com substituição + deslocamento
                    Escolha uma opção:\t""");
            int opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1 -> {
                    executar_cifra_cesar_expandida(senha, scanner, criptografador, quebrador);
                }
                case 2 -> {
                    executarCifraPermutada(senha, scanner, criptografador, quebrador);
                }

                case 3 -> {
                    executarCifraSubstituicao(senha, scanner, criptografador, quebrador);
                }

                default -> {
                    System.out.println("Opção inválida!");
                    menuCifra(senha, scanner, criptografador, quebrador);
                }
            }
        }

        public static void executar_cifra_cesar_expandida(Senha senha, Scanner scanner, Criptografador criptografador, QuebradorDeSenha quebrador){
            Random random = new Random();
            StringBuilder cifrada = new StringBuilder();
            String original = senha.getSenhaOriginal();

            int blocos = (int) Math.ceil(original.length() / 2.0);
            int[] deslocamentos = new int[blocos]; // Um deslocamento por bloco de 2 caracteres

            for (int i = 0; i < original.length(); i += 2) {
                int deslocamento = random.nextInt(95) + 1; // deslocamento entre 1 e 95
                deslocamentos[i / 2] = deslocamento;

                // Extrai até 2 caracteres para o bloco
                String trecho = (i + 1 < original.length())? original.substring(i, i + 2) : original.substring(i, i + 1);

                // Aplica cifra de César expandida no bloco
                String cifradoPar = criptografador.cifraDeCesarExpandida(trecho, deslocamento);
                cifrada.append(cifradoPar);
            }

            System.out.println("Senha cifrada: " + cifrada);
            System.out.println("Deslocamentos utilizados: " + Arrays.toString(deslocamentos));
            senha.setCriptografada(cifrada.toString(), "césar por pares com deslocamentos aleatórios");

            String senha_provavel = quebrador.quebrarCifraDeCesarExpandida(cifrada.toString(), senha.getSenhaOriginal());
            System.out.println("Senha provável: " + senha_provavel);
            System.out.println("Senha original: " + senha.getSenhaOriginal());
            menuCifra(senha, scanner, criptografador, quebrador);
        }

        public static void executarCifraPermutada(Senha senha, Scanner scanner, Criptografador criptografador, QuebradorDeSenha quebrador){
            Random random = new Random();
            String original = senha.getSenhaOriginal();

            // Gera permutação aleatória dos índices [0..7]
            List<Integer> permutacao = new ArrayList<>();
            int num_digitos = senha.getSenhaOriginal().length();
            for (int i = 0; i < num_digitos; i++) {
                permutacao.add(i);
            }
            Collections.shuffle(permutacao);

            // Gera 2 deslocamentos aleatórios (um para cada bloco de 4 caracteres)
            int[] deslocamentos = new int[2];
            for (int i = 0; i < 2; i++) {
                deslocamentos[i] = random.nextInt(95) + 1;
            }

            // Cifra com permutação + 2 deslocamentos
            String cifrada = criptografador.cifraPermutacaoMais2Deslocamentos(original, permutacao, deslocamentos);

            System.out.println("Senha cifrada: " + cifrada);
            System.out.println("Permutação utilizada: " + permutacao);
            System.out.println("Deslocamentos utilizados: " + Arrays.toString(deslocamentos));

            senha.setCriptografada(cifrada, "permutação + 2 deslocamentos");

            quebrador.quebrarPermutacaoMais2Deslocamentos(cifrada, senha.getSenhaOriginal());
            menuCifra(senha, scanner, criptografador, quebrador);
        }

        public static void executarCifraSubstituicao(Senha senha, Scanner scanner, Criptografador criptografador, QuebradorDeSenha quebrador){

            Random random = new Random();
            String original = senha.getSenhaOriginal();

            // Gera mapeamento aleatório de substituição para letras e dígitos
            String alfabeto = "abcdefghijklmnopqrstuvwxyz";
            String numeros = "0123456789";
            String simbolos = "!@#$%&*?";

            String todosCaracteres = alfabeto + numeros + simbolos;
            List<Character> lista = new ArrayList<>();
            for (char c : todosCaracteres.toCharArray()) {
                lista.add(c);
            }
            Collections.shuffle(lista, random);

            Map<Character, Character> mapaSubstituicao = new HashMap<>();
            Map<Character, Character> mapaInverso = new HashMap<>();
            for (int i = 0; i < todosCaracteres.length(); i++) {
                mapaSubstituicao.put(todosCaracteres.charAt(i), lista.get(i));
                mapaInverso.put(lista.get(i), todosCaracteres.charAt(i));
            }

            // Gera deslocamentos aleatórios por caractere
            int num_digitos = senha.getSenhaOriginal().length();
            int[] deslocamentos = new int[num_digitos];
            int num_deslocamentos = 15;
            for (int i = 0; i < num_digitos; i++) {
                deslocamentos[i] = random.nextInt(num_deslocamentos); // de 0 a 9 deslocamentos
            }

            String cifrada = criptografador.cifraSubstituicaoDeslocada(original, mapaSubstituicao, deslocamentos);
            System.out.println("Senha cifrada: " + cifrada);
            System.out.println("Deslocamentos usados: " + Arrays.toString(deslocamentos));

            senha.setCriptografada(cifrada, "substituição aleatória + deslocamento por caractere");

            String senhaProvavel = quebrador.quebrarSubstituicaoDeslocada(cifrada, mapaInverso, senha.getSenhaOriginal(), num_deslocamentos);
            System.out.println("Senha provável: " + senhaProvavel);
            System.out.println("Senha original: " + senha.getSenhaOriginal());
            menuCifra(senha, scanner, criptografador, quebrador);
        }
}
