<template>
  <NavBar />
  <router-view></router-view>
</template>

<script>
import 'bootstrap/dist/css/bootstrap.css';
import 'bootstrap/dist/js/bootstrap.js';
import NavBar from '@/components/NavBar.vue';

export default {
    name: "App",
    components: {
      NavBar
    },
    setup(){
    }
}

</script>

<style>
body{
  background-image: url("assets/images/background.png");
  background-size: cover;
}
</style>
