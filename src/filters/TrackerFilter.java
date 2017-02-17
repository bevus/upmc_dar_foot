package filters;

import utils.HelperFunctions;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class TrackerFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String url = request.getRequestURI();
        String queryString = request.getQueryString();
        String token = HelperFunctions.getSHA1(UUID.randomUUID().toString());
        String previousToken = request.getHeader("If-None-Match");

        if(previousToken == null){
            response.setHeader("ETag", token);
            response.setDateHeader("Last-Modified", System.currentTimeMillis());
            response.setContentType("images/gif");
            response.getOutputStream().write(0);
        }else{
            token = previousToken;
            response.setHeader("ETag", previousToken);
            response.setHeader("Last-Modified", request.getHeader("If-Modified-Since"));
            response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
        }
        HelperFunctions.saveTrace(req.getServletContext(), token, url, queryString);
        chain.doFilter(req, res);
    }

    public void init(FilterConfig config) throws ServletException {}

}
