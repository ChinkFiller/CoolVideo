// mcaptcha.js
 
export class Mcaptcha {
  constructor(options) {
    this.options = options;
    this.fontSize = options.height * 3 / 6;
    this.init();
    this.refresh();
  }
  init() {
    this.ctx = uni.createCanvasContext(this.options.el);
    this.ctx.setTextBaseline("middle");
    this.ctx.setFillStyle(this.randomColor(180, 240));
  }
  refresh() {
    var code = '';
    var txtArr = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q','r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',0,1,2,3,4,5,6,7,8,9]
    for(var i=0;i<4;i++){
      code += txtArr[this.randomNum(0, txtArr.length)];
    }
    this.options.createCodeImg = code;
    let arr = (code + '').split('');
    if (arr.length === 0) {
      arr = ['e', 'r', 'r','o','r'];
    };
    let offsetLeft = this.options.width * 0.6 / (arr.length - 1);
    let marginLeft = this.options.width * 0.2;
    arr.forEach((item, index) => {
      this.ctx.setFillStyle(this.randomColor(0, 180));
      let size = this.randomNum(24, this.fontSize);
      this.ctx.setFontSize(size);
      let dis = offsetLeft * index + marginLeft - size * 0.3;
      let deg = this.randomNum(-30, 30);
      this.ctx.translate(dis, this.options.height*0.5);
      this.ctx.rotate(deg * Math.PI / 180);
      this.ctx.fillText(item, 0, 0);
      this.ctx.rotate(-deg * Math.PI / 180);
      this.ctx.translate(-dis, -this.options.height * 0.5);
    })
    for (var i = 0; i < 4; i++) {
      this.ctx.strokeStyle = this.randomColor(40, 180);
      this.ctx.beginPath();
      this.ctx.moveTo(this.randomNum(0, this.options.width), this.randomNum(0, this.options.height));
      this.ctx.lineTo(this.randomNum(0, this.options.width), this.randomNum(0, this.options.height));
      this.ctx.stroke();
    }
    for (var i = 0; i < this.options.width / 4; i++) {
      this.ctx.fillStyle = this.randomColor(0, 255);
      this.ctx.beginPath();
      this.ctx.arc(this.randomNum(0, this.options.width), this.randomNum(0, this.options.height), 1, 0, 2 * Math.PI);
      this.ctx.fill();
    }
    this.ctx.draw();
  }
  validate(code){
    var code = code.toLowerCase();
    var v_code = this.options.createCodeImg.toLowerCase();
    //console.log(v_code.substring(v_code.length - 4))
    if (code == v_code.substring(v_code.length - 4)) {
      return true;
    } else {
      return false;
    }
  }
  getCode(){
	  var v_code = this.options.createCodeImg.toLowerCase();
	  return v_code.substring(v_code.length - 4)
  }
  randomNum(min, max) {
    return Math.floor(Math.random() * (max - min) + min);
  }
  randomColor(min, max) {
    let r = this.randomNum(min, max);
    let g = this.randomNum(min, max);
    let b = this.randomNum(min, max);
    return "rgb(" + r + "," + g + "," + b + ")";
  }
}