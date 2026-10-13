<script setup lang="ts">
import { reactive, ref } from 'vue';
import type { Membro, Papel } from '../types/api';

defineProps<{ membros: Membro[]; podeConvidar: boolean }>();
const emit = defineEmits<{ fechar: []; convidar: [email: string, papel: Papel] }>();

const form = reactive({ email: '', papel: 'LEITOR' as Papel });
const erro = ref('');

function convidar() {
  if (!form.email.trim()) return;
  erro.value = '';
  emit('convidar', form.email.trim(), form.papel);
  form.email = '';
}

defineExpose({ reportarErro: (mensagem: string) => { erro.value = mensagem; } });
</script>

<template>
  <div class="fixed inset-0 bg-slate-900/40 flex items-center justify-center p-4 z-50" @click.self="$emit('fechar')">
    <div class="bg-white rounded-lg shadow-lg w-full max-w-md p-6">
      <div class="flex items-start justify-between">
        <h2 class="text-lg font-bold text-slate-900">Membros do quadro</h2>
        <button type="button" class="text-slate-400 hover:text-slate-700" @click="$emit('fechar')">&times;</button>
      </div>

      <ul class="mt-4 flex flex-col gap-2">
        <li v-for="membro in membros" :key="membro.usuarioId"
            class="flex items-center justify-between text-sm border-b border-slate-100 pb-2">
          <div>
            <p class="font-medium text-slate-900">{{ membro.nome }}</p>
            <p class="text-xs text-slate-500">{{ membro.email }}</p>
          </div>
          <span class="text-xs font-semibold text-slate-500 uppercase">{{ membro.papel }}</span>
        </li>
      </ul>

      <form v-if="podeConvidar" class="mt-5 flex flex-col gap-2 border-t border-slate-100 pt-4" @submit.prevent="convidar">
        <label class="text-xs font-semibold text-slate-500">Convidar por e-mail</label>
        <div class="flex gap-2">
          <input v-model="form.email" type="email" placeholder="pessoa@exemplo.com" required
                 class="flex-1 border border-slate-300 rounded-md px-3 py-2 text-sm" />
          <select v-model="form.papel" class="border border-slate-300 rounded-md px-2 py-2 text-sm">
            <option value="LEITOR">Leitor</option>
            <option value="EDITOR">Editor</option>
            <option value="DONO">Dono</option>
          </select>
          <button type="submit" class="bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-semibold rounded-md px-3">
            Convidar
          </button>
        </div>
        <p v-if="erro" class="text-xs text-red-600">{{ erro }}</p>
      </form>
    </div>
  </div>
</template>
