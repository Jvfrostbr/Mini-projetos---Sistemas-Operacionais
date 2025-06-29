package GerenciamentoArquivos;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementação de alocador de memória usando tabela FAT (File Allocation Table).
 */
public class AlocadorFAT implements Alocador {

    private final Bloco[] blocos;
    private final int tamanhoBlocoKB;
    private final int[] tabelaFAT; // -1: livre, -2: EOF, >= 0: próximo bloco
    private final Map<String, Arquivo> tabelaArquivos = new HashMap<>();

    public AlocadorFAT(int totalMemoriaKB, int tamanhoBlocoKB) {
        int totalBlocos = totalMemoriaKB / tamanhoBlocoKB;
        this.blocos = new Bloco[totalBlocos];
        for (int i = 0; i < totalBlocos; i++) {
            blocos[i] = new Bloco(i);
        }
        this.tamanhoBlocoKB = tamanhoBlocoKB;
        this.tabelaFAT = new int[totalBlocos];
        Arrays.fill(tabelaFAT, -1);
    }

    @Override
    public int alocarNoBloco(String nome, int tamanhoDadoKB, Object objetoAlocado) {
        if (tabelaArquivos.containsKey(nome)) {
            throw new IllegalArgumentException("Já existe alocação com nome: " + nome);
        }

        int blocosNecessarios = calcularBlocosNecessarios(tamanhoDadoKB);
        int[] blocosAlocados = new int[blocosNecessarios];
        int index = 0;

        for (int i = 0; i < blocos.length && index < blocosNecessarios; i++) {
            if (tabelaFAT[i] == -1) {
                blocosAlocados[index++] = i;
            }
        }

        if (index < blocosNecessarios) {
            System.out.println("Espaço insuficiente para alocar '" + nome + "'");
            return -1;
        }

        for (int i = 0; i < blocosNecessarios; i++) {
            int blocoId = blocosAlocados[i];
            blocos[blocoId].alocar(nome, objetoAlocado);
            if (i == blocosNecessarios - 1) {
                tabelaFAT[blocoId] = -2; // EOF
            } else {
                tabelaFAT[blocoId] = blocosAlocados[i + 1];
            }
        }

        Arquivo arquivo = new Arquivo(nome, tamanhoDadoKB, blocosAlocados[0]);
        tabelaArquivos.put(nome, arquivo);
        System.out.println("Arquivo '" + nome + "' alocado. Bloco inicial: " + blocosAlocados[0]);
        return blocosAlocados[0];
    }

    @Override
    public void desalocarBloco(String nome) {
        Arquivo arquivo = tabelaArquivos.remove(nome);
        if (arquivo == null) {
            System.out.println("Arquivo não encontrado: " + nome);
            return;
        }

        int bloco = arquivo.getBlocoInicial();
        while (bloco != -2 && bloco != -1) {
            int proximo = tabelaFAT[bloco];
            tabelaFAT[bloco] = -1;
            blocos[bloco].desalocar();
            bloco = proximo;
        }
        System.out.println("Arquivo '" + nome + "' desalocado.");
    }

    @Override
    public void verificarFragmentacaoInterna(String nome, int tamanhoKB) {
        Arquivo arquivo = tabelaArquivos.get(nome);
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

        for (Arquivo arquivo : tabelaArquivos.values()) {
            int tamanhoKB = arquivo.getTamanho();
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
        System.out.println("Blocos alocados:");
        for (Bloco bloco : blocos) {
            if (!bloco.isOcupado()) {
                System.out.printf("Bloco %2d | LIVRE%n", bloco.getId());
            } else {
                String tipo = (bloco.getObjetoAlocado() instanceof Diretorio) ? "Diretório" : "Arquivo";
                System.out.printf("Bloco %2d | %s: %s%n", bloco.getId(), tipo, bloco.getNome());
            }
        }
        System.out.println("Tabela FAT:");
        for (int i = 0; i < tabelaFAT.length; i++) {
            String status;
            if (tabelaFAT[i] == -1) status = "Livre";
            else if (tabelaFAT[i] == -2) status = "EOF";
            else status = String.valueOf(tabelaFAT[i]);
            System.out.printf("[%3d] -> %s\n", i, status);
        }
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
}
