<%@ page import="com.myththewolf.ServerButler.lib.moderation.interfaces.TargetType" %>
<%@ page import="com.myththewolf.ServerButler.lib.mySQL.SQLAble" %>
<%@ page import="com.myththewolf.ServerButler.lib.webserver.ParamType" %>
<%@ page import="java.sql.ResultSet" %>
<%@include file="../APIUtils.jsp" %>
<%!
    private class HistoryFetch implements SQLAble {
        public JSONObject getHistory(String UUID, int startId, int amount) {
            JSONArray hist = new JSONArray();

            ResultSet rs;

            if (UUID == null) {
                rs = prepareAndExecuteSelectExceptionally("SELECT `ID` FROM `SB_Actions` WHERE `targetType` = ? AND `id`  <? ORDER BY `ID` DESC LIMIT ?", 3, TargetType.BUKKIT_PLAYER, startId, amount);

            } else {
                rs = prepareAndExecuteSelectExceptionally("SELECT `ID` FROM `SB_Actions` WHERE `targetType` = ? AND `target` = ? AND `id` <? ORDER BY `ID` DESC LIMIT ?", 4, TargetType.BUKKIT_PLAYER, UUID, startId, amount);
            }
            try {
                while (rs.next()) {
                    getLogger().info(DataCache.getActionByID(rs.getString("ID")).get().getDatabaseID());
                    hist.put(DataCache.getActionByID(rs.getString("ID")).orElseThrow(IllegalStateException::new).toJSON());
                }
            } catch (Exception E) {
                return exception(E);
            }
            return new JSONObject().put("error", false).put("history", hist);
        }
    }
%>
<%= paramCheck((Request) request, new JSONCallback() {
    @Override
    @RequiredGETParam(requiredType = ParamType.INT, name = "startIndex")
    @RequiredGETParam(requiredType = ParamType.INT, name = "amount")
    @OptionalGetParam(requiredType = ParamType.ACTION_TYPE, name = "actionType")
    public Object processValidatedInput(HttpServletRequest request) {
        HistoryFetch historyFetch = new HistoryFetch();
        return historyFetch.getHistory(request.getParameter("uuid"), Integer.parseInt(request.getParameter("startIndex")), Integer.parseInt(request.getParameter("amount")));
    }
})%>