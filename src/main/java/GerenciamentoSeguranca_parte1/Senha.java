package GerenciamentoSeguranca_parte1;

public class Senha {
    private final String senhaOriginal;
    private String senhaCriptografada;
    private String algoritmo;

    //Construtor:
    public Senha(String senhaOriginal) {
        this.senhaOriginal = senhaOriginal;
    }

    //Gets and sets:
    public String getSenhaOriginal() {
        return senhaOriginal;
    }

    public String getSenhaCriptografada() {
        return senhaCriptografada;
    }

    public void setCriptografada(String criptografada, String algoritmo) {
        this.senhaCriptografada = criptografada;
        this.algoritmo = algoritmo;
    }

    public String getAlgoritmo() {
        return algoritmo;
    }

    @Override
    public String toString() {
        return String.format("Senha original: %s | Criptografada: %s | Algoritmo: %s",
                senhaOriginal, senhaCriptografada, algoritmo);
    }
}
