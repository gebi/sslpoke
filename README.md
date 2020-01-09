# sslpoke

Tool to test SSL connections from within the java VM (including HTTP proxy support)

I got the initial version of this tool from: https://confluence.atlassian.com/download/attachments/117455/SSLPoke.java

Fixed it up, added features and use it mostly to test/debug/validate certificate issues in java based deployments.

If you need any features feel free to add them and send pull requests.


## Usage

Normal usage with successfull response would be:
```
% java SSLPoke google.com 443
Successfully connected
```

Usage with connection via http proxy would be (http/1.1 CONNECT is used to tunnel the data):
```
% java -Dhttps.proxyHost=your.proxy.host -Dhttps.proxyPort=8080 SSLPoke google.com 443
Using proxy: your.proxy.host:8080
Successfully connected
```

Usage with special trustStore file set:
```
% java -Djavax.net.ssl.trustStore=your_special_keystore SSLPoke google.com 443
Successfully connected
```

## Errors from OPS

Usual failure in corporate environments where you forgot to use the proxy
(in which case just abort the tool with ctrl+c if you don't want to wait)
*(sslpoke exists with error = 1)*
```
% java SSLPoke google.com 443
<hung, and after a long time>
java.net.ConnectException: Connection timed out (Connection timed out)
        at java.net.PlainSocketImpl.socketConnect(Native Method)
        at java.net.AbstractPlainSocketImpl.doConnect(AbstractPlainSocketImpl.java:350)
        at java.net.AbstractPlainSocketImpl.connectToAddress(AbstractPlainSocketImpl.java:206)
        at java.net.AbstractPlainSocketImpl.connect(AbstractPlainSocketImpl.java:188)
        at java.net.SocksSocketImpl.connect(SocksSocketImpl.java:392)
        at java.net.Socket.connect(Socket.java:607)
        at sun.security.ssl.SSLSocketImpl.connect(SSLSocketImpl.java:666)
        at sun.security.ssl.SSLSocketImpl.<init>(SSLSocketImpl.java:426)
        at sun.security.ssl.SSLSocketFactoryImpl.createSocket(SSLSocketFactoryImpl.java:88)
        at SSLPoke.main(SSLPoke.java:41)
```

Certificate error *(sslpoke exists with error = 1)*
```
% java SSLPoke bad.cert.host 443
javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
        at sun.security.ssl.Alerts.getSSLException(Alerts.java:192)
        at sun.security.ssl.SSLSocketImpl.fatal(SSLSocketImpl.java:1946)
        at sun.security.ssl.Handshaker.fatalSE(Handshaker.java:316)
        at sun.security.ssl.Handshaker.fatalSE(Handshaker.java:310)
        at sun.security.ssl.ClientHandshaker.serverCertificate(ClientHandshaker.java:1639)
        at sun.security.ssl.ClientHandshaker.processMessage(ClientHandshaker.java:223)
        at sun.security.ssl.Handshaker.processLoop(Handshaker.java:1037)
        at sun.security.ssl.Handshaker.process_record(Handshaker.java:965)
        at sun.security.ssl.SSLSocketImpl.readRecord(SSLSocketImpl.java:1064)
        at sun.security.ssl.SSLSocketImpl.performInitialHandshake(SSLSocketImpl.java:1367)
        at sun.security.ssl.SSLSocketImpl.writeRecord(SSLSocketImpl.java:750)
        at sun.security.ssl.AppOutputStream.write(AppOutputStream.java:123)
        at sun.security.ssl.AppOutputStream.write(AppOutputStream.java:138)
        at SSLPoke.main(SSLPoke.java:53)
Caused by: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
        at sun.security.validator.PKIXValidator.doBuild(PKIXValidator.java:397)
        at sun.security.validator.PKIXValidator.engineValidate(PKIXValidator.java:302)
        at sun.security.validator.Validator.validate(Validator.java:262)
        at sun.security.ssl.X509TrustManagerImpl.validate(X509TrustManagerImpl.java:330)
        at sun.security.ssl.X509TrustManagerImpl.checkTrusted(X509TrustManagerImpl.java:237)
        at sun.security.ssl.X509TrustManagerImpl.checkServerTrusted(X509TrustManagerImpl.java:132)
        at sun.security.ssl.ClientHandshaker.serverCertificate(ClientHandshaker.java:1621)
        ... 9 more
Caused by: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
        at sun.security.provider.certpath.SunCertPathBuilder.build(SunCertPathBuilder.java:141)
        at sun.security.provider.certpath.SunCertPathBuilder.engineBuild(SunCertPathBuilder.java:126)
        at java.security.cert.CertPathBuilder.build(CertPathBuilder.java:280)
        at sun.security.validator.PKIXValidator.doBuild(PKIXValidator.java:392)
        ... 15 more
```
