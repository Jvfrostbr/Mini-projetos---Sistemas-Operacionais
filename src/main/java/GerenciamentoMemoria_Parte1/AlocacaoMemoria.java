package GerenciamentoMemoria_Parte1;

import java.util.*;

public class AlocacaoMemoria {
    Processo[] memoria;
    ArrayList<Processo> processosAlocados;
    Queue<Processo> filaProcessos;
    ArrayList<Processo> processosSwapped;
    private final Estrategia estrategia; //First, Best ou Worst-fit

    public AlocacaoMemoria(int tamanhoMemoria, Estrategia estrategia) {
        this.memoria = new Processo[tamanhoMemoria];
        this.filaProcessos = new LinkedList<>();
        this.processosSwapped = new ArrayList<>();
        this.processosAlocados = new ArrayList<>();
        this.estrategia = estrategia;

    }

    public boolean alocarProcesso(Processo processo){
        return switch (estrategia) {
            case Estrategia.BEST_FIT -> alocarProcessoBestFit(processo);
            case Estrategia.WORST_FIT -> alocarProcessoWorstFit(processo);
            default -> alocarProcessoFirstFit(processo);
        };
    }

    public boolean alocarProcessoFirstFit(Processo processo) {
        int espacoDisponivel = 0;
        int inicioEspaco = -1;

        for (int i = 0; i < memoria.length; i++) {
            if (memoria[i] == null) {
                if (inicioEspaco == -1) {
                    inicioEspaco = i; // Marca o início do espaço livre
                }
                espacoDisponivel++;
                if (espacoDisponivel == processo.getTamanho()) {
                    // Aloca o processo
                    for (int j = inicioEspaco; j < inicioEspaco + processo.getTamanho(); j++) {
                        memoria[j] = processo;
                    }
                    processo.setEspacoInicial(inicioEspaco);
                    processosAlocados.add(processo);
                    return true;
                }
            } else {
                espacoDisponivel = 0; // Reseta o contador se encontrar um espaço ocupado
                inicioEspaco = -1; // Reseta o início do espaço livre
            }
        }
        return false; // Não foi possível alocar o processo
    }

    private boolean alocarProcessoBestFit(Processo processo) {
        int menorTamanho = memoria.length + 1;
        int inicioMelhor = -1;
        int espacosLivres = 0;
        int inicioAtual = -1;
        for (int i = 0; i <= memoria.length; i++) {
            if (i < memoria.length && memoria[i] == null) {
                if (inicioAtual < 0) inicioAtual = i;
                espacosLivres++;
            } else {
                if (espacosLivres >= processo.getTamanho() && espacosLivres < menorTamanho) {
                    menorTamanho = espacosLivres;
                    inicioMelhor = inicioAtual;
                }
                espacosLivres = 0;
                inicioAtual = -1;
            }
        }
        if (inicioMelhor >= 0) {
            for (int j = inicioMelhor; j < inicioMelhor + processo.getTamanho(); j++) {
                memoria[j] = processo;
            }
            processo.setEspacoInicial(inicioMelhor);
            processosAlocados.add(processo);
            return true;
        }
        return false;
    }

    private boolean alocarProcessoWorstFit(Processo processo) {
        int maiorTamanho = 0;
        int inicioMaior = -1;
        int espacosLivres = 0;
        int inicioAtual = -1;
        for (int i = 0; i <= memoria.length; i++) {
            if (i < memoria.length && memoria[i] == null) {
                if (inicioAtual < 0) inicioAtual = i;
                espacosLivres++;
            } else {
                if (espacosLivres >= processo.getTamanho() && espacosLivres > maiorTamanho) {
                    maiorTamanho = espacosLivres;
                    inicioMaior = inicioAtual;
                }
                espacosLivres = 0;
                inicioAtual = -1;
            }
        }
        if (inicioMaior >= 0) {
            for (int j = inicioMaior; j < inicioMaior + processo.getTamanho(); j++) {
                memoria[j] = processo;
            }
            processo.setEspacoInicial(inicioMaior);
            processosAlocados.add(processo);
            return true;
        }
        return false;
    }

    public void desalocarProcesso(Processo processo) {
        int espacoInicial = processo.getEspacoInicial();
        for (int i = espacoInicial; i < espacoInicial + processo.getTamanho(); i++) {
            memoria[i] = null; // Libera o espaço ocupado pelo processo
        }
        processosAlocados.remove(processo); // Remove o processo da lista de alocados
    }

    public void compactacao() {
        int indiceLivre = 0;
        for (int i = 0; i < memoria.length; i++) {
            if (memoria[i] != null) {
                Processo processo = memoria[i];
                if (indiceLivre != i) {
                    // Move o processo e atualiza o espacoInicial apenas na primeira célula do bloco
                    for (int j = 0; j < processo.getTamanho(); j++) {
                        memoria[indiceLivre + j] = processo;
                        memoria[i + j] = null;
                    }
                    processo.setEspacoInicial(indiceLivre);
                    i += processo.getTamanho() - 1;
                    indiceLivre += processo.getTamanho();
                } else {
                    indiceLivre++;
                }
            }
        }
        for (int i = indiceLivre; i < memoria.length; i++) {
            memoria[i] = null;
        }
    }

    public void adicionarProcessoFila(Processo processo) {
        filaProcessos.add(processo);
    }

    public void processarFila() {
        System.out.println("Iniciando processamento da fila de processos.");
        imprimirMemoriaFormatada();
        while (!filaProcessos.isEmpty()) {
            Processo processo = filaProcessos.poll();
            imprimirMensagens(Arrays.asList("Processando " + processo.getNome() + " com tamanho " + processo.getTamanho() + "."), processo);
            if (alocarProcesso(processo)) {
                assert processo != null;
                imprimirMensagens(Arrays.asList( processo + " alocado com sucesso."), processo);
                imprimirMemoriaFormatada();
            } else {
                imprimirMensagens(Arrays.asList("Memória insuficiente para alocar o " + processo + ".", "Compactando memória..."), processo);
                imprimirMemoriaFormatada();
                compactacao();
                if (alocarProcesso(processo)) {
                    imprimirMensagens(Arrays.asList(processo + " alocado após compactação."), processo);
                    imprimirMemoriaFormatada();
                } else {
                    imprimirMensagens(Arrays.asList("Ainda não foi possível alocar o " + processo, "Aplicando Swap..." ), processo);
                    imprimirMemoriaFormatada();
                    processosSwapped.add(processo);
                    Processo processoASwap = swapProcessos(); // Tenta desalocar um processo para liberar espaço
                    if (alocarProcesso(processo)) {
                        imprimirMensagens(Arrays.asList(processo + " alocado após swap.", processoASwap + " foi desalocado."), processo);
                        imprimirMemoriaFormatada();
                    } else {
                        imprimirMensagens(Arrays.asList("Não foi possível alocar o " + processo + " mesmo após swap.", processoASwap + " foi desalocado."), processo);
                        imprimirMemoriaFormatada();
                    }
                }
            }
        }
        imprimirMensagens(Arrays.asList("Estado final da memória após processar todos os processos na fila."), null);
        imprimirMemoriaFormatada();
    }

    public Processo swapProcessos() {
        int escolha = new Random().nextInt(processosAlocados.size());
        Processo processoASwap = processosAlocados.get(escolha);
        desalocarProcesso(processoASwap);
        processosSwapped.add(processoASwap);
        return processoASwap;
    }

    public void imprimirMensagens(List<String> mensagens, Processo processo) {
        if (processo == null) {
            System.out.println("=".repeat(30) + " Memória em execução " + "=".repeat(30));
        } else {
            System.out.println("=".repeat(23) + processo + " em execução " + "=".repeat(23));
        }
        System.out.println("Informações de Execução:");
        for (String msg : mensagens) {
            System.out.println(">> " + msg);
        }
        System.out.println();
    }

    public void imprimirMemoriaFormatada() {
        System.out.print("Memória ( " + memoria.length + "MB ): ");
        for (Processo p : memoria) {
            String conteudo = p == null ? "   " : String.format("%-3s", p.getNome());
            System.out.print("[" + conteudo + "]");
        }
        System.out.println("\n" + "=".repeat(80) + "\n");
    }

}
