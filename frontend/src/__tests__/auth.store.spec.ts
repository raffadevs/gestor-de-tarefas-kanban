import { beforeEach, describe, expect, it } from 'vitest';
import { createPinia, setActivePinia } from 'pinia';
import { useAuthStore } from '../stores/auth';
import type { TokenResponse } from '../types/api';

function tokenDeExemplo(): TokenResponse {
  return {
    accessToken: 'access-123',
    refreshToken: 'refresh-456',
    usuario: { id: 1, nome: 'Ana', email: 'ana@exemplo.com' },
  };
}

describe('useAuthStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
    localStorage.clear();
  });

  it('começa deslogado quando não há nada salvo', () => {
    const store = useAuthStore();
    expect(store.estaAutenticado).toBe(false);
    expect(store.usuario).toBeNull();
  });

  it('aplicarTokens autentica e persiste no localStorage', () => {
    const store = useAuthStore();
    store.aplicarTokens(tokenDeExemplo());

    expect(store.estaAutenticado).toBe(true);
    expect(store.usuario?.nome).toBe('Ana');

    const salvo = JSON.parse(localStorage.getItem('kanban.auth')!);
    expect(salvo.accessToken).toBe('access-123');
  });

  it('limpar remove a sessão e o localStorage', () => {
    const store = useAuthStore();
    store.aplicarTokens(tokenDeExemplo());

    store.limpar();

    expect(store.estaAutenticado).toBe(false);
    expect(store.usuario).toBeNull();
    expect(localStorage.getItem('kanban.auth')).toBeNull();
  });
});
