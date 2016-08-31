package com.excel.exceltimertask;

import java.util.TimerTask;

public class ExcelTimerTask extends TimerTask {
	
	private boolean hasStarted = false;
	
	@Override
	public void run() {
		this.hasStarted = true;
	}
	
	public boolean isRunning() {
        return this.hasStarted;
    }
	
	public void stopTimer(){
		this.hasStarted = false;
	}
	
	
}
