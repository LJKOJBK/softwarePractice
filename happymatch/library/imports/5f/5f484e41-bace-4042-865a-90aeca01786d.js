"use strict";
cc._RF.push(module, '5f4845Bus5AQoZakK7KAXht', 'LoginController');
// Script/Controller/LoginController.js

"use strict";

var _AudioUtils = require("../Utils/AudioUtils");

var _AudioUtils2 = _interopRequireDefault(_AudioUtils);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

var global = require('../Global.js'); // Learn cc.Class:
//  - [Chinese] http://docs.cocos.com/creator/manual/zh/scripting/class.html
//  - [English] http://www.cocos2d-x.org/docs/creator/en/scripting/class.html
// Learn Attribute:
//  - [Chinese] http://docs.cocos.com/creator/manual/zh/scripting/reference/attributes.html
//  - [English] http://www.cocos2d-x.org/docs/creator/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:
//  - [Chinese] http://docs.cocos.com/creator/manual/zh/scripting/life-cycle-callbacks.html
//  - [English] http://www.cocos2d-x.org/docs/creator/en/scripting/life-cycle-callbacks.html

cc.Class({
    extends: cc.Component,

    properties: {
        // foo: {
        //     // ATTRIBUTES:
        //     default: null,        // The default value will be used only when the component attaching
        //                           // to a node for the first time
        //     type: cc.SpriteFrame, // optional, default is typeof default
        //     serializable: true,   // optional, default is true
        // },
        // bar: {
        //     get () {
        //         return this._bar;
        //     },
        //     set (value) {
        //         this._bar = value;
        //     }
        // },
        loadingBar: {
            type: cc.ProgressBar,
            default: null
        },
        // loginButton: {
        //     type: cc.Button,
        //     default: null,
        // },
        loginBtn1: {
            type: cc.Button,
            default: null
        },
        loginBtn2: {
            type: cc.Button,
            default: null
        },
        worldSceneBGM: {
            type: cc.AudioClip,
            default: null
        }
    },

    // LIFE-CYCLE CALLBACKS:

    onLoad: function onLoad() {
        this.gameSceneBGMAudioId = cc.audioEngine.play(this.worldSceneBGM, true, 1);
    },
    start: function start() {},


    // 游戏全局入口，点击登录按钮调用
    // 状态0表示休闲模式，1表示计时模式
    onLogin: function onLogin(e, type) {
        this.loadingBar.node.active = true;
        this.loginBtn1.node.active = this.loginBtn2.node.active = false;
        global.type = type;
        this.loadingBar.progress = 0;
        var backup = cc.loader.onProgress;
        cc.loader.onProgress = function (count, amount) {
            this.loadingBar.progress = count / amount;
        }.bind(this);

        // 预加载游戏场景
        cc.director.preloadScene("Game", function () {
            cc.loader.onProgress = backup;
            this.loadingBar.node.active = false;
            this.loginBtn1.node.active = this.loginBtn2.node.active = true;
            cc.director.loadScene("Game");
        }.bind(this));
    },

    onDestroy: function onDestroy() {
        cc.audioEngine.stop(this.gameSceneBGMAudioId);
    }

    // update (dt) {},
});

cc._RF.pop();