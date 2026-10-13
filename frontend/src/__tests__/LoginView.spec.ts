import { describe, expect, it, vi } from 'vitest';
import { mount } from '@vue/test-utils';
import { createPinia, setActivePinia } from 'pinia';
import { createMemoryHistory, createRouter } from 'vue-router';
import LoginView from '../views/LoginView.vue';
import * as authService from '../services/auth';
import type { TokenResponse } from '../types/api';

vi.mock('../services/auth');

function tokenDeExemplo(): TokenResponse {
  return {
    accessToken: 'access-123',
    refreshToken: 'refresh-456',
    usuario: { id: 1, nome: 'Ana', email: 'ana@exemplo.com' },
  };
}

async function montarComRouter() {
  setActivePinia(createPinia());
  const router = createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/login', name: 'login', component: LoginView },
      { path: '/quadros', name: 'quadros', component: { template: '<div>quadros</div>' } },
    ],
  });
  router.push('/login');
  await router.isReady();

  const wrapper = mount(LoginView, { global: { plugins: [router] } });
  return { wrapper, router };
}

describe('LoginView', () => {
  it('loga e redireciona para /quadros', async () => {
    vi.mocked(authService.login).mockResolvedValue(tokenDeExemplo());
    const { wrapper, router } = await montarComRouter();

    await wrapper.find('#email').setValue('ana@exemplo.com');
    await wrapper.find('#senha').setValue('senha12345');
    await wrapper.find('form').trigger('submit.prevent');
    await flushPromises();

    expect(authService.login).toHaveBeenCalledWith('ana@exemplo.com', 'senha12345');
    expect(router.currentRoute.value.path).toBe('/quadros');
  });

  it('exibe mensagem de erro quando o login falha', async () => {
    vi.mocked(authService.login).mockRejectedValue({
      response: { data: { erro: 'Credenciais ou token inválidos' } },
    });
    const { wrapper } = await montarComRouter();

    await wrapper.find('#email').setValue('ana@exemplo.com');
    await wrapper.find('#senha').setValue('senha-errada');
    await wrapper.find('form').trigger('submit.prevent');
    await flushPromises();

    expect(wrapper.text()).toContain('Credenciais ou token inválidos');
  });
});

function flushPromises() {
  return new Promise((resolve) => setTimeout(resolve, 0));
}
