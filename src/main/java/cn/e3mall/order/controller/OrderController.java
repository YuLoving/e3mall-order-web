package cn.e3mall.order.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.order.pojo.OrderInfo;
import cn.e3mall.order.service.OrderService;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;

/**  

* <p>Title: OrderController</p>  

* <p>Description: 订单管理controller</p>  

* @author 赵天宇

* @date 2019年1月29日  

*/
@Controller
public class OrderController {
	@Autowired
	private CartService cartService;
	
	@Autowired
	private OrderService orderService;
	
	
	@RequestMapping("/order/order-cart")
	public String  showorder(HttpServletRequest request){
		//从request中取用户信息
		TbUser user = (TbUser) request.getAttribute("user");
		//根据id取收货地址列表，由于数据库中没有，所以使用假数据
		//根据id取支付方式列表，由于数据库中没有，所以使用假数据
		//根据用户id取购物车列表
		List<TbItem> cartlist = cartService.getCart(user.getId());
		//把购物车列表传递给jsp
		request.setAttribute("cartlist", cartlist);
		//返回页面
		return "order-cart";
	}
	
	
	@RequestMapping(value="/order/create", method=RequestMethod.POST)
	public String createOrder(OrderInfo orderInfo,HttpServletRequest request
			,HttpServletResponse response){
		//取用户信息
		TbUser user = (TbUser) request.getAttribute("user");
		//把用户信息添加到orderInfo
		orderInfo.setBuyerNick(user.getUsername());
		//调用服务生成订单
		E3Result result = orderService.createorder(orderInfo);
		//如果订单生成成功，需要删除购物车
		if(result.getStatus() == 200){
				//清空购物车
				cartService.clearcart(user.getId());
		}
		//把订单号传递给页面
		
		request.setAttribute("orderId", result.getData());
		request.setAttribute("payment", orderInfo.getPayment());
		//返回逻辑视图
		return "success";	
		}
	
	
}
