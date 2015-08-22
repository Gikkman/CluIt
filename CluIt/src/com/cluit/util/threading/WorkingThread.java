package com.cluit.util.threading;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.cluit.util.Const;
import com.cluit.util.AoP.MethodMapper;	

public abstract class WorkingThread extends Thread {
	public static enum Message {INIT_MESSAGE, CLUSTER_MESSAGE, TERMINATE_MESSAGE };
	
	private boolean mIsAlive = true;
	private final BlockingQueue<Message> mQueue = new LinkedBlockingQueue<>();
	
	public WorkingThread(){
		this.setDaemon(true);
	}
	
	@Override
	public void run() {
		super.run();
		while( mIsAlive){
			try {
				Message message = mQueue.poll(1, TimeUnit.SECONDS);
				if( message != null ) 
					handleMessage( message );			
			} catch (InterruptedException e) {
				MethodMapper.invoke(Const.METHOD_EXCEPTION_GENERAL, "A WorkingThread was interupted for an unknown reason", e);
			}
		}		
	}

	private void handleMessage(Message message) {
		switch (message) {
		case INIT_MESSAGE:
			doInit();
			break;
		case CLUSTER_MESSAGE:
			doWork();
			break;
		default:
			doTerminate();
		}
	}
	
	protected void doTerminate() {
		mIsAlive = false;
	}

	protected abstract void doInit();
	protected abstract void doWork();
}
