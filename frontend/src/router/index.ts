import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '../stores/auth';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', name: 'login', component: () => import('../views/LoginView.vue') },
    { path: '/registrar', name: 'registrar', component: () => import('../views/RegistrarView.vue') },
    {
      path: '/quadros',
      name: 'quadros',
      component: () => import('../views/QuadrosView.vue'),
      meta: { requerAutenticacao: true },
    },
    {
      path: '/quadros/:id',
      name: 'quadro',
      component: () => import('../views/QuadroView.vue'),
      props: (route) => ({ id: Number(route.params.id) }),
      meta: { requerAutenticacao: true },
    },
    { path: '/', redirect: '/quadros' },
  ],
});

router.beforeEach((to) => {
  const auth = useAuthStore();

  if (to.meta.requerAutenticacao && !auth.estaAutenticado) {
    return { name: 'login', query: { redirect: to.fullPath } };
  }
  if ((to.name === 'login' || to.name === 'registrar') && auth.estaAutenticado) {
    return { name: 'quadros' };
  }
  return true;
});

export default router;
