package com.handpay.arch.log4jx.key;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.spi.LoggingEvent;

import com.google.common.collect.Lists;

public class DefaultRepeatLogKey implements RepeatLogKey {
	public static final String KEY_CONTENT = "content";
	public static final String KEY_ERROR_STACK = "errorStack";
	public static final String KEY_ALL = "all";
	
	private String keyType = KEY_ERROR_STACK;
	private String seperator = "_";
	private int stackDepth = 1;
	
	public DefaultRepeatLogKey() {
		super();
	}

	public DefaultRepeatLogKey(String keyType,String seperator,int stackDepth) {
		super();
		if(keyType != null) this.keyType = keyType;
		if(seperator != null) this.seperator = seperator;
		if(stackDepth > 0) this.stackDepth = stackDepth;
	}

	@Override
	public String build(LoggingEvent event) {
		if(StringUtils.equalsIgnoreCase(KEY_CONTENT, getKeyType())){
			return processContent(event);
		}else if(StringUtils.equalsIgnoreCase(KEY_ERROR_STACK, getKeyType())){
			return processErrorStack(event);
		}else if(StringUtils.equalsIgnoreCase(KEY_ALL, getKeyType())){
			return processAll(event);
		}
		throw new IllegalArgumentException("error keyType="+getKeyType());
	}

	private String processContent(LoggingEvent event) {
		Object msgObj = event.getMessage();
		if(msgObj == null || StringUtils.isEmpty(msgObj.toString())){
			return processErrorStack(event);
		}else{
			return msgObj.toString();
		}
	}

	private String processAll(LoggingEvent event) {
		String msg = null;
		Object msgObj = event.getMessage();
		if(msgObj == null || StringUtils.isEmpty(msgObj.toString())){
			msg = StringUtils.EMPTY;
		}else{
			msg = msgObj.toString();
		}
		
		String[] cut = extractCut(event);
		List<String> cutList = Lists.newArrayList(cut);
		cutList.add(0, msg);
		return StringUtils.join(cutList, getSeperator());
	}

	private String processErrorStack(LoggingEvent event) {
		String[] cut = extractCut(event);
		return StringUtils.join(cut, getSeperator());
	}

	private String[] extractCut(LoggingEvent event) {
		String[] throwArr = event.getThrowableStrRep();
		int realDepth = getStackDepth()>throwArr.length?throwArr.length:getStackDepth();
		String[] cut = Arrays.copyOfRange(throwArr, 0, realDepth);
		return cut;
	}

	@Override
	public void activateOptions() {
		
	}

	public String getKeyType() {
		return keyType;
	}

	public void setKeyType(String keyType) {
		this.keyType = keyType;
	}

	public String getSeperator() {
		return seperator;
	}

	public void setSeperator(String seperator) {
		this.seperator = seperator;
	}

	public int getStackDepth() {
		return stackDepth;
	}

	public void setStackDepth(int stackDepth) {
		this.stackDepth = stackDepth;
	}
}
