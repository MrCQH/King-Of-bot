import { createRouter, createWebHistory } from 'vue-router';
import BattleView from '@/views/battle/BattleView.vue';
import GamelistView from '@/views/gamelist/GamelistView.vue';
import myinfoView from '@/views/user/myinfo/myinfoView.vue';
import NotfoundView from '@/views/notfound/NotfoundView.vue';
import RanklistView from '@/views/ranklist/RanklistView.vue';
import UserAccountLoginView from "@/views/user/account/UserAccountLoginView";
import UserAccountRegisterView from "@/views/user/account/UserAccountRegisterView";
import store from "@/store/index";

const routes = [
  {
    name: 'home',
    path: '/',
    redirect: '/battle/',
    meta: {
      requstAuth: true,
    },
  },
  {
    name: 'battle',
    path: '/battle/',
    component: BattleView,
    meta: {
      requstAuth: true,
    },
  },
  {
    name: 'gamelist',
    path: '/gamelist/',
    component: GamelistView,
    meta: {
      requstAuth: true,
    },
  },
  {
    name: 'myinfo',
    path: '/user/myinfo/',
    component: myinfoView,
    meta: {
      requstAuth: true,
    },
  },
  {
    name: 'user_account_login',
    path: '/user/account/login/',
    component: UserAccountLoginView,
    meta: {
      requstAuth: false,
    },
  },
  {
    name: 'user_account_register',
    path: '/user/account/register/',
    component: UserAccountRegisterView,
    meta: {
      requstAuth: false,
    },
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
    meta: {
      requstAuth: true,
    },
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

router.beforeEach((to, from, next) =>{
  if (!store.state.user.is_login && to.meta.requstAuth){
      next({name: 'user_account_login'});
  } else {
    next();
  }
})

export default router
