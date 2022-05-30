$(document).ready(function(){

    $.ajax('/sort-order', {
        type : 'POST',
        success : function (isSortingByLike) {
            if (isSortingByLike) {
                $("#flexSwitchCheckDefault").prop("checked", true);
            } else {
                $("#flexSwitchCheckDefault").prop("checked", false);
            }
        }
    });

})

function doChange() {

    let isSwitchOn = $("#flexSwitchCheckDefault").is(':checked');

    $.ajax('/change-sort-order', {
        data: { 'isSwitchOn' : isSwitchOn},
        type : 'POST',
        success : function () {
            location.reload();
        }
    });

}
