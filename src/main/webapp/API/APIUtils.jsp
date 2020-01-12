<%@ page import="com.myththewolf.ServerButler.lib.MythUtils.StringUtils" %>
<%@ page import="com.myththewolf.ServerButler.lib.webserver.JSONCallback" %>
<%@ page import="org.eclipse.jetty.server.Request" %>
<%@ page import="org.json.JSONObject" %>
<%@ page import="java.lang.reflect.Method" %>
<%@ page import="java.util.Arrays" %>
<%!
    JSONObject errorUserNotFound(String req) {
        JSONObject object = new JSONObject();
        object.put("error", true);
        object.put("message", "UUID " + req + " did not match any players in the database.");
        return object;
    }

    JSONObject exception(Exception e) {
        JSONObject object = new JSONObject();
        object.put("error", true);
        object.put("message", "Exception occurred while processing your request");
        object.put("stack_trace", StringUtils.getStackTrace(e));
        return object;
    }

    JSONObject errorParamInvalid(String name, Class requested, Class got) {
        JSONObject object = new JSONObject();
        object.put("error", true);
        object.put("message", "Param " + name + " expected to be of type '" + requested.getName() + "' got '" + got.getName() + "'");
        return object;
    }

    public JSONArray paramCheck(Request request, JSONCallback callback) {
        JSONArray errors = new JSONArray();
        try {
            Method M = callback.getClass().getMethod("processValidatedInput", HttpServletRequest.class);
            Arrays.stream(M.getAnnotationsByType(RequiredGETParam.class)).forEach(requiredGETParam -> {
                if (request.getParameter(requiredGETParam.name()) == null) {
                    JSONObject erorr = new JSONObject();
                    erorr.put("message", requiredGETParam.name() + " is a required parameter.");
                    errors.put(erorr);
                    return;
                }
                switch (requiredGETParam.requiredType()) {
                    case INT:
                        if (!StringUtils.isInt(request.getParameter(requiredGETParam.name()))) {
                            JSONObject erorr = new JSONObject();
                            erorr.put("message", requiredGETParam.name() + " must be of type INT");
                            errors.put(erorr);
                            return;
                        }
                        break;
                    case STRING:
                        break;
                    case PLAYER_UUID:
                        if (!DataCache.getPlayer(request.getParameter(requiredGETParam.name())).isPresent()) {
                            JSONObject erorr = new JSONObject();
                            erorr.put("message", requiredGETParam.name() + ":" + request.getParameter(requiredGETParam.name()) + " is not a valid player UUID.");
                            errors.put(erorr);
                            return;
                        }
                        break;
                    default:
                        break;
                }
            });
        } catch (Exception E) {
            E.printStackTrace();
        }
        if (errors.length() > 0) {
            return errors;
        } else {
            return callback.processValidatedInput(request);
        }
    }
%>