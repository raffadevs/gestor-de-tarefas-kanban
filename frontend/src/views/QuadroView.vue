<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { listarColunas, criarColuna, excluirColuna } from '../services/colunas';
import {
  adicionarEtiquetaAoCartao,
  atualizarCartao,
  criarCartao,
  excluirCartao,
  listarCartoes,
  moverCartao,
  removerEtiquetaDoCartao,
} from '../services/cartoes';
import { listarEtiquetas, criarEtiqueta } from '../services/etiquetas';
import { buscarQuadro, convidarMembro, listarMembros } from '../services/quadros';
import type { Cartao, Coluna, Etiqueta, Membro, Papel, Quadro } from '../types/api';
import ColunaBoard from '../components/ColunaBoard.vue';
import CartaoModal from '../components/CartaoModal.vue';
import MembrosModal from '../components/MembrosModal.vue';

const props = defineProps<{ id: number }>();

const quadro = ref<Quadro | null>(null);
const colunas = ref<Coluna[]>([]);
const cartoesPorColuna = reactive<Record<number, Cartao[]>>({});
const membros = ref<Membro[]>([]);
const etiquetas = ref<Etiqueta[]>([]);
const carregando = ref(true);
const erro = ref('');

const cartaoAberto = ref<Cartao | null>(null);
const mostrarMembros = ref(false);
const novoNomeColuna = ref('');
const mostrarFormularioColuna = ref(false);

const filtroEtiquetaId = ref<number | undefined>(undefined);
const filtroResponsavelId = ref<number | undefined>(undefined);
const filtroPrazoVencido = ref(false);

const mostrarFormularioEtiqueta = ref(false);
const novaEtiqueta = reactive({ nome: '', cor: '#5b4ff2' });

async function aoCriarEtiqueta() {
  if (!novaEtiqueta.nome.trim()) return;
  const etiqueta = await criarEtiqueta(props.id, novaEtiqueta.nome.trim(), novaEtiqueta.cor);
  etiquetas.value.push(etiqueta);
  novaEtiqueta.nome = '';
  mostrarFormularioEtiqueta.value = false;
}

const podeEditar = computed(() => quadro.value?.meuPapel === 'DONO' || quadro.value?.meuPapel === 'EDITOR');
const podeConvidar = computed(() => quadro.value?.meuPapel === 'DONO');
const filtroAtivo = computed(() => !!filtroEtiquetaId.value || !!filtroResponsavelId.value || filtroPrazoVencido.value);

async function carregarTudo() {
  carregando.value = true;
  erro.value = '';
  try {
    const [dadosQuadro, dadosColunas, dadosCartoes, dadosMembros, dadosEtiquetas] = await Promise.all([
      buscarQuadro(props.id),
      listarColunas(props.id),
      listarCartoes(props.id),
      listarMembros(props.id),
      listarEtiquetas(props.id),
    ]);

    quadro.value = dadosQuadro;
    colunas.value = dadosColunas;
    membros.value = dadosMembros;
    etiquetas.value = dadosEtiquetas;

    Object.keys(cartoesPorColuna).forEach((chave) => delete cartoesPorColuna[Number(chave)]);
    for (const coluna of dadosColunas) {
      cartoesPorColuna[coluna.id] = dadosCartoes
          .filter((c) => c.colunaId === coluna.id)
          .sort((a, b) => Number(a.posicao) - Number(b.posicao));
    }
  } catch {
    erro.value = 'Não foi possível carregar este quadro. Você pode não ter mais acesso a ele.';
  } finally {
    carregando.value = false;
  }
}

function cartoesExibidos(colunaId: number): Cartao[] {
  const todos = cartoesPorColuna[colunaId] ?? [];
  if (!filtroAtivo.value) return todos;

  const agora = Date.now();
  return todos.filter((c) => {
    if (filtroEtiquetaId.value && !c.etiquetas.some((e) => e.id === filtroEtiquetaId.value)) return false;
    if (filtroResponsavelId.value && c.responsavelId !== filtroResponsavelId.value) return false;
    if (filtroPrazoVencido.value && !(c.prazo && new Date(c.prazo).getTime() < agora)) return false;
    return true;
  });
}

async function aoCriarColuna() {
  if (!novoNomeColuna.value.trim()) return;
  const coluna = await criarColuna(props.id, novoNomeColuna.value.trim());
  colunas.value.push(coluna);
  cartoesPorColuna[coluna.id] = [];
  novoNomeColuna.value = '';
  mostrarFormularioColuna.value = false;
}

async function aoExcluirColuna(colunaId: number) {
  if (!confirm('Excluir esta coluna e todos os seus cartões?')) return;
  await excluirColuna(props.id, colunaId);
  colunas.value = colunas.value.filter((c) => c.id !== colunaId);
  delete cartoesPorColuna[colunaId];
}

async function aoCriarCartao(colunaId: number, titulo: string) {
  const cartao = await criarCartao(props.id, { colunaId, titulo });
  cartoesPorColuna[colunaId].push(cartao);
}

async function aoMoverCartao(cartaoId: number, colunaDestinoId: number, indiceDestino: number) {
  try {
    await moverCartao(props.id, cartaoId, colunaDestinoId, indiceDestino);
  } catch {
    // em caso de falha (ex.: papel insuficiente numa corrida de eventos), recarrega o estado real
    await carregarTudo();
  }
}

function abrirCartao(cartao: Cartao) {
  cartaoAberto.value = cartao;
}

function substituirCartaoLocal(atualizado: Cartao) {
  const lista = cartoesPorColuna[atualizado.colunaId];
  const indice = lista?.findIndex((c) => c.id === atualizado.id);
  if (lista && indice !== undefined && indice >= 0) {
    lista[indice] = atualizado;
  }
  cartaoAberto.value = atualizado;
}

async function aoSalvarCartao(payload: { titulo: string; descricao?: string; responsavelId?: number; prazo?: string }) {
  if (!cartaoAberto.value) return;
  const atualizado = await atualizarCartao(props.id, cartaoAberto.value.id, payload);
  substituirCartaoLocal({ ...atualizado, etiquetas: cartaoAberto.value.etiquetas });
  cartaoAberto.value = null;
}

async function aoExcluirCartaoAberto() {
  if (!cartaoAberto.value) return;
  const colunaId = cartaoAberto.value.colunaId;
  await excluirCartao(props.id, cartaoAberto.value.id);
  cartoesPorColuna[colunaId] = cartoesPorColuna[colunaId].filter((c) => c.id !== cartaoAberto.value!.id);
  cartaoAberto.value = null;
}

async function aoAlternarEtiqueta(etiquetaId: number, ativa: boolean) {
  if (!cartaoAberto.value) return;
  const atualizado = ativa
      ? await adicionarEtiquetaAoCartao(props.id, cartaoAberto.value.id, etiquetaId)
      : await removerEtiquetaDoCartao(props.id, cartaoAberto.value.id, etiquetaId);
  substituirCartaoLocal(atualizado);
}

const membrosModalRef = ref<InstanceType<typeof MembrosModal> | null>(null);

async function aoConvidar(email: string, papel: Papel) {
  try {
    const membro = await convidarMembro(props.id, email, papel);
    const existente = membros.value.findIndex((m) => m.usuarioId === membro.usuarioId);
    if (existente >= 0) membros.value[existente] = membro;
    else membros.value.push(membro);
  } catch (e: any) {
    membrosModalRef.value?.reportarErro(e?.response?.data?.erro ?? 'Não foi possível convidar este usuário.');
  }
}

onMounted(carregarTudo);
</script>

<template>
  <div v-if="carregando" class="px-5 py-8 text-sm text-slate-500">Carregando quadro...</div>
  <div v-else-if="erro" class="px-5 py-8 text-sm text-red-600">{{ erro }}</div>

  <div v-else class="flex flex-col h-[calc(100vh-57px)]">
    <div class="px-5 py-4 border-b border-slate-200 bg-white flex items-center justify-between flex-wrap gap-3">
      <div>
        <h1 class="text-lg font-bold text-slate-900">{{ quadro?.nome }}</h1>
        <p class="text-xs text-slate-500">papel: {{ quadro?.meuPapel.toLowerCase() }}</p>
      </div>

      <div class="flex items-center gap-3 flex-wrap text-sm">
        <select v-model="filtroEtiquetaId" class="border border-slate-300 rounded-md px-2 py-1.5">
          <option :value="undefined">Todas etiquetas</option>
          <option v-for="e in etiquetas" :key="e.id" :value="e.id">{{ e.nome }}</option>
        </select>
        <select v-model="filtroResponsavelId" class="border border-slate-300 rounded-md px-2 py-1.5">
          <option :value="undefined">Todos responsáveis</option>
          <option v-for="m in membros" :key="m.usuarioId" :value="m.usuarioId">{{ m.nome }}</option>
        </select>
        <label class="flex items-center gap-1.5 text-slate-600">
          <input v-model="filtroPrazoVencido" type="checkbox" />
          prazo vencido
        </label>
        <button type="button" class="text-indigo-600 font-semibold" @click="mostrarMembros = true">
          Membros ({{ membros.length }})
        </button>
        <button v-if="podeEditar" type="button" class="text-indigo-600 font-semibold"
                @click="mostrarFormularioEtiqueta = !mostrarFormularioEtiqueta">
          + etiqueta
        </button>
      </div>
    </div>

    <form v-if="mostrarFormularioEtiqueta" class="px-5 py-3 bg-white border-b border-slate-200 flex items-center gap-2"
          @submit.prevent="aoCriarEtiqueta">
      <input v-model="novaEtiqueta.nome" type="text" placeholder="Nome da etiqueta" required
             class="text-sm border border-slate-300 rounded-md px-2 py-1.5" />
      <input v-model="novaEtiqueta.cor" type="color" class="h-8 w-10 border border-slate-300 rounded" />
      <button type="submit" class="text-xs bg-indigo-600 text-white rounded px-3 py-1.5">Criar</button>
      <button type="button" class="text-xs text-slate-500" @click="mostrarFormularioEtiqueta = false">cancelar</button>
    </form>

    <p v-if="filtroAtivo" class="px-5 pt-2 text-xs text-amber-600">
      Filtro ativo: arrastar cartões fica desabilitado até limpar os filtros.
    </p>

    <div class="flex-1 overflow-x-auto px-5 py-4">
      <div class="flex gap-4 h-full items-start">
        <ColunaBoard
            v-for="coluna in colunas"
            :key="coluna.id"
            :coluna="coluna"
            :pode-editar="podeEditar && !filtroAtivo"
            :model-value="cartoesExibidos(coluna.id)"
            @update:model-value="(v) => (cartoesPorColuna[coluna.id] = v)"
            @mover-cartao="aoMoverCartao"
            @criar-cartao="aoCriarCartao"
            @excluir-coluna="aoExcluirColuna"
            @abrir-cartao="abrirCartao" />

        <div v-if="podeEditar" class="w-64 shrink-0">
          <form v-if="mostrarFormularioColuna" class="bg-slate-100 rounded-lg p-3 flex flex-col gap-2"
                @submit.prevent="aoCriarColuna">
            <input v-model="novoNomeColuna" type="text" placeholder="Nome da coluna" autofocus
                   class="text-sm border border-slate-300 rounded-md px-2 py-1.5" />
            <div class="flex gap-2">
              <button type="submit" class="text-xs bg-indigo-600 text-white rounded px-2 py-1">Adicionar</button>
              <button type="button" class="text-xs text-slate-500" @click="mostrarFormularioColuna = false">cancelar</button>
            </div>
          </form>
          <button v-else type="button"
                  class="text-sm text-slate-500 hover:text-slate-800 bg-slate-100 hover:bg-slate-200 rounded-lg px-3 py-2 w-full text-left"
                  @click="mostrarFormularioColuna = true">
            + adicionar coluna
          </button>
        </div>
      </div>
    </div>

    <CartaoModal
        v-if="cartaoAberto"
        :cartao="cartaoAberto"
        :membros="membros"
        :etiquetas="etiquetas"
        :pode-editar="podeEditar"
        @fechar="cartaoAberto = null"
        @salvar="aoSalvarCartao"
        @excluir="aoExcluirCartaoAberto"
        @alternar-etiqueta="aoAlternarEtiqueta" />

    <MembrosModal
        v-if="mostrarMembros"
        ref="membrosModalRef"
        :membros="membros"
        :pode-convidar="podeConvidar"
        @fechar="mostrarMembros = false"
        @convidar="aoConvidar" />
  </div>
</template>
