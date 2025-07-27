package GerenciamentoArquivos;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import GerenciamentoEntradaSaida_Parte2.RAID;

/**
 * Implementação de alocador de memória usando tabela FAT (File Allocation Table).
 */
public class AlocadorFAT implements Alocador {

    private final RAID raid;
    private final Bloco[] blocos; // Usado para indexar, mas o armazenamento real é feito no RAID
    private final int tamanhoBlocoKB;
    private final int[] tabelaFAT; // -1: livre, -2: EOF, >= 0: próximo bloco
    private final Map<String, Object> tabelaArquivos = new HashMap<>();


    public AlocadorFAT(int totalMemoriaKB, int tamanhoBlocoKB, RAID raid) {
        int totalBlocos = totalMemoriaKB / tamanhoBlocoKB;
        this.tabelaFAT = new int[totalBlocos];
        this.tamanhoBlocoKB = tamanhoBlocoKB;
        this.blocos = new Bloco[totalBlocos]; // Ainda usamos pra indexar, mas delegamos armazenamento real ao RAID
        Arrays.fill(tabelaFAT, -1);
        this.raid = raid;
    }

    @Override
    public int alocarNoBloco(String nome, int tamanhoDadoKB, Object objetoAlocado) {
        if (tabelaArquivos.containsKey(nome)) {
            throw new IllegalArgumentException("Já existe alocação com nome: " + nome);
        }

        int blocosNecessarios = calcularBlocosNecessarios(tamanhoDadoKB);
        int[] blocosAlocados = new int[blocosNecessarios];
        int blocosAlocadosCount = 0;
        int currentIndex = 0;

        // Busca cíclica por blocos livres
        while (blocosAlocadosCount < blocosNecessarios && currentIndex < tabelaFAT.length * 2) {
            int blocoId = currentIndex % tabelaFAT.length; // Garante que volte ao início

            if (tabelaFAT[blocoId] == -1) { // Bloco livre
                blocosAlocados[blocosAlocadosCount++] = blocoId;
            }
            currentIndex++;
        }

        if (blocosAlocadosCount < blocosNecessarios) {
            System.out.println("Espaço insuficiente para alocar '" + nome + "'");
            return -1;
        }

        // Aloca os blocos no RAID e atualiza a FAT
        for (int i = 0; i < blocosNecessarios; i++) {
            int blocoId = blocosAlocados[i];
            blocos[blocoId] = raid.armazenarBloco(blocoId, nome, objetoAlocado);
            tabelaFAT[blocoId] = (i == blocosNecessarios - 1) ? -2 : blocosAlocados[i + 1];
        }

        tabelaArquivos.put(nome, objetoAlocado);
        System.out.println("Arquivo '" + nome + "' alocado. Bloco inicial: " + blocosAlocados[0]);
        return blocosAlocados[0];
    }

    @Override
    public void desalocarBloco(String nome) {
        Arquivo arquivo = (Arquivo) (tabelaArquivos.remove(nome));
        if (arquivo == null) {
            System.out.println("Arquivo não encontrado: " + nome);
            return;
        }

        int blocoAtual = arquivo.getBlocoInicial();
        int blocosLiberados = 0;

        while (blocoAtual >= 0 && blocoAtual < tabelaFAT.length) {
            int proximoBloco = tabelaFAT[blocoAtual];

            // Desaloca fisicamente no RAID
            if (blocos[blocoAtual] != null && blocos[blocoAtual].isOcupado()) {
                raid.desalocarFisicamente(blocos[blocoAtual]);
                blocosLiberados++;
            }

            // Marca como livre na FAT
            tabelaFAT[blocoAtual] = -1;

            if (proximoBloco == -2 || proximoBloco == -1) {
                break;
            }

            blocoAtual = proximoBloco;
        }

        System.out.printf("Arquivo '%s' desalocado. %d blocos liberados.%n",
                nome, blocosLiberados);
    }

    @Override
    public void verificarFragmentacaoInterna(String nome, int tamanhoKB) {
        Arquivo arquivo = (Arquivo)(tabelaArquivos.get(nome));
        if (arquivo == null) {
            System.out.println("Arquivo não encontrado: " + nome);
            return;
        }

        int realKB = arquivo.getTamanho();
        int blocosUsados = calcularBlocosNecessarios(realKB);
        int cheio = (blocosUsados - 1) * tamanhoBlocoKB;
        int usadoNoUltimo = realKB - cheio;
        int fragInterna = tamanhoBlocoKB - usadoNoUltimo;
        System.out.println("Fragmentação interna de '" + nome + "': " + fragInterna + " KB");
    }

    public void verificarFragmentacaoInternaTotal() {
        int totalFragmentacao = 0;

        for (Object arquivo : tabelaArquivos.values()) {
            int tamanhoKB = ((Arquivo)(arquivo)).getTamanho();
            int blocosUsados = calcularBlocosNecessarios(tamanhoKB);
            int cheio = (blocosUsados - 1) * tamanhoBlocoKB;
            int usadoNoUltimo = tamanhoKB - cheio;
            int fragmento = tamanhoBlocoKB - usadoNoUltimo;

            totalFragmentacao += fragmento;
        }

        System.out.printf("Fragmentação interna total: %d KB%n", totalFragmentacao);
    }


    @Override
    public void mostrarBlocos() {
        System.out.println("Visualização lógica via FAT:");
        System.out.println("Blocos alocados:");

        if (tabelaArquivos.isEmpty()) {
            System.out.println("Nenhum bloco alocado.");
            return;
        }

        for (int i = 0; i < blocos.length; i++) {
            Bloco bloco = blocos[i];

            if (bloco == null || !bloco.isOcupado()) {
                // Se o bloco for null ou não ocupado, mostramos a posição lógica
                String pos = (bloco != null) ? bloco.getNomeFormatado() : "(" + i + ",?)";
                System.out.printf("Bloco %7s | LIVRE%n", pos);
            } else {
                String tipo = (bloco.getObjetoAlocado() instanceof Diretorio) ? "Diretório" : "Arquivo";
                System.out.printf("Bloco %7s | %-9s: %-16s%n",
                        bloco.getNomeFormatado(), tipo, bloco.getNome());
            }
        }

        System.out.println("\n-------------------- Tabela FAT --------------------");
        System.out.println("Legenda: -1 = LIVRE | -2 = EOF | outro = próximo bloco");

        for (int i = 0; i < tabelaFAT.length; i++) {
            String status;
            if (tabelaFAT[i] == -1) status = "LIVRE";
            else if (tabelaFAT[i] == -2) status = "EOF";
            else status = String.valueOf(tabelaFAT[i]);
            System.out.printf("[%3d] -> %s%n", i, status);
        }

        System.out.println("\nVisualização física via RAID:");
        raid.mostrarBlocos();
    }

    @Override
    public int contarBlocosLivres() {
        int count = 0;
        for (int i : tabelaFAT) {
            if (i == -1) count++;
        }
        return count;
    }

    @Override
    public int calcularBlocosNecessarios(int tamanhoArquivoOuDiretorioKB) {
        int blocos = tamanhoArquivoOuDiretorioKB / tamanhoBlocoKB;
        if (tamanhoArquivoOuDiretorioKB % tamanhoBlocoKB != 0) blocos++;
        return blocos;
    }

    @Override
    public RAID getRaid() {
        return raid;
    }
}
