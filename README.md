# Pendo Plugin

This plugin enables [Pendo](https://www.pendo.io) in our backend admin tool and provides a REST endpoint for "enagement" stats.

Here is what it does:

1. It provides a REST endpoint `/api/v1/dotexp/visit` that returns a logged in user's unique information
3. It copies a javascript file - `dotexp.js` into the `/dotAdmin/` folder and rewrites `/dotAdmin/index.html` to call this javascript file.  This file tries to "register" the visit with Pendo.  To do this, it calls the `/api/v1/dotexp/visit` REST endpoint every 5 seconds, which returns nothing unless the user has logged in.  Once the user has logged in to dotCMS, the JS will get the user data and register the dotCMS user with Pendo.  It does this to track what a user has seen, so users are not shown the same flyouts all the time.
4. It provides a WebInterceptor that adds CSP headers needed for Pendo to work.  This includes rewriting the `Content-Security-Policy` headers and the `X-Frame-Options` headers.

Note: Pendo needs A LOT of permissive permissions to run in dotCMS. Here is the CSP (Content Security Policy) headers needed to run it:

```
content-security-policy: script-src 'self' 'unsafe-inline' 'unsafe-eval' app.pendo.io *.storage.googleapis.com cdn.pendo.io pendo-static-48f8d56d-cb14-4cb7-7c09-db0a980aeaa5.storage.googleapis.com data.pendo.io ;
content-security-policy: style-src 'self' 'unsafe-inline' app.pendo.io cdn.pendo.io pendo-static-48f8d56d-cb14-4cb7-7c09-db0a980aeaa5.storage.googleapis.com;
content-security-policy: img-src 'self' cdn.pendo.io app.pendo.io pendo-static-48f8d56d-cb14-4cb7-7c09-db0a980aeaa5.storage.googleapis.com data.pendo.io 'self' data:;
content-security-policy: connect-src 'self' app.pendo.io data.pendo.io pendo-static-48f8d56d-cb14-4cb7-7c09-db0a980aeaa5.storage.googleapis.com;
content-security-policy: frame-ancestors 'self' cdn.pendo.io app.pendo.io;
content-security-policy: frame-src 'self' cdn.pendo.io app.pendo.io;
content-security-policy: child-src 'self' cdn.pendo.io app.pendo.io;
```



**Build**
```
./mvnw clean package
```

**Note:**
The plugin works on 23.10+
# com.dotcms.pendo
