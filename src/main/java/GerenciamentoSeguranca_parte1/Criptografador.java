package GerenciamentoSeguranca_parte1;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Criptografador {

    public String cifraDeCesarExpandida(String senha, int deslocamento) {
        StringBuilder resultado = new StringBuilder();

        for (char caractere : senha.toCharArray()) {

            // os valores de cada caracteres em ASCII
            if (caractere >= 32 && caractere <= 126) {
                int base = 32;
                int range = 95; // 126 - 32 + 1
                char cifrado = (char) ((caractere - base + deslocamento + range) % range + base);
                resultado.append(cifrado);
            } else {
                // Fora do intervalo do ASCII: deixa inalterado
                resultado.append(caractere);
            }
        }

        return resultado.toString();
    }

    public String cifraPermutacaoMais2Deslocamentos(String senha, List<Integer> permutacao, int[] deslocamentos) {
        char[] rearranjado = new char[8];
        for (int i = 0; i < 8; i++) {
            rearranjado[i] = senha.charAt(permutacao.get(i));
        }

        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int blocoIndex = i / 4; // 0 para i=0..3, 1 para i=4..7
            int base = 32, range = 95;
            char cifrado = (char) ((rearranjado[i] - base + deslocamentos[blocoIndex]) % range + base);
            resultado.append(cifrado);
        }

        return resultado.toString();
    }

    public String cifraSubstituicaoDeslocada(String senha, Map<Character, Character> mapaSubstituicao, int[] deslocamentos) {
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < senha.length(); i++) {
            char original = senha.charAt(i);
            char substituido = mapaSubstituicao.getOrDefault(original, original);

            int base = 32, range = 95;
            char deslocado = (char) ((substituido - base + deslocamentos[i]) % range + base);
            resultado.append(deslocado);
        }
        return resultado.toString();
    }
}
