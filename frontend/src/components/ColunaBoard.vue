<script setup lang="ts">
import { ref } from 'vue';
import draggable from 'vuedraggable';
import type { Cartao, Coluna } from '../types/api';
import CartaoCard from './CartaoCard.vue';

const props = defineProps<{
  coluna: Coluna;
  podeEditar: boolean;
}>();

const emit = defineEmits<{
  'mover-cartao': [cartaoId: number, colunaDestinoId: number, indiceDestino: number];
  'criar-cartao': [colunaId: number, titulo: string];
  'excluir-coluna': [colunaId: number];
  'abrir-cartao': [cartao: Cartao];
}>();

const cartoes = defineModel<Cartao[]>({ required: true });

const novoTitulo = ref('');
const mostrarFormulario = ref(false);

function aoMudar(evento: any) {
  if (evento.added) {
    emit('mover-cartao', evento.added.element.id, props.coluna.id, evento.added.newIndex);
  } else if (evento.moved) {
    emit('mover-cartao', evento.moved.element.id, props.coluna.id, evento.moved.newIndex);
  }
}

function criarCartao() {
  if (!novoTitulo.value.trim()) return;
  emit('criar-cartao', props.coluna.id, novoTitulo.value.trim());
  novoTitulo.value = '';
  mostrarFormulario.value = false;
}
</script>

<template>
  <div class="w-72 shrink-0 bg-slate-100 rounded-lg p-3 flex flex-col max-h-full">
    <div class="flex items-center justify-between mb-3">
      <h3 class="font-semibold text-sm text-slate-700">{{ coluna.nome }}</h3>
      <button v-if="podeEditar" type="button" class="text-slate-400 hover:text-red-600 text-xs"
              @click="$emit('excluir-coluna', coluna.id)">
        excluir
      </button>
    </div>

    <draggable
        v-model="cartoes"
        :disabled="!podeEditar"
        group="cartoes"
        item-key="id"
        class="flex flex-col gap-2 min-h-[40px] overflow-y-auto"
        ghost-class="opacity-40"
        @change="aoMudar">
      <template #item="{ element }">
        <CartaoCard :cartao="element" @abrir="$emit('abrir-cartao', element)" />
      </template>
    </draggable>

    <div v-if="podeEditar" class="mt-3">
      <form v-if="mostrarFormulario" class="flex flex-col gap-2" @submit.prevent="criarCartao">
        <textarea v-model="novoTitulo" rows="2" placeholder="Título do cartão" autofocus
                  class="text-sm border border-slate-300 rounded-md px-2 py-1.5 resize-none focus:outline-indigo-500" />
        <div class="flex gap-2">
          <button type="submit" class="text-xs bg-indigo-600 text-white rounded px-2 py-1">Adicionar</button>
          <button type="button" class="text-xs text-slate-500" @click="mostrarFormulario = false">cancelar</button>
        </div>
      </form>
      <button v-else type="button" class="text-xs text-slate-500 hover:text-slate-800"
              @click="mostrarFormulario = true">
        + adicionar cartão
      </button>
    </div>
  </div>
</template>
