import api from './api';
import type { Membro, Papel, Quadro } from '../types/api';

export async function listarQuadros(): Promise<Quadro[]> {
  const { data } = await api.get<Quadro[]>('/api/quadros');
  return data;
}

export async function criarQuadro(nome: string): Promise<Quadro> {
  const { data } = await api.post<Quadro>('/api/quadros', { nome });
  return data;
}

export async function buscarQuadro(id: number): Promise<Quadro> {
  const { data } = await api.get<Quadro>(`/api/quadros/${id}`);
  return data;
}

export async function excluirQuadro(id: number): Promise<void> {
  await api.delete(`/api/quadros/${id}`);
}

export async function listarMembros(quadroId: number): Promise<Membro[]> {
  const { data } = await api.get<Membro[]>(`/api/quadros/${quadroId}/membros`);
  return data;
}

export async function convidarMembro(quadroId: number, email: string, papel: Papel): Promise<Membro> {
  const { data } = await api.post<Membro>(`/api/quadros/${quadroId}/membros`, { email, papel });
  return data;
}
