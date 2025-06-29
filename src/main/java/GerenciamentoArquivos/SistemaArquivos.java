package GerenciamentoArquivos;

import java.util.*;

public class SistemaArquivos {

    private final Map<String, Diretorio> diretorios;
    private final Alocador alocador;
    private final int tamanhoBloco;

    // Construtor:
    public SistemaArquivos(int memoriaTotalKB, int tamanhoBloco, int tipoAlocador) {
        this.diretorios = new HashMap<>();
        this.tamanhoBloco = tamanhoBloco;

        if (tipoAlocador == 1) {
            this.alocador = new AlocadorEncadeado(memoriaTotalKB, tamanhoBloco);
        }
        else  {
            this.alocador = new AlocadorFAT();
        }
    }

    // Métodos:
    public void criarDiretorio(String nomeDiretorio) {
        if (diretorios.containsKey(nomeDiretorio)) {
            System.out.println("Erro: Já existe um arquivo ou diretório chamado " + nomeDiretorio);
        }
        else {
            Diretorio novoDiretorio = new Diretorio(nomeDiretorio, -1); // bloco ainda não definido
            int blocoDiretorio = alocador.alocarNoBloco(nomeDiretorio, tamanhoBloco, novoDiretorio);
            if (blocoDiretorio == -1) {
                System.out.println("Erro: Memória insuficiente para alocar diretório.");
            }
            else {
                novoDiretorio.setBlocoAlocado(blocoDiretorio); // Atualizando agr o bloco que o diretório foi alocado
                diretorios.put(nomeDiretorio, novoDiretorio);
                System.out.printf("Diretório '%s' criado no bloco %d.%n", nomeDiretorio, blocoDiretorio);
                mostrarEstadoBlocos();
            }
        }
    }

    public void excluirDiretorio(String nomeDiretorio, Scanner scanner) {
        Diretorio diretorio = diretorios.get(nomeDiretorio);

        if (diretorio == null) {
            System.out.println("Erro: Diretório " + nomeDiretorio + " não encontrado.");
        }
        else {
            if (!diretorio.getArquivos().isEmpty()) {
                System.out.printf("ATENÇÃO: O diretório " + nomeDiretorio + " contém arquivos\n" +
                        "ao apagar o diretório todos os arquivos contidos nele também serão apagados\n" +
                        "Deseja realmente apagar o diretório?\n" +
                        "1 - Sim \n" +
                        "2 - Não \n" +
                        "Escolha uma opção: ");
                int opcao = scanner.nextInt();

                if (opcao == 1) {
                    for (Arquivo arquivo : diretorio.getArquivos()) {
                        alocador.desalocarBloco(arquivo.getNome());
                        excluirArquivo(nomeDiretorio, arquivo.getNome());
                    }
                    alocador.desalocarBloco(nomeDiretorio);
                    diretorios.remove(nomeDiretorio);
                    System.out.printf("Diretório '%s' removido com sucesso.%n", nomeDiretorio);
                    mostrarEstadoBlocos();
                }
                else {
                    System.out.println("Operação cancelada.");
                }
            }
            else {
                alocador.desalocarBloco(nomeDiretorio);
                diretorios.remove(nomeDiretorio);
                System.out.printf("Diretório '%s' removido com sucesso.%n", nomeDiretorio);
                mostrarEstadoBlocos();
            }
        }
    }

    public void criarArquivo(String nomeDir, String nomeArquivo, int tamanhoArquivoKB) {
        Diretorio diretorio = diretorios.get(nomeDir);

        if (diretorio == null) {
            System.out.println("Erro: Diretório inesxistente");
        }
        else if (diretorio.possuiArquivo(nomeArquivo) || diretorios.containsKey(nomeArquivo)) {
            System.out.println("Erro: Já existe um arquivo ou diretório chamado " + nomeArquivo);
        }
        else{
            Arquivo novoArquivo = new Arquivo(nomeArquivo, tamanhoArquivoKB, -1);
            int blocoInicial = alocador.alocarNoBloco(nomeArquivo, tamanhoArquivoKB, novoArquivo); // bloco ainda não definido

            // se blocoInicial == -1 → Alocação do arquivo falhou
            if (blocoInicial == -1) {
                int livres = alocador.contarBlocosLivres();
                int blocosNecessarios = alocador.calcularBlocosNecessarios(tamanhoArquivoKB);

                if (livres >= blocosNecessarios) {
                    System.out.println("Possível fragmentação externa: há blocos livres suficientes, mas não contíguos.");
                }
            }
            else {
                novoArquivo.setBlocoInicial(blocoInicial); // Atualizando agr o bloco que o diretório foi alocado
                diretorio.adicionarArquivo(novoArquivo);
                System.out.printf("Arquivo '%s' criado (%dKB) dentro de '%s'.%n", nomeArquivo, tamanhoArquivoKB, nomeDir);
                alocador.verificarFragmentacaoInterna(nomeArquivo, tamanhoArquivoKB);
                mostrarEstadoBlocos();
            }
        }
    }

    public void excluirArquivo(String nomeDir, String nomeArquivo) {
        Diretorio diretorio = diretorios.get(nomeDir);
        if (diretorio == null) {
            System.out.println("Erro: Diretório inexistente.");
        }
        Arquivo arq = diretorio.buscarArquivo(nomeArquivo);
        if (arq == null) {
            System.out.println("Erro: Arquivo não encontrado em " + nomeDir);
        }
        else{
            alocador.desalocarBloco(nomeArquivo);
            diretorio.removerArquivo(nomeArquivo);
            System.out.println("Arquivo removido com sucesso");
            mostrarEstadoBlocos();
        }
    }

    public void listarDiretorios() {
        if (diretorios.isEmpty()) {
            System.out.println("O sistema não contém nenhum diretório");
        }
        else{
            System.out.println("[Raiz] Diretórios:");
            for(Diretorio diretorio : diretorios.values()){
                System.out.println(" - " + diretorio.getNome());
            }
        }
    }

    public void listarConteudoDiretorio(String nomeDir) {
        Diretorio diretorio = diretorios.get(nomeDir);
        if (diretorio == null) {
            System.out.println("Erro: Diretório inexistente");
        }
        else{
            System.out.println("Arquivos contidos em " + nomeDir);
            diretorio.listarArquivos();
        }
    }

    public void mostrarEstadoBlocos() {
        System.out.println("\n-------------------- Estado dos Blocos --------------------");
        alocador.mostrarBlocos();
        System.out.println("-----------------------------------------------------------\n");
    }
}
