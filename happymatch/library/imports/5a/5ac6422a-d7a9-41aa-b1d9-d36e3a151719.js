"use strict";
cc._RF.push(module, '5ac64Iq16lBqrHZ0246FRcZ', 'GameController');
// Script/Controller/GameController.js

'use strict';

var _GameModel = require('../Model/GameModel');

var _GameModel2 = _interopRequireDefault(_GameModel);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

var global = require('../Global.js');

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
        grid: {
            default: null,
            type: cc.Node
        }
    },

    // use this for initialization
    onLoad: function onLoad() {
        console.log('type', global.type);
        this.gameModel = new _GameModel2.default();
        this.gameModel.init(4);
        var gridScript = this.grid.getComponent("GridView");
        gridScript.setController(this);
        gridScript.initWithCellModels(this.gameModel.getCells());
    },

    selectCell: function selectCell(pos) {
        return this.gameModel.selectCell(pos);
    },
    cleanCmd: function cleanCmd() {
        this.gameModel.cleanCmd();
    }

    // called every frame, uncomment this function to activate update callback
    // update: function (dt) {

    // }, 
});

cc._RF.pop();