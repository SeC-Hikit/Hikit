let textOnlyAreaComponent = Vue.component('textarea-textonly', {
    mounted: function(){
        let editor = new Jodit("#text-only-textarea", {
            "buttons": "|,bold,strikethrough,underline,italic,|,|,ul,ol,|,outdent,indent,|,font,fontsize,brush,paragraph,|,table,link,|,undo,redo,|,fullsize,print"
          });
        editor.value = '<p>start</p>';
    },
    template: `
        <textarea id="text-only-textarea"></textarea>
    `
});
    
module.exports = textOnlyAreaComponent;
