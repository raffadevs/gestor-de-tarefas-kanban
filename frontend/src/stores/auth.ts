import { defineStore } from 'pinia';
import type { TokenResponse, Usuario } from '../types/api';

const CHAVE_ARMAZENAMENTO = 'kanban.auth';

interface EstadoAuth {
  accessToken: string | null;
  refreshToken: string | null;
  usuario: Usuario | null;
}

function carregarEstadoInicial(): EstadoAuth {
  try {
    const bruto = localStorage.getItem(CHAVE_ARMAZENAMENTO);
    if (bruto) return JSON.parse(bruto) as EstadoAuth;
  } catch {
    // localStorage indisponível (ex.: navegação privada) — segue deslogado
  }
  return { accessToken: null, refreshToken: null, usuario: null };
}

export const useAuthStore = defineStore('auth', {
  state: (): EstadoAuth => carregarEstadoInicial(),

  getters: {
    estaAutenticado: (state) => !!state.accessToken,
  },

  actions: {
    aplicarTokens(resposta: TokenResponse) {
      this.accessToken = resposta.accessToken;
      this.refreshToken = resposta.refreshToken;
      this.usuario = resposta.usuario;
      this.persistir();
    },

    limpar() {
      this.accessToken = null;
      this.refreshToken = null;
      this.usuario = null;
      try {
        localStorage.removeItem(CHAVE_ARMAZENAMENTO);
      } catch {
        // ignora
      }
    },

    persistir() {
      try {
        localStorage.setItem(CHAVE_ARMAZENAMENTO, JSON.stringify({
          accessToken: this.accessToken,
          refreshToken: this.refreshToken,
          usuario: this.usuario,
        }));
      } catch {
        // ignora
      }
    },
  },
});
