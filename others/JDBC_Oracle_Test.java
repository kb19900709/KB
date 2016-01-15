package com.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class JDBC_Oracle_Test {
	
	public static void main(String[] args){
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		
		final String url = "jdbc:oracle:thin:@localhost:1521:XE";
		final String user = "KB";
		final String password = "admin";
		
		String sql = "SELECT * FROM DEPT";

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(url, user, password);
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			rsmd = rs.getMetaData();
			int columnLength = rsmd.getColumnCount();
		
			while(rs.next()){
				System.out.println("***begin "+rs.getRow()+"***");
				for(int i=1;i<=columnLength;i++){
					System.out.println(rs.getString(i));
				}
				System.out.println("***end***\n");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally{
			try {
				rs.close();
				pst.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
