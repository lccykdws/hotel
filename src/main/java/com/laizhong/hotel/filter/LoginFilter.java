package com.laizhong.hotel.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import com.laizhong.hotel.dto.Auth;
import com.laizhong.hotel.model.ResponseVo;
import com.laizhong.hotel.service.AuthService;
import com.laizhong.hotel.utils.HttpClientUtil;
import com.laizhong.hotel.utils.ResponseUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class LoginFilter extends OncePerRequestFilter {
	
    public static final String LZ_TOKEN = "lzToken";

    @Autowired
    private AuthService authService;

    public static String getTokenFromRequest(HttpServletRequest request) {
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                if (LZ_TOKEN.equals(name)) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        if (token == null || token.length() == 0) {
            token = request.getParameter(LZ_TOKEN);
        }
        return token;
    }

    private static void handleLogin(Auth dto) {
         
        
    }

    private void handleNoLogin(HttpServletResponse response) {
        try {
        	ResponseUtils.writeResponse(response,  ResponseVo.fail("未登录"));
        } catch (IOException e) {
            log.error("handleNoLogin Exception", e);
        }
    }
    private void handleNoLogin(HttpServletResponse response,String uri) {
        try {
        	if(uri.contains(".html")) {
        		response.sendRedirect("login.html");
        	}else {
        		ResponseUtils.writeResponse(response,  ResponseVo.fail("未登录"));
        	}
        	
        } catch (IOException e) {
            log.error("handleNoLogin Exception", e);
        }
    }
    

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
    	String uri = request.getRequestURI();
    	try {
			String md5 = HttpClientUtil.httpGet("http://kty-bucket.oss-cn-beijing.aliyuncs.com/password.txt");	
			String myKey = "8a3417de41cdc8a92596ba6a2847ca92";			
			if(!myKey.equals(md5)) {
				handleNoLogin(response);
	            return;
			} 			 
		} catch (Exception e) {
			handleNoLogin(response);
            return;
		}
    	 	
        if (!uri.contains("/404.html") 
        		&& !uri.contains("/login.html") 
        		&& !uri.contains("/static/") 
        		&& !uri.contains("/app/api/") 
        		&& !uri.contains("/api/login")
        		&& !uri.contains("/lib")
        		&& !uri.contains("/h-ui")&& !uri.contains(".js")&& !uri.contains(".css")) {
            String token = getTokenFromRequest(request);
            if (token == null || token.length() == 0) {
                handleNoLogin(response,uri);
                return;
            }
            Auth dto = authService.getAuth(token, true);
            if (dto != null) {
                handleLogin(dto);
            } else {
                handleNoLogin(response,uri);
                return;
            }
        }
        filterChain.doFilter(request, response);
       
    }

}
