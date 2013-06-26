package com.adintellig.ella.derby;

import java.sql.Connection ;
import java.sql.Statement ;
import java.sql.PreparedStatement ;
import java.sql.ResultSet ;
import java.sql.SQLException ;

import java.math.BigDecimal ;
import java.sql.Date ;

public class ProductDAO {

	private String getProductSQL = 
		"SELECT itemNumber, price, stockDate, description " + 
		"FROM bigdog.products WHERE itemNumber = ?" ;

	private Connection con = null ;
	private PreparedStatement pstmt = null ;
	private int maxItemNumber = 0 ;

	public ProductDAO(Connection theCon){
		this.con = theCon ;	
		try{
			this.pstmt = con.prepareStatement(getProductSQL) ;

			Statement stmt = con.createStatement() ;

			ResultSet rs =
				stmt.executeQuery("SELECT MAX(itemNumber) FROM bigdog.products") ;

			if (rs.next())
				maxItemNumber = rs.getInt(1) ;
			
			rs.close() ;
			stmt.close() ;
			
		} catch(SQLException se) {
			printSQLException(se) ;
		}
	}

	public int getMaxItemNumber(){
		return(this.maxItemNumber) ;
	}

	public Product getProduct(int targetItemNumber){

		Product product = null ;

		try{
			pstmt.clearParameters() ;
			pstmt.setInt(1, targetItemNumber) ;

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				int itemNumber = rs.getInt("itemNumber") ;
				BigDecimal price = rs.getBigDecimal("price") ;
				Date stockDate = rs.getDate("stockDate") ;
				String description = rs.getString("description") ;

				product = new Product(itemNumber, price, stockDate, description) ;
			}
			
			rs.close() ;
			
		} catch(SQLException se) {
			printSQLException(se) ;
		}

		return product;
	}

	private void printSQLException(SQLException se) {
		while(se != null) {

			System.out.print("SQLException: State:   " + se.getSQLState());
			System.out.println("Severity: " + se.getErrorCode());
			System.out.println(se.getMessage());			

			se = se.getNextException();
		}
	}	
}