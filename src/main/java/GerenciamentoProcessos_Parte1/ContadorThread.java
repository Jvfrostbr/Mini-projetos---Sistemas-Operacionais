package GerenciamentoProcessos_Parte1;

class ContadorThread extends java.lang.Thread {
    boolean usarLock;

    public ContadorThread(boolean usarLock) {
        this.usarLock = usarLock;
    }

    @Override
    public void run() {
        while (true) {
            ContadorPalavrasConcorrente contadorPalavrasConcorrente = ContadorPalavrasConcorrente.getInstancia();

            if (contadorPalavrasConcorrente.bufferListaPalavras.isEmpty()){
                break;
            }
            else{
                String palavra = obterProximaPalavra(contadorPalavrasConcorrente);

                if(contadorPalavrasConcorrente.mapaContagem.containsKey(palavra)){
                    System.out.println("A thread " + this.getName() + " está tentando adicionar uma palavra já existente no mapPalavras");
                }
                else if (palavra != null){
                    int contagem = contarOcorrencias(palavra, contadorPalavrasConcorrente);
                    atualizarMapa(palavra, contagem, contadorPalavrasConcorrente);
                }
                else{
                    System.out.println("A thread " + this.getName() + " está tentando adicionar uma palavra nula no mapaPalavras");
                }
            }
        }
    }

    private String obterProximaPalavra(ContadorPalavrasConcorrente contadorPalavrasConcorrente) {
        if (usarLock){
            contadorPalavrasConcorrente.lockPalavras.lock();
        }
        String palavra = null;

        try {
            if (!contadorPalavrasConcorrente.bufferListaPalavras.isEmpty()) {
                palavra = contadorPalavrasConcorrente.bufferListaPalavras.removeFirst();
            }
        } finally {
            if (usarLock){
                contadorPalavrasConcorrente.lockPalavras.unlock();
            }
        }
        return palavra;
    }

    private int contarOcorrencias(String palavra, ContadorPalavrasConcorrente contadorPalavrasConcorrente) {
        int contagem = 0;
        if (usarLock){
            contadorPalavrasConcorrente.lockTextoCompleto.lock();
        }
        try{
            for (String palavraTexto : contadorPalavrasConcorrente.bufferTextoCompleto) {
               if (palavraTexto.equals(palavra)){
                   contagem++;
               }
            }
        } finally {
            if (usarLock){
                contadorPalavrasConcorrente.lockTextoCompleto.unlock();
            }
        }
        return contagem;
    }

    private void atualizarMapa(String palavra, int contagem, ContadorPalavrasConcorrente contadorPalavrasConcorrente) {
        if (usarLock){
            contadorPalavrasConcorrente.lockMapa.lock();
        }
        try {
            contadorPalavrasConcorrente.mapaContagem.put(palavra, contagem);
        } finally {
            if (usarLock){
                contadorPalavrasConcorrente.lockMapa.unlock();
            }
        }
    }
}
