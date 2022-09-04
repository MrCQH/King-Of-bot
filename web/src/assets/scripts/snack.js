import { GameObject } from "./GameObject";
import { Cell } from "./Cell";

export class Snack extends GameObject{
    constructor(info, gamemap){
        super();

        this.gamemap = gamemap;
        this.color = info.color;
        this.id = info.id;

        this.cells = [new Cell(info.r, info.c)];
        this.next_cell = null; //下一步的目标位置

        this.speed = 5; // 蛇每秒走5个格子
        this.direction = -1; // -1表示没有方向 0,1,2,3表示上右下左
        this.dr = [-1, 0, 1, 0];
        this.dc = [0, 1, 0, -1];
        this.status = "idle"; // idle表示静止，move表示移动，die表示死亡
        this.step = 0;
        this.eps = 1e-2;
        this.eye_direction = 0;
        if (this.id === 1) this.eye_direction = 2;

        this.eye_dx = [ // 两个眼镜在不同方向x的偏移量
            [-1, 1],
            [1, 1],
            [1, -1],
            [-1, -1],
        ]
        this.eye_dy = [  // 两个眼镜在不同方向y的偏移量
            [-1, -1],
            [-1, 1],
            [1, 1],
            [1, -1],
        ]
    }

    start(){

    }

    check_tail_increasing(){
        if (this.step <= 10) return true;
        if (this.step % 3 === 1) return true;
        return false;
    }


    set_direction(d){
        this.direction = d;
    }

    next_step(){ //进入下一步的状态
        let d = this.direction;
        this.next_cell = new Cell(this.cells[0].r + this.dr[d], this.cells[0].c + this.dc[d]);
        this.direction = -1;
        this.eye_direction = d;
        this.status = "move";
        this.step ++;

        const k = this.cells.length;
        for (let i = k; i > 0; i --){
            this.cells[i] = JSON.parse(JSON.stringify(this.cells[i - 1]));
        }
    }

    update_move(){
        const dx = this.next_cell.x - this.cells[0].x;
        const dy = this.next_cell.y - this.cells[0].y;
        const distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < this.eps){
            this.cells[0] = this.next_cell;
            this.next_cell = null;
            this.status = "idle";
            
            if (!this.check_tail_increasing()){
                this.cells.pop();
            }

        } else {
            const move_distance = this.speed * this.timedelta / 1000;
            this.cells[0].x += move_distance * dx / distance;
            this.cells[0].y += move_distance * dy / distance;

            if (!this.check_tail_increasing()){
                const k = this.cells.length;
                const update_cell = this.cells[k - 1], target_cell = this.cells[k - 2];
                const dx = target_cell.x - update_cell.x;
                const dy = target_cell.y - update_cell.y;
                update_cell.x += move_distance * dx / distance;
                update_cell.y += move_distance * dy / distance;
            }
        }
    }

    update(){
        if (this.status === 'move'){
            this.update_move();
        }
        this.render();
    }

    render(){
        const L = this.gamemap.L;
        const ctx = this.gamemap.ctx;
        ctx.fillStyle = this.color;

        if (this.status === "die"){
            ctx.fillStyle = "white";
        }

        for (const cell of this.cells){
            ctx.beginPath();
            ctx.arc(cell.x * L, cell.y * L, L / 2 * 0.8, 0, 2 * Math.PI);
            ctx.fill();
        }

        for (let i = 1; i < this.cells.length; i ++){
            const a = this.cells[i - 1], b = this.cells[i];
            if (Math.abs(a.x - b.x) < this.eps && Math.abs(a.y - b.y) < this.eps)
                continue;
            if (Math.abs(a.x - b.x) < this.eps){ // 竖直方向
                ctx.fillRect((a.x - 0.4) * L, Math.min(a.y, b.y) * L, L * 0.8, Math.abs(a.y - b.y) * L);
            } else { //水平方向
                ctx.fillRect(Math.min(a.x, b.x) * L, (a.y - 0.4) * L, Math.abs(a.x - b.x) * L, L * 0.8);
            }
        }

        ctx.fillStyle = 'black';
        for (let i = 0; i < 2; i ++){
            const eye_x = (this.cells[0].x + this.eye_dx[this.eye_direction][i] * 0.15) * L;
            const eye_y = (this.cells[0].y + this.eye_dy[this.eye_direction][i] * 0.15) * L;
            ctx.beginPath();
            ctx.arc(eye_x, eye_y, L * 0.05, 0, Math.PI * 2);
            ctx.fill();
        }

    }
}