let textOnlyAreaComponent = Vue.component('textarea-textonly', {
    data() {
        return {
            editor: new Object()
        }
    },
    props: {
        text : String
    },
    mounted: function(){
        this.editor = new Jodit("#text-only-textarea", {
            "buttons": "|,bold,strikethrough,underline,italic,|,|,ul,ol,|,outdent,indent,|,font,fontsize,brush,paragraph,|,table,link,|,undo,redo,|,fullsize,print"
          });
    },
    watch : { 
        text: function(){
            this.editor.value = '<p>'+ this.text +'</p>';
        }
    },
    template: `
        <textarea id="text-only-textarea"></textarea>
    `
});
    
module.exports = textOnlyAreaComponent;
