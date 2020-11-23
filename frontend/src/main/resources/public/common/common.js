// Configuring constants
let BASE_IMPORTER_ADDRESS = "http://localhost:8991/app"; 


// Configuring loading panel
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