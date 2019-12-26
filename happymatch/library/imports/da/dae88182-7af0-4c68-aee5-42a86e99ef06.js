"use strict";
cc._RF.push(module, 'dae88GCevBMaK7lQqhume8G', 'CellModel');
// Script/Model/CellModel.js

"use strict";

Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.default = undefined;

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _ConstValue = require("./ConstValue");

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

var CellModel = function () {
    function CellModel() {
        _classCallCheck(this, CellModel);

        this.type = null;
        this.status = _ConstValue.CELL_STATUS.COMMON;
        this.x = 1;
        this.y = 1;
        this.startX = 1;
        this.startY = 1;
        this.cmd = [];
        this.isDeath = false;
        this.objecCount = Math.floor(Math.random() * 1000);
    }

    _createClass(CellModel, [{
        key: "init",
        value: function init(type) {
            this.type = type;
        }
    }, {
        key: "isEmpty",
        value: function isEmpty() {
            return this.type == _ConstValue.CELL_TYPE.EMPTY;
        }
    }, {
        key: "setEmpty",
        value: function setEmpty() {
            this.type = _ConstValue.CELL_TYPE.EMPTY;
        }
    }, {
        key: "setXY",
        value: function setXY(x, y) {
            this.x = x;
            this.y = y;
        }
    }, {
        key: "setStartXY",
        value: function setStartXY(x, y) {
            this.startX = x;
            this.startY = y;
        }
    }, {
        key: "setStatus",
        value: function setStatus(status) {
            this.status = status;
        }
    }, {
        key: "moveToAndBack",
        value: function moveToAndBack(pos) {
            var srcPos = cc.v2(this.x, this.y);
            this.cmd.push({
                action: "moveTo",
                keepTime: _ConstValue.ANITIME.TOUCH_MOVE,
                playTime: 0,
                pos: pos
            });
            this.cmd.push({
                action: "moveTo",
                keepTime: _ConstValue.ANITIME.TOUCH_MOVE,
                playTime: _ConstValue.ANITIME.TOUCH_MOVE,
                pos: srcPos
            });
        }
    }, {
        key: "moveTo",
        value: function moveTo(pos, playTime) {
            var srcPos = cc.v2(this.x, this.y);
            this.cmd.push({
                action: "moveTo",
                keepTime: _ConstValue.ANITIME.TOUCH_MOVE,
                playTime: playTime,
                pos: pos
            });
            this.x = pos.x;
            this.y = pos.y;
        }
    }, {
        key: "toDie",
        value: function toDie(playTime) {
            this.cmd.push({
                action: "toDie",
                playTime: playTime,
                keepTime: _ConstValue.ANITIME.DIE
            });
            this.isDeath = true;
        }
    }, {
        key: "toShake",
        value: function toShake(playTime) {
            this.cmd.push({
                action: "toShake",
                playTime: playTime,
                keepTime: _ConstValue.ANITIME.DIE_SHAKE
            });
        }
    }, {
        key: "setVisible",
        value: function setVisible(playTime, isVisible) {
            this.cmd.push({
                action: "setVisible",
                playTime: playTime,
                keepTime: 0,
                isVisible: isVisible
            });
        }
    }, {
        key: "moveToAndDie",
        value: function moveToAndDie(pos) {}
    }, {
        key: "isBird",
        value: function isBird() {
            return this.type == _ConstValue.CELL_TYPE.G;
        }
    }]);

    return CellModel;
}();

exports.default = CellModel;
module.exports = exports["default"];

cc._RF.pop();