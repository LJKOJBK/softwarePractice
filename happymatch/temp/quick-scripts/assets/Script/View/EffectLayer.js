(function() {"use strict";var __module = CC_EDITOR ? module : {exports:{}};var __filename = 'preview-scripts/assets/Script/View/EffectLayer.js';var __require = CC_EDITOR ? function (request) {return cc.require(request, require);} : function (request) {return cc.require(request, __filename);};function __define (exports, require, module) {"use strict";
cc._RF.push(module, '0e925myn0dIjqdao1TpipF9', 'EffectLayer', __filename);
// Script/View/EffectLayer.js

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
        bombWhite: {
            default: null,
            type: cc.Prefab
        },
        crushEffect: {
            default: null,
            type: cc.Prefab
        },
        audioUtils: {
            type: _AudioUtils2.default,
            default: null
        }
    },

    // use this for initialization
    onLoad: function onLoad() {},
    playEffects: function playEffects(effectQueue) {
        if (!effectQueue || effectQueue.length <= 0) {
            return;
        }
        var soundMap = {}; //某一时刻，某一种声音是否播放过的标记，防止重复播放
        effectQueue.forEach(function (cmd) {
            var delayTime = cc.delayTime(cmd.playTime);
            var callFunc = cc.callFunc(function () {
                var instantEffect = null;
                var animation = null;
                if (cmd.action == "crush") {
                    instantEffect = cc.instantiate(this.crushEffect);
                    animation = instantEffect.getComponent(cc.Animation);
                    animation.play("effect");
                    !soundMap["crush" + cmd.playTime] && this.audioUtils.playEliminate(cmd.step);
                    soundMap["crush" + cmd.playTime] = true;
                } else if (cmd.action == "rowBomb") {
                    instantEffect = cc.instantiate(this.bombWhite);
                    animation = instantEffect.getComponent(cc.Animation);
                    animation.play("effect_line");
                } else if (cmd.action == "colBomb") {
                    instantEffect = cc.instantiate(this.bombWhite);
                    animation = instantEffect.getComponent(cc.Animation);
                    animation.play("effect_col");
                }

                instantEffect.x = _ConstValue.CELL_WIDTH * (cmd.pos.x - 0.5);
                instantEffect.y = _ConstValue.CELL_WIDTH * (cmd.pos.y - 0.5);
                instantEffect.parent = this.node;
                animation.on("finished", function () {
                    instantEffect.destroy();
                }, this);
            }, this);
            this.node.runAction(cc.sequence(delayTime, callFunc));
        }, this);
    }

    // called every frame, uncomment this function to activate update callback
    // update: function (dt) {

    // },
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
        //# sourceMappingURL=EffectLayer.js.map
        