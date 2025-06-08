package GerenciamentoMemoria_Parte2;

import java.util.*;

public class GerenciadorMemoriaPaginada {
    private int tamanhoPagina;
    private int numeroPaginasFisicas;
    private int numeroPaginasVirtuais;
    private int limiteFisicasPorProcesso;   // constante que limita o número de paginas alocadas na memória física
    private int pageMisses;                 // Contador de page misses
    private int ponteiroRelogio;            // Ponteiro utilizado pelo algoritmo do Relógio
    private Pagina[] memoriaFisica;
    private Pagina[] memoriaVirtual;
    private Map<ProcessoPaginacao, List<Pagina>> mapaProcessoPaginas;
    private Queue<ProcessoPaginacao> filaProcessos;     // Fila de processos a serem alocados (ordem FIFO)
    private Queue<Pagina> FIFOPaginas;                  // Fila que armazena os processos que foram alocados na memória física
    String modoExibicao;

    // CONSTRUTOR
    public GerenciadorMemoriaPaginada(int tamFisica, int tamVirtual, int tamPagina, int limiteFisicasPorProcesso) {
        this.tamanhoPagina = tamPagina;
        this.numeroPaginasFisicas = tamFisica / tamPagina;
        this.numeroPaginasVirtuais = tamVirtual / tamPagina;
        this.memoriaFisica = new Pagina[numeroPaginasFisicas];
        this.memoriaVirtual = new Pagina[numeroPaginasVirtuais];
        this.filaProcessos = new LinkedList<>();
        this.mapaProcessoPaginas = new LinkedHashMap<>();
        this.pageMisses = 0;
        this.ponteiroRelogio = 0;
        this.limiteFisicasPorProcesso = limiteFisicasPorProcesso;
        this.FIFOPaginas = new LinkedList<>();
    }

    //MÉTODOS:
    public void adicionarProcesso(ProcessoPaginacao processo) {
        filaProcessos.add(processo);
    }

    public void alocarTodosOsProcessos() {
        while (!filaProcessos.isEmpty()) {
            ProcessoPaginacao processo = filaProcessos.poll();
            alocarPaginas(processo);
        }
    }

    public void alocarPaginas(ProcessoPaginacao processo) {
        int paginasNecessarias = processo.getNumPaginas();

        List<Pagina> paginasDoProcesso = new ArrayList<>();

        int paginasFisicasAlocadas = 0;
        for (int i = 0; i < paginasNecessarias; i++) {
            Pagina pagina = new Pagina(processo.getId(), i + 1 + "Pg | " + processo.getNome());
            boolean alocada = false;

            if (paginasFisicasAlocadas < limiteFisicasPorProcesso) {
                int indiceFisico = encontrarIndiceLivre(memoriaFisica);
                if (indiceFisico != -1) {
                    memoriaFisica[indiceFisico] = pagina;
                    FIFOPaginas.add(pagina);      // Atualiza a fila de páginas alocadas na memória física
                    paginasFisicasAlocadas++;
                    alocada = true;
                }
            }

            if (!alocada) {
                int indiceVirtual = encontrarIndiceLivre(memoriaVirtual);
                if (indiceVirtual != -1) {
                    memoriaVirtual[indiceVirtual] = pagina;
                } else {
                    System.out.println("ERRO: Sem espaço na memória física e virtual para alocar página: " + pagina.getIdPagina());
                    continue;
                }
            }

            paginasDoProcesso.add(pagina);
        }
        mapaProcessoPaginas.put(processo, paginasDoProcesso);
    }

    private int encontrarIndiceLivre(Pagina[] memoria) {
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
        // Desalocando todas as paginas do processo da memória física
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
        mapaProcessoPaginas.remove(processo);
    }

    public void executarSimulacao(String algoritmo) {
        for (ProcessoPaginacao processo : new ArrayList<>(mapaProcessoPaginas.keySet())) {
            List<Pagina> paginasDoProcesso = mapaProcessoPaginas.get(processo);
            List<Integer> referencias = processo.getReferenciasPaginas();

            for (int indicePaginaReferenciada : referencias) {
                Pagina paginaAcessada = paginasDoProcesso.get(indicePaginaReferenciada);

                if(!paginaAcessada.getBitUso()){
                    // Verifica se está na memória física
                    boolean paginaEncontrada = verificarPaginaMemoriaFisica(paginaAcessada, memoriaFisica);
                    if (!paginaEncontrada) {

                        // Verifica se há ao menos 1 índice livre na memória fisica para alocar a página
                        int indiceLivre = encontrarIndiceLivre(memoriaFisica);
                        Pagina paginaAdionada = paginaAcessada;
                        if (indiceLivre != -1) {
                            pageMisses++;
                            memoriaFisica[indiceLivre] = paginaAcessada;  // Adiciona a pagina na memória livre
                            int indicePagina = procurarIndicePagina(paginaAcessada, memoriaVirtual);
                            memoriaVirtual[indicePagina] = null;           // Remove a página da memória virtual
                            exibirEstadoMemoria(processo, null, paginaAdionada, algoritmo, paginaAcessada);
                        }
                        else {
                            // Memória cheia, aplica substituição
                            pageMisses++;
                            Pagina paginaRemovida = substituirPagina(algoritmo, paginaAcessada);
                            exibirEstadoMemoria(processo, paginaRemovida, paginaAdionada, algoritmo, paginaAcessada);
                            paginaRemovida.setBitUso(false);
                        }
                        FIFOPaginas.add(paginaAcessada);   // Atualiza a fila de páginas alocadas na memória física
                    }
                    else{
                        exibirEstadoMemoria(processo, null, null, algoritmo, paginaAcessada);
                    }
                    paginaAcessada.setBitUso(true);
                }
            }
            // Quando a ordem de referências de um processo terminar
            desalocarPaginasDoProcesso(processo);
        }

        System.out.println("\nSimulação concluída! Total de page misses: " + pageMisses);
    }

    private boolean verificarPaginaMemoriaFisica(Pagina pagina, Pagina[] memoria) {
        boolean paginaEncontrada = false;

        for (int i = 0; i < memoria.length && !paginaEncontrada; i++) {
            if (pagina != null && pagina.equals(memoria[i])) {
                paginaEncontrada = true;
            }
        }
        return paginaEncontrada;
    }

    private Pagina substituirPagina(String algoritmo, Pagina paginaAcessada) {
        Pagina paginaRemovida = null;

        switch (algoritmo.toLowerCase()) {
            case "fifo" -> paginaRemovida = substituirPaginaFIFO(paginaAcessada);
            case "aleatório" -> paginaRemovida = substituirPaginaAleatoria(paginaAcessada);
            case "relógio" -> paginaRemovida = substituirPaginaRelogio(paginaAcessada);
            case "lru" -> paginaRemovida = substituirPaginaLRU(paginaAcessada);
        }
        return paginaRemovida;
    }

    private Pagina substituirPaginaFIFO(Pagina novaPagina) {
        Pagina paginaRemovida = FIFOPaginas.poll(); // Remove do início da fila
        int indiceRemocao = procurarIndicePagina(paginaRemovida, memoriaFisica);

        // Move a página removida para memória virtual
        int indiceVirtual = encontrarIndiceLivre(memoriaVirtual);
        if (indiceVirtual != -1) {
            memoriaVirtual[indiceVirtual] = paginaRemovida;
        }

        // Substituir na memória física
        memoriaFisica[indiceRemocao] = novaPagina;
        return paginaRemovida;
    }

    private Pagina substituirPaginaAleatoria(Pagina novaPagina) {
        Random random = new Random();
        int indiceAleatorio = random.nextInt(memoriaFisica.length);
        Pagina paginaRemovida = memoriaFisica[indiceAleatorio];

        // Colocando a página removida da memória física na memória virtual
        int indiceVirtual = encontrarIndiceLivre(memoriaVirtual);
        if (indiceVirtual != -1) {
            memoriaVirtual[indiceVirtual] = paginaRemovida;
        }
        memoriaFisica[indiceAleatorio] = novaPagina; // Atualizando a memória física com a nova página
        return paginaRemovida;
    }

    private Pagina substituirPaginaRelogio(Pagina novaPagina) {
        Pagina paginaRemovida;

        while (true) {
            Pagina paginaAtual = memoriaFisica[ponteiroRelogio];
            boolean paginaEstahEmUso = paginaAtual.getBitUso();

            if (!paginaEstahEmUso) {
                // Colocando a página removida da memória física na memória virtual
                int indiceVirtual = encontrarIndiceLivre(memoriaVirtual);
                if (indiceVirtual != -1) {
                    memoriaVirtual[indiceVirtual] = paginaAtual;
                }

                paginaRemovida = paginaAtual;
                // Atualizando a memória física com a nova página
                memoriaFisica[ponteiroRelogio] = novaPagina;
                ponteiroRelogio = (ponteiroRelogio + 1) % memoriaFisica.length;
                break;
            }
            else {
                // seta o bit de uso e avança ponteiro
                paginaAtual.setBitUso(false);
                ponteiroRelogio = (ponteiroRelogio + 1) % memoriaFisica.length;
            }
        }
        return paginaRemovida;
    }

    private Pagina substituirPaginaLRU(Pagina novaPagina) {
        int indiceLRU = -1;
        long tempoMaisAntigo = Long.MAX_VALUE;

        // Encontra a página com o menor tempo de uso (menos recentemente usada)
        for (int i = 0; i < memoriaFisica.length; i++) {
            Pagina pagina = memoriaFisica[i];
            if (pagina.getLastUsedTime() < tempoMaisAntigo) {
                tempoMaisAntigo = pagina.getLastUsedTime();
                indiceLRU = i;
            }
        }

        // Remove a página menos recentemente usada
        Pagina paginaRemovida = memoriaFisica[indiceLRU];

        // Colocando a página removida da memória física na memória virtual
        int indiceVirtual = encontrarIndiceLivre(memoriaVirtual);
        if (indiceVirtual != -1) {
            memoriaVirtual[indiceVirtual] = paginaRemovida;
        }

        // Atualizando a memória física com a nova página
        memoriaFisica[indiceLRU] = novaPagina;
        novaPagina.atualizarTempoDeUso(); // marca novo tempo de uso

        return paginaRemovida;
    }

    private void exibirEstadoMemoria(ProcessoPaginacao processoAtual, Pagina paginaRemovida, Pagina paginaAdicionada, String algoritmo, Pagina paginaAcessada) {
        limparTela();
        System.out.println("========== ESTADO ATUAL DA MEMÓRIA ==========\n");

        //Exibição do estado atual das memórias fisica e vitual
        System.out.println("----- Memória Física -----");
        exibirMatrizMemoria(memoriaFisica, 9);
        System.out.println("\n----- Memória Virtual -----");
        exibirMatrizMemoria(memoriaVirtual, 9);

        // Exibição de infos adicionais dos processos e paginas
        System.out.println("\n- Processo em execução: " + processoAtual.getNome());
        System.out.println("- Página acessada: " + paginaAcessada.getIdPagina());
        System.out.println("- Página removida da memória física: " + (paginaRemovida != null? paginaRemovida.getIdPagina() : "Nenhuma"));
        System.out.println("- Página adicionada à memória física: " + (paginaAdicionada != null ? paginaAdicionada.getIdPagina() : "Nenhuma"));
        System.out.println("- Total de Page Misses: " + pageMisses);
        System.out.println("- Algoritmo selecionado: " + algoritmo);

        // Calculando informações adicionais das memórias
        int paginasLivresFisica = contarPaginasLivres(memoriaFisica);
        int paginasOcupadasFisica = memoriaFisica.length - paginasLivresFisica;
        int paginasLivresVirtual = contarPaginasLivres(memoriaVirtual);
        int paginasOcupadasVirtual = memoriaVirtual.length - paginasLivresVirtual;

        int kbLivreFisica = paginasLivresFisica * tamanhoPagina;
        int kbOcupadoFisica = paginasOcupadasFisica * tamanhoPagina;
        int kbLivreVirtual = paginasLivresVirtual * tamanhoPagina;
        int kbOcupadoVirtual = paginasOcupadasVirtual * tamanhoPagina;

        // Impressão das informações adicionais
        System.out.println("- Memória Física Livre: " + kbLivreFisica + " KB");
        System.out.println("- Memória Física Ocupada: " + kbOcupadoFisica + " KB");
        System.out.println("- Memória Virtual Livre: " + kbLivreVirtual + " KB");
        System.out.println("- Memória Virtual Ocupada: " + kbOcupadoVirtual + " KB");

        // Pausa para visualização e leitura do estado atual da memória e dados adicionais
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

    private int contarPaginasLivres(Pagina[] memoria) {
        int count = 0;
        for (Pagina p : memoria) {
            if (p == null) count++;
        }
        return count;
    }

    private void exibirMatrizMemoria(Pagina[] memoria, int colunas) {
        for (int i = 0; i < memoria.length; i++) {
            Pagina p = memoria[i];
            String id = (p != null) ? p.getIdPagina() : " - ";
            System.out.printf("[%10s] ", id);

            if ((i + 1) % colunas == 0 || i == memoria.length - 1) {
                System.out.println();
            }
        }
    }

    private int procurarIndicePagina(Pagina pagina, Pagina[] memoria){
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

    public Queue<Pagina> getFIFOPaginas() {
        return FIFOPaginas;
    }

    public Map<ProcessoPaginacao, List<Pagina>> getMapaProcessoPaginas() {
        return mapaProcessoPaginas;
    }

    public Pagina[] getMemoriaFisica() {
        return memoriaFisica;
    }

    public Pagina[] getMemoriaVirtual() {
        return memoriaVirtual;
    }

    public void setModoExibicao(String modoExibicao) {
        this.modoExibicao = modoExibicao;
    }
}
