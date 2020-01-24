<%@include file="lib/headers.jsp" %>
<script src="static/js/scripts.js"></script>
<body>
<main role="main">
    <div class="jumbotron">
        <div class="container">
            <h1 class="display-3">Welcome to the ServerButler</h1>
            <h3>On this server, there are <span id="numTotal" style="color: red"></span> total players that play just as
                planned.</h3>
            <h5>Most are nice, but <span id="numBanned" style="color: red"></span> are banned.</h5>
        </div>
    </div>
</main>
<table id="tablePreview" class="table table-dark table-bordered">
    <!--Table head-->
    <thead>
    <tr>
        <th>#</th>
        <th>Target</th>
        <th>Action type</th>
        <th>Reason</th>
        <th>Applied By</th>
        <th>Expire Date</th>
        <th>Date Applied</th>
    </tr>
    </thead>
    <!--Table head-->
    <!--Table body-->
    <tbody id="historyBody">
    </tbody>

    <!--Table body-->
</table>
<input type="text" id="_lastID" hidden>
<input type="text" id="_numResults" hidden>
<div id="runMore">
    <div align="center">
        <button type="button" class="btn btn-primary" id="loadMoreButton">Load more</button>
    </div>
</div>
<div id="loading-gif" align="center">
    <img src="static/img/loading-edited.gif">
</div>
<!--Table-->
<script>

    function loadContent() {
        var startindex = $("#_lastID").val();
        $.ajax({
            url: "API/history/getHistory.jsp",
            data: "startIndex=" + startindex + "&amount=4",
            async: true,
            success: function (data) {
                var result = JSON.parse(data);
                $("#loadMoreButton").show();
                if (!checkResp(data)) {
                    result.history.forEach(function (act) {
                        $("#historyBody").append(createRowForAction(act));

                        $("#_lastID").val(act.ID);

                    });
                    $("#_numResults").val(parseInt($("#_numResults").val()) + result.history.length);
                }


            }
        });
    }


    $("#loadMoreButton").click(function () {
        var startindex = $("#_lastID").val();
        $.ajax({
            url: "API/history/getHistory.jsp",
            data: "startIndex=" + startindex + "&amount=4",
            async: true,
            success: function (data) {
                var result = JSON.parse(data);
                $("#loadMoreButton").show();
                if (!checkResp(data)) {
                    result.history.forEach(function (act) {
                        $("#historyBody").append(createRowForAction(act));
                        $("#_lastID").val(act.ID);
                    })
                }
                if (result.history.length > 1) {
                    $("html, body").animate({scrollTop: $(document).height()}, "slow");
                    $("#_numResults").val(parseInt($("#_numResults").val()) + result.history.length);
                    $("#loadMoreButton").removeClass("disabled");
                    $("#loadMoreButton").prop('disabled', false);
                    $("#loadMoreButton").html("Load more");
                } else {
                    $("#loadMoreButton").addClass("disabled");
                    $("#loadMoreButton").prop('disabled', true);
                    $("#loadMoreButton").html("[No more results]");
                }
            }
        });
    });


    $(document).ready(function () {
        $("#_lastID").val(99999999);
        $("#_numResults").val(0);
        $.ajaxSetup({
            async: false,
            beforeSend: function () {
                $("#loadMoreButton").hide();
                $("#loading-gif").show();
                $("#header").html("<h1>Loading...</h1>");
            },
            complete: function () {
                $("#loading-gif").hide();
                $("#loadMoreButton").show();
            },
            error: function (jqXHR, textStatus, errorThrown) {
                iziToast.error({
                    title: jqXHR.status,
                    message: textStatus + ": " + errorThrown,
                    position: "topRight",
                    transitionIn: 'bounceInDown',
                    icon: "fas fa-exclamation-triangle",
                    async: false
                });
            }
        });
        printStats();
        loadContent();

    });


    function createRowForAction(actionPacket) {
        var base = " <tr class=\"bg-[CLZ]\"><td id=\"action_row_[ID]\">[ID]</td>\n" +
            "        <td>[TARGET]</td>\n" +
            "        <td>[ATYPE]</td>\n" +
            "        <td>[RES]</td>\n" +
            "        <td>[AP]</td>\n" +
            "        <td>[ED]</td>" +
            "<td>[DA]</td></tr>";

        base = base.replace(/\[ID\]/g, actionPacket.ID).replace("[ATYPE]", actionPacket.actionType).replace("[RES]", actionPacket.reason).replace("[AP]", actionPacket.moderator == null ? headHREFFor("Servers") + " <b> CONSOLE</b>" : headHREFFor(actionPacket.moderator) + "<b> " + getName(actionPacket.moderator) + "<b>");
        base = base.replace("[ED]", actionPacket.expireDate);
        base = base.replace("[CLZ]", classByActionType(actionPacket.actionType));
        var theName = actionPacket.targetType === "BUKKIT_PLAYER" ? headHREFFor(actionPacket.target) + "    <b>" + getName(actionPacket.target) + "</b>" : headHREFFor("zasf") + "    <b>" + actionPacket.target + "</b>";
        base = base.replace("[TARGET]", theName).replace("[DA]", actionPacket.dateApplied);
        return base;
    }

    function headHREFFor(username) {
        return "<img src=\"https://mc-heads.net/avatar/" + username + "/40/\">";
    }

    function getName(uuid) {
        var pName = null;
        $.get("/API/player/getPlayer.jsp?uuid=" + uuid, function (x) {
            var data = JSON.parse(x);
            if (!checkResp(data)) {
                pName = data.player.name;
            }
        });
        console.log(pName);
        return pName;
    }


    function classByActionType(type) {
        switch (type) {
            case "PARDON":
                return "success";
                break;
            case "BAN":
                return "danger";
            case "TEMPBAN":
                return "warning";
            default:
                break;
        }
    }
</script>
</body>
</html>