

var elementLoading = $("#loadingPanel");
var heightWOHeader = window.innerHeight - $(".it-header-wrapper").height();
elementLoading.css("min-height", heightWOHeader);

function toggleLoading(toShow){
     if(toShow) { 
        $("#loadingPanel").fadeIn();
    } else {
        $("#loadingPanel").fadeOut();
    }
}