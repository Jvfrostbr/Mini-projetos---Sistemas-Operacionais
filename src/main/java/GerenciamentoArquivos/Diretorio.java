package GerenciamentoArquivos;

import java.util.ArrayList;
import java.util.List;

public class Diretorio {
    private String nome;
    private List<Arquivo> arquivos;

    // Construtor:
    public Diretorio(String nome) {
        this.nome = nome;
        this.arquivos = new ArrayList<>();
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

    public void listarArquivos() {
        if (arquivos.isEmpty()) {
            System.out.println("Diretório vazio.");
        } else {
            for (Arquivo arquivo : arquivos) {
                System.out.println("  - " + arquivo.getNome() + " tamanho: " + arquivo.getTamanho());
            }
        }
    }

    //Getters e Setters
    public String getNome() {
        return nome;
    }

    public List<Arquivo> getArquivos() {
        return arquivos;
    }
}
