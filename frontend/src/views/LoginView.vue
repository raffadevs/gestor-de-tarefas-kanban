<script setup lang="ts">
import { reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { login } from '../services/auth';
import { useAuthStore } from '../stores/auth';

const form = reactive({ email: '', senha: '' });
const erro = ref('');
const carregando = ref(false);

const auth = useAuthStore();
const router = useRouter();
const route = useRoute();

async function enviar() {
  erro.value = '';
  carregando.value = true;
  try {
    const resposta = await login(form.email.trim(), form.senha);
    auth.aplicarTokens(resposta);
    const destino = typeof route.query.redirect === 'string' ? route.query.redirect : '/quadros';
    router.push(destino);
  } catch (e: any) {
    erro.value = e?.response?.data?.erro ?? 'Não foi possível entrar. Verifique suas credenciais.';
  } finally {
    carregando.value = false;
  }
}
</script>

<template>
  <div class="max-w-sm mx-auto mt-16 px-5">
    <div class="bg-white border border-slate-200 rounded-lg shadow-sm p-6">
      <h1 class="text-lg font-bold text-slate-900">Entrar</h1>
      <p class="text-sm text-slate-500 mt-1">Acesse seus quadros Kanban.</p>

      <form class="mt-5 flex flex-col gap-4" @submit.prevent="enviar">
        <div>
          <label class="block text-sm font-semibold text-slate-600 mb-1" for="email">E-mail</label>
          <input id="email" v-model="form.email" type="email" required
                 class="w-full border border-slate-300 rounded-md px-3 py-2 focus:outline-indigo-500" />
        </div>
        <div>
          <label class="block text-sm font-semibold text-slate-600 mb-1" for="senha">Senha</label>
          <input id="senha" v-model="form.senha" type="password" required
                 class="w-full border border-slate-300 rounded-md px-3 py-2 focus:outline-indigo-500" />
        </div>

        <button type="submit" :disabled="carregando"
                class="bg-indigo-600 hover:bg-indigo-700 disabled:opacity-60 text-white font-semibold rounded-md py-2">
          {{ carregando ? 'Entrando...' : 'Entrar' }}
        </button>

        <p v-if="erro" class="text-sm text-red-600">{{ erro }}</p>
      </form>

      <p class="text-sm text-slate-500 mt-4">
        Não tem conta?
        <RouterLink to="/registrar" class="text-indigo-600 font-semibold">Criar conta</RouterLink>
      </p>
    </div>
  </div>
</template>
