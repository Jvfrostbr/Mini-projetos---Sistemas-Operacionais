package GerenciamentoArquivos;

import java.util.ArrayList;
import java.util.List;

public class AlocadorEncadeado {
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

    public int alocarArquivo(String nomeArquivo, int tamanhoArquivoKB) {
        int blocosNecessarios = tamanhoArquivoKB / tamanhoBloco;

        // if Necessário para casos onde o resultado da divisão dá um número quebrado1
        if (tamanhoBloco % tamanhoArquivoKB != 0) {
            blocosNecessarios++;
        }

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
                blocos.get(blocoAtual).alocar(nomeArquivo);
                if (i < livres.size() - 1) {
                    blocos.get(blocoAtual).setProximoBloco(livres.get(i + 1));
                }
            }
        }
        return livres.getFirst();
    }

    public void desalocarArquivo(String nomeArquivo) {
        for (Bloco bloco : blocos) {
            if (bloco.isOcupado() && nomeArquivo.equals(bloco.getNomeArquivo())) {
                bloco.desalocar();
            }
        }
    }

    public void mostrarBlocos(){
        //todo: melhorar
        for (Bloco b : blocos) {
            System.out.println(b);
        }
    }

    public boolean arquivoExiste(String nomeArquivo) {
        for (Bloco b : blocos) {
            if (nomeArquivo.equals(b.getNomeArquivo())) return true;
        }
        return false;
    }

    public void verificarFragmentacaoInterna(String nomeArquivo, int tamanhoArquivoKB) {
        int blocosNecessarios = (int) Math.ceil((double) tamanhoArquivoKB / tamanhoBloco);
        int ultimaPorcentagemUsada = tamanhoArquivoKB % tamanhoBloco;

        if (ultimaPorcentagemUsada != 0) {
            int desperdicio = tamanhoBloco - ultimaPorcentagemUsada;
            System.out.printf("Fragmentação interna detectada: %dKB desperdiçados.%n", desperdicio);
        }
    }
}
