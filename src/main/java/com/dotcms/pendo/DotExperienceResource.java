package com.dotcms.pendo;


import com.dotcms.enterprise.cluster.ClusterFactory;
import com.dotcms.enterprise.license.LicenseManager;
import com.dotcms.http.CircuitBreakerUrl;
import com.dotcms.rest.annotation.NoCache;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotRuntimeException;
import com.dotmarketing.util.UtilMethods;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.ReleaseInfo;
import io.vavr.Lazy;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.codec.digest.DigestUtils;
import org.glassfish.jersey.server.JSONP;


@Path("/v1/dotexp")
public class DotExperienceResource implements Serializable {


    private static final long serialVersionUID = 204840922704940654L;



    @Path("/visit")
    @GET
    @JSONP
    @NoCache
    @Produces({
            MediaType.APPLICATION_OCTET_STREAM,
            MediaType.APPLICATION_JSON})
    public final Response getUserInfo(@Context final HttpServletRequest request, @Context final HttpServletResponse response) {

        final User user = PortalUtil.getUser(request);

        if (user == null || !user.isBackendUser()) {
            return Response.ok(Map.of()).build();
        }

        final Map<String, Object> resultMap = this.getReleaseInfo();
        final String hostName = request.getHeader("host");
        final String ipAddress = request.getRemoteAddr();
        final String userAgent = request.getHeader("user-agent");



        final String userHash = hash(user.getUserId(), resultMap.get("clusterId").toString(), hostName, ipAddress, userAgent);

        final Map<String, Object> userMap = new TreeMap<>();
        userMap.put("userId", user.getUserId());
        userMap.put("email", user.getEmailAddress());
        userMap.put("fullName", user.getFullName());
        userMap.put("isAdmin", user.isAdmin());
        userMap.put("userHash", userHash);



        resultMap.put("user", userMap);
        resultMap.put("hostName", hostName);

        request.getSession().setAttribute("com.dotcms.experience.visit", resultMap);



        return Response.ok(resultMap).build();



    }

    private String hash(final String... strings) {
        return DigestUtils.sha256Hex(String.join("_", strings));
    }


    /**
     * This method proxies the pendo.js file with the correct host name and cache control headers
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws DotDataException
     */

    @Path("/pendo.js")
    @GET
    @Produces("application/javascript")
    public final Reader getPendo(@Context final HttpServletRequest request, @Context final HttpServletResponse response)
                    throws IOException, DotDataException {


        response.setHeader("cache-control", "max-age=604800");
        
        final String rootHostName = getRootHost(request.getHeader("host"));

        String finalJS = pendoJs.get().replaceAll("dotcms.com", rootHostName);


        return new StringReader(finalJS);


    }

    final static String PENDO_JS_URL =
                    "https://content.experience.dotcms.com/agent/static/48f8d56d-cb14-4cb7-7c09-db0a980aeaa5/pendo.js";

    
    final Lazy<String> pendoJs = Lazy.of(() -> {
        try {
            return new CircuitBreakerUrl(PENDO_JS_URL).doString();
        } catch (Exception e) {
            throw new DotRuntimeException(e);
        }
    });

    private String getRootHost(String hostHeader) {

        hostHeader = hostHeader.contains(":") ? hostHeader.substring(0,hostHeader.indexOf(":")) : hostHeader;
        
        
        String[] domains = hostHeader.split("\\.");

        List<String> domainList = new ArrayList<>();
        boolean greedy = false;
        for (String x : domains) {
            if (x.toLowerCase().contains("dotcms")) {
                greedy = true;
            }
            if (greedy) {
                domainList.add(x);
            }
        }
        
        return domainList.size() > 1 ? String.join(".", domainList) : "dotcms.com";
    }



    private Map<String, Object> getReleaseInfo() {

        final Map<String, Object> resultMap = new LinkedHashMap<>();
        resultMap.put("version", ReleaseInfo.getVersion());
        resultMap.put("buildDate", UtilMethods.dateToHTMLDate(ReleaseInfo.getBuildDate()));

        resultMap.put("name", ReleaseInfo.getName());
        resultMap.put("buildNumber", ReleaseInfo.getBuildNumber());
        resultMap.put("serverInfo", ReleaseInfo.getServerInfo());
        resultMap.put("releaseInfo", ReleaseInfo.getReleaseInfo());
        resultMap.put("clusterId", ClusterFactory.getClusterId());
        resultMap.put("licenseLevel", LicenseManager.getInstance().getLevel());
        resultMap.put("isEnterprise", LicenseManager.getInstance().isEnterprise());
        resultMap.put("licenseName", LicenseManager.getInstance().getClientName());
        resultMap.put("licenseType", LicenseManager.getInstance().getLicenseType());
        resultMap.put("licenseSerial", LicenseManager.getInstance().getSerial());
        resultMap.put("licenseTrial",
                        (LicenseManager.getInstance().getClientName() + LicenseManager.getInstance().getLicenseType())
                                        .toLowerCase().contains("trial"));

        return resultMap;

    }



}
