package GerenciamentoArquivos;

import GerenciamentoEntradaSaida_Parte2.RAID;

import java.util.ArrayList;
import java.util.List;

public class AlocadorEncadeado implements Alocador {
    private int tamanhoBloco;
    private RAID raid;

    // Construtor:
    public AlocadorEncadeado(int tamanhoBlocoKB, RAID raid) {
        this.tamanhoBloco = tamanhoBlocoKB;
        this.raid = raid;
    }

    // Métodos:
    @Override
    public int alocarNoBloco(String nome, int tamanhoDadoKB, Object objetoAlocado) {
        int blocosNecessarios = calcularBlocosNecessarios(tamanhoDadoKB);
        List<Bloco> blocosAlocados = new ArrayList<>();
        int currentIndex = 0;

        // Busca cíclica por blocos livres no RAID
        while (blocosAlocados.size() < blocosNecessarios && currentIndex < raid.getAllBlocos().size() * 2) {
            Bloco bloco = raid.getAllBlocos().get(currentIndex % raid.getAllBlocos().size());

            if (!bloco.isOcupado()) {
                try {
                    bloco = raid.armazenarBloco(bloco.getId(), nome, objetoAlocado);
                    blocosAlocados.add(bloco);
                } catch (RuntimeException e) {
                    System.out.println("Erro ao alocar bloco no RAID: " + e.getMessage());
                    for (Bloco b : blocosAlocados) {
                        raid.desalocarFisicamente(b);
                    }
                    return -1;
                }
            }
            currentIndex++;
        }

        if (blocosAlocados.size() < blocosNecessarios) {
            System.out.println("Espaço insuficiente para alocar '" + nome + "'");
            for (Bloco b : blocosAlocados) {
                raid.desalocarFisicamente(b);
            }
            return -1;
        }

        // Encadeia os blocos
        for (int i = 0; i < blocosAlocados.size() - 1; i++) {
            blocosAlocados.get(i).setProximoBloco(blocosAlocados.get(i + 1).getId());
        }
        blocosAlocados.get(blocosAlocados.size() - 1).setProximoBloco(-1); // Fim da cadeia

        System.out.printf("Arquivo '%s' alocado com %d blocos no RAID.%n", nome, blocosAlocados.size());
        return blocosAlocados.get(0).getId();
    }

    @Override
    public void desalocarBloco(String nome) {
        List<Bloco> todosBlocos = raid.getAllBlocos();
        for (Bloco bloco : todosBlocos) {
            if (bloco.isOcupado() && nome.equals(bloco.getNome())) {
                raid.desalocarFisicamente(bloco);
            }
        }
    }

    @Override
    public void mostrarBlocos() {
        if (raid == null) {
            throw new IllegalStateException("RAID não inicializado no alocador encadeado.");
        }

        System.out.println("Visualização lógica via Blocos:");
        List<Bloco> todosBlocos = raid.getAllBlocos();
        for (Bloco bloco : todosBlocos) {
            String blocoNome = String.format("(%d,%d)", bloco.getId(), bloco.getDiscoId());

            if (!bloco.isOcupado()) {
                System.out.printf("Bloco %s | LIVRE%n", blocoNome);
            } else {
                String tipo = (bloco.getObjetoAlocado() instanceof Diretorio) ? "Diretório" : "Arquivo";
                String nome = bloco.getNome();
                Integer proxBloco = bloco.getProximoBloco();
                String prox = (proxBloco == null || proxBloco == -1) ? "fim" : String.format("(%d,?)", proxBloco);
                // Caso você tenha o discoId do próximo bloco, poderia buscar e mostrar também

                System.out.printf("Bloco %s | %-9s: %-16s | Próximo: %s%n", blocoNome, tipo, nome, prox);
            }
        }

        System.out.println("Visualização lógica via Encadeamento:");
        for (Bloco bloco : todosBlocos) {
            if (bloco.isOcupado()) {
                String tipo = bloco.getObjetoAlocado() instanceof Diretorio ? "Diretório" : "Arquivo";
                String blocoNome = String.format("(%d,%d)", bloco.getId(), bloco.getDiscoId());
                Integer proxBloco = bloco.getProximoBloco();
                String prox = (proxBloco == null || proxBloco == -1) ? "fim" : String.format("(%d,?)", proxBloco);
                System.out.printf("Bloco %s | %s: %-16s | Próximo: %s%n", blocoNome, tipo, bloco.getNome(), prox);
            }
        }

        System.out.println("Visualização física via RAID:");
        raid.mostrarBlocos();
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
    public RAID getRaid() {
        return raid;
    }

    @Override
    public int contarBlocosLivres() {
        int livres = 0;
        List<Bloco> blocos = raid.getAllBlocos();
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
