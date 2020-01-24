function checkResp(data) {
    if (data.error) {
        data.errors.forEach(function (err) {
            iziToast.error({
                title: 'API Error',
                message: err.message,
                position: "topRight",
                transitionIn: 'bounceInDown',
                icon: "fas fa-exclamation-triangle",
                async: false
            });
        });
        return true
    } else
        return false;
}

function printStats() {

    $.ajax({
        url: "API/player/getStats.jsp",
        async: false,
        success: function (data) {
            var parsed = JSON.parse(data);
            if (!checkResp(parsed)) {
                $("#numTotal").html(parsed.usersTotal);
                $("#numBanned").html(parsed.usersBanned);
                $("#usersBadge").html(parsed.usersTotal);
            }
        }

    });
}