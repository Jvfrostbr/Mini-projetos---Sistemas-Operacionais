package GerenciamentoProcessos_Parte1;

public class Thread extends java.lang.Thread {
    int id;
    RecursosCompartilhados recursosCompartilhados;

    //CONSTRUTOR:
    public Thread(int id, RecursosCompartilhados recursosCompartilhados) {
        this.id = id;
        this.recursosCompartilhados = recursosCompartilhados;
    }

    //MÉTODOS
    @Override
    public void run() {
        try {
            adicionar(id);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            contador(id);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public void adicionar(int valor) throws InterruptedException {
        recursosCompartilhados.adicionar(valor);
        sleep(300);
    }

    public void contador(int valor) throws InterruptedException {
        recursosCompartilhados.contador(valor);
        sleep(300);
    }

}
