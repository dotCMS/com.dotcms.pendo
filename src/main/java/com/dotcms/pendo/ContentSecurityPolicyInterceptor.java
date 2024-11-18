package com.dotcms.pendo;

import com.dotcms.filters.interceptor.Result;
import com.dotcms.filters.interceptor.WebInterceptor;
import com.dotmarketing.util.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ContentSecurityPolicyInterceptor implements WebInterceptor {

    private static final long serialVersionUID = 1L;



    static final String[] CSP_HEADERS = new String[] {

            "script-src 'self' 'unsafe-inline' 'unsafe-eval' app.pendo.io *.storage.googleapis.com cdn.pendo.io pendo-static-SUB_ID.storage.googleapis.com data.pendo.io ;",
            "style-src 'self' 'unsafe-inline' app.pendo.io cdn.pendo.io pendo-static-SUB_ID.storage.googleapis.com;",
            "img-src 'self' cdn.pendo.io app.pendo.io pendo-static-SUB_ID.storage.googleapis.com data.pendo.io 'self' data:;",
            "connect-src 'self' app.pendo.io data.pendo.io pendo-static-SUB_ID.storage.googleapis.com;",
            "frame-ancestors 'self' cdn.pendo.io app.pendo.io;",
            "frame-src 'self' cdn.pendo.io app.pendo.io;",
            "child-src 'self' cdn.pendo.io app.pendo.io;"

    };


    static final String PENDO_ID = "6272865290747904";



    @Override
    public String[] getFilters() {
        return new String[] {
                "/dotAdmin*", "/c*", "/html/*"};
    }

    @Override
    public Result intercept(final HttpServletRequest request, final HttpServletResponse response) {


        final String host = request.getHeader("host");


        for (String headerIn : CSP_HEADERS) {

            final String header = headerIn.replace("foo.example.com", host).replace("SUB_ID", PENDO_ID);
            Logger.debug(getClass(), "adding header:" + header);
            response.addHeader("Content-Security-Policy", header);
            
        }
        response.setHeader("X-Frame-Options", "SAMEORIGIN");
        response.addHeader("Referrer-Policy", "unsafe-url");
        
        return Result.NEXT;

    }

}
