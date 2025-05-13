package GerenciamentoProcessos_Parte1;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RecursosCompartilhados {
    private static int contador = 0;
    private static ArrayList<Integer> buffer = new ArrayList<>();
    private final Lock lockBuffer = new ReentrantLock();
    private final Lock lockContador = new ReentrantLock();
    private final boolean usarLock;

    //CONSTRUTOR
    public RecursosCompartilhados(boolean usarLock) {
        this.usarLock = usarLock;
    }

    //MÉTODOS
    public void adicionar(int valor) {
        if (usarLock) {
            lockBuffer.lock();
        }
        try {
            buffer.add(valor);
            System.out.println("Valor " + valor + " adicionado ao buffer"+ buffer + "pela thread" + valor);
        } finally {
            if (usarLock) {
                lockBuffer.unlock();
            }
        }
    }

    public void contador (int valor) {
        if (usarLock) {
            lockContador.lock();
        }
        try {
            int soma = buffer.stream().mapToInt(Integer::intValue).sum();
            contador += soma;
            System.out.println("Contador: " + contador + " ,atualizado pela thread " + valor);
        } finally {
            if (usarLock) {
                lockContador.unlock();
            }
        }
    }

    //GETS AND SETS:
    public static int getContador() {
        return contador;
    }

    public static void resetarContador() {
        contador = 0;
    }
    public static ArrayList<Integer> getBuffer() {
        return buffer;
    }

    public static void resetarBuffer() {
        buffer.clear();
    }

}
