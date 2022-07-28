import { createRouter, createWebHistory } from 'vue-router';
import BattleView from '@/views/battle/BattleView.vue';
import GamelistView from '@/views/gamelist/GamelistView.vue';
import myinfoView from '@/views/user/myinfo/myinfoView.vue';
import NotfoundView from '@/views/notfound/NotfoundView.vue';
import RanklistView from '@/views/ranklist/RanklistView.vue';
import LogoutView from '@/views/logout/LogoutView.vue';

const routes = [
  {
    name: 'home',
    path: '/',
    redirect: '/battle/',
  },
  {
    name: 'battle',
    path: '/battle/',
    component: BattleView,
  },
  {
    name: 'gamelist',
    path: '/gamelist/',
    component: GamelistView,
  },
  {
    name: 'myinfo',
    path: '/user/myinfo/',
    component: myinfoView,
  },
  {
    name: '404',
    path: '/404/',
    component: NotfoundView,
  },
  {
    name: 'ranklist',
    path: '/ranklist/',
    component: RanklistView,
  },
  {
    name: 'logout',
    path: '/logout/',
    component: LogoutView,
  },
  {
    path: '/:catchAll(.*)',
    redirect: '/404/',
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
