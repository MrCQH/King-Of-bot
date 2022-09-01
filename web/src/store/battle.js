const ModuleBattle = {
    state: {
        socket: null,
        opponent_photo: "",
        opponent_username: "",
        status: "matching", // matching表示正在匹配, playing表示匹配完成
        gamemap: null,
    },
    getters: {
    },
    mutations: {
        updateSocket(state, socket){
            state.socket = socket;
        },
        updateOpponent(state, opponent){
            state.opponent_photo = opponent.photo;
            state.opponent_username = opponent.username;
        },
        updateStatus(state, status){
            state.status = status;
        },
        updateGamemap(state, gamemap){
            state.gamemap = gamemap;
        }
    },
    actions: {
    },
    modules: {
    }
}

export default ModuleBattle;