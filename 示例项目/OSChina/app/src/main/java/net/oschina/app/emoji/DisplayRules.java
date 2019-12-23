/*
 * Copyright (c) 2015, 张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.oschina.app.emoji;

import net.oschina.app.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Emoji在手机上的显示规则
 *
 * @author kymjs (http://www.kymjs.com)
 */
public enum DisplayRules {
    // 注意：value不能从0开始，因为0会被库自动设置为删除按钮
    // int type, int value, int resId, String cls
    KJEMOJI0(0, 1, R.mipmap.smiley_0, "[微笑]", "[0]"),
    KJEMOJI1(0, 1, R.mipmap.smiley_1, "[撇嘴]", "[1]"),
    KJEMOJI2(0, 1, R.mipmap.smiley_2, "[色]", "[2]"),
    KJEMOJI3(0, 1, R.mipmap.smiley_3, "[发呆]", "[3]"),
    KJEMOJI4(0, 1, R.mipmap.smiley_4, "[得意]", "[4]"),
    KJEMOJI5(0, 1, R.mipmap.smiley_5, "[流泪]", "[5]"),
    KJEMOJI6(0, 1, R.mipmap.smiley_6, "[害羞]", "[6]"),
    KJEMOJI7(0, 1, R.mipmap.smiley_7, "[闭嘴]", "[7]"),
    KJEMOJI8(0, 1, R.mipmap.smiley_8, "[睡]", "[8]"),
    KJEMOJI9(0, 1, R.mipmap.smiley_9, "[大哭]", "[9]"),
    KJEMOJI10(0, 1, R.mipmap.smiley_10, "[尴尬]", "[10]"),
    KJEMOJI11(0, 1, R.mipmap.smiley_11, "[发怒]", "[11]"),
    KJEMOJI12(0, 1, R.mipmap.smiley_12, "[调皮]", "[12]"),
    KJEMOJI13(0, 1, R.mipmap.smiley_13, "[呲牙]", "[13]"),
    KJEMOJI14(0, 1, R.mipmap.smiley_14, "[惊讶]", "[14]"),
    KJEMOJI15(0, 1, R.mipmap.smiley_15, "[难过]", "[15]"),
    KJEMOJI16(0, 1, R.mipmap.smiley_16, "[酷]", "[16]"),
    KJEMOJI17(0, 1, R.mipmap.smiley_17, "[冷汗]", "[17]"),
    KJEMOJI18(0, 1, R.mipmap.smiley_18, "[抓狂]", "[18]"),
    KJEMOJI19(0, 1, R.mipmap.smiley_19, "[吐]", "[19]"),
    KJEMOJI20(0, 1, R.mipmap.smiley_20, "[偷笑]", "[20]"),
    KJEMOJI21(0, 1, R.mipmap.smiley_21, "[可爱]", "[21]"),
    KJEMOJI22(0, 1, R.mipmap.smiley_22, "[白眼]", "[22]"),
    KJEMOJI23(0, 1, R.mipmap.smiley_23, "[傲慢]", "[23]"),
    KJEMOJI24(0, 1, R.mipmap.smiley_24, "[饥饿]", "[24]"),
    KJEMOJI25(0, 1, R.mipmap.smiley_25, "[困]", "[25]"),
    KJEMOJI26(0, 1, R.mipmap.smiley_26, "[惊恐]", "[26]"),
    KJEMOJI27(0, 1, R.mipmap.smiley_27, "[流汗]", "[27]"),
    KJEMOJI28(0, 1, R.mipmap.smiley_28, "[憨笑]", "[28]"),
    KJEMOJI29(0, 1, R.mipmap.smiley_29, "[大兵]", "[29]"),
    KJEMOJI30(0, 1, R.mipmap.smiley_30, "[奋斗]", "[30]"),
    KJEMOJI31(0, 1, R.mipmap.smiley_31, "[咒骂]", "[31]"),
    KJEMOJI32(0, 1, R.mipmap.smiley_32, "[疑问]", "[32]"),
    KJEMOJI33(0, 1, R.mipmap.smiley_33, "[嘘]", "[33]"),
    KJEMOJI34(0, 1, R.mipmap.smiley_34, "[晕]", "[34]"),
    KJEMOJI35(0, 1, R.mipmap.smiley_35, "[折磨]", "[35]"),
    KJEMOJI36(0, 1, R.mipmap.smiley_36, "[衰]", "[36]"),
    KJEMOJI37(0, 1, R.mipmap.smiley_37, "[骷髅]", "[37]"),
    KJEMOJI38(0, 1, R.mipmap.smiley_38, "[敲打]", "[38]"),
    KJEMOJI39(0, 1, R.mipmap.smiley_39, "[再见]", "[39]"),
    KJEMOJI40(0, 1, R.mipmap.smiley_40, "[擦汗]", "[40]"),
    KJEMOJI41(0, 1, R.mipmap.smiley_41, "[抠鼻]", "[41]"),
    KJEMOJI42(0, 1, R.mipmap.smiley_42, "[鼓掌]", "[42]"),
    KJEMOJI43(0, 1, R.mipmap.smiley_43, "[糗大了]", "[43]"),
    KJEMOJI44(0, 1, R.mipmap.smiley_44, "[坏笑]", "[44]"),
    KJEMOJI45(0, 1, R.mipmap.smiley_45, "[左哼哼]", "[45]"),
    KJEMOJI46(0, 1, R.mipmap.smiley_46, "[右哼哼]", "[46]"),
    KJEMOJI47(0, 1, R.mipmap.smiley_47, "[哈欠]", "[47]"),
    KJEMOJI48(0, 1, R.mipmap.smiley_48, "[鄙视]", "[48]"),
    KJEMOJI49(0, 1, R.mipmap.smiley_49, "[委屈]", "[49]"),
    KJEMOJI50(0, 1, R.mipmap.smiley_50, "[快哭了]", "[50]"),
    KJEMOJI51(0, 1, R.mipmap.smiley_51, "[阴险]", "[51]"),
    KJEMOJI52(0, 1, R.mipmap.smiley_52, "[亲亲]", "[52]"),
    KJEMOJI53(0, 1, R.mipmap.smiley_53, "[吓]", "[53]"),
    KJEMOJI54(0, 1, R.mipmap.smiley_54, "[可怜]", "[54]"),
    KJEMOJI55(0, 1, R.mipmap.smiley_55, "[菜刀]", "[55]"),
    KJEMOJI56(0, 1, R.mipmap.smiley_56, "[西瓜]", "[56]"),
    KJEMOJI57(0, 1, R.mipmap.smiley_57, "[啤酒]", "[57]"),
    KJEMOJI58(0, 1, R.mipmap.smiley_58, "[篮球]", "[58]"),
    KJEMOJI59(0, 1, R.mipmap.smiley_59, "[乒乓]", "[59]"),
    KJEMOJI60(0, 1, R.mipmap.smiley_60, "[咖啡]", "[60]"),
    KJEMOJI61(0, 1, R.mipmap.smiley_61, "[饭]", "[61]"),
    KJEMOJI62(0, 1, R.mipmap.smiley_62, "[猪头]", "[62]"),
    KJEMOJI63(0, 1, R.mipmap.smiley_63, "[玫瑰]", "[63]"),
    KJEMOJI64(0, 1, R.mipmap.smiley_64, "[凋谢]", "[64]"),
    KJEMOJI65(0, 1, R.mipmap.smiley_65, "[嘴唇]", "[65]"),
    KJEMOJI66(0, 1, R.mipmap.smiley_66, "[爱心]", "[66]"),
    KJEMOJI67(0, 1, R.mipmap.smiley_67, "[心碎]", "[67]"),
    KJEMOJI68(0, 1, R.mipmap.smiley_68, "[蛋糕]", "[68]"),
    KJEMOJI69(0, 1, R.mipmap.smiley_69, "[闪电]", "[69]"),
    KJEMOJI70(0, 1, R.mipmap.smiley_70, "[炸弹]", "[70]"),
    KJEMOJI71(0, 1, R.mipmap.smiley_71, "[刀]", "[71]"),
    KJEMOJI72(0, 1, R.mipmap.smiley_72, "[足球]", "[72]"),
    KJEMOJI73(0, 1, R.mipmap.smiley_73, "[瓢虫]", "[73]"),
    KJEMOJI74(0, 1, R.mipmap.smiley_74, "[便便]", "[74]"),
    KJEMOJI75(0, 1, R.mipmap.smiley_75, "[月亮]", "[75]"),
    KJEMOJI76(0, 1, R.mipmap.smiley_76, "[太阳]", "[76]"),
    KJEMOJI77(0, 1, R.mipmap.smiley_77, "[礼物]", "[77]"),
    KJEMOJI78(0, 1, R.mipmap.smiley_78, "[拥抱]", "[78]"),
    KJEMOJI79(0, 1, R.mipmap.smiley_79, "[强]", "[79]"),
    KJEMOJI80(0, 1, R.mipmap.smiley_80, "[弱]", "[80]"),
    KJEMOJI81(0, 1, R.mipmap.smiley_81, "[握手]", "[81]"),
    KJEMOJI82(0, 1, R.mipmap.smiley_82, "[胜利]", "[82]"),
    KJEMOJI83(0, 1, R.mipmap.smiley_83, "[抱拳]", "[83]"),
    KJEMOJI84(0, 1, R.mipmap.smiley_84, "[勾引]", "[84]"),
    KJEMOJI85(0, 1, R.mipmap.smiley_85, "[拳头]", "[85]"),
    KJEMOJI86(0, 1, R.mipmap.smiley_86, "[差劲]", "[86]"),
    KJEMOJI87(0, 1, R.mipmap.smiley_87, "[爱你]", "[87]"),
    KJEMOJI88(0, 1, R.mipmap.smiley_88, "[NO]", "[88]"),
    KJEMOJI89(0, 1, R.mipmap.smiley_89, "[OK]", "[89]"),
    KJEMOJI90(0, 1, R.mipmap.smiley_90, "[爱情]", "[90]"),
    KJEMOJI91(0, 1, R.mipmap.smiley_91, "[飞吻]", "[91]"),
    KJEMOJI92(0, 1, R.mipmap.smiley_92, "[跳跳]", "[92]"),
    KJEMOJI93(0, 1, R.mipmap.smiley_93, "[发抖]", "[93]"),
    KJEMOJI94(0, 1, R.mipmap.smiley_94, "[怄火]", "[94]"),
    KJEMOJI95(0, 1, R.mipmap.smiley_95, "[转圈]", "[95]"),
    KJEMOJI96(0, 1, R.mipmap.smiley_96, "[磕头]", "[96]"),
    KJEMOJI97(0, 1, R.mipmap.smiley_97, "[回头]", "[97]"),
    KJEMOJI98(0, 1, R.mipmap.smiley_98, "[跳绳]", "[98]"),
    KJEMOJI99(0, 1, R.mipmap.smiley_99, "[投降]", "[99]"),
    KJEMOJI100(0, 1, R.mipmap.smiley_100, "[激动]", "[100]"),
    KJEMOJI101(0, 1, R.mipmap.smiley_101, "[乱舞]", "[101]"),
    KJEMOJI102(0, 1, R.mipmap.smiley_102, "[献吻]", "[102]"),
    KJEMOJI103(0, 1, R.mipmap.smiley_103, "[左太极]", "[103]"),
    KJEMOJI104(0, 1, R.mipmap.smiley_104, "[右太极]", "[104]"),

    GITHUB0(1, 1, R.mipmap.bowtie, ":bowtie:", ":bowtie:"),
    GITHUB1(1, 1, R.mipmap.smile, ":smile:", ":smile:"),
    GITHUB2(1, 1, R.mipmap.laughing, ":laughing:", ":laughing:"),
    GITHUB3(1, 1, R.mipmap.blush, ":blush:", ":blush:"),
    GITHUB4(1, 1, R.mipmap.smiley, ":smiley:", ":smiley:"),
    GITHUB5(1, 1, R.mipmap.relaxed, ":relaxed:", ":relaxed:"),
    GITHUB6(1, 1, R.mipmap.smirk, ":smirk:", ":smirk:"),
    GITHUB7(1, 1, R.mipmap.heart_eyes, ":heart_eyes:", ":heart_eyes:"),
    GITHUB8(1, 1, R.mipmap.kissing_heart, ":kissing_heart:", ":kissing_heart:"),
    GITHUB9(1, 1, R.mipmap.kissing_closed_eyes, ":kissing_closed_eyes:", ":kissing_closed_eyes:"),
    GITHUB10(1, 1, R.mipmap.flushed, ":flushed:", ":flushed:"),
    GITHUB11(1, 1, R.mipmap.relieved, ":relieved:", ":relieved:"),
    GITHUB12(1, 1, R.mipmap.satisfied, ":satisfied:", ":satisfied:"),
    GITHUB13(1, 1, R.mipmap.grin, ":grin:", ":grin:"),
    GITHUB14(1, 1, R.mipmap.wink, ":wink:", ":wink:"),
    GITHUB15(1, 1, R.mipmap.stuck_out_tongue_winking_eye, ":stuck_out_tongue_winking_eye:", ":stuck_out_tongue_winking_eye:"),
    GITHUB16(1, 1, R.mipmap.stuck_out_tongue_closed_eyes, ":stuck_out_tongue_closed_eyes:", ":stuck_out_tongue_closed_eyes:"),
    GITHUB17(1, 1, R.mipmap.grinning, ":grinning:", ":grinning:"),
    GITHUB18(1, 1, R.mipmap.kissing, ":kissing:", ":kissing:"),
    GITHUB19(1, 1, R.mipmap.kissing_smiling_eyes, ":kissing_smiling_eyes:", ":kissing_smiling_eyes:"),
    GITHUB20(1, 1, R.mipmap.stuck_out_tongue, ":stuck_out_tongue:", ":stuck_out_tongue:"),
    GITHUB21(1, 1, R.mipmap.sleeping, ":sleeping:", ":sleeping:"),
    GITHUB22(1, 1, R.mipmap.worried, ":worried:", ":worried:"),
    GITHUB23(1, 1, R.mipmap.frowning, ":frowning:", ":frowning:"),
    GITHUB24(1, 1, R.mipmap.anguished, ":anguished:", ":anguished:"),
    GITHUB25(1, 1, R.mipmap.open_mouth, ":open_mouth:", ":open_mouth:"),
    GITHUB26(1, 1, R.mipmap.grimacing, ":grimacing:", ":grimacing:"),
    GITHUB27(1, 1, R.mipmap.confused, ":confused:", ":confused:"),
    GITHUB28(1, 1, R.mipmap.hushed, ":hushed:", ":hushed:"),
    GITHUB29(1, 1, R.mipmap.expressionless, ":expressionless:", ":expressionless:"),
    GITHUB30(1, 1, R.mipmap.unamused, ":unamused:", ":unamused:"),
    GITHUB31(1, 1, R.mipmap.sweat_smile, ":sweat_smile:", ":sweat_smile:"),
    GITHUB32(1, 1, R.mipmap.sweat, ":sweat:", ":sweat:"),
    GITHUB33(1, 1, R.mipmap.disappointed_relieved, ":disappointed_relieved:", ":disappointed_relieved:"),
    GITHUB34(1, 1, R.mipmap.weary, ":weary:", ":weary:"),
    GITHUB35(1, 1, R.mipmap.pensive, ":pensive:", ":pensive:"),
    GITHUB36(1, 1, R.mipmap.disappointed, ":disappointed:", ":disappointed:"),
    GITHUB37(1, 1, R.mipmap.confounded, ":confounded:", ":confounded:"),
    GITHUB38(1, 1, R.mipmap.fearful, ":fearful:", ":fearful:"),
    GITHUB39(1, 1, R.mipmap.cold_sweat, ":cold_sweat:", ":cold_sweat:"),
    GITHUB40(1, 1, R.mipmap.persevere, ":persevere:", ":persevere:"),
    GITHUB41(1, 1, R.mipmap.cry, ":cry:", ":cry:"),
    GITHUB42(1, 1, R.mipmap.sob, ":sob:", ":sob:"),
    GITHUB43(1, 1, R.mipmap.joy, ":joy:", ":joy:"),
    GITHUB44(1, 1, R.mipmap.astonished, ":astonished:", ":astonished:"),
    GITHUB45(1, 1, R.mipmap.scream, ":scream:", ":scream:"),
    GITHUB46(1, 1, R.mipmap.neckbeard, ":neckbeard:", ":neckbeard:"),
    GITHUB47(1, 1, R.mipmap.tired_face, ":tired_face:", ":tired_face:"),
    GITHUB48(1, 1, R.mipmap.angry, ":angry:", ":angry:"),
    GITHUB49(1, 1, R.mipmap.rage, ":rage:", ":rage:"),
    GITHUB50(1, 1, R.mipmap.triumph, ":triumph:", ":triumph:"),
    GITHUB51(1, 1, R.mipmap.sleepy, ":sleepy:", ":sleepy:"),
    GITHUB52(1, 1, R.mipmap.yum, ":yum:", ":yum:"),
    GITHUB53(1, 1, R.mipmap.mask, ":mask:", ":mask:"),
    GITHUB54(1, 1, R.mipmap.sunglasses, ":sunglasses:", ":sunglasses:"),
    GITHUB55(1, 1, R.mipmap.dizzy_face, ":dizzy_face:", ":dizzy_face:"),
    GITHUB56(1, 1, R.mipmap.imp, ":imp:", ":imp:"),
    GITHUB57(1, 1, R.mipmap.smiling_imp, ":smiling_imp:", ":smiling_imp:"),
    GITHUB58(1, 1, R.mipmap.neutral_face, ":neutral_face:", ":neutral_face:"),
    GITHUB59(1, 1, R.mipmap.no_mouth, ":no_mouth:", ":no_mouth:"),
    GITHUB60(1, 1, R.mipmap.innocent, ":innocent:", ":innocent:"),
    GITHUB61(1, 1, R.mipmap.alien, ":alien:", ":alien:"),
    GITHUB62(1, 1, R.mipmap.yellow_heart, ":yellow_heart:", ":yellow_heart:"),
    GITHUB63(1, 1, R.mipmap.blue_heart, ":blue_heart:", ":blue_heart:"),
    GITHUB64(1, 1, R.mipmap.purple_heart, ":purple_heart:", ":purple_hert:"),
    GITHUB65(1, 1, R.mipmap.heart, ":heart:", ":heart:"),
    GITHUB66(1, 1, R.mipmap.green_heart, ":green_heart:", ":green_heart:"),
    GITHUB67(1, 1, R.mipmap.broken_heart, ":broken_heart:", ":broken_heart:"),
    GITHUB68(1, 1, R.mipmap.heartbeat, ":heartbeat:", ":heartbeat:"),
    GITHUB69(1, 1, R.mipmap.heartpulse, ":heartpulse:", ":heartpulse:"),
    GITHUB70(1, 1, R.mipmap.two_hearts, ":two_hearts:", ":two_hearts:"),
    GITHUB71(1, 1, R.mipmap.revolving_hearts, ":revolving_hearts:", ":revolving_hearts:"),
    GITHUB72(1, 1, R.mipmap.cupid, ":cupid:", ":cupid:"),
    GITHUB73(1, 1, R.mipmap.sparkling_heart, ":sparkling_heart:", ":sparkling_heart:"),
    GITHUB74(1, 1, R.mipmap.sparkles, ":sparkles:", ":sparkles:"),
    GITHUB75(1, 1, R.mipmap.star, ":star:", ":star:"),
    GITHUB76(1, 1, R.mipmap.star2, ":star2:", ":star2:"),
    GITHUB77(1, 1, R.mipmap.dizzy, ":dizzy:", ":dizzy:"),
    GITHUB78(1, 1, R.mipmap.boom, ":boom:", ":boom:"),
    GITHUB79(1, 1, R.mipmap.collision, ":collision:", ":collision:"),
    GITHUB80(1, 1, R.mipmap.anger, ":anger:", ":anger:"),
    GITHUB81(1, 1, R.mipmap.exclamation, ":exclamation:", ":exclamation:"),
    GITHUB82(1, 1, R.mipmap.question, ":question:", ":question:"),
    GITHUB83(1, 1, R.mipmap.grey_exclamation, ":grey_exclamation:", ":grey_exclamation:"),
    GITHUB84(1, 1, R.mipmap.grey_question, ":grey_question:", ":grey_question:"),
    GITHUB85(1, 1, R.mipmap.zzz, ":zzz:", ":zzz:"),
    GITHUB86(1, 1, R.mipmap.dash, ":dash:", ":dash:"),
    GITHUB87(1, 1, R.mipmap.sweat_drops, ":sweat_drops:", ":sweat_drops:"),
    GITHUB88(1, 1, R.mipmap.notes, ":notes:", ":notes:"),
    GITHUB89(1, 1, R.mipmap.musical_note, ":musical_note:", ":musical_note:"),
    GITHUB90(1, 1, R.mipmap.fire, ":fire:", ":fire:"),
    GITHUB91(1, 1, R.mipmap.hankey, ":hankey:", ":hankey:"),
    GITHUB92(1, 1, R.mipmap.poop, ":poop:", ":poop:"),
    GITHUB93(1, 1, R.mipmap.shit, ":shit:", ":shit:"),
    GITHUB94(1, 1, R.mipmap.thumbsup, ":+1:", ":+1:"),
    GITHUB95(1, 1, R.mipmap.thumbsup, ":thumbsup:", ":thumbsup:"),
    GITHUB96(1, 1, R.mipmap.the_1, ":-1:", ":-1:"),
    GITHUB97(1, 1, R.mipmap.thumbsdown, ":thumbsdown:", ":thumbsdown:"),
    GITHUB98(1, 1, R.mipmap.ok_hand, ":ok_hand:", ":ok_hand:"),
    GITHUB99(1, 1, R.mipmap.punch, ":punch:", ":punch:"),
    GITHUB100(1, 1, R.mipmap.facepunch, ":facepunch:", ":facepunch:"),
    GITHUB101(1, 1, R.mipmap.fist, ":fist:", ":fist:"),
    GITHUB102(1, 1, R.mipmap.v, ":v:", ":v:"),
    GITHUB103(1, 1, R.mipmap.wave, ":wave:", ":wave:"),
    GITHUB104(1, 1, R.mipmap.hand, ":hand:", ":hand:"),
    GITHUB105(1, 1, R.mipmap.raised_hand, ":raised_hand:", ":raised_hand:"),
    GITHUB106(1, 1, R.mipmap.open_hands, ":open_hands:", ":open_hands:"),
    GITHUB107(1, 1, R.mipmap.point_up, ":point_up:", ":point_up:"),
    GITHUB108(1, 1, R.mipmap.point_down, ":point_down:", ":point_down:"),
    GITHUB109(1, 1, R.mipmap.point_left, ":point_left:", ":point_left:"),
    GITHUB110(1, 1, R.mipmap.point_right, ":point_right:", ":point_right:"),
    GITHUB111(1, 1, R.mipmap.raised_hands, ":raised_hands:", ":raised_hands:"),
    GITHUB112(1, 1, R.mipmap.pray, ":pray:", ":pray:"),
    GITHUB113(1, 1, R.mipmap.point_up_2, ":point_up_2:", ":point_up_2:"),
    GITHUB114(1, 1, R.mipmap.clap, ":clap:", ":clap:"),
    GITHUB115(1, 1, R.mipmap.muscle, ":muscle:", ":muscle:"),
    GITHUB116(1, 1, R.mipmap.metal, ":metal:", ":metal:"),
    GITHUB117(1, 1, R.mipmap.fu, ":fu:", ":fu:"),
    GITHUB118(1, 1, R.mipmap.walking, ":walking:", ":walking:"),
    GITHUB119(1, 1, R.mipmap.runner, ":runner:", ":runner:"),
    GITHUB120(1, 1, R.mipmap.running, ":running:", ":running:"),
    GITHUB121(1, 1, R.mipmap.couple, ":couple:", ":couple:"),
    GITHUB122(1, 1, R.mipmap.family, ":family:", ":family:"),
    GITHUB123(1, 1, R.mipmap.two_men_holding_hands, ":two_men_holding_hands:", ":two_men_holding_hands:"),
    GITHUB124(1, 1, R.mipmap.two_women_holding_hands, ":two_women_holding_hands:", ":two_women_holding_hands:"),
    GITHUB125(1, 1, R.mipmap.dancer, ":dancer:", ":dancer:"),
    GITHUB126(1, 1, R.mipmap.dancers, ":dancers:", ":dancers:"),
    GITHUB127(1, 1, R.mipmap.ok_woman, ":ok_woman:", ":ok_woman:"),
    GITHUB128(1, 1, R.mipmap.no_good, ":no_good:", ":no_good:"),
    GITHUB129(1, 1, R.mipmap.information_desk_person, ":information_desk_person:", ":information_desk_person:"),
    GITHUB130(1, 1, R.mipmap.raising_hand, ":raising_hand:", ":raising_hand:"),
    GITHUB131(1, 1, R.mipmap.bride_with_veil, ":bride_with_veil:", ":bride_with_veil:"),
    GITHUB132(1, 1, R.mipmap.person_with_pouting_face, ":person_with_pouting_face:", ":person_with_pouting_face:"),
    GITHUB133(1, 1, R.mipmap.person_frowning, ":person_frowning:", ":person_frowning:"),
    GITHUB134(1, 1, R.mipmap.bow, ":bow:", ":bow:"),
    GITHUB135(1, 1, R.mipmap.couplekiss, ":couplekiss:", ":couplekiss:"),
    GITHUB136(1, 1, R.mipmap.couple_with_heart, ":couple_with_heart:", ":couple_with_heart:"),
    GITHUB137(1, 1, R.mipmap.massage, ":massage:", ":massage:"),
    GITHUB138(1, 1, R.mipmap.haircut, ":haircut:", ":haircut:"),
    GITHUB139(1, 1, R.mipmap.nail_care, ":nail_care:", ":nail_care:"),
    GITHUB140(1, 1, R.mipmap.boy, ":boy:", ":boy:"),
    GITHUB141(1, 1, R.mipmap.girl, ":girl:", ":girl:"),
    GITHUB142(1, 1, R.mipmap.woman, ":woman:", ":woman:"),
    GITHUB143(1, 1, R.mipmap.man, ":man:", ":man:"),
    GITHUB144(1, 1, R.mipmap.baby, ":baby:", ":baby:"),
    GITHUB145(1, 1, R.mipmap.older_woman, ":older_woman:", ":older_woman:"),
    GITHUB146(1, 1, R.mipmap.older_man, ":older_man:", ":older_man:"),
    GITHUB147(1, 1, R.mipmap.person_with_blond_hair, ":person_with_blond_hair:", ":person_with_blond_hair:"),
    GITHUB148(1, 1, R.mipmap.man_with_gua_pi_mao, ":man_with_gua_pi_mao:", ":man_with_gua_pi_mao:"),
    GITHUB149(1, 1, R.mipmap.man_with_turban, ":man_with_turban:", ":man_with_turban:"),
    GITHUB150(1, 1, R.mipmap.construction_worker, ":construction_worker:", ":construction_worker:"),
    GITHUB151(1, 1, R.mipmap.cop, ":cop:", ":cop:"),
    GITHUB152(1, 1, R.mipmap.angel, ":angel:", ":angel:"),
    GITHUB153(1, 1, R.mipmap.princess, ":princess:", ":princess:"),
    GITHUB154(1, 1, R.mipmap.smiley_cat, ":smiley_cat:", ":smiley_cat:"),
    GITHUB155(1, 1, R.mipmap.smile_cat, ":smile_cat:", ":smile_cat:"),
    GITHUB156(1, 1, R.mipmap.heart_eyes_cat, ":heart_eyes_cat:", ":heart_eyes_cat:"),
    GITHUB157(1, 1, R.mipmap.kissing_cat, ":kissing_cat:", ":kissing_cat:"),
    GITHUB158(1, 1, R.mipmap.smirk_cat, ":smirk_cat:", ":smirk_cat:"),
    GITHUB159(1, 1, R.mipmap.scream_cat, ":scream_cat:", ":scream_cat:"),
    GITHUB160(1, 1, R.mipmap.crying_cat_face, ":crying_cat_face:", ":crying_cat_face:"),
    GITHUB161(1, 1, R.mipmap.joy_cat, ":joy_cat:", ":joy_cat:"),
    GITHUB162(1, 1, R.mipmap.pouting_cat, ":pouting_cat:", ":pouting_cat:"),
    GITHUB163(1, 1, R.mipmap.japanese_ogre, ":japanese_ogre:", ":japanese_ogre:"),
    GITHUB164(1, 1, R.mipmap.japanese_goblin, ":japanese_goblin:", ":japanese_goblin:"),
    GITHUB165(1, 1, R.mipmap.see_no_evil, ":see_no_evil:", ":see_no_evil:"),
    GITHUB166(1, 1, R.mipmap.hear_no_evil, ":hear_no_evil:", ":hear_no_evil:"),
    GITHUB167(1, 1, R.mipmap.speak_no_evil, ":speak_no_evil:", ":speak_no_evil:"),
    GITHUB168(1, 1, R.mipmap.guardsman, ":guardsman:", ":guardsman:"),
    GITHUB169(1, 1, R.mipmap.skull, ":skull:", ":skull:"),
    GITHUB170(1, 1, R.mipmap.feet, ":feet:", ":feet:"),
    GITHUB171(1, 1, R.mipmap.lips, ":lips:", ":lips:"),
    GITHUB172(1, 1, R.mipmap.kiss, ":kiss:", ":kiss:"),
    GITHUB173(1, 1, R.mipmap.droplet, ":droplet:", ":droplet:"),
    GITHUB174(1, 1, R.mipmap.ear, ":ear:", ":ear:"),
    GITHUB175(1, 1, R.mipmap.eyes, ":eyes:", ":eyes:"),
    GITHUB176(1, 1, R.mipmap.nose, ":nose:", ":nose:"),
    GITHUB177(1, 1, R.mipmap.tongue, ":tongue:", ":tongue:"),
    GITHUB178(1, 1, R.mipmap.love_letter, ":love_letter:", ":love_letter:"),
    GITHUB179(1, 1, R.mipmap.bust_in_silhouette, ":bust_in_silhouette:", ":bust_in_silhouette:"),
    GITHUB180(1, 1, R.mipmap.busts_in_silhouette, ":busts_in_silhouette:", ":busts_in_silhouette:"),
    GITHUB181(1, 1, R.mipmap.speech_balloon, ":speech_balloon:", ":speech_balloon:"),
    GITHUB182(1, 1, R.mipmap.thought_balloon, ":thought_balloon:", ":thought_balloon:"),
    GITHUB183(1, 1, R.mipmap.feelsgood, ":feelsgood:", ":feelsgood:"),
    GITHUB184(1, 1, R.mipmap.finnadie, ":finnadie:", ":finnadie:"),
    GITHUB185(1, 1, R.mipmap.goberserk, ":goberserk:", ":goberserk:"),
    GITHUB186(1, 1, R.mipmap.godmode, ":godmode:", ":godmode:"),
    GITHUB187(1, 1, R.mipmap.hurtrealbad, ":hurtrealbad:", ":hurtrealbad:"),
    GITHUB188(1, 1, R.mipmap.rage1, ":rage1:", ":rage1:"),
    GITHUB189(1, 1, R.mipmap.rage2, ":rage2:", ":rage2:"),
    GITHUB190(1, 1, R.mipmap.rage3, ":rage3:", ":rage3:"),
    GITHUB191(1, 1, R.mipmap.rage4, ":rage4:", ":rage4:"),
    GITHUB192(1, 1, R.mipmap.suspect, ":suspect:", ":suspect:"),
    GITHUB193(1, 1, R.mipmap.trollface, ":trollface:", ":trollface:"),

    Nature0(2, 1, R.mipmap.sunny, ":sunny:", ":sunny:"),
    Nature1(2, 1, R.mipmap.umbrella, ":umbrella:", ":umbrella:"),
    Nature2(2, 1, R.mipmap.cloud, ":cloud:", ":cloud:"),
    Nature3(2, 1, R.mipmap.snowflake, ":snowflake:", ":snowflake:"),
    Nature4(2, 1, R.mipmap.snowman, ":snowman:", ":snowman:"),
    Nature5(2, 1, R.mipmap.zap, ":zap:", ":zap:"),
    Nature6(2, 1, R.mipmap.cyclone, ":cyclone:", ":cyclone:"),
    Nature7(2, 1, R.mipmap.foggy, ":foggy:", ":foggy:"),
    Nature8(2, 1, R.mipmap.ocean, ":ocean:", ":ocean:"),
    Nature9(2, 1, R.mipmap.cat, ":cat:", ":cat:"),
    Nature10(2, 1, R.mipmap.dog, ":dog:", ":dog:"),
    Nature11(2, 1, R.mipmap.mouse, ":mouse:", ":mouse:"),
    Nature12(2, 1, R.mipmap.hamster, ":hamster:", ":hamster:"),
    Nature13(2, 1, R.mipmap.rabbit, ":rabbit:", ":rabbit:"),
    Nature14(2, 1, R.mipmap.wolf, ":wolf:", ":wolf:"),
    Nature15(2, 1, R.mipmap.frog, ":frog:", ":frog:"),
    Nature16(2, 1, R.mipmap.tiger, ":tiger:", ":tiger:"),
    Nature17(2, 1, R.mipmap.koala, ":koala:", ":koala:"),
    Nature18(2, 1, R.mipmap.bear, ":bear:", ":bear:"),
    Nature19(2, 1, R.mipmap.pig, ":pig:", ":pig:"),
    Nature20(2, 1, R.mipmap.pig_nose, ":pig_nose:", ":pig_nose:"),
    Nature21(2, 1, R.mipmap.cow, ":cow:", ":cow:"),
    Nature22(2, 1, R.mipmap.boar, ":boar:", ":boar:"),
    Nature23(2, 1, R.mipmap.monkey_face, ":monkey_face:", ":monkey_face:"),
    Nature24(2, 1, R.mipmap.monkey, ":monkey:", ":monkey:"),
    Nature25(2, 1, R.mipmap.horse, ":horse:", ":horse:"),
    Nature26(2, 1, R.mipmap.racehorse, ":racehorse:", ":racehorse:"),
    Nature27(2, 1, R.mipmap.camel, ":camel:", ":camel:"),
    Nature28(2, 1, R.mipmap.sheep, ":sheep:", ":sheep:"),
    Nature29(2, 1, R.mipmap.elephant, ":elephant:", ":elephant:"),
    Nature30(2, 1, R.mipmap.panda_face, ":panda_face:", ":panda_face:"),
    Nature31(2, 1, R.mipmap.snake, ":snake:", ":snake:"),
    Nature32(2, 1, R.mipmap.bird, ":bird:", ":bird:"),
    Nature33(2, 1, R.mipmap.baby_chick, ":baby_chick:", ":baby_chick:"),
    Nature34(2, 1, R.mipmap.hatched_chick, ":hatched_chick:", ":hatched_chick:"),
    Nature35(2, 1, R.mipmap.hatching_chick, ":hatching_chick:", ":hatching_chick:"),
    Nature36(2, 1, R.mipmap.chicken, ":chicken:", ":chicken:"),
    Nature37(2, 1, R.mipmap.penguin, ":penguin:", ":penguin:"),
    Nature38(2, 1, R.mipmap.turtle, ":turtle:", ":turtle:"),
    Nature39(2, 1, R.mipmap.bug, ":bug:", ":bug:"),
    Nature40(2, 1, R.mipmap.honeybee, ":honeybee:", ":honeybee:"),
    Nature41(2, 1, R.mipmap.ant, ":ant:", ":ant:"),
    Nature42(2, 1, R.mipmap.beetle, ":beetle:", ":beetle:"),
    Nature43(2, 1, R.mipmap.snail, ":snail:", ":snail:"),
    Nature44(2, 1, R.mipmap.octopus, ":octopus:", ":octopus:"),
    Nature45(2, 1, R.mipmap.tropical_fish, ":tropical_fish:", ":tropical_fish:"),
    Nature46(2, 1, R.mipmap.fish, ":fish:", ":fish:"),
    Nature47(2, 1, R.mipmap.whale, ":whale:", ":whale:"),
    Nature48(2, 1, R.mipmap.whale2, ":whale2:", ":whale2:"),
    Nature49(2, 1, R.mipmap.dolphin, ":dolphin:", ":dolphin:"),
    Nature50(2, 1, R.mipmap.cow2, ":cow2:", ":cow2:"),
    Nature51(2, 1, R.mipmap.ram, ":ram:", ":ram:"),
    Nature52(2, 1, R.mipmap.rat, ":rat:", ":rat:"),
    Nature53(2, 1, R.mipmap.water_buffalo, ":water_buffalo:", ":water_buffalo:"),
    Nature54(2, 1, R.mipmap.tiger2, ":tiger2:", ":tiger2:"),
    Nature55(2, 1, R.mipmap.rabbit2, ":rabbit2:", ":rabbit2:"),
    Nature56(2, 1, R.mipmap.dragon, ":dragon:", ":dragon:"),
    Nature57(2, 1, R.mipmap.goat, ":goat:", ":goat:"),
    Nature58(2, 1, R.mipmap.rooster, ":rooster:", ":rooster:"),
    Nature59(2, 1, R.mipmap.dog2, ":dog2:", ":dog2:"),
    Nature60(2, 1, R.mipmap.pig2, ":pig2:", ":pig2:"),
    Nature61(2, 1, R.mipmap.mouse2, ":mouse2:", ":mouse2:"),
    Nature62(2, 1, R.mipmap.ox, ":ox:", ":ox:"),
    Nature63(2, 1, R.mipmap.dragon_face, ":dragon_face:", ":dragon_face:"),
    Nature64(2, 1, R.mipmap.blowfish, ":blowfish:", ":blowfish:"),
    Nature65(2, 1, R.mipmap.crocodile, ":crocodile:", ":crocodile:"),
    Nature66(2, 1, R.mipmap.dromedary_camel, ":dromedary_camel:", ":dromedary_camel:"),
    Nature67(2, 1, R.mipmap.leopard, ":leopard:", ":leopard:"),
    Nature68(2, 1, R.mipmap.cat2, ":cat2:", ":cat2:"),
    Nature69(2, 1, R.mipmap.poodle, ":poodle:", ":poodle:"),
    Nature70(2, 1, R.mipmap.paw_prints, ":paw_prints:", ":paw_prints:"),
    Nature71(2, 1, R.mipmap.bouquet, ":bouquet:", ":bouquet:"),
    Nature72(2, 1, R.mipmap.cherry_blossom, ":cherry_blossom:", ":cherry_blossom:"),
    Nature73(2, 1, R.mipmap.tulip, ":tulip:", ":tulip:"),
    Nature74(2, 1, R.mipmap.four_leaf_clover, ":four_leaf_clover:", ":four_leaf_clover:"),
    Nature75(2, 1, R.mipmap.rose, ":rose:", ":rose:"),
    Nature76(2, 1, R.mipmap.sunflower, ":sunflower:", ":sunflower:"),
    Nature77(2, 1, R.mipmap.hibiscus, ":hibiscus:", ":hibiscus:"),
    Nature78(2, 1, R.mipmap.maple_leaf, ":maple_leaf:", ":maple_leaf:"),
    Nature79(2, 1, R.mipmap.leaves, ":leaves:", ":leaves:"),
    Nature80(2, 1, R.mipmap.fallen_leaf, ":fallen_leaf:", ":fallen_leaf:"),
    Nature81(2, 1, R.mipmap.herb, ":herb:", ":herb:"),
    Nature82(2, 1, R.mipmap.mushroom, ":mushroom:", ":mushroom:"),
    Nature83(2, 1, R.mipmap.cactus, ":cactus:", ":cactus:"),
    Nature84(2, 1, R.mipmap.palm_tree, ":palm_tree:", ":palm_tree:"),
    Nature85(2, 1, R.mipmap.evergreen_tree, ":evergreen_tree:", ":evergreen_tree:"),
    Nature86(2, 1, R.mipmap.deciduous_tree, ":deciduous_tree:", ":deciduous_tree:"),
    Nature87(2, 1, R.mipmap.chestnut, ":chestnut:", ":chestnut:"),
    Nature88(2, 1, R.mipmap.seedling, ":seedling:", ":seedling:"),
    Nature89(2, 1, R.mipmap.blossom, ":blossom:", ":blossom:"),
    Nature90(2, 1, R.mipmap.ear_of_rice, ":ear_of_rice:", ":ear_of_rice:"),
    Nature91(2, 1, R.mipmap.shell, ":shell:", ":shell:"),
    Nature92(2, 1, R.mipmap.globe_with_meridians, ":globe_with_meridians:", ":globe_with_meridians:"),
    Nature93(2, 1, R.mipmap.sun_with_face, ":sun_with_face:", ":sun_with_face:"),
    Nature94(2, 1, R.mipmap.full_moon_with_face, ":full_moon_with_face:", ":full_moon_with_face:"),
    Nature95(2, 1, R.mipmap.new_moon_with_face, ":new_moon_with_face:", ":new_moon_with_face:"),
    Nature96(2, 1, R.mipmap.new_moon, ":new_moon:", ":new_moon:"),
    Nature97(2, 1, R.mipmap.waxing_crescent_moon, ":waxing_crescent_moon:", ":waxing_crescent_moon:"),
    Nature98(2, 1, R.mipmap.first_quarter_moon, ":first_quarter_moon:", ":first_quarter_moon:"),
    Nature99(2, 1, R.mipmap.waxing_gibbous_moon, ":waxing_gibbous_moon:", ":waxing_gibbous_moon:"),
    Nature100(2, 1, R.mipmap.full_moon, ":full_moon:", ":full_moon:"),
    Nature101(2, 1, R.mipmap.waning_gibbous_moon, ":waning_gibbous_moon:", ":waning_gibbous_moon:"),
    Nature102(2, 1, R.mipmap.last_quarter_moon, ":last_quarter_moon:", ":last_quarter_moon:"),
    Nature103(2, 1, R.mipmap.waning_crescent_moon, ":waning_crescent_moon:", ":waning_crescent_moon:"),
    Nature104(2, 1, R.mipmap.last_quarter_moon_with_face, ":last_quarter_moon_with_face:", ":last_quarter_moon_with_face:"),
    Nature105(2, 1, R.mipmap.first_quarter_moon_with_face, ":first_quarter_moon_with_face:", ":first_quarter_moon_with_face:"),
    Nature106(2, 1, R.mipmap.moon, ":moon:", ":moon:"),
    Nature107(2, 1, R.mipmap.earth_africa, ":earth_africa:", ":earth_africa:"),
    Nature108(2, 1, R.mipmap.earth_americas, ":earth_americas:", ":earth_americas:"),
    Nature109(2, 1, R.mipmap.earth_asia, ":earth_asia:", ":earth_asia:"),
    Nature110(2, 1, R.mipmap.volcano, ":volcano:", ":volcano:"),
    Nature111(2, 1, R.mipmap.milky_way, ":milky_way:", ":milky_way:"),
    Nature112(2, 1, R.mipmap.partly_sunny, ":partly_sunny:", ":partly_sunny:"),
    Nature113(2, 1, R.mipmap.octocat, ":octocat:", ":octocat:"),
    Nature114(2, 1, R.mipmap.squirrel, ":squirrel:", ":squirrel:");

    /*********************************
     * 操作
     **************************************/
    private String emojiStr;
    private String remote;
    private int value;
    private int resId;
    private int type;
    private static Map<String, Integer> sEmojiMap;

     DisplayRules(int type, int value, int resId, String cls,
                         String remote) {
        this.type = type;
        this.emojiStr = cls;
        this.value = value;
        this.resId = resId;
        this.remote = remote;
    }

    public String getRemote() {
        return remote;
    }

    public void setRemote(String remote) {
        this.remote = remote;
    }

    public String getEmojiStr() {
        return emojiStr;
    }

    public int getValue() {
        return value;
    }

    public int getResId() {
        return resId;
    }

    public int getType() {
        return type;
    }

    private static Emojicon getEmojiFromEnum(DisplayRules data) {
        return new Emojicon(data.getResId(), data.getValue(),
                data.getEmojiStr(), data.getRemote());
    }

    public static Emojicon getEmojiFromRes(int resId) {
        for (DisplayRules data : values()) {
            if (data.getResId() == resId) {
                return getEmojiFromEnum(data);
            }
        }
        return null;
    }

    public static Emojicon getEmojiFromValue(int value) {
        for (DisplayRules data : values()) {
            if (data.getValue() == value) {
                return getEmojiFromEnum(data);
            }
        }
        return null;
    }

    public static Emojicon getEmojiFromName(String emojiStr) {
        for (DisplayRules data : values()) {
            if (data.getEmojiStr().equals(emojiStr)) {
                return getEmojiFromEnum(data);
            }
        }
        return null;
    }

    /**
     * 提高效率，忽略线程安全
     */
    public static Map<String, Integer> getMapAll() {
        if (sEmojiMap == null) {
            sEmojiMap = new HashMap<String, Integer>();
            for (DisplayRules data : values()) {
                sEmojiMap.put(data.getEmojiStr(), data.getResId());
                sEmojiMap.put(data.getRemote(), data.getResId());
            }
        }
        return sEmojiMap;
    }

    public static List<Emojicon> getAllByType(int type) {
        List<Emojicon> datas = new ArrayList<Emojicon>(values().length);
        for (DisplayRules data : values()) {
            if (data.getType() == type) {
                datas.add(getEmojiFromEnum(data));
            }
        }
        return datas;
    }
}
