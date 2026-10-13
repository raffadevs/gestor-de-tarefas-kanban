import axios, { type AxiosRequestConfig } from 'axios';
import { useAuthStore } from '../stores/auth';
import type { TokenResponse } from '../types/api';

// String vazia (padrão da build Docker) significa "mesma origem": o nginx do
// frontend faz proxy de /api para o backend.
const baseURL = import.meta.env.VITE_API_BASE_URL ?? '';

const api = axios.create({ baseURL });

api.interceptors.request.use((config) => {
  const auth = useAuthStore();
  if (auth.accessToken) {
    config.headers.Authorization = `Bearer ${auth.accessToken}`;
  }
  return config;
});

interface RequisicaoComRetry extends AxiosRequestConfig {
  _tentouRenovar?: boolean;
}

let renovacaoEmAndamento: Promise<string> | null = null;

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const original = error.config as RequisicaoComRetry | undefined;
    const auth = useAuthStore();
    const ehRotaDeAuth = original?.url?.includes('/api/auth/');

    if (error.response?.status === 401 && original && !original._tentouRenovar
        && auth.refreshToken && !ehRotaDeAuth) {
      original._tentouRenovar = true;

      try {
        if (!renovacaoEmAndamento) {
          renovacaoEmAndamento = axios
            .post<TokenResponse>(`${baseURL}/api/auth/refresh`, { refreshToken: auth.refreshToken })
            .then((resposta) => {
              auth.aplicarTokens(resposta.data);
              return resposta.data.accessToken;
            })
            .finally(() => {
              renovacaoEmAndamento = null;
            });
        }

        const novoAccessToken = await renovacaoEmAndamento;
        original.headers = { ...original.headers, Authorization: `Bearer ${novoAccessToken}` };
        return api(original);
      } catch (erroRenovacao) {
        auth.limpar();
        window.location.href = '/login';
        return Promise.reject(erroRenovacao);
      }
    }

    return Promise.reject(error);
  }
);

export default api;
