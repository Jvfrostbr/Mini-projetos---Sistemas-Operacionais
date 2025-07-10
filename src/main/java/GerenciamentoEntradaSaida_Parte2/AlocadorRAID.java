package GerenciamentoEntradaSaida_Parte2;

import GerenciamentoArquivos.Alocador;
import GerenciamentoArquivos.Arquivo;
import GerenciamentoArquivos.Bloco;
import java.util.ArrayList;
import java.util.List;

public class AlocadorRAID implements Alocador {
    private List<List<Bloco>> discos; // Lista de discos, cada disco é uma lista de blocos
    private final int tamanhoBloco;
    private final int discosMinimos = 3;

    public AlocadorRAID(int memoriaTotalKB, int tamanhoBloco, int quantidadeDiscos) {
        if (quantidadeDiscos < discosMinimos) {
            throw new IllegalArgumentException("RAID 5 requer no mínimo " + discosMinimos + " discos");
        }

        this.tamanhoBloco = tamanhoBloco;
        this.discos = new ArrayList<>();

        int blocosPorDisco = (memoriaTotalKB / tamanhoBloco) / quantidadeDiscos;
        for (int i = 0; i < quantidadeDiscos; i++) {
            List<Bloco> disco = new ArrayList<>();
            for (int j = 0; j < blocosPorDisco; j++) {
                disco.add(new Bloco(j));
            }
            discos.add(disco);
        }
    }

    public void simularFalhaDisco(int discoIndex) {
        if (discoIndex < 0 || discoIndex >= discos.size()) {
            throw new IllegalArgumentException("Índice de disco inválido");
        }

        System.out.println("\n>>> Simulando falha no Disco " + discoIndex + " <<<");
        this.reconstruirDisco(discoIndex);
    }

    public void mostrarDetalhesRAID() {
        System.out.println("\nINFORMAÇÕES DO RAID 5");
        System.out.println("Número de discos: " + discos.size());
        System.out.println("Blocos por disco: " + (discos.isEmpty() ? 0 : discos.get(0).size()));
        System.out.println("Tamanho do bloco: " + tamanhoBloco + "KB");
        System.out.println("Blocos de paridade: " + contarBlocosParidade());
        System.out.println("Taxa de utilização: " +
                String.format("%.1f%%", (1.0 - (double)contarBlocosLivres()/totalBlocos()) * 100));
    }

    public void verificarFragmentacaoRAID() {
        System.out.println("\nFRAGMENTAÇÃO NO RAID 5");
        verificarFragmentacaoExterna();

        // Verificação adicional específica para RAID
        int paridadesDesbalanceadas = 0;
        for (int i = 0; i < discos.size(); i++) {
            int paridadesNoDisco = (int) discos.get(i).stream()
                    .filter(Bloco::isParidade)
                    .count();

            if (paridadesNoDisco > (discos.get(0).size() / discos.size() + 1)) {
                paridadesDesbalanceadas++;
            }
        }

        if (paridadesDesbalanceadas > 0) {
            System.out.println("Aviso: Distribuição de paridade não está balanceada em " +
                    paridadesDesbalanceadas + " discos");
        } else {
            System.out.println("Paridade está bem distribuída entre os discos");
        }
    }

    public int getQuantidadeDiscos() {
        return discos.size();
    }

    @Override
    public int alocarNoBloco(String nome, int tamanhoDadoKB, Object objetoAlocado) {
        int blocosNecessarios = calcularBlocosNecessarios(tamanhoDadoKB);
        int primeiroBloco = -1;

        for (int i = 0; i < blocosNecessarios; i++) {
            // Determinar posição no RAID (striping com paridade rotativa)
            int stripe = i / (discos.size() - 1);
            int discoDados = (i % (discos.size() - 1) + stripe) % discos.size();

            // Encontrar bloco livre
            Bloco bloco = encontrarBlocoLivre(discoDados);
            if (bloco == null) {
                return -1; // Não há espaço
            }

            bloco.alocar(nome, objetoAlocado);
            if (primeiroBloco == -1) {
                primeiroBloco = bloco.getId();
            }

            // Se completou um conjunto de blocos (stripe), calcular paridade
            if ((i + 1) % (discos.size() - 1) == 0 || i == blocosNecessarios - 1) {
                calcularEAlocarParidade(stripe, i - (i % (discos.size() - 1)), i + 1);
            }
        }

        return primeiroBloco;
    }

    @Override
    public void desalocarBloco(String nome) {
        for (List<Bloco> disco : discos) {
            for (Bloco bloco : disco) {
                if (bloco.isOcupado() && bloco.getNome().equals(nome)) {
                    bloco.desalocar();
                    return; // Desalocou, sai do método
                }
            }
        }
        System.out.println("Arquivo '" + nome + "' não encontrado para desalocação.");
    }

    @Override
    public void verificarFragmentacaoInterna(String nome, int tamanhoKB) {
        int blocosNecessarios = calcularBlocosNecessarios(tamanhoKB);
        int espacoTotal = blocosNecessarios * tamanhoBloco;
        int espacoUtilizado = tamanhoKB;
        int fragmentacaoInterna = espacoTotal - espacoUtilizado;

        System.out.printf("\nANÁLISE DE FRAGMENTAÇÃO INTERNA PARA '%s'\n", nome);
        System.out.printf("Tamanho do arquivo: %dKB\n", tamanhoKB);
        System.out.printf("Blocos alocados: %d (de %dKB cada)\n", blocosNecessarios, tamanhoBloco);
        System.out.printf("Espaço total alocado: %dKB\n", espacoTotal);
        System.out.printf("Espaço utilizado: %dKB\n", espacoUtilizado);
        System.out.printf("Fragmentação interna: %dKB (%.1f%%)\n",
                fragmentacaoInterna,
                (fragmentacaoInterna * 100.0 / espacoTotal));
    }

    public void verificarFragmentacaoExterna() {
        System.out.println("\nANÁLISE DE FRAGMENTAÇÃO EXTERNA");

        int totalBlocosLivres = contarBlocosLivres();
        int blocosContiguos = 0;
        int maxBlocosContiguos = 0;
        int totalFragmentos = 0;

        // Verificar em cada disco
        for (int d = 0; d < discos.size(); d++) {
            int discBlocosContiguos = 0;
            int discMaxBlocosContiguos = 0;
            int discFragmentos = 0;

            for (Bloco bloco : discos.get(d)) {
                if (!bloco.isOcupado()) {
                    discBlocosContiguos++;
                } else {
                    if (discBlocosContiguos > 0) {
                        discFragmentos++;
                        if (discBlocosContiguos > discMaxBlocosContiguos) {
                            discMaxBlocosContiguos = discBlocosContiguos;
                        }
                        discBlocosContiguos = 0;
                    }
                }
            }

            // Verificar última sequência
            if (discBlocosContiguos > 0) {
                discFragmentos++;
                if (discBlocosContiguos > discMaxBlocosContiguos) {
                    discMaxBlocosContiguos = discBlocosContiguos;
                }
            }

            System.out.printf("Disco %d: %d fragmentos livres, maior bloco contíguo: %d\n",
                    d, discFragmentos, discMaxBlocosContiguos);

            if (discMaxBlocosContiguos > maxBlocosContiguos) {
                maxBlocosContiguos = discMaxBlocosContiguos;
            }
            totalFragmentos += discFragmentos;
        }

        System.out.println("\nRESUMO GERAL:");
        System.out.println("Total de blocos livres: " + totalBlocosLivres);
        System.out.println("Total de fragmentos livres: " + totalFragmentos);
        System.out.println("Maior bloco contíguo livre: " + maxBlocosContiguos);

        double mediaFragmentos = (double) totalFragmentos / discos.size();
        System.out.printf("Média de fragmentos por disco: %.1f\n", mediaFragmentos);
    }

    @Override
    public void mostrarBlocos() {
        System.out.println("\nESTADO DO RAID - DISTRIBUIÇÃO ENTRE DISCOS");
        System.out.println("Legenda: [D:arquivo] = Dados, [P:valor] = Paridade, [L] = Livre");

        // Encontrar o maior número de blocos em qualquer disco
        int maxBlocos = discos.stream().mapToInt(List::size).max().orElse(0);

        // Cabeçalho com números dos discos
        System.out.print("Bloco\t");
        for (int d = 0; d < discos.size(); d++) {
            System.out.print("Disco " + d + "\t");
        }
        System.out.println();

        // Mostrar cada linha (bloco)
        for (int i = 0; i < maxBlocos; i++) {
            System.out.print(i + "\t");
            for (int d = 0; d < discos.size(); d++) {
                if (i < discos.get(d).size()) {
                    Bloco bloco = discos.get(d).get(i);
                    if (bloco.isParidade()) {
                        System.out.print("[P:" + bloco.getValorParidade() + "]\t");
                    } else if (bloco.isOcupado()) {
                        System.out.print("[D:" + bloco.getNome() + "]\t");
                    } else {
                        System.out.print("[L]\t");
                    }
                } else {
                    System.out.print("-\t");
                }
            }
            System.out.println();
        }

        // Mostrar estatísticas
        System.out.println("\nESTATÍSTICAS:");
        System.out.println("Blocos livres: " + contarBlocosLivres());
        System.out.println("Blocos ocupados: " + (totalBlocos() - contarBlocosLivres()));
        System.out.println("Blocos de paridade: " + contarBlocosParidade());
    }

    public void reconstruirDisco(int discoFalho) {
        if (discoFalho < 0 || discoFalho >= discos.size()) {
            throw new IllegalArgumentException("Disco inválido");
        }

        System.out.println("\nINICIANDO RECONSTRUÇÃO DO DISCO " + discoFalho);

        for (int i = 0; i < discos.get(0).size(); i++) {
            // Determinar se este bloco deve ser paridade ou dados
            int stripe = i / (discos.size() - 1);
            int discoParidade = stripe % discos.size();

            if (discoFalho == discoParidade) {
                // Reconstruir bloco de paridade
                int novaParidade = 0;
                for (int d = 0; d < discos.size(); d++) {
                    if (d != discoFalho && i < discos.get(d).size()) {
                        Bloco bloco = discos.get(d).get(i);
                        if (bloco.isOcupado() && !bloco.isParidade()) {
                            Arquivo arquivo = (Arquivo) bloco.getObjetoAlocado();
                            novaParidade ^= arquivo.getHashParidade();
                        }
                    }
                }
                discos.get(discoFalho).get(i).alocarComoParidade(novaParidade);
                System.out.printf("Bloco %d: Paridade reconstruída (P:%d)\n", i, novaParidade);
            } else {
                // Reconstruir bloco de dados
                int dadosRecuperados = 0;
                for (int d = 0; d < discos.size(); d++) {
                    if (d != discoFalho && i < discos.get(d).size()) {
                        Bloco bloco = discos.get(d).get(i);
                        if (bloco.isOcupado()) {
                            if (bloco.isParidade()) {
                                continue;
                            }
                            Arquivo arquivo = (Arquivo) bloco.getObjetoAlocado();
                            dadosRecuperados ^= arquivo.getHashParidade();
                        }
                    }
                }

                // Criar arquivo simulado para o bloco recuperado
                String nomeRecuperado = "recuperado_" + i;
                int tamanhoRecuperado = Math.abs(dadosRecuperados % 128) + 1; // Tamanho entre 1-128KB
                Arquivo arquivoRecuperado = new Arquivo(nomeRecuperado, tamanhoRecuperado, i);

                discos.get(discoFalho).get(i).alocar(nomeRecuperado, arquivoRecuperado);
                System.out.printf("Bloco %d: Dados reconstruídos (%s, %dKB)\n",
                        i, nomeRecuperado, tamanhoRecuperado);
            }
        }

        System.out.println("\nRECONSTRUÇÃO DO DISCO " + discoFalho + " CONCLUÍDA COM SUCESSO");
    }

    @Override
    public int contarBlocosLivres() {
        int totalLivres = 0;
        for (List<Bloco> disco : discos) {
            for (Bloco bloco : disco) {
                if (!bloco.isOcupado()) {
                    totalLivres++;
                }
            }
        }
        return totalLivres;
    }

    private int totalBlocos() {
        return discos.stream().mapToInt(List::size).sum();
    }

    private int contarBlocosParidade() {
        int paridades = 0;
        for (List<Bloco> disco : discos) {
            for (Bloco bloco : disco) {
                if (bloco.isParidade()) {
                    paridades++;
                }
            }
        }
        return paridades;
    }

    @Override
    public int calcularBlocosNecessarios(int tamanhoArquivoOuDiretorioKB) {
        // Cada bloco pode armazenar até o tamanho especificado
        return (int) Math.ceil((double) tamanhoArquivoOuDiretorioKB / tamanhoBloco);
    }

    private Bloco encontrarBlocoLivre(int discoIndex) {
        for (Bloco bloco : discos.get(discoIndex)) {
            if (!bloco.isOcupado()) {
                return bloco;
            }
        }
        return null;
    }

    private void calcularEAlocarParidade(int stripe, int inicio, int fim) {
        // Calcular disco de paridade para este stripe (rotação)
        int discoParidade = stripe % discos.size();

        // Calcular valor da paridade
        int paridade = 0;
        for (int d = 0; d < discos.size(); d++) {
            if (d == discoParidade) continue;

            for (int i = inicio; i < fim; i++) {
                Bloco bloco = discos.get(d).get(i);
                if (bloco.isOcupado() && !bloco.isParidade()) {
                    Arquivo arquivo = (Arquivo) bloco.getObjetoAlocado();
                    paridade ^= arquivo.getHashParidade();
                }
            }
        }

        // Alocar bloco de paridade
        Bloco blocoParidade = encontrarBlocoLivre(discoParidade);
        if (blocoParidade != null) {
            blocoParidade.alocarComoParidade(paridade);
        }
    }
}