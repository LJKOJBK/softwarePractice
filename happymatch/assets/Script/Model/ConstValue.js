/* 
    状态Status包括： 魔力鸟（同类消除）、区域消除、行消除、列消除
    种类是各种动物的样式
*/
export const CELL_TYPE = {
    EMPTY : 0,
    A : 1,      // 六种动物
    B : 2,
    C : 3,
    D : 4,
    E : 5,
    F : 6,
    BIRD : 7    // 魔力鸟
}
export const CELL_BASENUM = 6;
export const CELL_STATUS = {
    COMMON: 0 ,
    CLICK: "click",
    LINE: "line",
    COLUMN: "column",
    WRAP: "wrap",
    BIRD: "bird"
} 

export const GRID_WIDTH = 9;
export const GRID_HEIGHT = 9;

export const CELL_WIDTH = 70;
export const CELL_HEIGHT = 70;

export const GRID_PIXEL_WIDTH = GRID_WIDTH * CELL_WIDTH;
export const GRID_PIXEL_HEIGHT = GRID_HEIGHT * CELL_HEIGHT;


// ********************   时间表  animation time **************************
export const ANITIME = {
    TOUCH_MOVE: 0.3,
    DIE: 0.2,
    DOWN: 0.5,
    BOMB_DELAY: 0.3,
    BOMB_BIRD_DELAY: 0.7,
    DIE_SHAKE: 0.4 // 死前抖动
}
