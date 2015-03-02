<%@page import="org.springframework.web.context.WebApplicationContext" %>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="org.apache.http.impl.conn.PoolingHttpClientConnectionManager" %>
<%

    //Get version of application

    java.util.Properties prop = new java.util.Properties();

    prop.load(getServletContext().getResourceAsStream("/META-INF/MANIFEST.MF"));

    String applVersion = prop.getProperty("Implementation-Version");
    String buildNumber = prop.getProperty("Implementation-Build");

    WebApplicationContext context = WebApplicationContextUtils
            .getWebApplicationContext(application);

    org.apache.http.impl.conn.PoolingHttpClientConnectionManager poolingHttpClientConnectionManager =
            (PoolingHttpClientConnectionManager) context.getBean("httpConnectionManager");

    String stats = poolingHttpClientConnectionManager.getTotalStats().toString();

%>

<%=applVersion%><br>
<%=buildNumber%><br>
<%=stats%>


