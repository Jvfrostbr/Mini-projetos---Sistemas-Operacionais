package GerenciamentoMemoria_Parte2;

import java.util.*;

public class GerenciadorMemoriaPaginada {
    private int tamanhoPagina;
    private int numeroPaginasFisicas;
    private int numeroPaginasVirtuais;
    private int limiteFisicasPorProcesso;   // constante que limita o número de paginas alocadas na memória física
    private int pageMisses;                 // Contador de page misses
    private int pageHit;
    private int ponteiroRelogio;            // Ponteiro utilizado pelo algoritmo do Relógio
    private PaginaFisica[] memoriaFisica;
    private PaginaVirtual[] memoriaVirtual;
    private Map<PaginaVirtual, PaginaFisica> mapaPaginaVirtualFisica;
    private Queue<ProcessoPaginacao> filaProcessos;     // Fila de processos a serem alocados (ordem FIFO)
    private Queue<PaginaFisica> FIFOPaginaFisicas;        // Fila que armazena os processos que foram alocados na memória física
    String modoExibicao;

    // CONSTRUTOR
    public GerenciadorMemoriaPaginada(int tamFisica, int tamVirtual, int tamPagina, int limiteFisicasPorProcesso) {
        this.tamanhoPagina = tamPagina;
        this.numeroPaginasFisicas = tamFisica / tamPagina;
        this.numeroPaginasVirtuais = tamVirtual / tamPagina;
        this.memoriaFisica = new PaginaFisica[numeroPaginasFisicas];
        this.memoriaVirtual = new PaginaVirtual[numeroPaginasVirtuais];
        this.filaProcessos = new LinkedList<>();
        this.mapaPaginaVirtualFisica = new LinkedHashMap<>();
        this.pageMisses = 0;
        this.pageHit = 0;
        this.ponteiroRelogio = 0;
        this.limiteFisicasPorProcesso = limiteFisicasPorProcesso;
        this.FIFOPaginaFisicas = new LinkedList<>();
    }

    //MÉTODOS:
    public void adicionarProcesso(ProcessoPaginacao processo) {
        filaProcessos.add(processo);
    }

    public void alocarTodosOsProcessos() {
       for (ProcessoPaginacao processo : filaProcessos) {
            alocarPaginas(processo);
        }
    }

    public void alocarPaginas(ProcessoPaginacao processo) {
        int paginasNecessarias = processo.getNumPaginas();

        int paginasFisicasAlocadas = 0;
        for (int i = 0; i < paginasNecessarias; i++) {
            // Formata o número da página com 2 dígitos (ex: 01, 02, ..., 15)
            String numeroPagina = String.format("%02d", i + 1);

            PaginaFisica paginaFisica = new PaginaFisica(numeroPagina + "PágF | " + processo.getNome(), processo.getId(), i);
            PaginaVirtual paginaVirtual = new PaginaVirtual(numeroPagina + "PágV | " + processo.getNome(), processo.getId(), i);

            boolean alocada = false;

            // Alocando as páginas na memória física:
            if (paginasFisicasAlocadas < limiteFisicasPorProcesso) {
                int indiceFisico = encontrarIndiceLivreMemoria(memoriaFisica);
                if (indiceFisico != -1) {
                    memoriaFisica[indiceFisico] = paginaFisica;
                    FIFOPaginaFisicas.add(paginaFisica);      // Atualiza a fila de páginas alocadas na memória física
                    paginasFisicasAlocadas++;
                    alocada = true;
                }
            }

            // Alocando páginas na memória virtual:
            if (!alocada) {
                int indiceVirtual = encontrarIndiceLivreMemoria(memoriaVirtual);
                if (indiceVirtual != -1) {
                    memoriaVirtual[indiceVirtual] = paginaVirtual;
                } else {
                    System.out.println("ERRO: Sem espaço na memória física e virtual para alocar página: " + paginaFisica.getNome());
                    continue;
                }
            }
            mapaPaginaVirtualFisica.put(paginaVirtual, paginaFisica);
        }
    }

    private int encontrarIndiceLivreMemoria(Object[] memoria) {
        boolean achou = false;
        int indice = -1;

        for (int i = 0; i < memoria.length && !achou; i++) {
            if (memoria[i] == null) {
                indice = i;
                achou = true;
            }
        }
        return indice;
    }

    private void desalocarPaginasDoProcesso(ProcessoPaginacao processo) {
        // Desalocando todas as páginas do processo da memória física
        for (int i = 0; i < memoriaFisica.length; i++) {
            if (memoriaFisica[i] != null && memoriaFisica[i].getIdProcesso() == processo.getId()) {
                memoriaFisica[i] = null;
            }
        }

        // Desalocando todas as páginas do processo da memória virtual
        for (int i = 0; i < memoriaVirtual.length; i++) {
            if (memoriaVirtual[i] != null && memoriaVirtual[i].getIdProcesso() == processo.getId()) {
                memoriaVirtual[i] = null;
            }
        }
    }

    public void executarSimulacao(String algoritmo) {
        for (ProcessoPaginacao processo : filaProcessos) {
            List<Integer> referencias = processo.getReferenciasPaginas();
            processo.imprimirReferencias();

            System.out.println("Digite enter para iniciar a simulação do processo " + processo.getNome() + ':');
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();   // Para consumir o enter digitado

            for (int indicePaginaReferenciada : referencias) {
                Pagina paginaEncontrada = ProcurarPaginaNaMemoria(indicePaginaReferenciada, processo.getId(), memoriaFisica);

                // Se a paǵina não foi encontrada na memória fisica
                if (paginaEncontrada == null) {
                    // Procura a página virtual pelo indice
                    Pagina paginaRequisitada = ProcurarPaginaNaMemoria(indicePaginaReferenciada, processo.getId(), memoriaVirtual);
                    PaginaVirtual paginaVirtualRequisitada = (PaginaVirtual) paginaRequisitada;

                    // Obtém a página física correspondente pelo map
                    PaginaFisica paginaFisicaRequisitada = mapaPaginaVirtualFisica.get(paginaVirtualRequisitada);

                    if(!paginaFisicaRequisitada.getBitUso()){
                        // Verifica se há ao menos 1 índice livre na memória fisica para alocar a página física
                        int indiceLivre = encontrarIndiceLivreMemoria(memoriaFisica);

                        if (indiceLivre != -1) {
                            pageMisses++;
                            memoriaFisica[indiceLivre] = paginaFisicaRequisitada;  // Adiciona a pagina na memória livre
                            int indicePagina = procurarIndicePagina(paginaVirtualRequisitada, memoriaVirtual);
                            memoriaVirtual[indicePagina] = null;           // Remove a página da memória virtual
                            exibirEstadoMemoria(processo, null, paginaFisicaRequisitada, algoritmo, paginaFisicaRequisitada);
                        }
                        else {
                            // Memória cheia, aplica substituição
                            pageMisses++;
                            PaginaFisica paginaFisicaRemovida = substituirPagina(algoritmo, paginaFisicaRequisitada);
                            int indicePagina = procurarIndicePagina(paginaVirtualRequisitada, memoriaVirtual);
                            memoriaVirtual[indicePagina] = null;           // Remove a página da memória virtual
                            exibirEstadoMemoria(processo, paginaFisicaRemovida, paginaFisicaRequisitada, algoritmo, paginaFisicaRequisitada);
                            paginaFisicaRemovida.setBitUso(false);
                        }
                        FIFOPaginaFisicas.add(paginaFisicaRequisitada);   // Atualiza a fila de páginas alocadas na memória física
                        paginaFisicaRequisitada.setBitUso(true);
                    }
                }
                else{
                    pageHit++;
                    PaginaFisica paginaFisica = (PaginaFisica) paginaEncontrada;
                    exibirEstadoMemoria(processo, null, null, algoritmo, paginaFisica);
                    paginaFisica.setBitUso(true);
                }
            }
            // Quando a ordem de referências de um processo terminar
            desalocarPaginasDoProcesso(processo);
            System.out.println("\n----- SIMULAÇÃO DO PROCESSO " + processo.getNome() + " ENCERRADA ----");
        }

        System.out.println("\nSimulação concluída! Total de page misses: " + pageMisses);
        pageHit = 0;
        pageMisses = 0;
    }


    private Pagina ProcurarPaginaNaMemoria(int indice, int idProcesso, Pagina[] memoria) {
        Pagina paginaEncontrada = null;

        for (int i = 0; i < memoria.length && paginaEncontrada == null; i++) {
            Pagina pagina = memoria[i];

            if (pagina != null && indice == pagina.getId() && idProcesso == pagina.getIdProcesso()) {
                paginaEncontrada = pagina;
            }
        }
        return paginaEncontrada;
    }

    private PaginaFisica substituirPagina(String algoritmo, PaginaFisica paginaFisicaAcessada) {
        PaginaFisica paginaFisicaRemovida = null;

        switch (algoritmo.toLowerCase()) {
            case "fifo" -> paginaFisicaRemovida = substituirPaginaFIFO(paginaFisicaAcessada);
            case "aleatório" -> paginaFisicaRemovida = substituirPaginaAleatoria(paginaFisicaAcessada);
            case "relógio" -> paginaFisicaRemovida = substituirPaginaRelogio(paginaFisicaAcessada);
            case "lru" -> paginaFisicaRemovida = substituirPaginaLRU(paginaFisicaAcessada);
        }
        return paginaFisicaRemovida;
    }

    public PaginaVirtual obterPaginaVirtualPorFisica(PaginaFisica paginaFisica) {
        PaginaVirtual paginaVirtual = null;

        for (Map.Entry<PaginaVirtual, PaginaFisica> entry : mapaPaginaVirtualFisica.entrySet()) {
            if (entry.getValue().equals(paginaFisica)) {
                paginaVirtual = entry.getKey();
                break;
            }
        }
        return paginaVirtual;
    }

    private PaginaFisica substituirPaginaFIFO(PaginaFisica novaPaginaFisica) {
        PaginaFisica paginaFisicaRemovida = FIFOPaginaFisicas.poll(); // Remove do início da fila
        int indiceRemocao = procurarIndicePagina(paginaFisicaRemovida, memoriaFisica);

        // Move a página removida para memória virtual
        int indiceVirtual = encontrarIndiceLivreMemoria(memoriaVirtual);
        if (indiceVirtual != -1) {

            //Obtém a página virtual correspondente a página fisica e adiciona na memória virtual
            PaginaVirtual paginaVirtual = obterPaginaVirtualPorFisica(paginaFisicaRemovida);
            memoriaVirtual[indiceVirtual] = paginaVirtual;
        }

        // Substituir na memória física
        memoriaFisica[indiceRemocao] = novaPaginaFisica;
        return paginaFisicaRemovida;
    }

    private PaginaFisica substituirPaginaAleatoria(PaginaFisica novaPaginaFisica) {
        Random random = new Random();
        int indiceAleatorio = random.nextInt(memoriaFisica.length);
        PaginaFisica paginaFisicaRemovida = memoriaFisica[indiceAleatorio];

        // Colocando a página removida da memória física na memória virtual
        int indiceVirtual = encontrarIndiceLivreMemoria(memoriaVirtual);
        if (indiceVirtual != -1) {

            //Obtém a página virtual correspondente a página fisica e adiciona na memória virtual
            PaginaVirtual paginaVirtual = obterPaginaVirtualPorFisica(paginaFisicaRemovida);
            memoriaVirtual[indiceVirtual] = paginaVirtual;
        }
        memoriaFisica[indiceAleatorio] = novaPaginaFisica; // Atualizando a memória física com a nova página
        return paginaFisicaRemovida;
    }

    private PaginaFisica substituirPaginaRelogio(PaginaFisica novaPaginaFisica) {
        PaginaFisica paginaFisicaRemovida;

        while (true) {
            PaginaFisica paginaFisicaAtual = memoriaFisica[ponteiroRelogio];
            boolean paginaEstahEmUso = paginaFisicaAtual.getBitUso();

            if (!paginaEstahEmUso) {
                // Colocando a página removida da memória física na memória virtual
                int indiceVirtual = encontrarIndiceLivreMemoria(memoriaVirtual);
                if (indiceVirtual != -1) {
                    //Obtém a página virtual correspondente a página fisica e adiciona na memória virtual
                    PaginaVirtual paginaVirtual = obterPaginaVirtualPorFisica(paginaFisicaAtual);
                    memoriaVirtual[indiceVirtual] = paginaVirtual;
                }

                paginaFisicaRemovida = paginaFisicaAtual;
                // Atualizando a memória física com a nova página
                memoriaFisica[ponteiroRelogio] = novaPaginaFisica;
                ponteiroRelogio = (ponteiroRelogio + 1) % memoriaFisica.length;
                break;
            }
            else {
                // seta o bit de uso e avança ponteiro
                paginaFisicaAtual.setBitUso(false);
                ponteiroRelogio = (ponteiroRelogio + 1) % memoriaFisica.length;
            }
        }
        return paginaFisicaRemovida;
    }

    private PaginaFisica substituirPaginaLRU(PaginaFisica novaPaginaFisica) {
        int indiceLRU = -1;
        long tempoMaisAntigo = Long.MAX_VALUE;

        // Encontra a página com o menor tempo de uso (menos recentemente usada)
        for (int i = 0; i < memoriaFisica.length; i++) {
            PaginaFisica paginaFisica = memoriaFisica[i];
            if (paginaFisica.getLastUsedTime() < tempoMaisAntigo) {
                tempoMaisAntigo = paginaFisica.getLastUsedTime();
                indiceLRU = i;
            }
        }

        // Remove a página menos recentemente usada
        PaginaFisica paginaFisicaRemovida = memoriaFisica[indiceLRU];

        // Colocando a página removida da memória física na memória virtual
        int indiceVirtual = encontrarIndiceLivreMemoria(memoriaVirtual);
        if (indiceVirtual != -1) {
            //Obtém a página virtual correspondente a página fisica e adiciona na memória virtual
            PaginaVirtual paginaVirtual = obterPaginaVirtualPorFisica(paginaFisicaRemovida);
            memoriaVirtual[indiceVirtual] = paginaVirtual;
        }

        // Atualizando a memória física com a nova página
        memoriaFisica[indiceLRU] = novaPaginaFisica;
        novaPaginaFisica.atualizarTempoDeUso(); // marca novo tempo de uso

        return paginaFisicaRemovida;
    }

    private void exibirEstadoMemoria(ProcessoPaginacao processoAtual, PaginaFisica paginaFisicaRemovida, PaginaFisica paginaFisicaAdicionada, String algoritmo, PaginaFisica paginaFisicaAcessada) {
        limparTela();
        System.out.println("\n========== ESTADO ATUAL DA MEMÓRIA ==========\n");

        //Exibição do estado atual das memórias fisica e vitual
        System.out.println("------------ MEMÓRIA FÍSICA ------------");
        exibirMatrizMemoria(memoriaFisica, 9);
        System.out.println("\n------------ MEMÓRIA VIRTUAL ------------");
        exibirMatrizMemoria(memoriaVirtual, 9);

        // Exibição de infos adicionais dos processos e paginas
        System.out.println("\n- Processo em execução: " + processoAtual.getNome());
        System.out.println("- Página acessada: " + paginaFisicaAcessada.getNome());
        System.out.println("- Página removida da memória física: " + (paginaFisicaRemovida != null? paginaFisicaRemovida.getNome() : "Nenhuma"));
        System.out.println("- Página adicionada à memória física: " + (paginaFisicaAdicionada != null ? paginaFisicaAdicionada.getNome() : "Nenhuma"));
        System.out.println("- Total de Page Misses: " + pageMisses);
        System.out.println("- Total de Page hit: " + pageHit);
        System.out.println("- Algoritmo utilizado: " + (paginaFisicaRemovida != null? algoritmo : "Nenhum"));

        // Pausa de 3s para visualização e leitura do estado atual da memória e dados adicionais
        if(modoExibicao.equals("continuo")){
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        else{
            System.out.println("Digite a tecla enter para continuar: ");
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine(); // aguarda Enter
        }
    }

    public void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private int contarPaginasLivres(Object[] memoria) {
        int contador = 0;

        // conta qlqr array de object (no caso paginaFisica ou Virtual)
        for (Object pagina : memoria) {
            if (pagina == null) contador++;
        }
        return contador;
    }

    private void exibirMatrizMemoria(Object[] memoria, int colunas) {
        for (int i = 0; i < memoria.length; i++) {
            Object pagina = memoria[i];
            String id;

            if (pagina != null && pagina instanceof PaginaFisica pf) {
                id = pf.getNome();
            }
            else if (pagina != null && pagina instanceof PaginaVirtual pv) {
                id = pv.getNome();
            }
            else {
                id = "    ----    ";
            }

            System.out.printf("[%12s] ", id);

            if ((i + 1) % colunas == 0 || i == memoria.length - 1) {
                System.out.println();
            }
        }
    }

    private int procurarIndicePagina(Object pagina, Object[] memoria){
        boolean achou = false;
        int indice = -1;

        for (int i = 0; i < memoria.length && !achou; i++){
            if(pagina.equals(memoria[i])){
                achou = true;
                indice = i;
            }
        }
        return indice;
    }

    // GETS AND SETS:
    public int getTamanhoPagina() {
        return tamanhoPagina;
    }

    public void setPageMisses(int pageMisses) {
        this.pageMisses = pageMisses;
    }

    public void setPonteiroRelogio(int ponteiroRelogio) {
        this.ponteiroRelogio = ponteiroRelogio;
    }


    public void setNumeroPaginasFisicas(int numeroPaginasFisicas) {
        this.numeroPaginasFisicas = numeroPaginasFisicas;
    }

    public void setNumeroPaginasVirtuais(int numeroPaginasVirtuais) {
        this.numeroPaginasVirtuais = numeroPaginasVirtuais;
    }

    public Queue<PaginaFisica> getFIFOPaginas() {
        return FIFOPaginaFisicas;
    }

    public Map<PaginaVirtual, PaginaFisica> getMapaPaginaVirtualFisica() {
        return mapaPaginaVirtualFisica;
    }

    public PaginaFisica[] getMemoriaFisica() {
        return memoriaFisica;
    }

    public PaginaVirtual[] getMemoriaVirtual() {
        return memoriaVirtual;
    }

    public void setModoExibicao(String modoExibicao) {
        this.modoExibicao = modoExibicao;
    }
}
