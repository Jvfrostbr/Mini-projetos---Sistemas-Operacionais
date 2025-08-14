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

        // Gerando um acréscimo aleatório de 0 a numPaginas de páginas que serão referenciadas (acessadas)
        int acrescimoReferencias = rand.nextInt(numPaginas + 1);
        int numReferencias = numPaginas + acrescimoReferencias;

        // Gerando referências para simular uma execução de um processo
        for (int i = 0; i < numReferencias; i++) {
            if (aleatorio) {
                referenciasPaginas.add(rand.nextInt(numPaginas));
            } else {
                referenciasPaginas.add(i % numPaginas); // FIFO circular
            }
        }
    }

    public void imprimirReferencias() {
        System.out.println("\nOrdem das páginas a serem referenciadas do processo " + nome + ':');
        for (int i = 0; i < referenciasPaginas.size(); i++) {
            System.out.println((i + 1) + " - Página " + (referenciasPaginas.get(i) + 1));
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
