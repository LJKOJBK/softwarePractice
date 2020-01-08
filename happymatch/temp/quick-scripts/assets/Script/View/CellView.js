(function() {"use strict";var __module = CC_EDITOR ? module : {exports:{}};var __filename = 'preview-scripts/assets/Script/View/CellView.js';var __require = CC_EDITOR ? function (request) {return cc.require(request, require);} : function (request) {return cc.require(request, __filename);};function __define (exports, require, module) {"use strict";
cc._RF.push(module, 'fbf19Cx4ptFV62UZ7+qJJpQ', 'CellView', __filename);
// Script/View/CellView.js

"use strict";

var _ConstValue = require("../Model/ConstValue");

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
        defaultFrame: {
            default: null,
            type: cc.SpriteFrame
        }
    },

    // use this for initialization
    onLoad: function onLoad() {
        //this.model = null;
        this.isSelect = false;
    },
    initWithModel: function initWithModel(model) {
        this.model = model;
        var x = model.startX;
        var y = model.startY;
        this.node.x = _ConstValue.CELL_WIDTH * (x - 0.5);
        this.node.y = _ConstValue.CELL_HEIGHT * (y - 0.5);
        var animation = this.node.getComponent(cc.Animation);
        if (model.status == _ConstValue.CELL_STATUS.COMMON) {
            animation.stop();
        } else {
            animation.play(model.status);
        }
    },
    // 执行移动动作
    updateView: function updateView() {
        var _this = this;

        var cmd = this.model.cmd;
        if (cmd.length <= 0) {
            return;
        }
        var actionArray = [];
        var curTime = 0;
        for (var i in cmd) {
            if (cmd[i].playTime > curTime) {
                var delay = cc.delayTime(cmd[i].playTime - curTime);
                actionArray.push(delay);
            }
            if (cmd[i].action == "moveTo") {
                var x = (cmd[i].pos.x - 0.5) * _ConstValue.CELL_WIDTH;
                var y = (cmd[i].pos.y - 0.5) * _ConstValue.CELL_HEIGHT;
                var move = cc.moveTo(_ConstValue.ANITIME.TOUCH_MOVE, cc.v2(x, y));
                actionArray.push(move);
            } else if (cmd[i].action == "toDie") {
                if (this.status == _ConstValue.CELL_STATUS.BIRD) {
                    var animation = this.node.getComponent(cc.Animation);
                    animation.play("effect");
                    actionArray.push(cc.delayTime(_ConstValue.ANITIME.BOMB_BIRD_DELAY));
                }
                var callFunc = cc.callFunc(function () {
                    this.node.destroy();
                }, this);
                actionArray.push(callFunc);
            } else if (cmd[i].action == "setVisible") {
                (function () {
                    var isVisible = cmd[i].isVisible;
                    actionArray.push(cc.callFunc(function () {
                        if (isVisible) {
                            this.node.opacity = 255;
                        } else {
                            this.node.opacity = 0;
                        }
                    }, _this));
                })();
            } else if (cmd[i].action == "toShake") {
                var rotateRight = cc.rotateBy(0.06, 30);
                var rotateLeft = cc.rotateBy(0.12, -60);
                actionArray.push(cc.repeat(cc.sequence(rotateRight, rotateLeft, rotateRight), 2));
            }
            curTime = cmd[i].playTime + cmd[i].keepTime;
        }

        if (actionArray.length == 1) {
            this.node.runAction(actionArray[0]);
        } else {
            var _cc;

            this.node.runAction((_cc = cc).sequence.apply(_cc, actionArray));
        }
    },
    // called every frame, uncomment this function to activate update callback
    // update: function (dt) {

    // },
    setSelect: function setSelect(flag) {
        var animation = this.node.getComponent(cc.Animation);
        var bg = this.node.getChildByName("select");
        if (flag == false && this.isSelect && this.model.status == _ConstValue.CELL_STATUS.COMMON) {
            animation.stop();
            this.node.getComponent(cc.Sprite).spriteFrame = this.defaultFrame;
        } else if (flag && this.model.status == _ConstValue.CELL_STATUS.COMMON) {
            animation.play(_ConstValue.CELL_STATUS.CLICK);
        } else if (flag && this.model.status == _ConstValue.CELL_STATUS.BIRD) {
            animation.play(_ConstValue.CELL_STATUS.CLICK);
        }
        bg.active = flag;
        this.isSelect = flag;
    }
});

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
        //# sourceMappingURL=CellView.js.map
        