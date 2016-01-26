package com.handpay.arch.log4jx.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class RepeatTest implements Runnable{
	final static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(RepeatTest.class);

	public static void main(String[] args) throws FileNotFoundException, InterruptedException {
		
		
		/*log.info("test msg  info ...");
		log.debug("test msg  debug ...");
		log.trace("test msg  trace ...");*/
		log.warn("test msg  warn ...");
		//log.error("test msg  error ...",new IllegalArgumentException("参数有误"));
		
		for(int j=0;j<100;j++){
			new Thread(new RepeatTest()).start();
		}
		
	}

	private static void rootCause() {
		try {
			FileInputStream fis = new FileInputStream("test");
			fis.close();
		} catch (FileNotFoundException e) {
			log.error("file cannot be null",e);
		} catch (IOException e) {
			log.error("io error",e);
		}
	}

	@Override
	public void run() {
		for(int i=0;i<100;i++){
			rootCause();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
