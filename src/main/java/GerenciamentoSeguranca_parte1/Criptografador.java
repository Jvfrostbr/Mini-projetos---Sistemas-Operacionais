package GerenciamentoSeguranca_parte1;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
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

    public String substituicao(String senha) {
        String alfabeto = "abcdefghijklmnopqrstuvwxyz";
        String substitutoAlf = "qazwsxedcrfvtgbyhnujmikolp";
        String numeros = "1234567890";
        String substitutoNum = "7418529630";

        StringBuilder resultado = new StringBuilder();

        for (char c : senha.toLowerCase().toCharArray()) {
            int indexAlf = alfabeto.indexOf(c);
            int indexNum = numeros.indexOf(c);

            if (indexAlf != -1) {
                resultado.append(substitutoAlf.charAt(indexAlf));
            } else if (indexNum != -1) {
                resultado.append(substitutoNum.charAt(indexNum));
            } else {
                resultado.append(c); // mantém outros caracteres intactos
            }
        }
        return resultado.toString();
    }

    public String hashSHA256(String senha) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(senha.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar SHA-256", e);
        }
    }

    public String hashSHA256ComSalt(String senha, String salt) {
        return hashSHA256(salt + senha);
    }

    public String gerarSalt(int tamanho) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder salt = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < tamanho; i++) {
            salt.append(chars.charAt(random.nextInt(chars.length())));
        }
        return salt.toString();
    }
}
