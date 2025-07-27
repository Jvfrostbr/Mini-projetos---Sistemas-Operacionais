package GerenciamentoSeguranca_parte1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class QuebradorDeSenha {

    public String quebrarCifraDeCesarExpandida(String senhaCriptografada, String senhaOriginal) {
        System.out.println("Tentando quebrar a senha cifrada: " + senhaCriptografada);
        Cronometro cronometro = new Cronometro();
        cronometro.start();

        int numDigitos = senhaOriginal.length();
        int blocos = (numDigitos % 2 == 0) ? (numDigitos / 2) : (numDigitos / 2 + 1); // Para casos onde a divisão inteira dá numero quebrado Ex: 5 /2 = 3 blocos
        int[] deslocamentos = new int[blocos];
        int num_possibilidades = (int) Math.pow(95, blocos);

        for (int i = 0; i < num_possibilidades; i++) {
            int temp = i;

            for (int j = 0; j < blocos; j++) {
                deslocamentos[j] = temp % 95;
                temp /= 95;
            }

            StringBuilder tentativa = new StringBuilder();
            for (int b = 0; b < blocos; b++) {
                int inicio = b * 2;
                int fim = Math.min(inicio + 2, senhaCriptografada.length());
                String blocoCriptografado = senhaCriptografada.substring(inicio, fim);
                tentativa.append(descriptografarCesarExpandida(blocoCriptografado, deslocamentos[b]));
            }

            if (tentativa.toString().equals(senhaOriginal)) {
                cronometro.parar();
                System.out.println("\nSenha quebrada!");
                System.out.print("Deslocamentos usados: ");
                for (int d : deslocamentos) {
                    System.out.print(d + " ");
                }
                System.out.println();
                return tentativa.toString();
            }
        }

        cronometro.parar();
        System.out.println("\nSenha não encontrada.");
        return null;
    }

    private String descriptografarCesarExpandida(String senhaCriptografada, int deslocamento) {
        StringBuilder resultado = new StringBuilder();

        for (char caractere : senhaCriptografada.toCharArray()) {
            if (caractere >= 32 && caractere <= 126) {
                int base = 32;
                int range = 95;
                char decifrado = (char) ((caractere - base - deslocamento + range) % range + base);
                resultado.append(decifrado);
            } else {
                resultado.append(caractere); // caractere fora do intervalo visível
            }
        }

        return resultado.toString();
    }

    public void quebrarPermutacaoMais2Deslocamentos(String senhaCriptografada, String senhaOriginal) {
        System.out.println("\nIniciando quebra da senha por permutação + 2 deslocamentos...");
        Cronometro cronometro = new Cronometro();
        cronometro.start();

        int num_digitos = senhaOriginal.length();
        List<List<Integer>> todasPermutacoes = gerarTodasPermutacoes(num_digitos);

        for (List<Integer> permutacao : todasPermutacoes) {
            for (int i = 0; i < 9025; i++) { // 95^2 = 9025
                int[] deslocamentos = new int[2];
                int temp = i;
                deslocamentos[0] = temp % 95;
                temp /= 95;
                deslocamentos[1] = temp % 95;

                String tentativa = descriptografarPermutacaoMais2Deslocamentos(senhaCriptografada, permutacao, deslocamentos);
                if (tentativa.equals(senhaOriginal)) {
                    cronometro.parar();
                    System.out.println("\nSenha quebrada com sucesso!");
                    System.out.println("Permutação correta: " + permutacao);
                    System.out.println("Deslocamentos corretos: " + Arrays.toString(deslocamentos));
                    System.out.println("Senha original: " + tentativa);
                    return;
                }
            }
        }

        cronometro.parar();
        System.out.println("\nSenha não encontrada.");
    }

    private List<List<Integer>> gerarTodasPermutacoes(int n) {
        List<List<Integer>> resultado = new ArrayList<>();
        permutar(new ArrayList<>(), new boolean[n], n, resultado);
        return resultado;
    }

    private void permutar(List<Integer> atual, boolean[] usado, int n, List<List<Integer>> resultado) {
        if (atual.size() == n) {
            resultado.add(new ArrayList<>(atual));
            return;
        }
        for (int i = 0; i < n; i++) {
            if (!usado[i]) {
                usado[i] = true;
                atual.add(i);
                permutar(atual, usado, n, resultado);
                usado[i] = false;
                atual.remove(atual.size() - 1);
            }
        }
    }


    private String descriptografarPermutacaoMais2Deslocamentos(String senhaCriptografada, List<Integer> permutacao, int[] deslocamentos) {
        int num_digitos = senhaCriptografada.length();
        char[] restaurado = new char[8];

        for (int i = 0; i < num_digitos; i++) {
            int blocoIndex = i / 4;
            char c = senhaCriptografada.charAt(i);
            int base = 32, range = 95;
            char decifrado = (char) ((c - base - deslocamentos[blocoIndex] + range) % range + base);
            restaurado[permutacao.get(i)] = decifrado;
        }
        return new String(restaurado);
    }

    public String quebrarSubstituicaoDeslocada(String senhaCriptografada, Map<Character, Character> mapaInverso, String senhaOriginal, int num_deslocamentos) {
            Cronometro cronometro = new Cronometro();
            cronometro.start();

            int num_digitos = senhaOriginal.length();

            int total = (int) Math.pow(num_deslocamentos, num_digitos); // total de possibilidades de deslocamento
            int[] deslocamentos = new int[num_digitos];

        for (int tentativa = 0; tentativa < total; tentativa++) {
            int temp = tentativa;
            for (int i = 0; i < num_digitos; i++) {
                deslocamentos[i] = temp % num_deslocamentos;
                temp /= num_deslocamentos;
            }

            StringBuilder tentativaTexto = new StringBuilder();
            for (int i = 0; i < num_digitos; i++) {
                char c = senhaCriptografada.charAt(i);
                int base = 32, range = 95;
                char desfeito = (char) ((c - base - deslocamentos[i] + range) % range + base);
                tentativaTexto.append(mapaInverso.getOrDefault(desfeito, desfeito));
            }

            if (tentativaTexto.toString().equals(senhaOriginal)) {
                cronometro.parar();
                System.out.println("\nSenha quebrada!");
                System.out.println("Deslocamentos: " + Arrays.toString(deslocamentos));
                return tentativaTexto.toString();
            }
        }

        cronometro.parar();
        return "\nSenha não encontrada";
    }
}