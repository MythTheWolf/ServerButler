<%@ page import="com.myththewolf.ServerButler.lib.webserver.ParamType" %>
<%@include file="../APIUtils.jsp" %>
<%= paramCheck((Request) request, new JSONCallback() {
    @Override
    @RequiredGETParam(requiredType = ParamType.PLAYER_UUID, name = "uuid")
    public Object processValidatedInput(HttpServletRequest request) {
        return new JSONObject().put("error", false).put("player", DataCache.getPlayer(request.getParameter("uuid")).orElseThrow(IllegalStateException::new).toJSON());
    }
})%>