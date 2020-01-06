(function() {"use strict";var __module = CC_EDITOR ? module : {exports:{}};var __filename = 'preview-scripts/assets/Script/Model/ConstValue.js';var __require = CC_EDITOR ? function (request) {return cc.require(request, require);} : function (request) {return cc.require(request, __filename);};function __define (exports, require, module) {"use strict";
cc._RF.push(module, 'f9088esGbNBtJmNaJsz0Gq4', 'ConstValue', __filename);
// Script/Model/ConstValue.js

"use strict";

Object.defineProperty(exports, "__esModule", {
    value: true
});
/* 
    状态Status包括： 魔力鸟（同类消除）、区域消除、行消除、列消除
    种类是各种动物的样式
*/
var CELL_TYPE = exports.CELL_TYPE = {
    EMPTY: 0,
    A: 1, // 六种动物
    B: 2,
    C: 3,
    D: 4,
    E: 5,
    F: 6,
    BIRD: 7 // 魔力鸟
};
var CELL_BASENUM = exports.CELL_BASENUM = 6;
var CELL_STATUS = exports.CELL_STATUS = {
    COMMON: 0,
    CLICK: "click",
    LINE: "line",
    COLUMN: "column",
    WRAP: "wrap",
    BIRD: "bird"
};

var GRID_WIDTH = exports.GRID_WIDTH = 9;
var GRID_HEIGHT = exports.GRID_HEIGHT = 9;

var CELL_WIDTH = exports.CELL_WIDTH = 70;
var CELL_HEIGHT = exports.CELL_HEIGHT = 70;

var GRID_PIXEL_WIDTH = exports.GRID_PIXEL_WIDTH = GRID_WIDTH * CELL_WIDTH;
var GRID_PIXEL_HEIGHT = exports.GRID_PIXEL_HEIGHT = GRID_HEIGHT * CELL_HEIGHT;

// ********************   时间表  animation time **************************
var ANITIME = exports.ANITIME = {
    TOUCH_MOVE: 0.3,
    DIE: 0.2,
    DOWN: 0.5,
    BOMB_DELAY: 0.3,
    BOMB_BIRD_DELAY: 0.7,
    DIE_SHAKE: 0.4 // 死前抖动
};

cc._RF.pop();
        }
        if (CC_EDITOR) {
            __define(__module.exports, __require, __module);
        }
        else {
            cc.registerModuleFunc(__filename, function () {
                __define(__module.exports, __require, __module);
            });
        }
        })();
        //# sourceMappingURL=ConstValue.js.map
        