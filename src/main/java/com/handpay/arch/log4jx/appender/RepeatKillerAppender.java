package com.handpay.arch.log4jx.appender;

import java.io.Writer;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.map.LRUMap;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

import com.handpay.arch.log4jx.LogOutputTask;
import com.handpay.arch.log4jx.LogValue;

/**
 * 防刷恶意重复日志类。<p>
 * 可选模式：
 * <li>定时输出
 * <li>定量输出
 * <p>模式建议二选一
 * @author sxjiang
 *
 */
public class RepeatKillerAppender extends WriterAppender {
	private long repeatInterval = 0;//interval分钟强制输出一次重复日志(若有)  0表示禁用此功能
	private int repeatLimit = 0;//超过times次重复日志强制输出   0表示禁用此功能

	//1.定义需监控的级别  2.定义日志重复判断条件
	private Level standardLevel = Level.ERROR;//超过这个级别记录
	
	private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
	
	private int lruMapMaxSize = 150;
	private Map<String, LogValue> logMap = Collections.synchronizedMap(new LRUMap(getLruMapMaxSize()));

	public RepeatKillerAppender() {
		super();
	}
	
	@Override
	public synchronized void setWriter(Writer writer) {
		super.setWriter(writer);
		
		extractTask();
	}

	private void extractTask() {
		if(repeatInterval > 0){
			LogOutputTask task = new LogOutputTask();
			task.setImmediateFlush(getImmediateFlush());
			task.setLayout(getLayout());
			task.setLogMap(logMap);
			task.setQuietWriter(qw);
			scheduledExecutorService.scheduleAtFixedRate(task, repeatInterval, repeatInterval, TimeUnit.SECONDS);
		}
	}


	@Override
	protected void subAppend(LoggingEvent event) {
		//判断相同日志的条件：日志内容+异常堆栈
		String[] errStrArr = event.getThrowableStrRep();
		
		String key = event.getMessage().toString();//FIXME 目前判断相同日志的条件：日志内容
		
		if(event.getLevel().isGreaterOrEqual(standardLevel) && errStrArr != null){//超过指定级别   & 形如log(msg,exception)
			if(logMap.containsKey(key)){
				LogValue val = logMap.get(key);
				val.setDisplayTimes(val.getDisplayTimes()+1);
				
				//System.out.println(Thread.currentThread().getName()+"---条件监控："+val.getDisplayTimes()+"<="+repeatLimit);
				if(repeatLimit > 0 && val.getDisplayTimes() >= repeatLimit){//定量统一输出并清除key
					doLogAppend(event,val.getContent());
					logMap.remove(val.getKey());
				}
			}else{//first time
				String content = extractContent(event);
				LogValue val = new LogValue();
				val.setKey(key);
				val.setBeginDate(new Date());
				val.setDisplayTimes(1);
				val.setContent(content);
				logMap.put(key, val);
				super.subAppend(event);//第一次正常打印全部信息+异常
			}
			
		}else{
			super.subAppend(event);//不符合条件 正常打印全部信息+异常
		}
	}
	
	private String extractContent(LoggingEvent event) {
		StringBuilder buffer = new StringBuilder();
		buffer.append(this.layout.format(event));
		if (layout.ignoresThrowable()) {
			String[] s = event.getThrowableStrRep();
			if (s != null) {
				int len = s.length;
				for (int i = 0; i < len; i++) {
					buffer.append(s[i]);
					buffer.append(Layout.LINE_SEP);
				}
			}
		}
		return buffer.toString();
	}

	private void doLogAppend(LoggingEvent event, String content) {
		this.qw.write(Layout.LINE_SEP);
		this.qw.write("##############定量统一输出BEGIN############");
		this.qw.write(Layout.LINE_SEP);
		
		this.qw.write(content);
		
		this.qw.write("############定量统一输出END， log above repeated:"+repeatLimit+" tims.  ############");
		this.qw.write(Layout.LINE_SEP);
		this.qw.write(Layout.LINE_SEP);

		if (this.immediateFlush) {
			this.qw.flush();
		}
	}

	public String getStandardLevel() {
		return standardLevel.toString();
	}

	public void setStandardLevel(String standardLevel) {
		this.standardLevel = Level.toLevel(standardLevel);
	}

	public int getLruMapMaxSize() {
		return lruMapMaxSize;
	}

	public void setLruMapMaxSize(int lruMapMaxSize) {
		this.lruMapMaxSize = lruMapMaxSize;
	}

	public int getRepeatLimit() {
		return repeatLimit;
	}

	public void setRepeatLimit(int repeatLimit) {
		this.repeatLimit = repeatLimit;
	}

	public long getRepeatInterval() {
		return repeatInterval;
	}

	public void setRepeatInterval(long repeatInterval) {
		this.repeatInterval = repeatInterval;
	}
}
