package dev.raffadevs.kanban.quadro;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PapelTest {

    @Test
    void donoAtendeQualquerNivel() {
        assertThat(Papel.DONO.atendeNoMinimo(Papel.DONO)).isTrue();
        assertThat(Papel.DONO.atendeNoMinimo(Papel.EDITOR)).isTrue();
        assertThat(Papel.DONO.atendeNoMinimo(Papel.LEITOR)).isTrue();
    }

    @Test
    void editorNaoAtendeNivelDono() {
        assertThat(Papel.EDITOR.atendeNoMinimo(Papel.DONO)).isFalse();
        assertThat(Papel.EDITOR.atendeNoMinimo(Papel.EDITOR)).isTrue();
        assertThat(Papel.EDITOR.atendeNoMinimo(Papel.LEITOR)).isTrue();
    }

    @Test
    void leitorSoAtendeNivelLeitor() {
        assertThat(Papel.LEITOR.atendeNoMinimo(Papel.DONO)).isFalse();
        assertThat(Papel.LEITOR.atendeNoMinimo(Papel.EDITOR)).isFalse();
        assertThat(Papel.LEITOR.atendeNoMinimo(Papel.LEITOR)).isTrue();
    }
}
