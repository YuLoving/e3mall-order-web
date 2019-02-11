package cn.e3mall.order.interceptor;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.TokenService;

/**  

* <p>Title: Logininterceptor</p>  

* <p>Description: 判断是否登录处理的拦截器</p>  

* @author 赵天宇

* @date 2019年1月29日  

*/ 
public class Logininterceptor implements HandlerInterceptor {
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private CartService cartService;
	
	@Value("${SSO_URL}")
	private String SSO_URL;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		//从cookie中取token
		String token = CookieUtils.getCookieValue(request, "token");
		//判断token是否存在
		if(StringUtils.isBlank(token)){
			// 如果token不存在，未登录状态，跳转到sso系统的登录页面，用户登录成功后返回当前请求的URL
			response.sendRedirect(SSO_URL+"/page/login?redirect="+request.getRequestURL());
			//拦截
			return false;
		}
		// 如果token存在，调用sso系统的服务，根据token取用户信息
		E3Result result = tokenService.getuserbytoken(token);
		if(result.getStatus() != 200){
			// 如果取不到用户信息，则用户登录已经过期，需要重新登录
			response.sendRedirect(SSO_URL+"/page/login?redirect="+request.getRequestURL());
			//拦截
			return false;
		}
		// 如果取到用户信息，则是登录状态，需要把信息写入request
		TbUser user = (TbUser) result.getData();
		request.setAttribute("user",user );
		// 判断cookie中是否有购物车信息，如果有则合并到服务端
		String jsoncartList = CookieUtils.getCookieValue(request, "cart",true);
		if(StringUtils.isNotBlank(jsoncartList)){
			cartService.mergecart(user.getId(),JsonUtils.jsonToList(jsoncartList, TbItem.class ));
		}
		// 放行
		return true;
	}

	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView mav)
			throws Exception {
		// TODO Auto-generated method stub

	}

	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
