<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { criarQuadro, listarQuadros } from '../services/quadros';
import type { Quadro } from '../types/api';

const quadros = ref<Quadro[]>([]);
const carregando = ref(true);
const erro = ref('');

const novoNome = ref('');
const criando = ref(false);

async function carregar() {
  carregando.value = true;
  try {
    quadros.value = await listarQuadros();
  } catch {
    erro.value = 'Não foi possível carregar seus quadros.';
  } finally {
    carregando.value = false;
  }
}

async function criar() {
  if (!novoNome.value.trim()) return;
  criando.value = true;
  try {
    const quadro = await criarQuadro(novoNome.value.trim());
    quadros.value.unshift(quadro);
    novoNome.value = '';
  } catch {
    erro.value = 'Não foi possível criar o quadro.';
  } finally {
    criando.value = false;
  }
}

onMounted(carregar);
</script>

<template>
  <div class="max-w-4xl mx-auto px-5 py-8">
    <h1 class="text-xl font-bold text-slate-900">Meus quadros</h1>

    <form class="mt-4 flex gap-2" @submit.prevent="criar">
      <input v-model="novoNome" type="text" placeholder="Nome do novo quadro" required
             class="flex-1 border border-slate-300 rounded-md px-3 py-2 focus:outline-indigo-500" />
      <button type="submit" :disabled="criando"
              class="bg-indigo-600 hover:bg-indigo-700 disabled:opacity-60 text-white font-semibold rounded-md px-4">
        {{ criando ? 'Criando...' : 'Criar quadro' }}
      </button>
    </form>

    <p v-if="erro" class="text-sm text-red-600 mt-3">{{ erro }}</p>

    <div v-if="carregando" class="text-sm text-slate-500 mt-6">Carregando...</div>

    <div v-else-if="quadros.length === 0" class="text-sm text-slate-500 mt-6">
      Você ainda não é membro de nenhum quadro. Crie o primeiro acima.
    </div>

    <ul v-else class="mt-6 grid grid-cols-1 sm:grid-cols-2 gap-3">
      <li v-for="quadro in quadros" :key="quadro.id">
        <RouterLink :to="`/quadros/${quadro.id}`"
                    class="block bg-white border border-slate-200 rounded-lg p-4 hover:border-indigo-400 transition-colors">
          <p class="font-semibold text-slate-900">{{ quadro.nome }}</p>
          <p class="text-xs text-slate-500 mt-1">papel: {{ quadro.meuPapel.toLowerCase() }}</p>
        </RouterLink>
      </li>
    </ul>
  </div>
</template>
