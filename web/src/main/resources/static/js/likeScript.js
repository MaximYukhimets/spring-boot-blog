
function likeScript(id) {
    console.log(id);

    if($('#like-button-' + id).hasClass('btn btn-outline-success')) {
        $.ajax('/post/' + id + '/like', {
            type : 'POST',
            success : function (likeNumber) {
                $('#like-button-' + id).removeClass().addClass("btn btn-outline-secondary shadow-none");
                $('#like-icon-' + id).removeClass().addClass("bi-check-circle");

                $('#like-value-' + id).html(likeNumber);
            }
        });
    } else {
        $.ajax('/post/' + id + '/unlike', {
            type : 'POST',
            success : function (likeNumber) {
                $('#like-button-' + id).removeClass().addClass('btn btn-outline-success shadow-none');
                $('#like-icon-' + id).removeClass().addClass("bi bi-hand-thumbs-up");

                $('#like-value-' + id).html(likeNumber);
            }
        });
    }

}
