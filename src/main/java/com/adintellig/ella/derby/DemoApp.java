package com.adintellig.ella.derby;

import java.io.BufferedReader ;
import java.io.InputStreamReader ;

public class DemoApp {
	private DBManager dbm = null ;
	private ProductDAO pdao = null ;
	private int maxItemNumber = 0 ;

	public DemoApp() {
		this.dbm = new DBManager() ;
		this.pdao = dbm.getProductDAO() ;
		this.maxItemNumber = pdao.getMaxItemNumber() ;
	}

	public void showProduct() {

		int itemNumber = 0 ;
		Product p = null ;

		BufferedReader cin = new BufferedReader(new InputStreamReader(System.in)) ;

		while(true) {
			try {
				System.out.print("Enter object item number (0 to Exit): ") ;
				itemNumber = Integer.parseInt(cin.readLine()) ;
			}catch(Exception ex){
				; // Do nothing. In this simple demo we ignore any input errors
			}	
			
			if(itemNumber == 0)
				break ;
			else if ((itemNumber < 0) || (itemNumber > this.maxItemNumber))
				System.out.println ("Invalid product item number, please try again.") ;
			else{
				p = pdao.getProduct(itemNumber) ;
				p.printProduct() ;
			}
		}
		dbm.close() ;
	}

	public static void main(String[] args) {
		DemoApp da = new DemoApp() ;
		da.showProduct() ;
	}
}