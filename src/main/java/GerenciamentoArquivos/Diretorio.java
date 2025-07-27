package GerenciamentoArquivos;

import java.util.ArrayList;
import java.util.List;

public class Diretorio {
    private final String nome;
    private final List<Arquivo> arquivos;
    private int blocoAlocado;
    private boolean protegido;
    private String senha;

    // Construtor:
    public Diretorio(String nome, int blocoAlocado, boolean protegido, String senha) {
        this.nome = nome;
        this.arquivos = new ArrayList<>();
        this.blocoAlocado = blocoAlocado;
        this.protegido = protegido;
        this.senha = senha;
    }

    // Métodos:
    public void adicionarArquivo(Arquivo arquivo) {
        arquivos.add(arquivo);
    }

    public void removerArquivo(String nomeArquivo) {
        Arquivo arquivo = buscarArquivo(nomeArquivo);

        if(arquivo != null){
            arquivos.remove(arquivo);
        }
        else{
            System.out.println("Arquivo não encontrado.");
        }
    }

    public Arquivo buscarArquivo(String nomeArquivo) {
        Arquivo arquivo = null;

        for (int i = 0; i < arquivos.size() && arquivo == null; i++) {
            if (arquivos.get(i).getNome().equals(nomeArquivo)) {
                arquivo = arquivos.get(i);
            }
        }
        return arquivo;
    }

    public boolean possuiArquivo(String nomeArquivo){
        Arquivo arquivo = buscarArquivo(nomeArquivo);
        return arquivo != null;
    }

    public void listarArquivos() {
        if (arquivos.isEmpty()) {
            System.out.println("Diretório vazio.");
        } else {
            for (Arquivo arquivo : arquivos) {
                System.out.println("  - " + arquivo.getNome() + " | tamanho: " + arquivo.getTamanho());
            }
        }
    }

    // Getters e Setters:
    public String getNome() {
        return nome;
    }

    public List<Arquivo> getArquivos() {
        return arquivos;
    }

    public int getBlocoAlocado() {
        return blocoAlocado;
    }

    public void setBlocoAlocado(int blocoAlocado) {
        this.blocoAlocado = blocoAlocado;
    }

    public boolean isProtegido() {
        return protegido;
    }

    public void setProtegido(boolean protegido) {
        this.protegido = protegido;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
