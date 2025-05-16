package GerenciamentoProcessos_Parte2;

import GerenciamentoProcessos_Parte1.RecursosCompartilhados;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Simulador extends Thread {
    Escalonador escalonador;

    @Override
    public void run() {
        try {
            escalonador.executarProcessos();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }



// A ideia é nessa main, você pode escolher o escalonador que deseja usar, e esse escalonador vai rodando como uma thread.
// Enquanto isso você pode adicionar processos a ele. Pela lista "Não chegados", os escalonadores vão pegando os processos
// dessa lista, adicionando na lista de Ready, e executando com base do seu algoritmo.
    public static void main(String[] args) {
        Simulador simulador = new Simulador();
        simulador.escalonador = new Round_Robin(2);
        simulador.start();

        List<Processo> listaProcessos = new ArrayList<>();
        menuPrincipal(listaProcessos);
    }

    public static void menuPrincipal(List<Processo> listaProcessos){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Selecione uma das opções: \n" +
                            "1 - Criar processos \n" +
                            "2 - Configurar o quantum tempo\n" +
                            "3. Iniciar simulação\n");
        int opcao = scanner.nextInt();

        switch(opcao){
            case 1:
                criarProcesos();
                break;

            case 2:
                break;

            case 3:
                break;

            default:
                menuPrincipal(listaProcessos);
                break;
        }
    }

    // A ideia é que a gente vai adicionando um processo de cada vez na lista de processos
    public Processo criarProcesos(){
        Scanner scanner = new Scanner(System.in);

        System.out.println("Digite o nome do processo: ");
        String nome = scanner.nextLine();
        System.out.println("Digite a sua prioridade (de 1 a 32, sendo 1 baixo e 32 muito alto):");
        String prioridadeS = scanner.nextLine();
        int prioridade = Integer.parseInt(prioridadeS);

    }


}
