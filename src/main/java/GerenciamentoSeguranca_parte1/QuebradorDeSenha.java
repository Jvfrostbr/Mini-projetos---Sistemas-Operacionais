package GerenciamentoSeguranca_parte1;

public class QuebradorDeSenha {

    private final Criptografador criptografador = new Criptografador();

    public String quebrarCifraDeCesarExpandida(String senhaCriptografada) {
        System.out.println("Tentando quebrar a senha cifrada: " + senhaCriptografada);
        String tentativa = "";
        Cronometro cronometro = new Cronometro();
        cronometro.start();

        for (int deslocamento = 1; deslocamento < 95; deslocamento++) {
            tentativa = descriptografarCesarExpandida(senhaCriptografada, deslocamento);
        }

        cronometro.parar();
        return tentativa;
    }

    private String descriptografarCesarExpandida(String senha, int deslocamento) {
        StringBuilder resultado = new StringBuilder();
        Cronometro cronometro = new Cronometro();
        cronometro.start();

        for (char caractere : senha.toCharArray()) {
            if (caractere >= 32 && caractere <= 126) {
                int base = 32;
                int range = 95;
                char decifrado = (char) ((caractere - base - deslocamento + range) % range + base);
                resultado.append(decifrado);
            } else {
                resultado.append(caractere); // caractere fora do intervalo visível
            }
        }

        cronometro.parar();
        return resultado.toString();
    }

    public String quebrarSubstituicao(String senhaCriptografada) {
        String alfabeto = "abcdefghijklmnopqrstuvwxyz";
        String substitutoAlf = "qazwsxedcrfvtgbyhnujmikolp";
        String numeros = "123456789";
        String substitutoNum = "741852963";
        StringBuilder resultado = new StringBuilder();
        Cronometro cronometro = new Cronometro();
        cronometro.start();

        for (char c : senhaCriptografada.toLowerCase().toCharArray()) {
            if (substitutoAlf.indexOf(c) != -1) {
                int index = substitutoAlf.indexOf(c);
                resultado.append(alfabeto.charAt(index));
            } else if (substitutoNum.indexOf(c) != -1) {
                int index = substitutoNum.indexOf(c);
                resultado.append(numeros.charAt(index));
            } else {
                resultado.append(c); // mantém símbolos e outros caracteres
            }
        }
        return resultado.toString();
    }

    public String quebrarSHA256(String hashAlvo) {
        final String charset = "abcdefghijklmnopqrstuvwxyz0123456789";
        long inicio = System.currentTimeMillis();

        for (int tamanho = 1; tamanho <= 8; tamanho++) {
            String resultado = tentarTodasCombinacoesSHA("", tamanho, charset, hashAlvo, null);
            if (resultado != null) {
                long fim = System.currentTimeMillis();
                System.out.println("Tempo total: " + (fim - inicio) + " ms");
                return resultado;
            }
        }
        return "Senha não encontrada.";
    }

    private String tentarTodasCombinacoesSHA(String base, int tamanho, String charset, String hashAlvo, String salt) {
        if (base.length() == tamanho) {
            String tentativaHash = (salt == null)
                    ? criptografador.hashSHA256(base)
                    : criptografador.hashSHA256ComSalt(base, salt);

            if (tentativaHash.equals(hashAlvo)) {
                return base;
            }
            return null;
        }

        for (char c : charset.toCharArray()) {
            String tentativa = tentarTodasCombinacoesSHA(base + c, tamanho, charset, hashAlvo, salt);
            if (tentativa != null) return tentativa;
        }
        return null;
    }
}