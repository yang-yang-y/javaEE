package com.bluemsun.interceptor;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bluemsun.cache.JedisUtil;
import com.bluemsun.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 系统登录验证拦截器
 */

public class LoginInterceptor extends HandlerInterceptorAdapter {
	/**
	 * 主要做事前拦截，即用户操作发生前，改写preHandle里的逻辑，进行拦截
	 */
	@Autowired
	private JedisUtil.Keys jedisUtilKeys;
	@Autowired
	private JedisUtil.Strings jedisUtilStrings;
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String token = request.getHeader("token");
		if (token!=null) {
			if (jedisUtilKeys.exists(token))
		{
			String userString = jedisUtilStrings.get(token);
			JSONObject userJSON = JSON.parseObject(userString);
			User user = JSON.toJavaObject(userJSON,User.class);
			// 做空值判断，确保userId不为空并且该帐号的可用状态为
			if (user != null && user.getUserId() != null && user.getUserId() > 0 && user.getAvailable() == 1)
				// 若通过验证则返回true,拦截器返回true之后，用户接下来的操作得以正常执行
				return true;}
		}
		// 若不满足登录验证，则直接跳转到帐号登录页面
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<script>");
		out.println("window.location.href=\"http://bluemsun.work:8080/Login\"");
		out.println("</script>");
		out.println("</html>");
		return false;
	}
}
