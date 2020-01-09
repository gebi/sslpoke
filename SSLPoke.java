/* Author: Michael Gebetsroither <michael@mgeb.org>
 * License: Apache 2
 */
import java.net.Socket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLParameters;
import java.io.*;

/** Establish a SSL connection to a host and port, writes a byte and
 * prints the response. See
 * http://confluence.atlassian.com/display/JIRA/Connecting+to+SSL+services
 */
public class SSLPoke {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: "+SSLPoke.class.getName()+" <host> <port>");
            System.out.println("   use proxy with: -Dhttps.proxyHost=<proxy_host> -Dhttps.proxyPort=<proxy_port>");
            System.exit(1);
        }
        try {
            String host = args[0];
            int port = Integer.parseInt(args[1]);
            SSLSocket sslsocket;

            SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

            String tunnel_host = System.getProperty("https.proxyHost");
            Integer tunnel_port_i = Integer.getInteger("https.proxyPort");
            int tunnel_port = 0;

            if (tunnel_host != null && tunnel_port_i != null) {
                // tls through tunnel
                tunnel_port = tunnel_port_i.intValue();
                System.err.println("Using proxy: " + tunnel_host + ":" + tunnel_port);
                Socket tunnel = new Socket(tunnel_host, tunnel_port);
                open_http_connect_tunnel(tunnel, tunnel_host, tunnel_port, host, port);
                sslsocket = (SSLSocket) sslsocketfactory.createSocket(tunnel, host, port, true);
            } else {
                // tls direct connect
                sslsocket = (SSLSocket) sslsocketfactory.createSocket(args[0], Integer.parseInt(args[1]));
            }

            // add full verification currently just https style
            SSLParameters sslparams = new SSLParameters();
            sslparams.setEndpointIdentificationAlgorithm("HTTPS");
            sslsocket.setSSLParameters(sslparams);

            InputStream in = sslsocket.getInputStream();
            OutputStream out = sslsocket.getOutputStream();

            // Write a test byte to get a reaction
            out.write(1);

            while (in.available() > 0) {
                System.out.print(in.read());
            }
            System.out.println("Successfully connected");

        } catch (Exception exception) {
            exception.printStackTrace();
            System.exit(1);
        }
    }

    private static void open_http_connect_tunnel(Socket tunnel, String tunnel_host, int tunnel_port, String host, int port)
    throws IOException
    {
        OutputStream out = tunnel.getOutputStream();
        String msg = "CONNECT " + host + ":" + port + " HTTP/1.1\n"
                     + "User-Agent: " + sun.net.www.protocol.http.HttpURLConnection.userAgent + "\r\n"
                     + "\r\n";
        byte b[];
        try {
            b = msg.getBytes("ASCII7");
        } catch (UnsupportedEncodingException ignored) {
            b = msg.getBytes();
        }
        out.write(b);
        out.flush();

        byte            reply[] = new byte[200];
        int             replyLen = 0;
        int             newlinesSeen = 0;
        boolean         headerDone = false;     /* Done on first newline */

        InputStream     in = tunnel.getInputStream();
        boolean         error = false;

        while (newlinesSeen < 2) {
            int i = in.read();
            if (i < 0) {
                throw new IOException("Unexpected EOF from proxy");
            }
            if (i == '\n') {
                headerDone = true;
                ++newlinesSeen;
            } else if (i != '\r') {
                newlinesSeen = 0;
                if (!headerDone && replyLen < reply.length) {
                    reply[replyLen++] = (byte) i;
                }
            }
        }

        String replyStr;
        try {
            replyStr = new String(reply, 0, replyLen, "ASCII7");
        } catch (UnsupportedEncodingException ignored) {
            replyStr = new String(reply, 0, replyLen);
        }

        // check if connect request was successfully
        if (!(replyStr.startsWith("HTTP/1.0 200") || replyStr.startsWith("HTTP/1.1 200"))) {
            throw new IOException("Unable to tunnel through " + tunnel_host + ":" + tunnel_port + ".  Proxy returns \"" + replyStr + "\"");
        }

        // success
    }
}
