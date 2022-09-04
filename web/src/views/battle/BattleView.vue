<template>
    <PlayGround v-if="$store.state.battle.status === 'playing'" />
    <MatchGround v-if="$store.state.battle.status === 'matching'" />
    <ResultBoard v-if="$store.state.battle.loser !== 'none'"/>
</template>

<script>
import PlayGround from '@/components/PlayGround.vue'
import { onMounted, onUnmounted } from "vue"
import { useStore } from 'vuex';
import MatchGround from '@/components/MatchGround.vue';
import ResultBoard from '@/components/ResultBoard.vue';

export default{
    name: "BattleView",
    components: {
    PlayGround,
    MatchGround,
    ResultBoard,
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
                    }, 200);
                    store.commit("updateGame", data.game);
                } else if (data.event === "move"){
                    console.log(data);
                    const game = store.state.battle.gameObject;
                    let [snack0, snack1] = game.snacks;
                    snack0.set_direction(data.a_direction);
                    snack1.set_direction(data.b_direction);
                } else if (data.event === "result"){
                    console.log(data);
                    const game = store.state.battle.gameObject;
                    let [snack0, snack1] = game.snacks;
                    if (data.loser === "all" || data.loser === "a"){
                        snack0.status = 'die';
                    }
                    if (data.loser === "all" || data.loser === "b"){
                        snack1.status = 'die';
                    }
                    store.commit("updateLoser", data.loser);
                }
            }

            socket.onclose = () =>{
                console.log("disconnected!");
            }
        });

        onUnmounted(()=>{ // 在组件关闭时，执行
            socket.close();
            store.commit("updateStatus", "matching");
            store.commit("updateLoser", 'none');
        });
    }
}
</script>

<style scoped>
</style>