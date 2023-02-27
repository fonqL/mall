package com.example.demo.interceptor;

import com.example.demo.util.JwtUtil;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor implements HandlerInterceptor {

	public JwtUtil jwtUtil;

	public LoginInterceptor(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	// @Override
	// public boolean preHandle(HttpServletRequest request,
	//                          @NotNull HttpServletResponse response,
	//                          @NotNull Object handler) throws Exception {
	// 	String jwt = request.getHeader("Auth");
	// 	if (jwt == null || jwt.isEmpty()) {
	// 		// response.sendRedirect(request.getContextPath() + "/login.html");
	// 		return false;
	// 	}
	// 	Claims claims = jwtUtil.parseJwt(jwt);
	// 	if (jwtUtil.isExpired(claims)) {
	// 		// response.sendRedirect(request.getContextPath() + "/login.html");
	// 		return false;
	// 	}
	// 	// response.setHeader("Auth", jwtUtil.refreshToken(claims));
	// 	return true;
	// }
}
