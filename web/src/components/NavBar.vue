<template>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <router-link class="navbar-brand" :to="{name: 'home'}">King of Kob</router-link>
            <div class="collapse navbar-collapse" id="navbarText">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item">
                        <router-link :class="route_name == 'battle' ? 'nav-link active' : 'nav-link'" aria-current="page" :to="{name: 'battle'}">对战</router-link>
                    </li>
                    <li class="nav-item">
                        <router-link :class="route_name == 'gamelist' ? 'nav-link active' : 'nav-link'" :to="{name: 'gamelist'}">对局列表</router-link>
                    </li>
                    <li class="nav-item">
                        <router-link :class="route_name == 'ranklist'? 'nav-link active' : 'nav-link'" :to="{name: 'ranklist'}">排行榜</router-link>
                    </li>
                </ul>
                <ul class="navbar-nav">
                    <ul class="navbar-nav">
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle" href="#" id="navbarDarkDropdownMenuLink" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                mrchen
                            </a>
                            <ul class="dropdown-menu dropdown-menu-dark" aria-labelledby="navbarDarkDropdownMenuLink">
                                <li><router-link class="dropdown-item" :to="{name: 'myinfo'}">我的信息</router-link></li>
                                <li><router-link class="dropdown-item" :to="{name: 'logout'}">退出</router-link></li>
                            </ul>
                        </li>
                    </ul>
                </ul>
            </div>
        </div>
    </nav>
</template>

<script>
import { useRoute } from 'vue-router';
import { computed } from 'vue';

export default{
    name: 'NavBar',
    setup(){
        const route = useRoute();
        let route_name = computed(()=> route.name);
        return {
            route_name,

        }
    }
}
</script>

<style scoped>
</style>