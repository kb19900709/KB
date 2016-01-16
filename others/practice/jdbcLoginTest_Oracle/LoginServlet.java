package com.test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String userID = req.getParameter("userID");
		String passWord = req.getParameter("passWord");
			
		List<String> errMsgList = new ArrayList<String>();
		if(isEmpty(userID)){
			errMsgList.add("請輸入userID");
		}
		
		if(isEmpty(passWord)){
			errMsgList.add("請輸入passWord");
		}
		
		if(errMsgList.size()>0){
			String errMsg = null;
			for(int i=0;i<errMsgList.size();i++){
				if(i!=0){
					errMsg += ","+errMsgList.get(i);
				}else{
					errMsg = errMsgList.get(i);
				}
			}
			
			req.setAttribute("msg", errMsg);
			forWard(req, resp);
		}else{
			//do logging at dao
			String sql = "SELECT * FROM KB_USER WHERE USERID = ? AND PASSWORD = ?";
			
			try {
				Connection conn = JDBC_Oracle_Test.getConnection();
				PreparedStatement psmt = conn.prepareStatement(sql);
				psmt.setString(1, userID.trim());
				psmt.setString(2, passWord.trim());
				ResultSet rs = psmt.executeQuery();
				if(rs.next()){
					String resultUserID = rs.getString(1);
					req.setAttribute("msg", resultUserID+" >>> 歡迎回來");
					JDBC_Oracle_Test.closeConn(conn, psmt, rs);
					forWard(req, resp);				
				}else{
					req.setAttribute("msg", "登入失敗，請確認使用者資訊");
					JDBC_Oracle_Test.closeConn(conn, psmt, rs);
					forWard(req, resp);
				}
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void forWard(HttpServletRequest req,HttpServletResponse resp){
		RequestDispatcher requestDispatcher = req.getRequestDispatcher("login.jsp");
		try {
			requestDispatcher.forward(req, resp);
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean isEmpty(String str){
		if(str!=null && str.trim().length()>0){
			return false;
		}
		return true;
	}
}
