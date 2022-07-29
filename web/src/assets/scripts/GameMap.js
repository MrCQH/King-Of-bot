import { GameObject } from "./GameObject";
import { Wall } from "./Wall";
import { Snack } from './snack';

export class GameMap extends GameObject{
    constructor(ctx, parent){
        super();

        this.ctx = ctx,
        this.parent = parent; // 根据父元素动态变化的map大小
        this.L = 0; // 一个单位的长度

        this.rows = 13;
        this.cols = 14;

        this.inner_walls_count = 20;
        this.walls = [];

        this.snacks = [
            new Snack({id: 0, color: "#4b76e5", r: this.rows - 2, c: 1}, this),
            new Snack({id: 1, color: "#ea544a", r: 1, c: this.cols - 2}, this),
        ];
    }

    check_valid(cell){
        for (const wall of this.walls){
            if (wall.r === cell.r && wall.c === cell.c) return false;
        }

        for (const snack of this.snacks){
            let k = snack.cells.length;
            if (snack.check_tail_increasing()){
                k --;
            }
            for (let i = 0; i < k; i ++){
                if (cell.r === snack.cells[i].r && cell.c === snack.cells[i].c) return false;
            }
        }
        return true;
    }

    check_connectivity(g, sx, sy, tx, ty){
        if(sx == tx && sy == ty) return true;
        g[sx][sy] = true;
        let dx = [-1, 0, 1, 0], dy = [0, 1, 0, -1];
        for (let i = 0; i < 4; i ++){
            let x = sx + dx[i], y = sy + dy[i];
            if (!g[x][y] && this.check_connectivity(g, x, y, tx, ty))
                return true;
        }
        return false;
    }

    create_walls(){
        const g = [];
        for (let r  = 0; r < this.rows; r ++){
            g[r] = [];
            for (let c = 0; c < this.cols; c ++){
                g[r][c] = false;
            }
        }

        // 确定给左右两边画墙
        for (let r = 0; r < this.rows; r ++){
            g[r][0] = g[r][this.cols - 1] = true;
        }

        // 确定给上下两边画墙
        for (let c = 0; c < this.cols; c ++){
            g[0][c] = g[this.rows - 1][c] = true;
        }

        for (let i = 0; i < this.inner_walls_count / 2; i ++){
            for (let j = 0; j < 1000; j ++){
                let r = parseInt(Math.random() * this.rows);
                let c = parseInt(Math.random() * this.cols);
                if (g[r][c] || g[this.rows - 1 - r][this.cols - 1 - c]) continue;
                if (r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2) continue;
                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = true;
                break;
            }
        }

        const copy_g = JSON.parse(JSON.stringify(g));
        if (!this.check_connectivity(copy_g, this.rows - 2, 1, 1, this.cols - 2)) return false;

        // 画墙
        for (let r = 0; r < this.rows; r ++){
            for (let c = 0; c < this.cols; c++){
                if (g[r][c]){
                    this.walls.push(new Wall(r, c, this));
                }
            }
        }

        return true;
    }

    add_listening_events(){
        this.ctx.canvas.focus();

        const [snack0, snack1] = this.snacks;
        this.ctx.canvas.addEventListener("keydown", e =>{
            if (e.key === 'w') snack0.set_direction(0);
            else if (e.key === 'd') snack0.set_direction(1);
            else if (e.key === 's') snack0.set_direction(2);
            else if (e.key === 'a') snack0.set_direction(3);
            else if (e.key === 'ArrowUp') snack1.set_direction(0);
            else if (e.key === 'ArrowRight') snack1.set_direction(1);
            else if (e.key === 'ArrowDown') snack1.set_direction(2);
            else if (e.key === 'ArrowLeft') snack1.set_direction(3);
        });
    }

    start(){
        for (let i = 0; i < 1000; i ++)
        if (this.create_walls())
            break;
        this.add_listening_events();
    }

    update_size(){
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows)); // 墙之间存在浮点数问题,有缝
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }

    check_ready(){
        for (const snack of this.snacks){
            if (snack.direction === -1) return false;
            if (snack.status !== "idle") return false;
        }
        return true;
    }

    next_step(){
        for (const snack of this.snacks){
            snack.next_step();
        }
    }

    update(){
        this.update_size();
        if (this.check_ready()){
            this.next_step();    
        }
        this.render();
    }

    render(){
        const color_even = "#b2d45f", color_odd = "#a9cd56";
        for (let r = 0; r < this.rows; r ++){
            for (let c = 0; c < this.cols; c ++){
                if ((r + c) % 2 == 0){
                    this.ctx.fillStyle = color_even;
                } else {
                    this.ctx.fillStyle = color_odd;
                }
                this.ctx.fillRect(c * this.L, r * this.L, this.L, this.L);
            }
        }
    }
}