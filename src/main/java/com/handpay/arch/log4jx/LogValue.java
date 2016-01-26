package com.handpay.arch.log4jx;

import java.util.Date;

public class LogValue {
	private String key;
	private int displayTimes;
	private Date beginDate;
	private String content;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getDisplayTimes() {
		return displayTimes;
	}

	public void setDisplayTimes(int displayTimes) {
		this.displayTimes = displayTimes;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
