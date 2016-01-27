# log4jx
To prevent print same logs that may cause system fail in a short time.
Extends from log4j appenders, do something to prevent this happens and print the batch logs with scheduled time or reach the batch logs size.

you must specify **repeatLimit** or **repeatInterval** in your log4j.xml
- repeatLimit stands for how many times can log repeat, then batch print in log steam;
- repeatInterval stands for how long the repeat log print, timeUnit is seconds. 