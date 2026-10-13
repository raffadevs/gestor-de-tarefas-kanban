<script setup lang="ts">
import type { Cartao } from '../types/api';

defineProps<{ cartao: Cartao }>();
defineEmits<{ abrir: [cartao: Cartao] }>();

function prazoVencido(prazo: string | null): boolean {
  return !!prazo && new Date(prazo).getTime() < Date.now();
}

function formatarPrazo(prazo: string): string {
  return new Date(prazo).toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit' });
}
</script>

<template>
  <button type="button"
          class="w-full text-left bg-white border border-slate-200 rounded-md p-3 shadow-sm hover:border-indigo-400 transition-colors cursor-grab active:cursor-grabbing"
          @click="$emit('abrir', cartao)">
    <div v-if="cartao.etiquetas.length" class="flex flex-wrap gap-1 mb-2">
      <span v-for="etiqueta in cartao.etiquetas" :key="etiqueta.id"
            class="text-[10px] font-semibold px-2 py-0.5 rounded-full text-white"
            :style="{ backgroundColor: etiqueta.cor }">
        {{ etiqueta.nome }}
      </span>
    </div>

    <p class="text-sm font-medium text-slate-900">{{ cartao.titulo }}</p>

    <p v-if="cartao.prazo"
       class="text-xs mt-2 inline-block px-1.5 py-0.5 rounded"
       :class="prazoVencido(cartao.prazo) ? 'bg-red-100 text-red-700' : 'bg-slate-100 text-slate-500'">
      prazo: {{ formatarPrazo(cartao.prazo) }}
    </p>
  </button>
</template>
