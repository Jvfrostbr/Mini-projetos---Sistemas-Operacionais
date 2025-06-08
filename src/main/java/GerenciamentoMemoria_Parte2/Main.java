package GerenciamentoMemoria_Parte2;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        GerenciadorMemoriaPaginada gerenciador = null;
        menu(gerenciador);
    }

    public static void menu(GerenciadorMemoriaPaginada gerenciador){
        Scanner scanner = new Scanner(System.in);
        System.out.println("""
          ------------------------------------------
          Menu - Gerenciamento de Memória Paginação
          ------------------------------------------
          1. Configurar memória
          2. Adicionar processo
          3. Executar alocação de memória
          ------------------------------------------
          Escolha uma opção:""");
        int opcao = scanner.nextInt();
        switch (opcao) {

            case 1:
                gerenciador = configurarMemoria();  // Atualiza gerenciador com uma nova instância
                menu(gerenciador);
            break;

            case 2:
                criarProcessos(gerenciador);
                menu(gerenciador);
            break;

            case 3:
                executarSimulacao(gerenciador);
                menu(gerenciador);
            break;

            default:
                System.out.println("Opção inválida.");
                menu(gerenciador);
                break;
        }
    }

    public static GerenciadorMemoriaPaginada configurarMemoria(){
        Scanner scanner = new Scanner(System.in);
        int tamFisica, tamVirtual, tamPagina, limiteFisicasPorProcesso;

        do{
            System.out.print("Defina o tamanho da memória física (em KB): ");
            tamFisica= scanner.nextInt();

            System.out.print("Defina o tamanho da memória virtual (em KB): ");
            tamVirtual = scanner.nextInt();

            System.out.print("Defina o tamanho das páginas (em KB): ");
            tamPagina = scanner.nextInt();
            scanner.nextLine();

            if(tamFisica > tamVirtual){
                System.out.println("Você digitou um valor de memória virtual menor que a física, tente novamente");
            } else if (tamFisica < tamPagina) {
                System.out.println("Você digitou um valor de mómória de pagina maior que a memória física, tente novamente ");
            }

        } while (tamFisica > tamVirtual || tamPagina > tamFisica);

        System.out.println("Defina a quantidade limite de páginas por processo a serem alocadas inicialmente na memória física: ");
        limiteFisicasPorProcesso = scanner.nextInt();

        System.out.println("---- Memória configurada com sucesso ----" );
        return new GerenciadorMemoriaPaginada(tamFisica, tamVirtual, tamPagina, limiteFisicasPorProcesso);
    }

    public static void criarProcessos(GerenciadorMemoriaPaginada gerenciador){
        Scanner scanner = new Scanner(System.in);

        if (gerenciador == null) {
            System.out.println("Configure a memória antes de adicionar processos.");
            menu(gerenciador);
        }
        else{
            System.out.print("Defina o nome do processo: ");
            String nome = scanner.nextLine();

            System.out.print("Defina o ID do processo (inteiro): ");
            int id = scanner.nextInt();

            System.out.print("Defina o tamanho do processo (em KB): ");
            int tamanho = scanner.nextInt();
            scanner.nextLine();

            System.out.print("""
                Defina o a forma de como as páginas serão referenciadas:
                1 - FIFO
                2 - Aletório
                Escolha uma opção:
                """);

            int tipoReferencia = scanner.nextInt();
            scanner.nextLine();

            String TipoReferenciaPaginas = (tipoReferencia == 1? "FIFO" : "Aleatório");

            ProcessoPaginacao processo = new ProcessoPaginacao(nome, id, tamanho, gerenciador.getTamanhoPagina(), TipoReferenciaPaginas);
            gerenciador.adicionarProcesso(processo);
            System.out.println("\n---- Processo adicionado à fila ----\n");
        }
    }

    public static void executarSimulacao(GerenciadorMemoriaPaginada gerenciador){
        Scanner scanner = new Scanner(System.in);
        if (gerenciador == null) {
            System.out.println("Configure a memória antes de executar.");
            menu(gerenciador);
        }
        else{
            System.out.println("""
                    Defina o algoritmo de substituição dessa simulação:
                    1 - FIFO
                    2 - Relógio
                    3 - Aleatório
                    4 - LRU
                    Escolha uma opção:
                    """);
            int opcao = scanner.nextInt();
            String algoritmo = "";

            switch (opcao) {
                case 1 -> algoritmo = "FIFO";
                case 2 -> algoritmo = "Relógio";
                case 3 -> algoritmo = "Aleatório";
                case 4 -> algoritmo = "LRU";
                default -> {
                    System.out.println("Opção inválida. Simulação cancelada.");
                    executarSimulacao(gerenciador);
                }
            }

            System.out.println("""
                    Defina o modo de exibição da memória:
                    1- Contínuo (Usando apenas a função sleep())
                    2- Pausado  (Digitando a tecla enter para continuar com a exibição)
                    Escolha uma opção:
                    """);
            opcao = scanner.nextInt();
            String modoExibicao = opcao == 1? "continuo" : "pausado";
            gerenciador.setModoExibicao(modoExibicao);

            gerenciador.alocarTodosOsProcessos();
            gerenciador.executarSimulacao(algoritmo);
            System.out.println("\n---------- Execução concluída ----------\n");
            limparDadosGerenciador(gerenciador);
        }
    }

    public static void limparDadosGerenciador(GerenciadorMemoriaPaginada gerenciador){
        //Limpando alguns dados do gerenciador
        gerenciador.setPageMisses(0);
        gerenciador.setPonteiroRelogio(0);
        gerenciador.setNumeroPaginasFisicas(0);
        gerenciador.setNumeroPaginasVirtuais(0);
        gerenciador.getFIFOPaginas().clear();
        gerenciador.getMapaProcessoPaginas().clear();
        Pagina[] memoriaFisica = gerenciador.getMemoriaFisica();
        Pagina[] memoriaVirtual = gerenciador.getMemoriaVirtual();

        for(int i = 0; i < memoriaFisica.length - 1; i++){
            memoriaFisica[i] = null;
        }
        for(int i = 0; i < memoriaVirtual.length - 1; i++){
            memoriaVirtual[i] = null;
        }
    }
}