package vn.com.nimbus.blog.api.config;

import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;
import reactor.netty.http.server.HttpServer;

@Component
public class NettyWebConfig implements WebServerFactoryCustomizer<NettyReactiveWebServerFactory> {

    @Override
    public void customize(NettyReactiveWebServerFactory factory) {
        factory.addServerCustomizers(new PortCustomizer(ApiPublicConfigLoader.apiInternalConfig.getPort(), ApiPublicConfigLoader.apiInternalConfig.getHost()));
    }

    private static class PortCustomizer implements NettyServerCustomizer {

        private final int port;
        private final String host;

        private PortCustomizer(int port, String host) {
            this.port = port;
            this.host = host;
        }

        @Override
        public HttpServer apply(HttpServer httpServer) {
            return httpServer.port(port).host(host);
        }
    }

}