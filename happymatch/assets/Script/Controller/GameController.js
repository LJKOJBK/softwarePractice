import GameModel from "../Model/GameModel";
let global = require('../Global.js');

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
        grid:{
            default: null,
            type: cc.Node
        },
        timeLabel: {
            default: null,
            type: cc.Label
        },
        mask: {
            default: null,
            type: cc.Node
        }
    },

    // use this for initialization
    onLoad: function () {
        this.gameover = false
        this.handleTimingModel()  // 处理与计时模式有关的模式
        this.gameModel = new GameModel();
        this.gameModel.init(4);
        var gridScript = this.grid.getComponent("GridView");
        gridScript.setController(this);
        gridScript.initWithCellModels(this.gameModel.getCells());
    },

    selectCell: function(pos){
        return this.gameModel.selectCell(pos);
    },

    cleanCmd: function(){
        this.gameModel.cleanCmd();
    },

    handleTimingModel: function() {
        this.isTimingModel = (global.type == 1)
        if(!this.isTimingModel) return 
        
        this.timeLast = 60
        this.timeLabel.node.active = true
    },

    // called every frame, uncomment this function to activate update callback
    update: function (dt) {
        if(this.isTimingModel && !this.gameover) {
            this.timeLast -= dt
            this.timeLabel.string = Math.ceil(this.timeLast)
            if(this.timeLast <= 0) {
                this.gameover = true
                this.mask.active = true
            }
        }
    }, 
});
