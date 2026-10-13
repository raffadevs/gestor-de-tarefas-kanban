import api from './api';
import type { TokenResponse } from '../types/api';

export async function registrar(nome: string, email: string, senha: string): Promise<TokenResponse> {
  const { data } = await api.post<TokenResponse>('/api/auth/registrar', { nome, email, senha });
  return data;
}

export async function login(email: string, senha: string): Promise<TokenResponse> {
  const { data } = await api.post<TokenResponse>('/api/auth/login', { email, senha });
  return data;
}

export async function logout(refreshToken: string): Promise<void> {
  await api.post('/api/auth/logout', { refreshToken });
}
