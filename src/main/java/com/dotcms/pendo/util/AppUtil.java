package com.dotcms.experience.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.apache.commons.io.IOUtils;
import com.dotmarketing.util.Logger;

public class AppUtil {
    private static final String JS_FILE_NAME = "dotexp.js";
    private static final String JS_INSTALL_PATH = "/dotAdmin/" + JS_FILE_NAME;
    private static final File JS_INSTALL_FILE = new File(com.liferay.util.FileUtil.getRealPath(JS_INSTALL_PATH));

    private static final String TAG_TO_REPLACE = "</body>";
    private static final String JS_LINE_TO_ADD = "\n<script src='" + JS_INSTALL_PATH + "'></script>\n";

    private static final String INDEX_PAGE_PATH = com.liferay.util.FileUtil.getRealPath("/dotAdmin/index.html");
    private static final File INDEX_PAGE_FILE = new File(INDEX_PAGE_PATH);
    
    
    
    private static final String JSP_FILE_NAME ="dotexp.jsp";
    private static final String JSP_INSTALL_PATH = "/html/common/" + JSP_FILE_NAME;
    private static final File JSP_INSTALL_FILE = new File(com.liferay.util.FileUtil.getRealPath(JSP_INSTALL_PATH));

    private static final String JSP_LINE_TO_ADD = "\n<jsp:include page=\"/html/common/" + JSP_FILE_NAME +"\" />\n";
    
    private static final String INIT_JSP_PATH = com.liferay.util.FileUtil.getRealPath("/html/common/init.jsp");
    private static final File INIT_JSP_FILE = new File(INIT_JSP_PATH);
    /**
     * copies the App yaml to the apps directory and refreshes the apps
     * 
     * @throws IOException
     */
    public void copyJs() throws IOException {


        Logger.info(this.getClass().getName(), "copying js File:" + JS_INSTALL_PATH);
        try (final InputStream in = this.getClass().getResourceAsStream("/" + JS_FILE_NAME);
                        final OutputStream out = Files.newOutputStream(JS_INSTALL_FILE.toPath());) {
            IOUtils.copy(in, out);
        }



        String readFileIn = IOUtils.toString(Files.newInputStream(INDEX_PAGE_FILE.toPath()), StandardCharsets.UTF_8);


        if (readFileIn.contains(JS_LINE_TO_ADD)) {
            Logger.info(getClass(), "js already added to :" + INDEX_PAGE_PATH);
            return;
        }

        readFileIn = readFileIn.replace(TAG_TO_REPLACE, JS_LINE_TO_ADD + TAG_TO_REPLACE);
        Logger.info(getClass(), "writing to:" + INDEX_PAGE_PATH);
        IOUtils.write(readFileIn, Files.newOutputStream(INDEX_PAGE_FILE.toPath()), StandardCharsets.UTF_8);

    }

    public void clearJs() throws IOException {

        String readFileIn = IOUtils.toString(Files.newInputStream(INDEX_PAGE_FILE.toPath()), StandardCharsets.UTF_8);

        if (!readFileIn.contains(JS_LINE_TO_ADD + TAG_TO_REPLACE)) {
            Logger.info(getClass(), "no js found on " + INDEX_PAGE_PATH);
            return;
        }
        readFileIn = readFileIn.replace(JS_LINE_TO_ADD + TAG_TO_REPLACE, "</body>");
        Logger.info(getClass(), "removing js from :" + INDEX_PAGE_PATH);
        IOUtils.write(readFileIn, Files.newOutputStream(INDEX_PAGE_FILE.toPath()), StandardCharsets.UTF_8);

    }
    
    public void copyJsp() throws IOException {

        
        
        
        Logger.info(this.getClass().getName(), "copying jsp File:" + JSP_INSTALL_PATH);
        try (final InputStream in = this.getClass().getResourceAsStream("/" + JSP_FILE_NAME)) {
            IOUtils.copy(in, Files.newOutputStream(JSP_INSTALL_FILE.toPath()));
        }


        Logger.info(getClass(), "reading :" + INIT_JSP_FILE);
        String readFileIn = IOUtils.toString(Files.newInputStream(INIT_JSP_FILE.toPath()), StandardCharsets.UTF_8);


        if (readFileIn.contains(JSP_LINE_TO_ADD)) {
            Logger.info(getClass(), "jsp already added to :" + INIT_JSP_FILE);
            return;
        }

        readFileIn = readFileIn + JSP_LINE_TO_ADD;
        Logger.info(getClass(), "writing to:" + INIT_JSP_FILE);
        IOUtils.write(readFileIn, Files.newOutputStream(INIT_JSP_FILE.toPath()), StandardCharsets.UTF_8);

    }
    
    public void clearJsp() throws IOException {

        String readFileIn = IOUtils.toString(Files.newInputStream(INIT_JSP_FILE.toPath()), StandardCharsets.UTF_8);


        if (!readFileIn.contains(JSP_LINE_TO_ADD)) {
            Logger.info(getClass(), "jsp already removed to :" + INIT_JSP_FILE);
            return;
        }

        readFileIn = readFileIn.replace(JSP_LINE_TO_ADD,"");
        Logger.info(getClass(), "removing js from :" + INIT_JSP_PATH);
        IOUtils.write(readFileIn, Files.newOutputStream(INIT_JSP_FILE.toPath()), StandardCharsets.UTF_8);

    }

}
