export type Papel = 'DONO' | 'EDITOR' | 'LEITOR';

export interface Usuario {
  id: number;
  nome: string;
  email: string;
}

export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
  usuario: Usuario;
}

export interface Quadro {
  id: number;
  nome: string;
  donoId: number;
  criadoEm: string;
  meuPapel: Papel;
}

export interface Membro {
  usuarioId: number;
  nome: string;
  email: string;
  papel: Papel;
}

export interface Coluna {
  id: number;
  quadroId: number;
  nome: string;
  posicao: string;
}

export interface Etiqueta {
  id: number;
  quadroId: number;
  nome: string;
  cor: string;
}

export interface Cartao {
  id: number;
  colunaId: number;
  titulo: string;
  descricao: string | null;
  responsavelId: number | null;
  prazo: string | null;
  posicao: string;
  criadoEm: string;
  etiquetas: Etiqueta[];
}

export interface ErroResponse {
  timestamp: string;
  status: number;
  erro: string;
  detalhes: string[];
}
