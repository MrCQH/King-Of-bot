<template>
    <ContentView>
        我的信息
    </ContentView>
</template>

<script>
import ContentView from '@/components/ContentView.vue';
import $ from "jquery";
import { useStore } from 'vuex';

export default{
    name: "myinfoView",
    components: { ContentView },
    setup(){
        const store = useStore();

        // $.ajax({
        //     url: "http://127.0.0.1:3000/user/bot/add/",
        //     type: "post",
        //     headers: {
        //         Authorization: "Bearer " + store.state.user.token,
        //     },
        //     data: {
                // title: "Bot的标题",
                // description: "Bot的简介",
                // content: "Bot的代码",
        //     },
        //     success(resp){
        //         console.log(resp);
        //     },
        // });
        // $.ajax({
        //     url: "http://127.0.0.1:3000/user/bot/remove/",
        //     type: "post",
        //     headers: {
        //         Authorization: "Bearer " + store.state.user.token,
        //     },
        //     data: {
        //         bot_id: 3,
        //     },
        //     success(resp){
        //         console.log(resp);
        //     },
        // });
        // $.ajax({
        //     url: "http://127.0.0.1:3000/user/bot/modify/",
        //     type: "post",
        //     headers: {
        //         Authorization: "Bearer " + store.state.user.token,
        //     },
        //     data: {
        //         bot_id: 8,
        //         title: "Bot的标题8",
        //         description: "Bot的简介8",
        //         content: "Bot的代码8",
        //     },
        //     success(resp){
        //         console.log(resp);
        //     },
        // });
        $.ajax({
            url: "http://127.0.0.1:3000/user/bot/getlist/",
            type: "get",
            headers: {
                Authorization: "Bearer " + store.state.user.token,
            },
            success(resp){
                console.log(resp);
            },
        });
    }
}
</script>

<style scoped>
</style>