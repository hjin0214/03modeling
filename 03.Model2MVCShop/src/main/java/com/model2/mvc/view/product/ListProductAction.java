package com.model2.mvc.view.product;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.framework.Action;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.impl.ProductServiceImpl;
import com.model2.mvc.service.user.UserService;
import com.model2.mvc.service.user.impl.UserServiceImpl;


public class ListProductAction extends Action {

	@Override
	public String execute(	HttpServletRequest request,
												HttpServletResponse response) throws Exception {
		Search search=new Search();
		
		int currentPage=1;

		if(request.getParameter("currentPage") != null) {
			currentPage=Integer.parseInt(request.getParameter("currentPage"));
		System.out.println("현재페이지 : "+currentPage);
		}
		
		search.setCurrentPage(currentPage);
		search.setSearchCondition(request.getParameter("searchCondition"));
		search.setSearchKeyword(request.getParameter("searchKeyword"));
		System.out.println(request.getParameter("searchCondition"));
		System.out.println("searchCondition 위 값");
		
		//searchVO.setSearchKeyword(URLDecoder.decode((String)request.getParameter("searchKeyword", "UTF-8")));
		
		//String searchKeyword = request.getParameter("searchKeyword");
		//searchKeyword = URLDecoder.decode(searchKeyword, "UTF-8");
		//System.out.println("써치키워드 : "+searchKeyword);
		
		System.out.println(request.getParameter("searchKeyword"));
		System.out.println("searchKeyword 위 값");
		
		int pageSize = Integer.parseInt( getServletContext().getInitParameter("pageSize"));
		int pageUnit  =  Integer.parseInt(getServletContext().getInitParameter("pageUnit"));
		search.setPageSize(pageSize);
		
		ProductService service=new ProductServiceImpl();
		Map<String,Object> map=service.getProductList(search);
		System.out.println("맵이 문제?:"+map);
		System.out.println("뭘까:"+((Integer)map.get("totalCount")).intValue());
		
		Page resultPage	= 
				new Page( currentPage, ((Integer)map.get("totalCount")).intValue(), pageUnit, pageSize);
		System.out.println("ListProductAction ::"+resultPage);

		request.setAttribute("list", map.get("list"));
		request.setAttribute("resultPage", resultPage);
		request.setAttribute("search", search);
		request.setAttribute("menu", request.getParameter("menu"));
		
		System.out.println("페이지나와라 : "+resultPage);
		System.out.println("ListproductAction : "+search);
		
		return "forward:/product/listProduct.jsp";
	}
}