class NoticeBox{
    constructor() {
        this.doc=document.createElement("div")
        this.doc.style.position="absolute"
        this.doc.style.top="0px"
        this.doc.style.background="#fad9cc"
        this.doc.style.width="30%"
        this.doc.style.margin="0px 35% 0px 35%"
        this.doc.style.borderRadius="5px"
        this.doc.style.filter='opacity(0%)'
        this.doc.style.whiteSpace="nowrap"
        this.doc.style.transition="all 0.4s"
        this.doc.style.textAlign="center"
        this.doc.style.height="40px"
        this.doc.style.lineHeight="40px"
        this.doc.style.color="#d42c2c"
        document.body.appendChild(this.doc)
    }
    show(msg){
        this.doc.style.top="1.5%"
        this.doc.style.filter="opacity(100%)"
        this.doc.innerText=msg
        setTimeout(()=>{
            this.doc.style.filter='opacity(0%)'
            this.doc.style.top="-60px"
            setTimeout(()=>{
                this.doc.remove()
            },400)
       },3000)
    }
}
function showAlert(msg){
    let box=new NoticeBox();
    setTimeout(()=>{
        box.show(msg)
    },150)
}
