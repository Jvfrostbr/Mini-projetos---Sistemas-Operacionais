import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;
import java.util.Base64;

class Bloco {
    int numeroBloco;
    int disco;
    Bloco proximo;

    public Bloco(int numeroBloco, int disco) {
        this.numeroBloco = numeroBloco;
        this.disco = disco;
        this.proximo = null;
    }
}

class Arquivo {
    String nome;
    int tamanho;
    Bloco primeiroBloco;
    String senhaCriptografada;
    private static final String CHAVE_SECRETA = "1234567890123456"; // Chave AES fixa de 16 bytes

    public Arquivo(String nome, int tamanho, Bloco primeiroBloco, String senha) {
        this.nome = nome;
        this.tamanho = tamanho;
        this.primeiroBloco = primeiroBloco;
        this.senhaCriptografada = senha != null ? criptografarSenha(senha) : null;
    }

    private String criptografarSenha(String senha) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            SecretKey secretKey = new SecretKeySpec(CHAVE_SECRETA.getBytes(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] senhaCriptografada = cipher.doFinal(senha.getBytes());
            return Base64.getEncoder().encodeToString(senhaCriptografada);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criptografar a senha", e);
        }
    }

    private String descriptografarSenha() {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            SecretKey secretKey = new SecretKeySpec(CHAVE_SECRETA.getBytes(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] senhaDescriptografada = cipher.doFinal(Base64.getDecoder().decode(senhaCriptografada));
            return new String(senhaDescriptografada);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao descriptografar a senha", e);
        }
    }

    public boolean verificarSenha(String senha) {
        return senhaCriptografada == null || senha.equals(descriptografarSenha());
    }

    @Override
    public String toString() {
        return "Arquivo: " + nome + ", Tamanho: " + tamanho + " blocos, Bloco Inicial: " + primeiroBloco.numeroBloco + " (Disco " + primeiroBloco.disco + ")";
    }
}

class SimuladorSistemaArquivos {
    private final int TOTAL_BLOCOS = 50;
    private final int TOTAL_DISCOS = 3;
    private boolean[][] alocacaoBlocos;
    private List<Arquivo> arquivos;

    public SimuladorSistemaArquivos() {
        alocacaoBlocos = new boolean[TOTAL_DISCOS][TOTAL_BLOCOS];
        arquivos = new ArrayList<>();
    }

    private Bloco alocarBlocos(int tamanho) {
        Bloco cabeca = null;
        Bloco atual = null;
        int blocosAlocados = 0;
        int discoAtual = 0;

        while (blocosAlocados < tamanho) {
            for (int i = 0; i < TOTAL_BLOCOS && blocosAlocados < tamanho; i++) {
                if (!alocacaoBlocos[discoAtual][i]) {
                    alocacaoBlocos[discoAtual][i] = true;
                    Bloco novoBloco = new Bloco(i, discoAtual);
                    if (cabeca == null) {
                        cabeca = novoBloco;
                    } else {
                        atual.proximo = novoBloco;
                    }
                    atual = novoBloco;
                    blocosAlocados++;
                    discoAtual = (discoAtual + 1) % TOTAL_DISCOS;
                }
            }
        }
        return cabeca;
    }

    public void criarArquivo(String nome, int tamanho, String senha) {
        Bloco primeiroBloco = alocarBlocos(tamanho);
        if (primeiroBloco != null) {
            arquivos.add(new Arquivo(nome, tamanho, primeiroBloco, senha));
            System.out.println("Arquivo criado com sucesso.");
        }
    }

    public void acessarArquivo(String nome, String senha) {
        for (Arquivo arq : arquivos) {
            if (arq.nome.equals(nome)) {
                if (arq.verificarSenha(senha)) {
                    System.out.println("Acesso permitido: " + arq);
                } else {
                    System.out.println("Senha incorreta. Acesso negado.");
                }
                return;
            }
        }
        System.out.println("Arquivo não encontrado.");
    }

    public void visualizarAlocacao() {
        System.out.println("Alocação de Blocos nos Discos:");
        for (int d = 0; d < TOTAL_DISCOS; d++) {
            System.out.println("Disco " + d + ":");
            for (int i = 0; i < TOTAL_BLOCOS; i++) {
                System.out.print(alocacaoBlocos[d][i] ? "[X] " : "[ ] ");
                if ((i + 1) % 10 == 0) System.out.println();
            }
            System.out.println();
        }
    }
}

public class comRaid {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SimuladorSistemaArquivos fs = new SimuladorSistemaArquivos();

        while (true) {
            System.out.println("\nSimulador de Sistema de Arquivos com RAID 0:");
            System.out.println("1. Criar Arquivo");
            System.out.println("2. Acessar Arquivo");
            System.out.println("3. Visualizar Alocação de Blocos");
            System.out.println("4. Sair");
            System.out.print("Escolha uma opção: ");

            int escolha = scanner.nextInt();
            scanner.nextLine();

            switch (escolha) {
                case 1:
                    System.out.print("Digite o nome do arquivo: ");
                    String nomeArquivo = scanner.nextLine();
                    System.out.print("Digite o tamanho do arquivo (em blocos): ");
                    int tamanho = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Deseja proteger o arquivo com senha? (s/n): ");
                    String resposta = scanner.nextLine();
                    String senha = null;
                    if (resposta.equalsIgnoreCase("s")) {
                        System.out.print("Digite a senha: ");
                        senha = scanner.nextLine();
                    }
                    fs.criarArquivo(nomeArquivo, tamanho, senha);
                    break;
                case 2:
                    System.out.print("Digite o nome do arquivo: ");
                    String nomeAcesso = scanner.nextLine();
                    System.out.print("Digite a senha (ou pressione Enter se não houver senha): ");
                    String senhaAcesso = scanner.nextLine();
                    fs.acessarArquivo(nomeAcesso, senhaAcesso);
                    break;
                case 3:
                    fs.visualizarAlocacao();
                    break;
                case 4:
                    System.out.println("Saindo...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }
}
