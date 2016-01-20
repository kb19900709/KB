package com.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class JDBC_Oracle_Test {
	
	static final String driverPackage = "oracle.jdbc.driver.OracleDriver";
	static final String url = "jdbc:oracle:thin:@localhost:1521:XE";
	static final String user = "KB";
	static final String password = "ADMIN";
	
	public static Connection getConnection() throws ClassNotFoundException, SQLException{
		/**
		 * http://openhome.cc/Gossip/JavaGossip-V2/ConnectDB-JDBC4.htm
		 * The JDBC 4.0 standard support is provided by JDK 1.6 and later versions.
		 */
		Class.forName(driverPackage);
		return DriverManager.getConnection(url, user, password);
	}
	
	public static void closeConn(Connection conn,PreparedStatement pst,ResultSet rs){
		try {
			rs.close();
			pst.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		
		String sql = "SELECT * FROM DEPT";

		try {
			conn = getConnection();
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
			closeConn(conn, pst, rs);
		}
	}
}
