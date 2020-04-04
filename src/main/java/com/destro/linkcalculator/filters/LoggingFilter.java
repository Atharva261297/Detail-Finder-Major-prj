package com.destro.linkcalculator.filters;

import org.apache.commons.io.output.TeeOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.DelegatingServletOutputStream;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.UUID;

@Component
public class LoggingFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException { }

    @Override
    public void doFilter(final ServletRequest servletRequest,
                         final ServletResponse servletResponse,
                         final FilterChain filterChain) throws IOException, ServletException {

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final PrintStream ps = new PrintStream(outputStream);

        final HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;

        final HttpServletResponseWrapper responseWrapperForLogging =
                new HttpServletResponseWrapper((HttpServletResponse) servletResponse) {
                    @Override
                    public ServletOutputStream getOutputStream() throws IOException {
                        return new DelegatingServletOutputStream(new TeeOutputStream(super.getOutputStream(), ps)
                        );
                    }
                    @Override
                    public  PrintWriter getWriter() throws IOException {
                        return new PrintWriter(new DelegatingServletOutputStream (new TeeOutputStream(super.getOutputStream(), ps))
                        );
                    }
                };

        final UUID requestUUID = UUID.randomUUID();

        logger.info("Incoming[{}] request-URI: {}; request-method: {};", requestUUID,
                httpRequest.getRequestURI(), httpRequest.getMethod());

        filterChain.doFilter(servletRequest, responseWrapperForLogging);

        logger.info("Outgoing[{}] response-body: {};", requestUUID, outputStream);

    }

}
