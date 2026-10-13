<script setup lang="ts">
import { reactive, watch } from 'vue';
import type { Cartao, Etiqueta, Membro } from '../types/api';

const props = defineProps<{
  cartao: Cartao;
  membros: Membro[];
  etiquetas: Etiqueta[];
  podeEditar: boolean;
}>();

const emit = defineEmits<{
  fechar: [];
  salvar: [payload: { titulo: string; descricao?: string; responsavelId?: number; prazo?: string }];
  excluir: [];
  'alternar-etiqueta': [etiquetaId: number, ativa: boolean];
}>();

const form = reactive({
  titulo: props.cartao.titulo,
  descricao: props.cartao.descricao ?? '',
  responsavelId: props.cartao.responsavelId ?? undefined as number | undefined,
  prazo: props.cartao.prazo ? props.cartao.prazo.slice(0, 16) : '',
});

watch(() => props.cartao, (novo) => {
  form.titulo = novo.titulo;
  form.descricao = novo.descricao ?? '';
  form.responsavelId = novo.responsavelId ?? undefined;
  form.prazo = novo.prazo ? novo.prazo.slice(0, 16) : '';
});

function temEtiqueta(etiquetaId: number): boolean {
  return props.cartao.etiquetas.some((e) => e.id === etiquetaId);
}

function salvar() {
  emit('salvar', {
    titulo: form.titulo.trim(),
    descricao: form.descricao.trim() || undefined,
    responsavelId: form.responsavelId,
    prazo: form.prazo ? new Date(form.prazo).toISOString() : undefined,
  });
}
</script>

<template>
  <div class="fixed inset-0 bg-slate-900/40 flex items-center justify-center p-4 z-50" @click.self="$emit('fechar')">
    <div class="bg-white rounded-lg shadow-lg w-full max-w-lg p-6">
      <div class="flex items-start justify-between">
        <h2 class="text-lg font-bold text-slate-900">Detalhes do cartão</h2>
        <button type="button" class="text-slate-400 hover:text-slate-700" @click="$emit('fechar')">&times;</button>
      </div>

      <form class="mt-4 flex flex-col gap-3" @submit.prevent="salvar">
        <div>
          <label class="block text-xs font-semibold text-slate-500 mb-1">Título</label>
          <input v-model="form.titulo" type="text" :disabled="!podeEditar" required
                 class="w-full border border-slate-300 rounded-md px-3 py-2 disabled:bg-slate-50" />
        </div>

        <div>
          <label class="block text-xs font-semibold text-slate-500 mb-1">Descrição</label>
          <textarea v-model="form.descricao" rows="3" :disabled="!podeEditar"
                    class="w-full border border-slate-300 rounded-md px-3 py-2 disabled:bg-slate-50" />
        </div>

        <div class="grid grid-cols-2 gap-3">
          <div>
            <label class="block text-xs font-semibold text-slate-500 mb-1">Responsável</label>
            <select v-model.number="form.responsavelId" :disabled="!podeEditar"
                    class="w-full border border-slate-300 rounded-md px-3 py-2 disabled:bg-slate-50">
              <option :value="undefined">Ninguém</option>
              <option v-for="membro in membros" :key="membro.usuarioId" :value="membro.usuarioId">
                {{ membro.nome }}
              </option>
            </select>
          </div>
          <div>
            <label class="block text-xs font-semibold text-slate-500 mb-1">Prazo</label>
            <input v-model="form.prazo" type="datetime-local" :disabled="!podeEditar"
                   class="w-full border border-slate-300 rounded-md px-3 py-2 disabled:bg-slate-50" />
          </div>
        </div>

        <div v-if="etiquetas.length">
          <label class="block text-xs font-semibold text-slate-500 mb-1">Etiquetas</label>
          <div class="flex flex-wrap gap-2">
            <button v-for="etiqueta in etiquetas" :key="etiqueta.id" type="button"
                    :disabled="!podeEditar"
                    class="text-xs font-semibold px-2 py-1 rounded-full border-2 transition-opacity"
                    :style="{
                      backgroundColor: etiqueta.cor,
                      borderColor: etiqueta.cor,
                      color: '#fff',
                      opacity: temEtiqueta(etiqueta.id) ? 1 : 0.35,
                    }"
                    @click="$emit('alternar-etiqueta', etiqueta.id, !temEtiqueta(etiqueta.id))">
              {{ etiqueta.nome }}
            </button>
          </div>
        </div>

        <div v-if="podeEditar" class="flex justify-between items-center mt-2">
          <button type="button" class="text-sm text-red-600 hover:text-red-800" @click="$emit('excluir')">
            Excluir cartão
          </button>
          <button type="submit" class="bg-indigo-600 hover:bg-indigo-700 text-white font-semibold rounded-md px-4 py-2 text-sm">
            Salvar
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
