<script setup lang="ts">
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { registrar } from '../services/auth';
import { useAuthStore } from '../stores/auth';

const form = reactive({ nome: '', email: '', senha: '' });
const erro = ref('');
const carregando = ref(false);

const auth = useAuthStore();
const router = useRouter();

async function enviar() {
  erro.value = '';
  carregando.value = true;
  try {
    const resposta = await registrar(form.nome.trim(), form.email.trim(), form.senha);
    auth.aplicarTokens(resposta);
    router.push('/quadros');
  } catch (e: any) {
    erro.value = e?.response?.data?.detalhes?.join(', ') || e?.response?.data?.erro
        || 'Não foi possível criar a conta.';
  } finally {
    carregando.value = false;
  }
}
</script>

<template>
  <div class="max-w-sm mx-auto mt-16 px-5">
    <div class="bg-white border border-slate-200 rounded-lg shadow-sm p-6">
      <h1 class="text-lg font-bold text-slate-900">Criar conta</h1>
      <p class="text-sm text-slate-500 mt-1">Organize suas tarefas em quadros Kanban.</p>

      <form class="mt-5 flex flex-col gap-4" @submit.prevent="enviar">
        <div>
          <label class="block text-sm font-semibold text-slate-600 mb-1" for="nome">Nome</label>
          <input id="nome" v-model="form.nome" type="text" required
                 class="w-full border border-slate-300 rounded-md px-3 py-2 focus:outline-indigo-500" />
        </div>
        <div>
          <label class="block text-sm font-semibold text-slate-600 mb-1" for="email">E-mail</label>
          <input id="email" v-model="form.email" type="email" required
                 class="w-full border border-slate-300 rounded-md px-3 py-2 focus:outline-indigo-500" />
        </div>
        <div>
          <label class="block text-sm font-semibold text-slate-600 mb-1" for="senha">Senha</label>
          <input id="senha" v-model="form.senha" type="password" minlength="8" required
                 class="w-full border border-slate-300 rounded-md px-3 py-2 focus:outline-indigo-500" />
          <p class="text-xs text-slate-400 mt-1">Pelo menos 8 caracteres.</p>
        </div>

        <button type="submit" :disabled="carregando"
                class="bg-indigo-600 hover:bg-indigo-700 disabled:opacity-60 text-white font-semibold rounded-md py-2">
          {{ carregando ? 'Criando...' : 'Criar conta' }}
        </button>

        <p v-if="erro" class="text-sm text-red-600">{{ erro }}</p>
      </form>

      <p class="text-sm text-slate-500 mt-4">
        Já tem conta?
        <RouterLink to="/login" class="text-indigo-600 font-semibold">Entrar</RouterLink>
      </p>
    </div>
  </div>
</template>
