import api from './api';
import type { Cartao } from '../types/api';

export interface FiltrosCartao {
  etiquetaId?: number;
  responsavelId?: number;
  prazoVencido?: boolean;
}

export interface CriarCartaoPayload {
  colunaId: number;
  titulo: string;
  descricao?: string;
  responsavelId?: number;
  prazo?: string;
}

export interface AtualizarCartaoPayload {
  titulo: string;
  descricao?: string;
  responsavelId?: number;
  prazo?: string;
}

export async function listarCartoes(quadroId: number, filtros: FiltrosCartao = {}): Promise<Cartao[]> {
  const { data } = await api.get<Cartao[]>(`/api/quadros/${quadroId}/cartoes`, { params: filtros });
  return data;
}

export async function criarCartao(quadroId: number, payload: CriarCartaoPayload): Promise<Cartao> {
  const { data } = await api.post<Cartao>(`/api/quadros/${quadroId}/cartoes`, payload);
  return data;
}

export async function atualizarCartao(quadroId: number, cartaoId: number,
                                       payload: AtualizarCartaoPayload): Promise<Cartao> {
  const { data } = await api.patch<Cartao>(`/api/quadros/${quadroId}/cartoes/${cartaoId}`, payload);
  return data;
}

export async function moverCartao(quadroId: number, cartaoId: number, colunaDestinoId: number,
                                   indiceDestino: number): Promise<Cartao> {
  const { data } = await api.patch<Cartao>(`/api/quadros/${quadroId}/cartoes/${cartaoId}/mover`, {
    colunaDestinoId,
    indiceDestino,
  });
  return data;
}

export async function excluirCartao(quadroId: number, cartaoId: number): Promise<void> {
  await api.delete(`/api/quadros/${quadroId}/cartoes/${cartaoId}`);
}

export async function adicionarEtiquetaAoCartao(quadroId: number, cartaoId: number,
                                                  etiquetaId: number): Promise<Cartao> {
  const { data } = await api.put<Cartao>(`/api/quadros/${quadroId}/cartoes/${cartaoId}/etiquetas/${etiquetaId}`);
  return data;
}

export async function removerEtiquetaDoCartao(quadroId: number, cartaoId: number,
                                                etiquetaId: number): Promise<Cartao> {
  const { data } = await api.delete<Cartao>(`/api/quadros/${quadroId}/cartoes/${cartaoId}/etiquetas/${etiquetaId}`);
  return data;
}
