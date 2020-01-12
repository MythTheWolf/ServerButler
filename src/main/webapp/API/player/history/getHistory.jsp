<%@ page import="com.myththewolf.ServerButler.lib.cache.DataCache" %>
<%@ page import="com.myththewolf.ServerButler.lib.moderation.interfaces.TargetType" %>
<%@ page import="com.myththewolf.ServerButler.lib.mySQL.SQLAble" %>
<%@ page import="com.myththewolf.ServerButler.lib.webserver.ParamType" %>
<%@ page import="com.myththewolf.ServerButler.lib.webserver.RequiredGETParam" %>
<%@ page import="org.json.JSONArray" %>
<%@ page import="java.sql.ResultSet" %>
<%@include file="../../APIUtils.jsp" %>
<%!
    private class HistoryFetch implements SQLAble {
        public JSONObject getHistory(String UUID, int startId, int amount) {
            JSONArray hist = new JSONArray();
            if (!DataCache.getPlayer(UUID).isPresent()) {
                return errorUserNotFound(UUID);
            } else {
                ResultSet rs = prepareAndExecuteSelectExceptionally("SELECT `ID` FROM `SB_Actions` WHERE `targetType` = ? AND `target` = ? AND `id` >=? ORDER BY `ID` DESC LIMIT ?", 4, TargetType.BUKKIT_PLAYER, UUID, startId, amount);
                try {
                    while (rs.next()) {
                        getLogger().info(DataCache.getActionByID(rs.getString("ID")).get().getDatabaseID());
                        hist.put(DataCache.getActionByID(rs.getString("ID")).orElseThrow(IllegalStateException::new).toJSON());
                    }
                } catch (Exception E) {
                    return exception(E);
                }
            }
            return new JSONObject().put("error", false).put("history", hist);
        }
    }
%>
<%= paramCheck((Request) request, new JSONCallback() {
    @Override
    @RequiredGETParam(requiredType = ParamType.PLAYER_UUID, name = "id")
    @RequiredGETParam(requiredType = ParamType.BOOL, name = "rec")
    public JSONArray processValidatedInput(HttpServletRequest request) {
        return new JSONArray().put("YAY!");
    }
})%>