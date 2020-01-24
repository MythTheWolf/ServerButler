<%@ page import="com.myththewolf.ServerButler.lib.mySQL.SQLAble" %>
<%@ page import="java.sql.ResultSet" %>
<%@include file="../APIUtils.jsp" %>
<% class StatsFetch implements SQLAble {
    public Object getStats() {
        JSONObject stats = new JSONObject();
        ResultSet rs = prepareAndExecuteSelectExceptionally("SELECT COUNT(`ID`) AS \"rowCount\" FROM `SB_Players` WHERE `loginStatus` = \"BANNED\" OR `loginStatus` = \"TEMP_BANNED\"", 0);
        try {
            if (rs.next()) {
                stats.put("usersBanned", rs.getInt("rowCount"));
            }
            rs = prepareAndExecuteSelectExceptionally("SELECT COUNT(`ID`) AS \"rowCount\" FROM `SB_Players`", 0);
            if (rs.next()) {
                stats.put("usersTotal", rs.getInt("rowCount"));
            }
        } catch (Exception e) {
            return exception(e);
        }
        return stats;
    }
}%>
<%= paramCheck((Request) request, new JSONCallback() {
    @Override
    public Object processValidatedInput(HttpServletRequest request) {
        return new StatsFetch().getStats();
    }
})%>