function doCheck() {
    $.ajax('/check-strength', {
        type : 'GET',
        data : ({password : $('#floatingPassword_1').val()}),
        success : function (data) {
            $('#strengthValue').html(data)
            if (data === 'weak') {
                $('#strengthValue').css("color", "#DC3545FF");
            } else if (data === 'medium') {
                $('#strengthValue').css("color", "#FFC107FF");
            } else if (data === 'strong') {
                $('#strengthValue').css("color", "#198754FF");
            }

        }
    });
}