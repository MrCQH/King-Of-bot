<template>
    <div class="matchground">
        <div class="row">
            <div class="col-6">
                <div class="user-photo">
                    <img :src="$store.state.user.photo" alt="">
                </div>
                <div class="user-username">
                    {{ $store.state.user.username }}
                </div>
            </div>
            <div class="col-6">
                <div class="user-photo">
                    <img :src="$store.state.battle.opponent_photo" alt="">
                </div>
                <div class="user-username">
                    {{$store.state.battle.opponent_username}}
                </div>
            </div>
            <div class="col-12" style="text-align: center; margin-top: 8vh;">
                <button @click="btn_info_update" type="button" class="btn btn-info btn-lg">{{btn_info}}</button>
            </div>
        </div>
    </div>
</template>

<script>
import {ref} from "vue";
import { useStore } from "vuex";

export default{
    setup(){
        const store = useStore();
        let btn_info = ref("开始匹配");
        const btn_info_update = ()=>{
            if (btn_info.value === "开始匹配"){
                btn_info.value = "取消";
                store.state.battle.socket.send(JSON.stringify({
                    event: "start-matching",
                }));
            } else {
                btn_info.value = "开始匹配";
                store.state.battle.socket.send(JSON.stringify({
                    event: "stop-matching",
                }));
            }
        }
        return {
            btn_info,
            btn_info_update
        }
    }
}

</script>

<style scoped>
.matchground{
    width: 70vw;
    height: 60vh;
    margin: 40px auto;
    background-color: rgba(230,230,250, 0.8);
}

.user-photo{
    text-align: center;
    padding-top: 8vh;
}

.user-photo > img{
    border-radius: 50%;
    width: 20vh;
}

.user-username{
    font-size: 24px;
    text-align: center;
    padding-top: 1vh;
}

/* 去掉btn的highlight */
.btn:focus { 
  outline: none;
  box-shadow: none;
}

</style>