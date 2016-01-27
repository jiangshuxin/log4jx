package com.handpay.arch.log4jx.key;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.OptionHandler;

public interface RepeatLogKey extends OptionHandler{

	public String build(LoggingEvent event);
}
