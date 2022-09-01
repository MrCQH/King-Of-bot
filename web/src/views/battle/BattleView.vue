<template>
    <PlayGround v-if="$store.state.battle.status === 'playing'" />
    <MatchGround v-if="$store.state.battle.status === 'matching'" />
</template>

<script>
import PlayGround from '@/components/PlayGround.vue'
import { onMounted, onUnmounted } from "vue"
import { useStore } from 'vuex';
import MatchGround from '../../components/MatchGround.vue';

export default{
    name: "BattleView",
    components: {
    PlayGround,
    MatchGround
},
    setup(){
        const store = useStore();

        let socket = null;
        let socketUrl = `ws://127.0.0.1:3000/websocket/${store.state.user.token}`;

        onMounted(() => { // 在组建加载时，执行

            store.commit("updateOpponent", {
                username: "我的对手",
                photo: "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
            });

            socket = new WebSocket(socketUrl);

            socket.onopen = () =>{
                console.log("connected!");
                store.commit("updateSocket", socket);
            }

            socket.onmessage = msg =>{
                const data = JSON.parse(msg.data);
                if (data.event === "start-matching"){ // 匹配成功
                    store.commit("updateOpponent",{
                        username: data.opponent_username,
                        photo: data.opponent_photo,
                    });
                    setTimeout(()=>{
                        store.commit("updateStatus", "playing");
                    }, 2000);
                    store.commit("updateGamemap", data.gamemap);
                }
            }

            socket.onclose = () =>{
                console.log("disconnected!");
            }
        });

        onUnmounted(()=>{ // 在组建完毕时，执行
            socket.close();
            store.commit("updateStatus", "matching");
        });
    }
}
</script>

<style scoped>
</style>