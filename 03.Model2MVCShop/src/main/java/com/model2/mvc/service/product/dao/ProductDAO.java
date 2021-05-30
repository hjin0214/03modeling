package com.model2.mvc.service.product.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.model2.mvc.common.Search;
import com.model2.mvc.common.util.DBUtil;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.User;


public class ProductDAO {
	
	public ProductDAO(){
	}

	public void insertProduct(Product product) throws Exception {
		
		Connection con = DBUtil.getConnection();

		String sql = "INSERT INTO product VALUES (seq_product_prod_no.nextval, ?, ?, ?, ?, ?, SYSDATE)";
		 System.out.println(product);
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setString(5, product.getFileName());
	
		stmt.setString(3, product.getManuDate());
		stmt.setInt(4, product.getPrice());
		stmt.setString(2, product.getProdDetail());
//		System.out.println("name 위");
		stmt.setString(1, product.getProdName());
		
//		System.out.println(productVO.getProdName());
//		stmt.setInt(6, productVO.getProdNo());
//		stmt.setString(8, productVO.getProTranCode());
		
		int a = stmt.executeUpdate();
		System.out.println("쿼리실행결과:"+a);
		
		con.close();
	}

	public Product findProduct(int prodNo) throws Exception {
		
		Connection con = DBUtil.getConnection();

		String sql = "SELECT * FROM product WHERE prod_No=?";
		
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setInt(1, prodNo);

		ResultSet rs = stmt.executeQuery();

		Product product = null;
		while (rs.next()) {
			product = new Product();
			product.setProdName(rs.getString("prod_name"));
			product.setFileName(rs.getString("IMAGE_FILE"));
			product.setManuDate(rs.getString("MANUFACTURE_DAY"));
			product.setPrice(rs.getInt("price"));
			product.setProdDetail(rs.getString("prod_Detail"));
			product.setProdNo(rs.getInt("prod_No"));
			product.setRegDate(rs.getDate("reg_Date"));
//			productVO.setProTranCode(rs.getString("proTranCode"));
		}
		
		con.close();

		return product;
	}

	public Map<String,Object> getProductList(Search search) throws Exception {
		
		Map<String , Object>  map = new HashMap<String, Object>();
	
		Connection con = DBUtil.getConnection();
		
		String sql = "SELECT * FROM product ";
		if (search.getSearchCondition() != null) {
			if (search.getSearchCondition().equals("0")) {
				sql += "WHERE PROD_NO LIKE '%" + search.getSearchKeyword()+"%'";
			} else if (search.getSearchCondition().equals("1")) {
				sql += "WHERE PROD_NAME LIKE '%" + search.getSearchKeyword()+"%'";
				System.out.println("불러오는 키워드 : "+search.getSearchKeyword());
			}else if  (search.getSearchCondition().equals("2")) {
				sql += "WHERE PRICE LIKE '%" + search.getSearchKeyword()+"%'";
			}
		}
		sql += " ORDER BY PROD_NO";

//		PreparedStatement stmt = 
		int totalCount = this.getTotalCount(sql);
		System.out.println("ProductDAO :: totalCount  :: " + totalCount);
		
		//==> CurrentPage 게시물만 받도록 Query 다시구성
		sql = makeCurrentPageSql(sql, search);
		PreparedStatement pStmt = con.prepareStatement(sql);
		ResultSet rs = pStmt.executeQuery();

		List<Product> list = new ArrayList<Product>();
		while(rs.next()){
				Product vo = new Product();
				
				vo.setProdName(rs.getString("prod_Name"));
				vo.setFileName(rs.getString("IMAGE_FILE"));
				vo.setManuDate(rs.getString("MANUFACTURE_DAY"));
				vo.setPrice(rs.getInt("price"));
				vo.setProdDetail(rs.getString("prod_Detail"));
				vo.setProdNo(rs.getInt("prod_No"));
				vo.setRegDate(rs.getDate("reg_Date"));
				//vo.setProTranCode(rs.getString("proTranCode"));

				list.add(vo);
			}
		
		map.put("totalCount", new Integer(totalCount));
		System.out.println("list.size() : "+ list.size());
		map.put("list", list);
		System.out.println("map().size() : "+ map.size());

		rs.close();
		pStmt.close();
		con.close();
			
		return map;
	}

	public void updateProduct(Product product) throws Exception {
		
		Connection con = DBUtil.getConnection();

		String sql = "UPDATE product SET prod_Name=?, PROD_DETAIL=?, MANUFACTURE_DAY=?, price=? WHERE prod_No=?";
		
		PreparedStatement stmt = con.prepareStatement(sql);
		
		stmt.setString(1, product.getProdName());
		stmt.setString(2, product.getProdDetail());
		stmt.setString(3, product.getManuDate());
		stmt.setInt(4, product.getPrice());
		stmt.setInt(5, product.getProdNo());
		
		stmt.executeUpdate();
		System.out.println(stmt);
				
		con.close();
	}
	// 게시판 Page 처리를 위한 전체 Row(totalCount)  return
	private int getTotalCount(String sql) throws Exception {
		
		sql = "SELECT COUNT(*) "+
		          "FROM ( " +sql+ ") countTable";
		
		Connection con = DBUtil.getConnection();
		PreparedStatement pStmt = con.prepareStatement(sql);
		ResultSet rs = pStmt.executeQuery();
		
		int totalCount = 0;
		if( rs.next() ){
			totalCount = rs.getInt(1);
		}
		
		pStmt.close();
		con.close();
		rs.close();
		
		return totalCount;
	}
	
	// 게시판 currentPage Row 만  return 
	private String makeCurrentPageSql(String sql , Search search){
		sql = 	"SELECT * "+ 
					"FROM (		SELECT inner_table. * ,  ROWNUM AS row_seq " +
									" 	FROM (	"+sql+" ) inner_table "+
									"	WHERE ROWNUM <="+search.getCurrentPage()*search.getPageSize()+" ) " +
					"WHERE row_seq BETWEEN "+((search.getCurrentPage()-1)*search.getPageSize()+1) +" AND "+search.getCurrentPage()*search.getPageSize();
		
		System.out.println("ProductDAO :: make SQL :: "+ sql);	
		
		return sql;
	}
}