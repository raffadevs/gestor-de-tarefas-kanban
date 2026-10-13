import api from './api';
import type { Etiqueta } from '../types/api';

export async function listarEtiquetas(quadroId: number): Promise<Etiqueta[]> {
  const { data } = await api.get<Etiqueta[]>(`/api/quadros/${quadroId}/etiquetas`);
  return data;
}

export async function criarEtiqueta(quadroId: number, nome: string, cor: string): Promise<Etiqueta> {
  const { data } = await api.post<Etiqueta>(`/api/quadros/${quadroId}/etiquetas`, { nome, cor });
  return data;
}

export async function excluirEtiqueta(quadroId: number, etiquetaId: number): Promise<void> {
  await api.delete(`/api/quadros/${quadroId}/etiquetas/${etiquetaId}`);
}
