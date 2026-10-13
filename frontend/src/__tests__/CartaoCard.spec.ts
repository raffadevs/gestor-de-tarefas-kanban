import { describe, expect, it } from 'vitest';
import { mount } from '@vue/test-utils';
import CartaoCard from '../components/CartaoCard.vue';
import type { Cartao } from '../types/api';

function cartaoDeExemplo(overrides: Partial<Cartao> = {}): Cartao {
  return {
    id: 1,
    colunaId: 10,
    titulo: 'Revisar PR',
    descricao: null,
    responsavelId: null,
    prazo: null,
    posicao: '1000',
    criadoEm: new Date().toISOString(),
    etiquetas: [],
    ...overrides,
  };
}

describe('CartaoCard', () => {
  it('emite "abrir" ao clicar', async () => {
    const wrapper = mount(CartaoCard, { props: { cartao: cartaoDeExemplo() } });

    await wrapper.find('button').trigger('click');

    expect(wrapper.emitted('abrir')).toHaveLength(1);
  });

  it('destaca em vermelho um prazo vencido', () => {
    const wrapper = mount(CartaoCard, {
      props: { cartao: cartaoDeExemplo({ prazo: '2020-01-01T00:00:00Z' }) },
    });

    const badge = wrapper.find('p.text-xs');
    expect(badge.classes()).toContain('text-red-700');
  });

  it('não destaca um prazo futuro', () => {
    const wrapper = mount(CartaoCard, {
      props: { cartao: cartaoDeExemplo({ prazo: '2099-01-01T00:00:00Z' }) },
    });

    const badge = wrapper.find('p.text-xs');
    expect(badge.classes()).not.toContain('text-red-700');
  });

  it('renderiza as etiquetas do cartão', () => {
    const wrapper = mount(CartaoCard, {
      props: {
        cartao: cartaoDeExemplo({
          etiquetas: [{ id: 1, quadroId: 1, nome: 'urgente', cor: '#d64545' }],
        }),
      },
    });

    expect(wrapper.text()).toContain('urgente');
  });
});
