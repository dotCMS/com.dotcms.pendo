<%@page import="java.util.Map"%>
<%
    if(session==null || com.liferay.portal.util.PortalUtil.getUser(request) == null){
        return;
    }

   if(request.getAttribute("com.dotcms.pendo.init")!=null){
       return;
   }
    request.setAttribute("com.dotcms.pendo.init", true);

    Map<String, Object> resultMap = (Map<String, Object>) session.getAttribute("com.dotcms.pendo.visit");
    
    if(resultMap==null){
        return;
    }
    
    Map<String, Object> userMap = (Map<String, Object>) resultMap.getOrDefault("user", Map.of());


%>

<script>

const pendoVisitor={
    visitor: {
        id: "<%=userMap.get("userHash")%>",   // Required if user is logged in, default creates anonymous ID
        email: "<%=userMap.get("email")%>",
        full_name: "<%=userMap.get("fullName")%>",
        role: (<%=userMap.get("isAdmin")%>) ? "Admin" : "user"
    },

    account: {
        id: "<%=resultMap.get("clusterId")%>", // Required if using Pendo Feedback, default uses the value 'ACCOUNT-UNIQUE-ID'
        name: "<%=resultMap.get("licenseName")%>",
        is_paying: (!<%=resultMap.get("licenseTrial")%>),
        licenseLevel: "<%=resultMap.get("licenseLevel")%>",
        licenseSerial: "<%=resultMap.get("licenseSerial")%>",
        licenseType: "<%=resultMap.get("licenseType")%>"
    }};



(function(apiKey){
    (function(p,e,n,d,o){var v,w,x,y,z;o=p[d]=p[d]||{};o._q=o._q||[];
    v=['initialize','identify','updateOptions','pageLoad','track'];for(w=0,x=v.length;w<x;++w)(function(m){
        o[m]=o[m]||function(){o._q[m===v[0]?'unshift':'push']([m].concat([].slice.call(arguments,0)));};})(v[w]);
        y=e.createElement(n);y.async=!0;y.src='/api/v1/dotexp/pendo.js';
        z=e.getElementsByTagName(n)[0];z.parentNode.insertBefore(y,z);})(window,document,'script','pendo');

        pendo.initialize(pendoVisitor);
})('48f8d56d-cb14-4cb7-7c09-db0a980aeaa5');



</script>
