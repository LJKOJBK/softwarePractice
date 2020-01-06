"use strict";
cc._RF.push(module, 'd0d1fDj9rlDx5QUtP+2toQV', 'GridView');
// Script/View/GridView.js

"use strict";

var _ConstValue = require("../Model/ConstValue");

var _AudioUtils = require("../Utils/AudioUtils");

var _AudioUtils2 = _interopRequireDefault(_AudioUtils);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

cc.Class({
    extends: cc.Component,

    properties: {
        // foo: {
        //    default: null,      // The default value will be used only when the component attaching
        //                           to a node for the first time
        //    url: cc.Texture2D,  // optional, default is typeof default
        //    serializable: true, // optional, default is true
        //    visible: true,      // optional, default is true
        //    displayName: 'Foo', // optional
        //    readonly: false,    // optional, default is false
        // },
        // ...

        // 记录所有预设动物元素
        aniPre: {
            default: [],
            type: [cc.Prefab]
        },
        effectLayer: {
            default: null,
            type: cc.Node
        },
        audioUtils: {
            type: _AudioUtils2.default,
            default: null
        }

    },

    // use this for initialization
    onLoad: function onLoad() {
        this.setListener(); // 所有事件监听
        this.lastTouchPos = cc.Vec2(-1, -1);
        this.isCanMove = true;
        this.isInPlayAni = false; // 是否在播放中
    },
    setController: function setController(controller) {
        this.controller = controller;
    },

    initWithCellModels: function initWithCellModels(cellsModels) {
        this.cellViews = [];
        for (var i = 1; i <= 9; i++) {
            this.cellViews[i] = [];
            for (var j = 1; j <= 9; j++) {
                var type = cellsModels[i][j].type;
                var aniView = cc.instantiate(this.aniPre[type]);
                aniView.parent = this.node;
                var cellViewScript = aniView.getComponent("CellView");
                cellViewScript.initWithModel(cellsModels[i][j]);
                this.cellViews[i][j] = aniView;
            }
        }
    },
    setListener: function setListener() {
        // 添加点击事件
        this.node.on(cc.Node.EventType.TOUCH_START, function (e) {
            if (this.isInPlayAni) {
                //播放动画中，不允许点击
                return true;
            }
            // 获取点击位置，通过点击位置推出点击元素坐标
            var cellPos = this.convertTouchPosToCell(e.getLocation());
            cellPos && this.selectCell(cellPos);
            // 点击处有实际格子则可以移动
            this.isCanMove = !!cellPos;
            return true;
        }, this);
        // 滑动操作逻辑
        this.node.on(cc.Node.EventType.TOUCH_MOVE, function (e) {
            if (this.isCanMove) {
                var startCellPos = this.convertTouchPosToCell(e.getStartLocation());
                var cellPos = this.convertTouchPosToCell(e.getLocation());
                if (startCellPos.x != cellPos.x || startCellPos.y != cellPos.y) {
                    this.isCanMove = false;
                    this.selectCell(cellPos);
                }
            }
        }, this);
    },
    // 根据点击的像素位置，转换成网格中的位置(数学中的二维坐标轴)
    convertTouchPosToCell: function convertTouchPosToCell(pos) {
        pos = this.node.convertToNodeSpace(pos);
        if (pos.x < 0 || pos.x >= _ConstValue.GRID_PIXEL_WIDTH || pos.y < 0 || pos.y >= _ConstValue.GRID_PIXEL_HEIGHT) {
            return false;
        }
        var x = Math.floor(pos.x / _ConstValue.CELL_WIDTH) + 1;
        var y = Math.floor(pos.y / _ConstValue.CELL_HEIGHT) + 1;
        return cc.v2(x, y);
    },
    // 移动格子
    updateView: function updateView(changeModels) {
        var newCellViewInfo = [];
        for (var i in changeModels) {
            var model = changeModels[i];
            var viewInfo = this.findViewByModel(model);
            var view = null;
            // 如果原来的cell不存在，则新建
            if (!viewInfo) {
                var type = model.type;
                var aniView = cc.instantiate(this.aniPre[type]);
                aniView.parent = this.node;
                var cellViewScript = aniView.getComponent("CellView");
                cellViewScript.initWithModel(model);
                view = aniView;
            }
            // 如果已经存在
            else {
                    view = viewInfo.view;
                    this.cellViews[viewInfo.y][viewInfo.x] = null;
                }
            var cellScript = view.getComponent("CellView");
            cellScript.updateView(); // 执行移动动作
            if (!model.isDeath) {
                newCellViewInfo.push({
                    model: model,
                    view: view
                });
            }
        }
        // 重新标记this.cellviews的信息
        newCellViewInfo.forEach(function (ele) {
            var model = ele.model;
            this.cellViews[model.y][model.x] = ele.view;
        }, this);
    },
    // 显示选中的格子背景
    updateSelect: function updateSelect(pos) {
        for (var i = 1; i <= 9; i++) {
            for (var j = 1; j <= 9; j++) {
                if (this.cellViews[i][j]) {
                    var cellScript = this.cellViews[i][j].getComponent("CellView");
                    if (pos.x == j && pos.y == i) {
                        cellScript.setSelect(true);
                    } else {
                        cellScript.setSelect(false);
                    }
                }
            }
        }
    },
    /**
     * 根据cell的model返回对应的view
     */
    findViewByModel: function findViewByModel(model) {
        for (var i = 1; i <= 9; i++) {
            for (var j = 1; j <= 9; j++) {
                if (this.cellViews[i][j] && this.cellViews[i][j].getComponent("CellView").model == model) {
                    return { view: this.cellViews[i][j], x: j, y: i };
                }
            }
        }
        return null;
    },
    getPlayAniTime: function getPlayAniTime(changeModels) {
        if (!changeModels) {
            return 0;
        }
        var maxTime = 0;
        changeModels.forEach(function (ele) {
            ele.cmd.forEach(function (cmd) {
                if (maxTime < cmd.playTime + cmd.keepTime) {
                    maxTime = cmd.playTime + cmd.keepTime;
                }
            }, this);
        }, this);
        return maxTime;
    },
    // 获得爆炸次数， 同一个时间算一个
    getStep: function getStep(effectsQueue) {
        if (!effectsQueue) {
            return 0;
        }
        return effectsQueue.reduce(function (maxValue, efffectCmd) {
            return Math.max(maxValue, efffectCmd.step || 0);
        }, 0);
    },
    //一段时间内禁止操作
    disableTouch: function disableTouch(time, step) {
        if (time <= 0) {
            return;
        }
        this.isInPlayAni = true;
        this.node.runAction(cc.sequence(cc.delayTime(time), cc.callFunc(function () {
            this.isInPlayAni = false;
            this.audioUtils.playContinuousMatch(step);
        }, this)));
    },
    // 正常击中格子后的操作
    selectCell: function selectCell(cellPos) {
        var result = this.controller.selectCell(cellPos); // 直接先丢给model处理数据逻辑
        var changeModels = result[0]; // 有改变的cell，包含新生成的cell和生成马上摧毁的格子
        var effectsQueue = result[1]; //各种特效
        this.playEffect(effectsQueue);
        this.disableTouch(this.getPlayAniTime(changeModels), this.getStep(effectsQueue));
        this.updateView(changeModels);
        this.controller.cleanCmd();
        if (changeModels.length >= 2) {
            this.updateSelect(cc.v2(-1, -1));
            this.audioUtils.playSwap();
        } else {
            this.updateSelect(cellPos);
            this.audioUtils.playClick();
        }
        return changeModels;
    },
    playEffect: function playEffect(effectsQueue) {
        this.effectLayer.getComponent("EffectLayer").playEffects(effectsQueue);
    }

    // called every frame, uncomment this function to activate update callback
    // update: function (dt) {

    // },
});

cc._RF.pop();