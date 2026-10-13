package dev.raffadevs.kanban.quadro;

public enum Papel {
    LEITOR(1),
    EDITOR(2),
    DONO(3);

    private final int nivel;

    Papel(int nivel) {
        this.nivel = nivel;
    }

    /**
     * True se este papel tem privilégios iguais ou maiores que {@code minimo}.
     * Ex.: EDITOR.atendeNoMinimo(LEITOR) == true; LEITOR.atendeNoMinimo(EDITOR) == false.
     */
    public boolean atendeNoMinimo(Papel minimo) {
        return this.nivel >= minimo.nivel;
    }
}
