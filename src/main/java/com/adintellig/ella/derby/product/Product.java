package com.adintellig.ella.derby.product;

import java.math.BigDecimal;
import java.sql.Date;

// As it stands, this class is somewhat incomplete. 
// Formally, it should include full bean getter/setter methods.

public class Product {
	
	private int itemNumber = 0 ;
	private BigDecimal price = null ;
	private Date stockDate = null ;
	private String description = null ;
	
	public Product(int itemNumber, BigDecimal price, Date stockDate, String description){
		this.itemNumber = itemNumber ;
		this.price = price ;
		this.stockDate = stockDate ;
		this.description = description ;
	}
	
	// This helper method prints out a product in a nice, formatted manner
	
	public void printProduct() {
		String line = "------------------------------------" ;
		
		System.out.println("\nBigdog's Surf Shop Product Information") ;
		System.out.println(line + line);
		
		System.out.printf("Item Number      : %-11s\n", this.itemNumber) ;
		System.out.printf("Item Price       : $%-8.2f\n", this.price) ;
		System.out.printf("Item StockDate   : %-10s\n", this.stockDate) ;
		System.out.printf("Item Description : %-40s\n", this.description) ;
		
		System.out.println(line + line + "\n");
	}
	
	// This method is used solely for test purposes
	
	public static void main(String[] args) {

		BigDecimal aPrice = new BigDecimal(40.00) ;
		Date aDate = new Date(System.currentTimeMillis()) ;
		
		Product aProduct = new Product(1, aPrice, aDate, "This is a test!") ;
		aProduct.printProduct() ;
	}
}