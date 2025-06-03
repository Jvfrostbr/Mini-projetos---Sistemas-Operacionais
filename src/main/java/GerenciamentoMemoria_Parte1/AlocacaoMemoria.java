package GerenciamentoMemoria_Parte1;

import java.lang.reflect.Array;
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

    public AlocacaoMemoria(int tamanhoMemoria) {
        this.memoria = new Processo[tamanhoMemoria];
        this.filaProcessos = new PriorityQueue<Processo>(Comparator.comparingInt(Processo::getTempoEspera));
        this.processosSwapped = new ArrayList<Processo>();
        this.processosAlocados = new ArrayList<>();
    }

    public boolean alocarProcesso(Processo processo) {
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

    public void desalocarProcesso(Processo processo) {
        int espacoInicial = processo.getEspacoInicial();
        for (int i = espacoInicial; i < espacoInicial + processo.getTamanho(); i++) {
            memoria[i] = null; // Libera o espaço ocupado pelo processo
        }
        processosAlocados.remove(processo); // Remove o processo da lista de alocados
    }

    public void compactacao() {
        int indiceLivre = 0; // Posição onde o próximo processo será alocado
        for (int i = 0; i < memoria.length; i++) {
            if (memoria[i] != null) {
                // Move o processo para a posição livre
                memoria[indiceLivre] = memoria[i];
                memoria[i] = null; // Libera a posição original
                indiceLivre++;
            }
        }
        // Preenche o restante da memória com null
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
            if (alocarProcesso(processo)) {
                System.out.println("Processo " + processo.getNome() + " alocado com sucesso.");
            } else {
                System.out.println("Memória insuficiente para alocar o processo " + processo.getNome());
                System.out.println("Compactando memória...");
                compactacao();
                if (alocarProcesso(processo)) {
                    System.out.println("Processo " + processo.getNome() + " alocado após compactação.");
                } else {
                    System.out.println("Ainda não foi possível alocar o processo " + processo.getNome() + ". Colocando na fila de swap.");
                    processosSwapped.add(processo);
                    swapProcessos(); // Tenta desalocar um processo para liberar espaço
                    if (alocarProcesso(processo)) {
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

class Processo {
    private String nome;
    private int tamanho;
    private int tempoEspera;
    private int espacoInicial;

    public Processo(String nome, int tamanho) {
        this.nome = nome;
        this.tamanho = tamanho;
    }

    public String getNome() {
        return nome;
    }

    public int getTamanho() {
        return tamanho;
    }

    public int getTempoEspera() {
        return tempoEspera;
    }
    public void setTempoEspera(int tempoEspera) {
        this.tempoEspera = tempoEspera;
    }
    public int getEspacoInicial() {
        return espacoInicial;
    }
    public void setEspacoInicial(int espacoInicial) {
        this.espacoInicial = espacoInicial;
    }

    @Override
    public String toString() {
        return "Processo{" + "nome=" + nome + ", tamanho=" + tamanho + '}';
    }
}

class main {
    public static void main(String[] args) {
        AlocacaoMemoria alocacao = new AlocacaoMemoria(10);
        alocacao.processosAlocados = new ArrayList<>();

        Processo p1 = new Processo("P1", 3);
        Processo p2 = new Processo("P2", 4);
        Processo p3 = new Processo("P3", 4);

        alocacao.adicionarProcessoFila(p1);
        alocacao.adicionarProcessoFila(p2);
        alocacao.adicionarProcessoFila(p3);

        alocacao.processarFila();
    }
}
