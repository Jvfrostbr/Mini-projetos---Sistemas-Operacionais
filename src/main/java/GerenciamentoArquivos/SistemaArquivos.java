package GerenciamentoArquivos;

import java.util.*;

public class SistemaArquivos {

    private final Map<String, Diretorio> diretorios;
    private final AlocadorEncadeado alocador;
    private final int tamanhoBlocoKB;

    public SistemaArquivos(int memoriaTotalKB, int tamanhoBlocoKB) {
        this.diretorios = new HashMap<>();
        this.alocador = new AlocadorEncadeado(memoriaTotalKB, tamanhoBlocoKB);
        this.tamanhoBlocoKB = tamanhoBlocoKB;
    }

    public void criarDiretorio(String nomeDir) {
        if (diretorios.containsKey(nomeDir) || alocador.arquivoExiste(nomeDir)) {
            System.out.printf("Erro: Já existe um arquivo ou diretório chamado '%s'.%n", nomeDir);
            return;
        }
        diretorios.put(nomeDir, new Diretorio(nomeDir));
        System.out.printf("Diretório '%s' criado com sucesso.%n", nomeDir);
        mostrarEstadoBlocos();
    }

    public void excluirDiretorio(String nomeDir) {
        Diretorio dir = diretorios.get(nomeDir);
        if (dir == null) {
            System.out.printf("Erro: Diretório '%s' não encontrado.%n", nomeDir);
            return;
        }
        if (!dir.getArquivos().isEmpty()) {
            System.out.printf("Erro: Diretório '%s' não está vazio.%n", nomeDir);
            return;
        }
        diretorios.remove(nomeDir);
        System.out.printf("Diretório '%s' removido com sucesso.%n", nomeDir);
        mostrarEstadoBlocos();
    }

    public void criarArquivo(String nomeDir, String nomeArquivo, int tamanhoKB) {
        Diretorio dir = diretorios.get(nomeDir);
        if (dir == null) {
            System.out.printf("Erro: Diretório '%s' inexistente.%n", nomeDir);
            return;
        }
//        if (dir.possuiArquivo(nomeArquivo) || diretorios.containsKey(nomeArquivo) || alocador.arquivoExiste(nomeArquivo)) {
//            System.out.printf("Erro: Já existe um arquivo ou diretório chamado '%s'.%n", nomeArquivo);
//            return;
//        }

        int blocoInicial = alocador.alocarArquivo(nomeArquivo, tamanhoKB);
//        if (blocoInicial == -1) {
//            int livres = alocador.contarBlocosLivres();
//            int blocosNecessarios = (int) Math.ceil((double) tamanhoKB / tamanhoBlocoKB);
//            if (livres >= blocosNecessarios) {
//                System.out.println("Possível fragmentação externa: há blocos livres suficientes, mas não contíguos.");
//            }
//            return;
//        }

        Arquivo novo = new Arquivo(nomeArquivo, tamanhoKB, blocoInicial);
        dir.adicionarArquivo(novo);
        System.out.printf("Arquivo '%s' criado (%dKB) dentro de '%s'.%n", nomeArquivo, tamanhoKB, nomeDir);
        alocador.verificarFragmentacaoInterna(nomeArquivo, tamanhoKB);
        mostrarEstadoBlocos();
    }

    public void excluirArquivo(String nomeDir, String nomeArquivo) {
        Diretorio dir = diretorios.get(nomeDir);
        if (dir == null) {
            System.out.printf("Erro: Diretório '%s' inexistente.%n", nomeDir);
            return;
        }
        Arquivo arq = dir.buscarArquivo(nomeArquivo);
        if (arq == null) {
            System.out.printf("Erro: Arquivo '%s' não encontrado em '%s'.%n", nomeArquivo, nomeDir);
            return;
        }
        alocador.desalocarArquivo(nomeArquivo);
        dir.removerArquivo(nomeArquivo);
        System.out.printf("Arquivo '%s' removido de '%s'.%n", nomeArquivo, nomeDir);
        mostrarEstadoBlocos();
    }

    public void listarDiretorios() {
        if (diretorios.isEmpty()) {
            System.out.println("[Raiz] Diretórios: (nenhum)");
            return;
        }
        System.out.println("[Raiz] Diretórios:");
        diretorios.keySet().forEach(d -> System.out.println("  - " + d));
    }

    public void listarConteudoDiretorio(String nomeDir) {
        Diretorio dir = diretorios.get(nomeDir);
        if (dir == null) {
            System.out.printf("Erro: Diretório '%s' inexistente.%n", nomeDir);
            return;
        }
        System.out.printf("Arquivos em '%s':%n", nomeDir);
        dir.listarArquivos();
    }

    public void mostrarEstadoBlocos() {
        System.out.println("-------------------- Estado dos Blocos --------------------");
        alocador.mostrarBlocos();
        System.out.println("-----------------------------------------------------------\n");
    }
}
