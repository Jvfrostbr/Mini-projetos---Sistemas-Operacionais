package GerenciamentoProcessos_Parte2;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Simulador extends Thread {
    static Escalonador escalonador;
    static List<Processo> listaProcessosInicial = new ArrayList<>();
    static int contadorID = 1;

    public Simulador() {
        escalonador = null;
    }

    @Override
    public void run() {
        try {
            escalonador.executarProcessos();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;

        while (continuar) {
            System.out.println("\nSelecione uma das opções: ");
            System.out.println("1. Criar processo");
            System.out.println("2. Escolher algoritmo de escalonamento");
            System.out.println("3. Configurar quantum (Round Robin)");
            System.out.println("4. Iniciar simulação");
            System.out.println("5. Carregar processos de teste");
            System.out.println("6. Sair");

            int opcao = scanner.nextInt();
            scanner.nextLine(); // Consumir o \n pendente

            switch (opcao) {
                case 1:
                    Processo processo = criarProcessos(scanner);
                    if (escalonador != null) {
                        escalonador.adicionarProcesso(processo);
                    } else {
                        listaProcessosInicial.add(processo);
                    }
                    System.out.println("Processo criado e adicionado à lista de chegada.");

                case 2:
                    System.out.println("Escolha o algoritmo de escalonamento: ");
                    System.out.println("1. Round Robin");
                    System.out.println("2. Shortest Remaining Time First");
                    System.out.println("3. Preemption Priority");
                    int algoritmo = scanner.nextInt();
                    scanner.nextLine();

                    switch (algoritmo) {
                        case 1:
                            System.out.print("Digite o quantum: ");
                            int quantum = scanner.nextInt();
                            scanner.nextLine();
                            escalonador = new Round_Robin(quantum, listaProcessosInicial);
                            break;
                        case 2:
                            escalonador = new Shortest_Remaining_Time_First(listaProcessosInicial);
                            break;
                        case 3:
                            escalonador = new Preemption_Priority(listaProcessosInicial);
                            break;
                        default:
                            System.out.println("Opção inválida.");
                    }
                    break;

                case 3:
                    if (escalonador instanceof Round_Robin) {
                        System.out.print("Digite o novo quantum: ");
                        int novoQuantum = scanner.nextInt();
                        scanner.nextLine();
                        ((Round_Robin) escalonador).setQuantum(novoQuantum);
                        System.out.println("Quantum atualizado.");
                    } else {
                        System.out.println("Esse algoritmo não utiliza quantum.");
                    }
                    break;

                case 4:
                    if (escalonador == null) {
                        System.out.println("Escolha um algoritmo de escalonamento primeiro.");
                    } else {
                        escalonador.setProcessosNaoChegados(listaProcessosInicial);
                        escalonador.run();
                        escalonador.mostrarInformacoes();
                        double mediaTurnAround = 0;
                        double mediaEspera = 0;
                        for (Processo p : escalonador.getProcessosTerminated()) {
                            mediaTurnAround += p.getTempoTurnAround();
                            mediaEspera += p.getTempoEspera();
                        }
                        mediaTurnAround /= escalonador.getProcessosTerminated().size();
                        mediaEspera /= escalonador.getProcessosTerminated().size();
                        System.out.printf("%-30s: %.2f | %-30s: %.2f%n", "Tempo médio de Turnaround", mediaTurnAround, "Tempo médio de Espera", mediaEspera);

                    }
                    break;
                case 5:
                    carregarProcessosTeste();
                    System.out.println("Processos de teste carregados.");
                    break;
                case 6:
                    continuar = false;
                    System.out.println("Encerrando o simulador...");
                    break;

                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    public static Processo criarProcessos(Scanner scanner) {
        System.out.print("Digite o nome do processo: ");
        String nome = scanner.nextLine();

        System.out.print("Digite a prioridade (1-32, onde 1 = baixa prioridade, 32 = alta prioridade): ");
        int prioridade = scanner.nextInt();

        System.out.print("O processo é do tipo (1) CPU ou (2) IO? ");
        boolean tipo = scanner.nextInt() != 1;

        System.out.print("Digite o tempo de execução (em milisegundos): ");
        int tempoCPU = scanner.nextInt();

        System.out.print("Digite o tempo de chegada: ");
        int tempoChegada = scanner.nextInt();
        scanner.nextLine(); // Consumir o \n

        Processo novo = new Processo(contadorID++, nome,prioridade, tempoCPU, tempoChegada, tipo);
        return novo;
    }
    public static void carregarProcessosTeste() {
        listaProcessosInicial.add(new Processo(1, "P1", 15, 5, 0, true));
        listaProcessosInicial.add(new Processo(2, "P2", 10, 3, 2, false));
        listaProcessosInicial.add(new Processo(3, "P3", 20, 8, 4, true));
        listaProcessosInicial.add(new Processo(4, "P4", 5, 4, 6, false));
        listaProcessosInicial.add(new Processo(5, "P5", 30, 2, 1, true));
    }

}
