package GerenciamentoMemoria_Parte2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProcessoPaginacao {
    private String nome;
    private int id;
    private int tamanho;
    private int numPaginas;
    private List<Integer> referenciasPaginas; // sequência de páginas a acessar

    public ProcessoPaginacao(String nome, int id, int tamanho, int tamanhoPagina, String tipoReferenciaPaginas) {
        this.nome = nome;
        this.id = id;
        this.tamanho = tamanho;
        this.numPaginas = tamanho / tamanhoPagina;
        this.referenciasPaginas = new ArrayList<>();

        // if Necessário para casos onde o resultado da divisão dá um número quebrado1
        if (tamanho % tamanhoPagina != 0) {
            numPaginas++;
        }
        gerarReferencias(tipoReferenciaPaginas);
    }

    //MÉTODOS
    private void gerarReferencias(String tipoRefenciaPaginas) {
        Random rand = new Random();
        boolean aleatorio = tipoRefenciaPaginas.equals("Aleatório");

        // Gera referências para simular execução contínua
        for (int i = 0; i < numPaginas + 5; i++) {
            if (aleatorio) {
                referenciasPaginas.add(rand.nextInt(numPaginas));
            } else {
                referenciasPaginas.add(i % numPaginas); // FIFO circular
            }
        }
    }

    //GETS AND SETS
    public String getNome() {
        return nome;
    }

    public int getId() {
        return id;
    }

    public int getTamanho() {
        return tamanho;
    }

    public int getNumPaginas() {
        return numPaginas;
    }

    public List<Integer> getReferenciasPaginas() {
        return referenciasPaginas;
    }
}
