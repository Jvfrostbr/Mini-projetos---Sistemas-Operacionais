package GerenciamentoArquivos;

import java.util.ArrayList;
import java.util.List;

public class AlocadorEncadeado implements Alocador {
    private List<Bloco> blocos;
    private int tamanhoBloco;

    // Construtor:
    public AlocadorEncadeado(int totalMemoriaKB, int tamanhoBlocoKB) {
        this.tamanhoBloco = tamanhoBlocoKB;
        int totalBlocos = totalMemoriaKB / tamanhoBlocoKB;
        this.blocos = new ArrayList<>();
        iniciaizarAlocador(totalBlocos);
    }

    // Métodos:
    private void iniciaizarAlocador(int totalBlocos){
        for (int i = 0; i < totalBlocos; i++) {
            blocos.add(new Bloco(i));
        }
    }

    @Override
    public int alocarNoBloco(String nome, int tamanhoDadoKB) {
        int blocosNecessarios = calcularBlocosNecessarios(tamanhoDadoKB);
        List<Integer> livres = new ArrayList<>();

        for (Bloco bloco : blocos) {
            if (!bloco.isOcupado()) {
                livres.add(bloco.getId());
            }
            if (livres.size() == blocosNecessarios) {
                break;
            }

            if (livres.size() < blocosNecessarios) {
                System.out.println("Erro: Memória insuficiente.");
                return -1;
            }

            // Alocação encadeada
            for (int i = 0; i < livres.size(); i++) {
                int blocoAtual = livres.get(i);
                blocos.get(blocoAtual).alocar(nome);
                if (i < livres.size() - 1) {
                    blocos.get(blocoAtual).setProximoBloco(livres.get(i + 1));
                }
            }
        }
        return livres.getFirst();
    }

    @Override
    public void desalocarBloco(String nome) {
        boolean blocoEncontrado = false;

        for (int i = 0; i < blocos.size() && !blocoEncontrado; i++) {
            Bloco bloco = blocos.get(i);
            if (bloco.isOcupado() && nome.equals(bloco.getNomeArquivo())) {
                bloco.desalocar();
            }
        }
    }

    @Override
    public void mostrarBlocos(){
        //todo: melhorar
        for (Bloco b : blocos) {
            System.out.println(b);
        }
    }

    @Override
    public int calcularBlocosNecessarios(int tamanhoDadosKB){
        int blocosNecessarios = tamanhoDadosKB / tamanhoBloco;

        /* if Necessário para casos onde o resultado da divisão não dá um número inteiro
           ex: tamanho do arquivo → 53 kb
               tamanho do bloco → 4 kb
               blocosNecessarios = 53 / 4 = 13 (sobra 1 KB), então são necessários 14 blocos para alocar o arquivo
        */
        if (tamanhoBloco % tamanhoDadosKB != 0) {
            blocosNecessarios++;
        }

        return blocosNecessarios;
    }

    @Override
    public int contarBlocosLivres() {
        int livres = 0;
        for (Bloco bloco : blocos) {
            if (!bloco.isOcupado()) {
                livres++;
            }
        }
        return livres;
    }

    @Override
    public boolean arquivoExiste(String nomeArquivo) {
        for (Bloco b : blocos) {
            if (nomeArquivo.equals(b.getNomeArquivo())) return true;
        }
        return false;
    }

    @Override
    public void verificarFragmentacaoInterna(String nomeArquivo, int tamanhoArquivoKB) {
        int blocosNecessarios = calcularBlocosNecessarios(tamanhoArquivoKB);
        int ultimaPorcentagemUsada = tamanhoArquivoKB % tamanhoBloco;

        if (ultimaPorcentagemUsada != 0) {
            int desperdicio = tamanhoBloco - ultimaPorcentagemUsada;
            System.out.printf("Fragmentação interna detectada: %dKB desperdiçados.%n", desperdicio);
        }
    }
}
