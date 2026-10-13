<script setup lang="ts">
import { RouterLink, RouterView, useRouter } from 'vue-router';
import { useAuthStore } from './stores/auth';
import { logout as logoutRequest } from './services/auth';

const auth = useAuthStore();
const router = useRouter();

async function sair() {
  if (auth.refreshToken) {
    try {
      await logoutRequest(auth.refreshToken);
    } catch {
      // mesmo se a revogação falhar no servidor, limpamos a sessão local
    }
  }
  auth.limpar();
  router.push({ name: 'login' });
}
</script>

<template>
  <div class="min-h-screen flex flex-col">
    <header class="border-b border-slate-200 bg-white">
      <div class="max-w-6xl mx-auto px-5 py-3 flex items-center justify-between">
        <RouterLink to="/quadros" class="font-bold text-slate-900">gestor-de-tarefas-kanban</RouterLink>
        <div v-if="auth.estaAutenticado" class="flex items-center gap-4 text-sm">
          <span class="text-slate-500">{{ auth.usuario?.nome }}</span>
          <button class="text-indigo-600 hover:text-indigo-800" type="button" @click="sair">Sair</button>
        </div>
      </div>
    </header>

    <main class="flex-1">
      <RouterView />
    </main>
  </div>
</template>
