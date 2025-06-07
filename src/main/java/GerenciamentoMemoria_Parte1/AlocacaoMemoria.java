package GerenciamentoMemoria_Parte1;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

public class AlocacaoMemoria {
    Processo[] memoria;
    ArrayList<Processo> processosAlocados;
    Queue<Processo> filaProcessos;
    ArrayList<Processo> processosSwapped;
    private String estrategia;//first, best ou worst

    public AlocacaoMemoria(int tamanhoMemoria, String estrategia) {
        this.memoria = new Processo[tamanhoMemoria];
        this.filaProcessos = new PriorityQueue<Processo>(Comparator.comparingInt(Processo::getTempoEspera));
        this.processosSwapped = new ArrayList<Processo>();
        this.processosAlocados = new ArrayList<>();
        this.estrategia = estrategia.toLowerCase();

    }

    public boolean alocarProcesso(Processo processo){
        switch (estrategia) {
            case "best":
                return alocarProcessoBestFit(processo);
            case "worst":
                return alocarProcessoWorstFit(processo);
            default:
                return alocarProcessoFirstFit(processo);
        }
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
        while (!filaProcessos.isEmpty()) {
            imprimirMemoria(); // Imprime o estado atual da memória
            Processo processo = filaProcessos.poll();
            if (alocarProcessoFirstFit(processo)) {
                System.out.println("Processo " + processo.getNome() + " alocado com sucesso.");
            } else {
                System.out.println("Memória insuficiente para alocar o processo " + processo.getNome());
                System.out.println("Compactando memória...");
                compactacao();
                if (alocarProcessoFirstFit(processo)) {
                    System.out.println("Processo " + processo.getNome() + " alocado após compactação.");
                } else {
                    System.out.println("Ainda não foi possível alocar o processo " + processo.getNome() + ". Colocando na fila de swap.");
                    processosSwapped.add(processo);
                    swapProcessos(); // Tenta desalocar um processo para liberar espaço
                    if (alocarProcessoFirstFit(processo)) {
                        System.out.println("Processo " + processo.getNome() + " alocado após swap.");
                    } else {
                        System.out.println("Não foi possível alocar o processo " + processo.getNome() + " mesmo após swap.");
                    }
                }
            }
        }
        imprimirMemoria(); // Imprime o estado final da memória
    }

    public void swapProcessos() {
        int escolha = new Random().nextInt(processosAlocados.size());
        Processo processoASwap = processosAlocados.get(escolha);
        System.out.println("Desalocando processo " + processoASwap.getNome() + " para swap.");
        desalocarProcesso(processoASwap);
        processosSwapped.add(processoASwap);
    }

    public void imprimirMemoria() {
        System.out.print("Memória: ");
        for (Processo p : memoria) {
            System.out.print(p == null ? "[ ]" : "[" + p.getNome() + "]");
        }
        System.out.println();
    }
}
