import api from './api';
import type { Coluna } from '../types/api';

export async function listarColunas(quadroId: number): Promise<Coluna[]> {
  const { data } = await api.get<Coluna[]>(`/api/quadros/${quadroId}/colunas`);
  return data;
}

export async function criarColuna(quadroId: number, nome: string): Promise<Coluna> {
  const { data } = await api.post<Coluna>(`/api/quadros/${quadroId}/colunas`, { nome });
  return data;
}

export async function renomearColuna(quadroId: number, colunaId: number, nome: string): Promise<Coluna> {
  const { data } = await api.patch<Coluna>(`/api/quadros/${quadroId}/colunas/${colunaId}`, { nome });
  return data;
}

export async function excluirColuna(quadroId: number, colunaId: number): Promise<void> {
  await api.delete(`/api/quadros/${quadroId}/colunas/${colunaId}`);
}
