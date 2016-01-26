package com.handpay.arch.log4jx;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Layout;
import org.apache.log4j.helpers.QuietWriter;

import com.google.common.collect.Lists;

/**
 * 定时输出重复日志任务类
 * @author sxjiang
 *
 */
public class LogOutputTask implements Runnable {
	/**
	 * 重复次数。在定时输出模式下，只有重复次数超过该值才会输出
	 */
	private int logRepeatTimes = 1;
	private Map<String, LogValue> logMap;
	protected QuietWriter quietWriter;
	protected boolean immediateFlush = true;
	protected Layout layout;
	
	@Override
	public void run() {
		Collection<LogValue> collection = logMap.values();
		List<LogValue> list = Lists.newArrayList();
		for(LogValue lv : collection){
			if(lv.getDisplayTimes() > logRepeatTimes){
				list.add(lv);
			}
		}
		for(LogValue lv : list){
			doLogAppend(lv);
			logMap.remove(lv.getKey());
		}
	}
	
	private void doLogAppend(LogValue logValue) {
		this.quietWriter.write(Layout.LINE_SEP);
		this.quietWriter.write("##############^定时统一输出BEGIN^############");
		this.quietWriter.write(Layout.LINE_SEP);
		
		this.quietWriter.write(logValue.getContent());
		
		this.quietWriter.write("############^定时统一输出END， log above repeated:"+logValue.getDisplayTimes()+" tims.^############");
		this.quietWriter.write(Layout.LINE_SEP);
		this.quietWriter.write(Layout.LINE_SEP);

		if (this.immediateFlush) {
			this.quietWriter.flush();
		}
	}

	public Map<String, LogValue> getLogMap() {
		return logMap;
	}

	public void setLogMap(Map<String, LogValue> logMap) {
		this.logMap = logMap;
	}

	public int getLogRepeatTimes() {
		return logRepeatTimes;
	}

	public void setLogRepeatTimes(int logRepeatTimes) {
		this.logRepeatTimes = logRepeatTimes;
	}

	public QuietWriter getQuietWriter() {
		return quietWriter;
	}

	public void setQuietWriter(QuietWriter quietWriter) {
		this.quietWriter = quietWriter;
	}

	public boolean isImmediateFlush() {
		return immediateFlush;
	}

	public void setImmediateFlush(boolean immediateFlush) {
		this.immediateFlush = immediateFlush;
	}

	public Layout getLayout() {
		return layout;
	}

	public void setLayout(Layout layout) {
		this.layout = layout;
	}
}
