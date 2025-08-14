package GerenciamentoMemoria_Parte2;

import java.util.Scanner;

public class Main {
    private static int idProcesso = 1; // Variável para indicar o id do processor (será auto incrementada)

    public static void main(String[] args) {
        GerenciadorMemoriaPaginada gerenciador = configurarMemoria();
        menu(gerenciador);
    }

    public static void menu(GerenciadorMemoriaPaginada gerenciador){
        Scanner scanner = new Scanner(System.in);
        System.out.println("""
          ------------------------------------------
          Menu - Gerenciamento de Memória Paginação
          ------------------------------------------
          1. Adicionar processo
          2. Executar alocação de memória
          ------------------------------------------
          Escolha uma opção:""");
        int opcao = scanner.nextInt();
        switch (opcao) {

            case 1:
                criarProcessos(gerenciador);
                menu(gerenciador);
            break;

            case 2:
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

        System.out.println("""
                \n========================================================================
                ATENÇÃO PARA UMA MELHOR VISUALIZAÇÃO DO ESTADO DA MEMÓRIA NA SIMULAÇÃO
                DIGITE UM VALOR PEQUENO PARA O TAMANHO DA MEMÓRIA FÍSICA E VIRTUAL
                ========================================================================

                Ex:
                72 KB  -> Memória fisica
                144 KB -> Memória virtual
                """);

        System.out.print("Defina o tamanho da memória física (em KB): ");
        tamFisica= scanner.nextInt();

        System.out.print("Defina o tamanho da memória virtual (em KB): ");
        tamVirtual = scanner.nextInt();

        System.out.print("Defina o tamanho das páginas (em KB): ");
        tamPagina = scanner.nextInt();
        scanner.nextLine();

        if(tamFisica >= tamVirtual){
            System.out.println("Você digitou um valor de memória virtual menor que a física, tente novamente");
            configurarMemoria();
        } else if (tamFisica <= tamPagina) {
            System.out.println("Você digitou um valor de mómória de pagina maior que a memória física, tente novamente ");
            configurarMemoria();
        }

        System.out.println("""
                \n===================================================
                Defina a quantidade de páginas por processo a
                serem alocadas inicialmente na memória física
                ===================================================
                
                Ex: se um processo X tem 8 páginas e a quantidade digitada
                for 3, as páginas serão alocadas na memória da seguinte forma:
                
                Memória Fisica: pág 1 , pág 2, pág 3
                Memória Virtual: pág 4, pág 5, pág 6, pág 7, pág 8
                
                Quantidade:\t""");

        limiteFisicasPorProcesso = scanner.nextInt();

        System.out.println("---- Memória configurada com sucesso ----" );
        return new GerenciadorMemoriaPaginada(tamFisica, tamVirtual, tamPagina, limiteFisicasPorProcesso);
    }

    public static void criarProcessos(GerenciadorMemoriaPaginada gerenciador){
        Scanner scanner = new Scanner(System.in);
        System.out.print("Defina o tamanho do processo (em KB): ");
        int tamanho = scanner.nextInt();
        scanner.nextLine();

        if(tamanho < 4){
            System.out.println("Digite um tamanho maior que 4KB");
            criarProcessos(gerenciador);
        }

        System.out.print("""
            Defina o a forma de como as páginas serão referenciadas (acessadas):
            1 - FIFO
            2 - Aleatório
            Escolha uma opção:
            """);

        int tipoReferencia = scanner.nextInt();
        scanner.nextLine();

        String TipoReferenciaPaginas = (tipoReferencia == 1? "FIFO" : "Aleatório");

        String nome = "PC" + idProcesso;
        ProcessoPaginacao processo = new ProcessoPaginacao(nome, idProcesso, tamanho, gerenciador.getTamanhoPagina(), TipoReferenciaPaginas);
        gerenciador.adicionarProcesso(processo);
        System.out.println("\n---- Processo adicionado à fila ----\n");
        idProcesso++;
    }

    public static void executarSimulacao(GerenciadorMemoriaPaginada gerenciador){
        Scanner scanner = new Scanner(System.in);
        System.out.println("""
                Defina o algoritmo de substituição dessa simulação:
                1 - FIFO
                2 - Relógio
                3 - Aleatório
                4 - LRU
                Escolha uma opção:\t
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

    public static void limparDadosGerenciador(GerenciadorMemoriaPaginada gerenciador){
        //Limpando alguns dados do gerenciador
        gerenciador.setPageMisses(0);
        gerenciador.setPonteiroRelogio(0);
        gerenciador.setNumeroPaginasFisicas(0);
        gerenciador.setNumeroPaginasVirtuais(0);
        gerenciador.getFIFOPaginas().clear();
        gerenciador.getMapaPaginaVirtualFisica().clear();
        PaginaFisica[] memoriaFisica = gerenciador.getMemoriaFisica();
        PaginaVirtual[] memoriaVirtual = gerenciador.getMemoriaVirtual();

        for(int i = 0; i < memoriaFisica.length - 1; i++){
            memoriaFisica[i] = null;
        }
        for(int i = 0; i < memoriaVirtual.length - 1; i++){
            memoriaVirtual[i] = null;
        }
    }
}