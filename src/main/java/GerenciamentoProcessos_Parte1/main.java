package GerenciamentoProcessos_Parte1;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class main {
    public static void main(String[] args) {
        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Digite o número de threads:");
            int numThreads = scanner.nextInt();
            System.out.println("Usar Lock? (y/n):");
            String lockInput = scanner.next();
            boolean usarLock = lockInput.equalsIgnoreCase("y");
            System.out.println(usarLock ? "Usando Lock" : "Não usando Lock");

            RecursosCompartilhados recursosCompartilhados = new RecursosCompartilhados(usarLock);
            List<Thread> threads = new ArrayList<>();

            for (int i = 0; i < numThreads; i++) {
                Thread thread = new Thread(i, recursosCompartilhados);
                threads.add(thread);
            }

            for (Thread thread : threads) {
                thread.start();
            }

            // Espera todas as threads terminarem
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Contador final: " + RecursosCompartilhados.getContador());
            System.out.println("Buffer final: " + RecursosCompartilhados.getBuffer());
            System.out.println("Deseja reiniciar? (y/n):");
            String resposta = scanner.next();
            if (resposta.equalsIgnoreCase("n")) {
                break;
            } else {
                RecursosCompartilhados.resetarContador();
                RecursosCompartilhados.resetarBuffer();
            }
        }
    }
}
