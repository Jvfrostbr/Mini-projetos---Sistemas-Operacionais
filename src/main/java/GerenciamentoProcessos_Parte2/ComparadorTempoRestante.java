package GerenciamentoProcessos_Parte2;

import java.util.Comparator;

public class ComparadorTempoRestante implements Comparator<Processo> {
    @Override
    public int compare(Processo p1, Processo p2) {
        return Integer.compare(p1.getTempoRestante(), p2.getTempoRestante());
    }
}
