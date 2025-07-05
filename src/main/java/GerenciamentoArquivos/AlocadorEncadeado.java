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
        inicializarAlocador(totalBlocos);
    }

    // Métodos:
    private void inicializarAlocador(int totalBlocos){
        for (int i = 0; i < totalBlocos; i++) {
            blocos.add(new Bloco(i));
        }
    }

    @Override
    public int alocarNoBloco(String nome, int tamanhoDadoKB, Object objetoAlocado) {
        int retorno;
        int blocosNecessarios = calcularBlocosNecessarios(tamanhoDadoKB);
        List<Integer> livres = new ArrayList<>();

        for (int i = 0; i < blocos.size() && livres.size() < blocosNecessarios; i++) {
            if (!blocos.get(i).isOcupado()) {
                livres.add(blocos.get(i).getId());
            }
        }

        if (livres.size() < blocosNecessarios) {
            System.out.println("Erro: Memória insuficiente.");
            retorno = -1;
        }
        else{
            // Alocação encadeada
            for (int i = 0; i < livres.size(); i++) {
                int blocoAtual = livres.get(i);
                blocos.get(blocoAtual).alocar(nome, objetoAlocado);
                if (i < livres.size() - 1) {
                    blocos.get(blocoAtual).setProximoBloco(livres.get(i + 1));
                }
            }
            // marcando o fim da cadeia no último bloco
            blocos.get(livres.getLast()).setProximoBloco(-1);
            retorno = livres.getFirst();
        }
        return retorno;
    }

    @Override
    public void desalocarBloco(String nome) {
        for (Bloco bloco : blocos) {
            if (bloco.isOcupado() && nome.equals(bloco.getNome())) {
                bloco.desalocar();
            }
        }
    }

    @Override
    public void mostrarBlocos(){
        for (Bloco bloco : blocos) {
            if (!bloco.isOcupado()) {
                System.out.printf("Bloco %2d | LIVRE%n", bloco.getId());
            }
            else {
                String tipo;
                if (bloco.getObjetoAlocado() instanceof Diretorio) {
                    tipo = "Diretorio";
                }
                else {
                    tipo = "Arquivo";
                }
                String nome = bloco.getNome();
                String prox = bloco.getProximoBloco() == -1 ? "fim" : String.valueOf(bloco.getProximoBloco());

                System.out.printf("Bloco %2d | %-9s: %-16s | Próximo: %s%n", bloco.getId(), tipo, nome, prox);
            }
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
        if ( tamanhoDadosKB % tamanhoBloco != 0) {
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
    public void verificarFragmentacaoInterna(String nomeArquivo, int tamanhoArquivoKB) {
        int ultimaPorcentagemUsada = tamanhoArquivoKB % tamanhoBloco;

        if (ultimaPorcentagemUsada != 0) {
            int desperdicio = tamanhoBloco - ultimaPorcentagemUsada;
            System.out.printf("Fragmentação interna detectada: %dKB desperdiçados.%n", desperdicio);
        }
    }
}
