package GerenciamentoProcessos_Parte1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ContadorPalavrasConcorrente {
    private static ContadorPalavrasConcorrente instancia;
    public List<String> bufferListaPalavras;   //buffer que contém todas as palavras do .txt (sem repetição de palavras)
    public List<String> bufferTextoCompleto;   //buffer que contém todas as palavras do txt completo (com repetição de palavras)
    public Map<String, Integer> mapaContagem;  //Map que a armazenará (chave: palavra, valor: qtd de ocorrências da palavra)

    // Mutex para controle de acesso aos buffers
    public final Mutex lockPalavras;
    public final Mutex lockTextoCompleto;
    public final Mutex lockMapa;

    // CONSTRUTOR
    private ContadorPalavrasConcorrente() {
        this.lockPalavras = new Mutex();
        this.lockTextoCompleto = new Mutex();
        this.mapaContagem = new HashMap<>();
        this.bufferTextoCompleto = new ArrayList<>();
        this.bufferListaPalavras = new ArrayList<>();
        this.lockMapa = new Mutex();
    }

    //SINGLETON
    public static synchronized ContadorPalavrasConcorrente getInstancia() {
        if (instancia == null) {
            instancia = new ContadorPalavrasConcorrente();
        }
        return instancia;
    }

    public static String caminhoArquivo = "biblia-em-txt.txt";

    //MÉTODOS:
    public static void main(String[] args) {
        ContadorPalavrasConcorrente contadorPalavrasConcorrente = ContadorPalavrasConcorrente.getInstancia();
        Scanner scanner = new Scanner(System.in);

        inicializarBuffers(contadorPalavrasConcorrente);
        System.out.println("\nDigite a quantidade de threads: ");
        int qtdThreads = scanner.nextInt();

        criarThreads(qtdThreads);
        menuPrincipal(contadorPalavrasConcorrente, qtdThreads);
    }

    private static void inicializarBuffers(ContadorPalavrasConcorrente contadorPalavrasConcorrente) {
        try (BufferedReader leitor = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;

            System.out.println("\nFazendo a leitura do arquivo...");

            //Enquanto a linha não estiver vazia
            while ((linha = leitor.readLine()) != null) {

                linha = linha.toLowerCase();    // Converte a linha para minúsculas
                linha = linha.replaceAll("[^a-záàâãéêíóôõúç ]", " ");  // Remove caracteres que não são letras ou espaços, mantendo apenas letras e caracteres especiais do português
                String[] palavras = linha.split("\\s+");  // Divide a linha em palavras, separando por espaços
                contadorPalavrasConcorrente.bufferTextoCompleto.addAll(Arrays.asList(palavras));  // Adiciona todas as palavras do .txt

                // Adiciona a palavra no buffer de palavras caso não haja mais de uma ocorrência
                for (String palavra : palavras) {
                    if (!palavra.isBlank() && !contadorPalavrasConcorrente.bufferListaPalavras.contains(palavra)) {
                        contadorPalavrasConcorrente.bufferListaPalavras.add(palavra);
                    }
                }
            }
            System.out.println("\n================= Leitura do Arquivo Concluída =================" +
                               "\nTotal de palavras lidas (com repetições): " + contadorPalavrasConcorrente.bufferTextoCompleto.size() +
                               "\nTotal de palavras únicas (sem repetições): " + contadorPalavrasConcorrente.bufferListaPalavras.size() +
                               "\n=================================================================");
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }

    private static void menuPrincipal(ContadorPalavrasConcorrente contadorPalavrasConcorrente, int qtdThreads) {
        Scanner scanner = new Scanner(System.in);

        System.out.println( "\n=================================================================" +
                "\nDigite uma das opções: \n" +
                "1 - Ranquear as palavras que mais aparecem \n" +
                "2 - Ranquear as palavras que menos aparecem \n" +
                "3 - Pesquisar alguma palavra específica \n" +
                "4 - Voltar para o menu de seleção de lock\n" +
                "================================================================= \n");
        int opcao = scanner.nextInt();
        int qtd;
        switch (opcao){
            case 1:
                System.out.println("Digite a quantidade de palavras que você deseja ranquear: ");
                qtd = scanner.nextInt();
                ranquearPalavrasMaisAparecem(contadorPalavrasConcorrente, qtd);
                menuPrincipal(contadorPalavrasConcorrente, qtdThreads);
                break;

            case 2:
                System.out.println("Digite a quantidade de palavras que você deseja ranquear: ");
                qtd = scanner.nextInt();
                ranquearPalavrasMenosAparecem(contadorPalavrasConcorrente, qtd);
                menuPrincipal(contadorPalavrasConcorrente, qtdThreads);
                break;

            case 3:
                pesquisarPalavraEspecifica(contadorPalavrasConcorrente);
                menuPrincipal(contadorPalavrasConcorrente, qtdThreads);
                break;

            case 4:
                contadorPalavrasConcorrente.mapaContagem.clear();
                contadorPalavrasConcorrente.bufferTextoCompleto.clear();
                contadorPalavrasConcorrente.bufferListaPalavras.clear();
                inicializarBuffers(contadorPalavrasConcorrente);
                criarThreads(qtdThreads);
                menuPrincipal(contadorPalavrasConcorrente, qtdThreads);
                break;

            default:
                menuPrincipal(contadorPalavrasConcorrente, qtdThreads);
                break;
        }
    }

    private static void criarThreads(int qtdThreads){
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n=================================================================" +
                "\n Digite uma das opções: " +
                "\n1 - Sem lock" +
                "\n2 - Com lock" +
                "\n=================================================================");
        int opcao = scanner.nextInt();
        boolean opcaoLock = (opcao == 2);

        // Instancia as threads e bota elas pra trabalhar
        List<ContadorThread> listaThreads = new ArrayList<>();
        for (int i = 0; i < qtdThreads; i++){
            ContadorThread contadorThread = new ContadorThread(opcaoLock);
            listaThreads.add(contadorThread);
            contadorThread.start();
        }

        System.out.println("Iniciando a contagem de palavras no arquivo, por favor aguarde...");

        // Espera todas as threads finalizarem o trabalho
        for (ContadorThread thread : listaThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.out.println("Erro ao aguardar a thread: " + e.getMessage());
            }
        }

        ContadorPalavrasConcorrente contadorPalavrasConcorrente = ContadorPalavrasConcorrente.getInstancia();
        System.out.println("Quantidade de palavras contabilizadas: " + contadorPalavrasConcorrente.mapaContagem.size());
    }

    public static void ranquearPalavrasMaisAparecem(ContadorPalavrasConcorrente contadorPalavrasConcorrente, int qtd) {
        List<Map.Entry<String, Integer>> listaOrdenada = new ArrayList<>(contadorPalavrasConcorrente.mapaContagem.entrySet());
        listaOrdenada.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        if (qtd > contadorPalavrasConcorrente.mapaContagem.size()){
            qtd = contadorPalavrasConcorrente.mapaContagem.size();
        }
        for (int i = 0; i < qtd; i++) {
            Map.Entry<String, Integer> entry = listaOrdenada.get(i);
            System.out.printf(i + 1 + "- Palavra:  %-15s | Quantidade: %10d\n", entry.getKey(),  entry.getValue());
        }
    }

    public static void ranquearPalavrasMenosAparecem(ContadorPalavrasConcorrente contadorPalavrasConcorrente, int qtd) {
        List<Map.Entry<String, Integer>> listaOrdenada = new ArrayList<>(contadorPalavrasConcorrente.mapaContagem.entrySet());
        listaOrdenada.sort(Map.Entry.comparingByValue());

        if (qtd > contadorPalavrasConcorrente.mapaContagem.size()){
            qtd = contadorPalavrasConcorrente.mapaContagem.size();
        }

        for (int i = 0; i < qtd; i++) {
            Map.Entry<String, Integer> entry = listaOrdenada.get(i);
            System.out.printf( "%-5s- Palavra:  %-15s | Quantidade: %10d\n", i + 1, entry.getKey(),  entry.getValue());
        }
    }

    public static void pesquisarPalavraEspecifica(ContadorPalavrasConcorrente contadorPalavrasConcorrente) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Digite a palavra que deseja pesquisar: ");
        String palavra = scanner.nextLine().toLowerCase();

        if (contadorPalavrasConcorrente.mapaContagem.containsKey(palavra)) {
            System.out.println("A palavra '" + palavra + "' aparece " + contadorPalavrasConcorrente.mapaContagem.get(palavra) + " vezes.");
        } else {
            System.out.println("A palavra '" + palavra + "' não foi encontrada no texto.");
        }
    }
}