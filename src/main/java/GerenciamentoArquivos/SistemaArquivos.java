package GerenciamentoArquivos;

import java.util.*;
import GerenciamentoEntradaSaida_Parte2.RAID;
import java.util.Scanner;

public class SistemaArquivos {

    private final Map<String, Diretorio> diretorios;
    private final Alocador alocador;
    private final int tamanhoBloco;
    private final RAID RAID;
    private Scanner scanner;

    // Construtor:
    public SistemaArquivos(int memoriaTotalKB, int tamanhoBloco, int tipoAlocador, int numeroDiscos) {
        this.diretorios = new HashMap<>();
        this.diretorios.put("[Raiz]", new Diretorio("[Raiz]", -1, false, null)); // Diretório raiz
        this.tamanhoBloco = tamanhoBloco;
        this.RAID = new RAID(memoriaTotalKB, tamanhoBloco, numeroDiscos);
        this.scanner = new Scanner(System.in);

        if (tipoAlocador == 1) {
            this.alocador = new AlocadorEncadeado(tamanhoBloco, RAID);
        }
        else {
            this.alocador = new AlocadorFAT(memoriaTotalKB, tamanhoBloco, RAID);
        }
    }

    // Métodos:
    public void criarDiretorio(String nomeDiretorio, boolean protegido, String senha) {
        if (diretorios.containsKey(nomeDiretorio)) {
            System.out.println("Erro: Já existe um arquivo ou diretório chamado " + nomeDiretorio);
        }
        else {
            Diretorio novoDiretorio = new Diretorio(nomeDiretorio, -1, protegido, senha); // bloco ainda não definido
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

        if ("[Raiz]".equals(nomeDiretorio)) {
            System.out.println("Não é possível excluir o diretório raiz!");
            return;
        }

        if (diretorio == null) {
            System.out.println("Erro: Diretório " + nomeDiretorio + " não encontrado.");
            return;
        }

        if (diretorio.isProtegido()) {
            System.out.println("Esse diretório é protegido, digite a senha para poder excluí-lo.");
            String senha = scanner.nextLine();
            if (!senha.equals(diretorio.getSenha())) {
                System.out.println("Senha incorreta. Operação cancelada.");
                return;
            }
        }

        if (!diretorio.getArquivos().isEmpty()) {
            System.out.println("ATENÇÃO: O diretório " + nomeDiretorio + " contém arquivos.");
            System.out.println("Ao apagar o diretório, todos os arquivos contidos nele também serão apagados.");
            System.out.println("Deseja realmente apagar o diretório?");
            System.out.println("1 - Sim");
            System.out.println("2 - Não");
            System.out.print("Escolha uma opção: ");

            int opcao = scanner.nextInt();
            scanner.nextLine(); // limpar o buffer do scanner

            if (opcao != 1) {
                System.out.println("Operação cancelada.");
                return;
            }

            boolean sucesso = true;
            for (Arquivo arquivo : diretorio.getArquivos()) {
                alocador.desalocarBloco(arquivo.getNome());
                if (!excluirArquivo(nomeDiretorio, arquivo.getNome(), true)) {
                    System.out.println("Erro ao excluir arquivo: " + arquivo.getNome());
                    sucesso = false;
                }
            }
            if (!sucesso) {
                System.out.println("Alguns arquivos não foram excluídos. Diretório não removido.");
                return;
            }
        }

        alocador.desalocarBloco(nomeDiretorio);
        diretorios.remove(nomeDiretorio);
        System.out.printf("Diretório '%s' removido com sucesso.%n", nomeDiretorio);
        mostrarEstadoBlocos();
    }

    public void criarArquivo(String nomeDir, String nomeArquivo, int tamanhoArquivoKB, String conteudo, boolean protegido, String senha) {
        Diretorio diretorio = diretorios.get(nomeDir);

        if (diretorio == null && !nomeDir.isEmpty()) {
                System.out.println("Erro: Diretório " + nomeDir + " não encontrado.");
                return;
        }
        diretorio = diretorio == null ? diretorios.get("[Raiz]") : diretorio; // se o diretório não existir, cria no raiz

        if (diretorio.isProtegido()) {
            System.out.println("Esse diretório é protegido, digite a senha:");
            String tentativa = scanner.nextLine();
            if (!tentativa.equals(diretorio.getSenha())){
                System.out.println("Senha incorreta. Criação de arquivo abortada.");
                return;
            }
        }

        if (diretorio.possuiArquivo(nomeArquivo) || diretorios.containsKey(nomeArquivo)) {
            System.out.println("Erro: Já existe um arquivo ou diretório chamado " + nomeArquivo);
        } else{
            Arquivo novoArquivo = new Arquivo(nomeArquivo, tamanhoArquivoKB, -1, conteudo, protegido, senha);
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

    public boolean excluirArquivo(String nomeDir, String nomeArquivo, boolean excluindoDir) {
        Diretorio diretorio = diretorios.get(nomeDir);
        if (diretorio == null & !nomeDir.isEmpty()) {
            System.out.println("Erro: Diretório inexistente.");
            return false;
        }
        diretorio = diretorio == null ? diretorios.get("[Raiz]") : diretorio; // se o diretório não existir, cria no raiz
        if (diretorio.isProtegido() && !excluindoDir){
            System.out.println("Esse diretório é protegido, digite a senha:");
            String senha = scanner.nextLine();
            if (!senha.equals(diretorio.getSenha())){
                System.out.println("Senha incorreta.");
                return false;
            }
        }
        Arquivo arq = diretorio.buscarArquivo(nomeArquivo);
        if (arq == null) {
            System.out.println("Erro: Arquivo não encontrado em " + nomeDir);
        }

        if (arq.isProtegido()){
            System.out.println("Esse arquivo é protegido, digite a senha:");
            String senha = scanner.nextLine();
            if (!senha.equals(arq.getSenha())){
                System.out.println("Senha incorreta.");
                return false;
            }
        }
        alocador.desalocarBloco(nomeArquivo);
        diretorio.removerArquivo(nomeArquivo);
        alocador.verificarFragmentacaoInterna(nomeArquivo, arq.getTamanho());
        System.out.println("Arquivo removido com sucesso");
        mostrarEstadoBlocos();
        return true;
    }

    public void visualizarConteudoArquivo(String nomeDir, String nomeArquivo, Scanner scanner) {
        Diretorio diretorio = diretorios.get(nomeDir);

        if (diretorio == null && !nomeDir.isEmpty()) {
            System.out.println("Erro: Diretório inexistente.");
            return;
        }

        diretorio = diretorio == null ? diretorios.get("[Raiz]") : diretorio; // usa raiz como fallback

        Arquivo arquivo = diretorio.buscarArquivo(nomeArquivo);
        if (arquivo == null) {
            System.out.println("Erro: Arquivo '" + nomeArquivo + "' não encontrado no diretório '" + nomeDir + "'.");
            return;
        }

        if (arquivo.isProtegido()) {
            System.out.print("Esse arquivo é protegido. Digite a senha para visualizar o conteúdo: ");
            String senha = scanner.nextLine();
            if (!senha.equals(arquivo.getSenha())) {
                System.out.println("Senha incorreta. Acesso negado.");
                return;
            }
        }

        System.out.println("Conteúdo do arquivo '" + nomeArquivo + "':");
        System.out.println(arquivo.getConteudo());
    }


    public void listarDiretorios() {
        if (diretorios.isEmpty()) {
            System.out.println("O sistema não contém nenhum diretório");
            return;
        }
        for (Diretorio diretorio : diretorios.values()) {
            if (!diretorio.getNome().equals("[Raiz]")) {
                System.out.println(diretorio.getNome());
                if (!diretorio.isProtegido()){
                    for (Arquivo arquivo : diretorio.getArquivos()) {
                        System.out.println("  └── " + arquivo.getNome());
                    }
                } else {
                    System.out.println("   Diretório protegido");
                }
            } else {
                for (Arquivo arquivo : diretorio.getArquivos()) {
                    System.out.println(arquivo.getNome());
                }
            }
        }
    }

    public void listarConteudoDiretorio(String nomeDir, Scanner scanner) {
        Diretorio diretorio = diretorios.get(nomeDir);
        if (diretorio == null & !nomeDir.isEmpty()) {
            System.out.println("Erro: Diretório inexistente");
            return;
        }
        if (diretorio != null && diretorio.isProtegido()) {
            System.out.print("Esse diretório é protegido. Digite a senha para visualizar o conteúdo: ");
            String senha = scanner.nextLine();
            if (!senha.equals(diretorio.getSenha())) {
                System.out.println("Senha incorreta. Acesso negado.");
                return;
            }
        }
        diretorio = diretorio == null ? diretorios.get("[Raiz]") : diretorio; // se o diretório não existir, cria no raiz
        System.out.println("Diretório " + nomeDir + ":");
        diretorio.listarArquivos();
    }

    public void mostrarEstadoBlocos() {
        System.out.println("\n-------------------- Estado dos Blocos --------------------");
        alocador.mostrarBlocos();
        System.out.println("-----------------------------------------------------------\n");
    }

    public void mostrarFragmentacaoInternaTotal() {
        if (alocador instanceof AlocadorFAT) {
            ((AlocadorFAT) alocador).verificarFragmentacaoInternaTotal();
        } else {
            System.out.println("Fragmentação interna total não disponível para alocador encadeado.");
        }
    }

    public Map<String, Diretorio> getDiretorios() {
        return diretorios;
    }

    public Alocador getAlocador() {
        return alocador;
    }

    public int getTamanhoBloco() {
        return tamanhoBloco;
    }
}
