package GerenciamentoEntradaSaida_Parte2;

import GerenciamentoArquivos.Arquivo;
import GerenciamentoArquivos.Bloco;
import java.util.ArrayList;
import java.util.List;

public class RAID{
    private List<List<Bloco>> discos; // Lista de discos, cada disco é uma lista de blocos
    private final int tamanhoBloco;
    private int qtdDiscos = 3;
    private int contadorEscritas = 0;

    public RAID(int memoriaTotalKB, int tamanhoBloco, int numeroDiscos) {
        this.qtdDiscos = numeroDiscos;
        int blocosTotais = memoriaTotalKB / tamanhoBloco;
        int blocosPorDisco = blocosTotais / numeroDiscos;
        this.tamanhoBloco = tamanhoBloco;
        this.discos = new ArrayList<>();

        if (blocosPorDisco <= 0) {
            throw new IllegalArgumentException("Configuração inválida: blocos por disco = " + blocosPorDisco);
        }

        for (int d = 0; d < qtdDiscos; d++) {
            List<Bloco> disco = new ArrayList<>();
            for (int b = 0; b < blocosPorDisco; b++) {
                disco.add(new Bloco(b, d)); // id, discoId, tamanho
            }
            discos.add(disco);
        }
    }
    public Bloco armazenarBloco(int idLogico, String nome, Object objetoAlocado) {
        int numDiscos = discos.size();
        int numDiscosDados = numDiscos - 1;

        // Primeiro verifica se há blocos desocupados em qualquer posição
        for (int d = 0; d < numDiscos; d++) {
            for (int s = 0; s < discos.get(d).size(); s++) {
                Bloco bloco = discos.get(d).get(s);
                if (!bloco.isOcupado() && d != discoParidade(s)) {
                    bloco.alocar(nome, objetoAlocado);

                    // Verifica se todos os outros blocos do stripe estão ocupados antes de calcular a paridade
                    boolean todosOcupados = true;
                    int discoPar = discoParidade(s);
                    for (int dd = 0; dd < numDiscos; dd++) {
                        if (dd != discoPar && !discos.get(dd).get(s).isOcupado()) {
                            todosOcupados = false;
                            break;
                        }
                    }

                    if (todosOcupados) {
                        calcularEAlocarParidade(s);
                    }
                    return bloco;
                }
            }
        }

        // Se não encontrou blocos desocupados, aloca um novo seguindo o esquema RAID5
        if (contadorEscritas >= discos.get(0).size() * numDiscosDados) {
            System.out.println("Todos os blocos estão ocupados. Não há espaço disponível.");
            return null;
        }

        int stripe = contadorEscritas / numDiscosDados;
        int posicaoRelativa = contadorEscritas % numDiscosDados;

        if (stripe >= discos.get(0).size()) {
            throw new IllegalStateException("Stripe inválido: " + stripe);
        }

        int discoPar = discoParidade(stripe);
        int discoDados = posicaoRelativa;
        if (posicaoRelativa >= discoPar) {
            discoDados++;
        }

        Bloco bloco = discos.get(discoDados).get(stripe);
        if (bloco.isOcupado()) {
            throw new IllegalStateException("Bloco já ocupado: Disco " + discoDados + ", Bloco " + stripe);
        }

        bloco.alocar(nome, objetoAlocado);
        contadorEscritas++;

        // Só calcula paridade se todos os blocos do stripe estiverem ocupados
        boolean todosOcupados = true;
        for (int dd = 0; dd < numDiscos; dd++) {
            if (dd != discoPar && !discos.get(dd).get(stripe).isOcupado()) {
                todosOcupados = false;
                break;
            }
        }

        if (todosOcupados) {
            calcularEAlocarParidade(stripe);
        }

        return bloco;
    }

    private int discoParidade(int stripe) {
        return stripe % discos.size();
    }

    public int calcularIndiceDoStripe(int idBloco) {
        return idBloco / (discos.size() - 1);
    }

    public void desalocarFisicamente(Bloco bloco) {
        if (bloco == null || !bloco.isOcupado()) {
            return;
        }

        int idBloco = bloco.getId();
        int stripeIndex = calcularIndiceDoStripe(idBloco);

        // Desaloca o bloco
        bloco.desalocar();

        // Atualiza contador de escritas se necessário
        if (idBloco == contadorEscritas - 1) {
            contadorEscritas--;
        }

        // Recalcula paridade para o stripe
        calcularEAlocarParidade(stripeIndex);
    }

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

    private void calcularEAlocarParidade(int stripe) {
        int discoParidade = discoParidade(stripe);
        int paridade = 0;

        for (int d = 0; d < discos.size(); d++) {
            if (d == discoParidade) continue;

            Bloco bloco = discos.get(d).get(stripe);
            if (bloco.isOcupado()) {
                paridade ^= bloco.getObjetoAlocado().hashCode();
            }
        }

        Bloco blocoParidade = discos.get(discoParidade).get(stripe);
        blocoParidade.alocarComoParidade(paridade);
    }

    public List<Bloco> getAllBlocos() {
        List<Bloco> todosBlocos = new ArrayList<>();
        for (List<Bloco> disco : discos) {
            todosBlocos.addAll(disco);
        }
        return todosBlocos;
    }
}