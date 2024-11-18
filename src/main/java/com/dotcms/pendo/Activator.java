package com.dotcms.pendo;

import com.dotcms.experience.util.AppUtil;
import com.dotcms.filters.interceptor.FilterWebInterceptorProvider;
import com.dotcms.filters.interceptor.WebInterceptor;
import com.dotcms.filters.interceptor.WebInterceptorDelegate;
import com.dotcms.rest.config.RestServiceUtil;
import com.dotmarketing.filters.InterceptorFilter;
import com.dotmarketing.osgi.GenericBundleActivator;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.Logger;
import org.osgi.framework.BundleContext;

public class Activator extends GenericBundleActivator {
    
    private WebInterceptor webInterceptor = new ContentSecurityPolicyInterceptor();

    final WebInterceptorDelegate delegate =
                    FilterWebInterceptorProvider.getInstance(Config.CONTEXT).getDelegate(
                            InterceptorFilter.class);

    
    private AppUtil util = new AppUtil();
    

    public void start(final org.osgi.framework.BundleContext context) throws Exception {

        Logger.info(Activator.class.getName(), "Starting dotCDN Plugin");

        
        delegate.addFirst(webInterceptor);;

        try {
            // Register Resource
            RestServiceUtil.addResource(DotExperienceResource.class);
            // Adding APP yaml
            Logger.info(Activator.class.getName(), "Copying js file");
            util.copyJs();
            util.copyJsp();

        } catch (Throwable t) {
            delegate.remove(webInterceptor.getName(),true);

        }

        
    }


    @Override
    public void stop(BundleContext context) throws Exception {


        delegate.remove(webInterceptor.getName(),true);
        RestServiceUtil.removeResource(DotExperienceResource.class);
        util.clearJs() ;
        util. clearJsp();






    }


}
