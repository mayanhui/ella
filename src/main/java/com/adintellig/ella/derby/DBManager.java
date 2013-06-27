package com.adintellig.ella.derby;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.BatchUpdateException;
import java.sql.Statement;
import java.sql.PreparedStatement;
//import java.sql.ResultSet;

import java.math.BigDecimal;
import java.sql.Date;

public class DBManager {
	private static Connection con = null ;	

	private static final String driver = "org.apache.derby.jdbc.EmbeddedDriver" ;
	private static final String url = "jdbc:derby:" ;
	private static final String dbName = "SurfShop" ;

	private static final String createProductsSQL = 
		"CREATE TABLE bigdog.products (" +
		"itemNumber INT NOT NULL," +
		"price DECIMAL(5, 2)," +
		"stockDate DATE," +
		"description VARCHAR(40))" ;

	private static final String insertProductsSQL = 
		"INSERT INTO bigdog.products(itemNumber, price, stockDate, description) " + 
		"VALUES(?, ?, ?, ?)" ;

	private static final int[] itemNumbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10} ;

	private static final BigDecimal[] prices = 
	{new BigDecimal(19.95), new BigDecimal(99.99), new BigDecimal(0.99), 
		new BigDecimal(29.95), new BigDecimal(49.95), new BigDecimal(9.95), 
		new BigDecimal(24.95), new BigDecimal(32.95), 
		new BigDecimal(12.95), new BigDecimal(34.95)} ;

	private static final Date[] dates = 
	{Date.valueOf("2006-03-31"), Date.valueOf("2006-03-29"), 
		Date.valueOf("2006-02-28"), Date.valueOf("2006-02-10"), 
		Date.valueOf("2006-02-20"), Date.valueOf("2006-01-15"),
		Date.valueOf("2005-12-20"), Date.valueOf("2005-12-22"), 
		Date.valueOf("2006-03-12"), Date.valueOf("2006-01-24")} ;

	private static final String[] descriptions = 
	{"Hooded sweatshirt", "Beach umbrella", "", "Male bathing suit, blue", 
		"Female bathing suit, one piece, aqua", "Child sand toy set", 
		"White beach towel", "Blue-stripe beach towel", 
		"Flip-flop", "Open-toed sandal"} ;

	private ProductDAO pdao = null ;

	public DBManager(){
		if(!dbExists()){
			try {
				Class.forName(driver) ;
				con = DriverManager.getConnection(url + dbName + ";create=true");

				processStatement(createProductsSQL) ;

				con.setAutoCommit(false) ;
				batchInsertData(insertProductsSQL) ;
				con.commit() ;

			}catch(BatchUpdateException bue) {
				try{
					con.rollback() ;
					System.err.println("Batch Update Exception: Transaction Rolled Back") ;
					printSQLException((SQLException)bue) ;
				}catch (SQLException se) {
					printSQLException(se) ;
				}
			} catch (SQLException se) {
				printSQLException(se) ;
			} catch(ClassNotFoundException e){
				System.err.println("JDBC Driver " + driver + " not found in CLASSPATH") ;
			}
		}
		pdao = new ProductDAO(con) ;
	}

	public void close() {
		try {
			con = DriverManager.getConnection(url + ";shutdown=true") ;
		}catch(SQLException se) {
			; // Do Nothing. System has shut down.
		}
		con = null ;
	}

	public ProductDAO getProductDAO() {
		return pdao ;
	}

	private Boolean dbExists() {
		Boolean exists = false ;
		try{
			Class.forName(driver) ;
			con = DriverManager.getConnection(url + dbName);
			exists = true ;
		} catch(Exception e){
			; // Do nothing, as DB does not (yet) exist
		}
		return(exists) ;
	}
	
	// We ignore wranings and return counts for simplicity in this demo
	
	private void processStatement(String sql) throws SQLException {

		Statement stmt = con.createStatement() ;
		int count = stmt.executeUpdate(sql) ;

		stmt.close() ;
	}

	// We ignore wranings and return counts for simplicity in this demo
	
	private void batchInsertData(String sql) throws SQLException {

		PreparedStatement stmt = con.prepareStatement(sql) ;

		for(int itemNumber: itemNumbers){
			stmt.setInt(1, itemNumbers[itemNumber - 1]) ;
			stmt.setBigDecimal(2, prices[itemNumber - 1]) ;
			stmt.setDate(3, dates[itemNumber - 1]) ;
			stmt.setString(4, descriptions[itemNumber - 1]) ;
			stmt.addBatch() ;
		}

		int[] counts = stmt.executeBatch() ;

		stmt.close() ;
	}

	public static void main(String[] args) {
		DBManager dbm = new DBManager() ;
		ProductDAO pdao = dbm.getProductDAO() ;
		Product p = pdao.getProduct(1) ;
		p.printProduct() ;
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