import { createRouter, createWebHistory } from 'vue-router';
import BattleView from '@/views/battle/BattleView.vue';
import GamelistView from '@/views/gamelist/GamelistView.vue';
import myinfoView from '@/views/user/myinfo/myinfoView.vue';
import NotfoundView from '@/views/notfound/NotfoundView.vue';
import RanklistView from '@/views/ranklist/RanklistView.vue';
import UserAccountLoginView from "@/views/user/account/UserAccountLoginView";
import UserAccountRegisterView from "@/views/user/account/UserAccountRegisterView";

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
    name: 'user_account_login',
    path: '/user/account/login/',
    component: UserAccountLoginView,
  },
  {
    name: 'user_account_register',
    path: '/user/account/register/',
    component: UserAccountRegisterView,
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
    path: '/:catchAll(.*)',
    redirect: '/404/',
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
