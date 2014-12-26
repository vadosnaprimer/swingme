/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.yura.mobile.logging;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/**
 *
 * @author Administrator
 */
public class CallStack
{
	private static final int
		INT_MASK = 0xF0,
		OBJECT_MASK = 0xF,
		ARGS_MASK = 0xFF00;
	private static final int
		STACK_SIZE = 512,
		MAX_THREADS = 16;
	private static CallStack[]
		callStacks = new CallStack[MAX_THREADS*2];
	private static RecordStore
		crashMonitor;

	private final Thread
		thread;
  final int
    index;
	private int
		intPtr,
		objPtr;
  public int
    line;
	private final int[]
		intStack = new int[STACK_SIZE];
	private final Object[]
		objStack = new Object[STACK_SIZE];
  StringBuffer
     stacktrace = new StringBuffer();

	public static boolean startCrashMonitor()
	{
		try
		{
			crashMonitor = RecordStore.openRecordStore("crash_log", true);
  		return crashMonitor.getNumRecords()>0;
		}
		catch(RecordStoreException e)
		{
			//#debug debug
			Logger.warn(null, e);
		}
    return false;
	}

	public static byte[][] getCrashLog()
	{
		try
		{
      byte[][] crashLog = new byte[crashMonitor.getNumRecords()][];
      for(int i=0; i<crashMonitor.getNumRecords(); i++)
        crashLog[i] = crashMonitor.getRecord(i);
      return crashLog;
		}
		catch(RecordStoreException e)
		{
			//#debug debug
			Logger.warn(null, e);
		}
    return null;
	}

  public static void writeCrashLog(String dir)
  {
    try
    {
      long time = System.currentTimeMillis();
			for(int i=0; i<crashMonitor.getNumRecords(); i++)
      {
        FileConnection file = (FileConnection)Connector.open(dir + time + "_" + i + ".tra", Connector.WRITE);
        file.create();
        file.openOutputStream().write(crashMonitor.getRecord(i));
        file.close();
      }
    }
		catch(Exception e)
		{
			//#debug debug
			Logger.warn("cant write to " + dir, e);
		}
  }

	public static void stopCrashMonitor()
	{
		try
		{
			crashMonitor.closeRecordStore();
			crashMonitor = null;
			RecordStore.deleteRecordStore("crash_log");
		}
		catch(RecordStoreException e)
		{
			//#debug debug
			Logger.warn(null, e);
		}
	}

  public static String createStacktrace()
  {
    CallStack callStack = getCallStack();
    callStack.printStacktrace(Thread.currentThread());
		return callStack.stacktrace.toString();
  }

	public static String getStacktrace()
	{
    CallStack stack = getCallStack();
		if(stack.intPtr==0)
      callStacks[stack.index] = null;
		return stack.stacktrace.toString();
	}

	private static CallStack getCallStack()
	{
		int i = Math.abs(Thread.currentThread().hashCode()%MAX_THREADS), j = i;
		for(; i<callStacks.length; i++)
			if(callStacks[i]!=null && callStacks[i].thread==Thread.currentThread())
				return callStacks[i];
		synchronized(CallStack.class)
		{
			while(callStacks[j]!=null)
				j++;
			callStacks[j] = crashMonitor==null ? new CallStack(Thread.currentThread(), j) : new MonitoredCallStack(Thread.currentThread(), j);
			return callStacks[j];
		}
	}

	CallStack(Thread thread, int index)
	{
		this.thread = thread;
    this.index = index;
	}

	void pushMethod(int method, int args)
	{
		intStack[intPtr++] = (line<<16)|args;
		intStack[intPtr++] = method;
	}

  void pushInt(int value)
  {
		intStack[intPtr++] = value;
  }

  void pushObject(Object object)
  {
		objStack[objPtr++] = object;
  }

	public void pop()
	{
    for(intPtr -=2; (intStack[intPtr]&OBJECT_MASK)>0; intStack[intPtr]--)
      objStack[--objPtr] = null;
    line = intStack[intPtr]>>16;
    intPtr -= (intStack[intPtr]&INT_MASK)>>4;
		if(intPtr==0)
      callStacks[index] = null;
	}

  public void unwind(Throwable t, int method)
  {
    printStacktrace(t);
		while(intStack[intPtr-1]!=method)
    {
      for(intPtr -=2; (intStack[intPtr]&OBJECT_MASK)>0; intStack[intPtr]--)
        objStack[--objPtr] = null;
      intPtr -= (intStack[intPtr]&INT_MASK)>>4;
    }
  }

  void printStacktrace(Object message)
	{
    stacktrace.setLength(0);
    stacktrace.append(message.toString().replace('\n', ' ')).append('\n');
    for(int intPtr = this.intPtr, objPtr = this.objPtr; intPtr>0;)
    {
      stacktrace.append("\tat ").append(Integer.toString(intStack[--intPtr], Character.MAX_RADIX)).append("(?:").append(line).append(')').append('\n');
      line = intStack[--intPtr]>>16;
      switch(intStack[intPtr]&ARGS_MASK)
      {
//          case 0:
//            args.append(intStack[--intPtr]);
//            break;
//          case 1:
//            args.append(objStack[--objPtr]);
//            break;
//          case 2:
//            args.append(intStack[--intPtr]).append(", ").append(intStack[--intPtr]);
//            break;
//          case 3:
//            append(args.append(intStack[--intPtr]).append(", "), objStack[--objPtr]);
//            break;
//          case 4:
//            append(args, objStack[--objPtr]).append(", ").append(intStack[--intPtr]);
//            break;
//          case 5:
//            append(append(args, objStack[--objPtr]).append(", "), objStack[--objPtr]);
//            break;
      }
    }
	}

	private static StringBuffer append(StringBuffer buffer, Object object)
	{
		if(object instanceof String)
			buffer.append('"').append(object).append('"');
		else if(object instanceof Vector)
		{
			buffer.append('{');
			Vector vector = (Vector)object;
			for(int i=0; i<vector.size(); i++)
				append(buffer, vector.elementAt(i)).append(", ");
			buffer.delete(buffer.length()-2, buffer.length()).append('}');
		}
		else if(object instanceof Hashtable)
		{
			buffer.append('{');
			Hashtable hashtable = (Hashtable)object;
			for(Enumeration e = hashtable.keys(); e.hasMoreElements();)
			{
				Object key = e.nextElement();
				append(append(buffer, key).append(" => "), hashtable.get(key)).append(", ");
			}
			buffer.delete(buffer.length()-2, buffer.length()).append('}');
		}
		else if(object instanceof int[])
		{
			buffer.append('{');
			int[] array = (int[])object;
			for(int i=0; i<array.length; i++)
				buffer.append(array[i]).append(", ");
			buffer.delete(buffer.length()-2, buffer.length()).append('}');
		}
		else if(object instanceof Object[])
		{
			buffer.append('{');
			Object[] array = (Object[])object;
			for(int i=0; i<array.length; i++)
				append(buffer, array[i]).append(", ");
			buffer.delete(buffer.length()-2, buffer.length()).append('}');
		}
		else
			buffer.append(object);
		return buffer;
	}

	private static class MonitoredCallStack extends CallStack
	{

		MonitoredCallStack(Thread thread, int id)
		{
			super(thread, id);
		}

		void pushMethod(int method, int args)
		{
			super.pushMethod(method, args);
			saveStacktrace();
		}

		public void pop()
		{
			super.pop();
			saveStacktrace();
		}

		public void unwind(Throwable t, int method)
		{
			super.unwind(t, method);
			saveStacktrace();
		}

		private void saveStacktrace()
		{
			try
			{
        printStacktrace(Thread.currentThread());
				byte[] bytes = stacktrace.toString().getBytes();
				if(crashMonitor.getNextRecordID()>index)
					crashMonitor.setRecord(index, bytes, 0, bytes.length);
				else synchronized(getClass())
				{
					while(crashMonitor.getNextRecordID()<index)
						crashMonitor.addRecord(null, 0, 0);
					crashMonitor.addRecord(bytes, 0, bytes.length);
				}
			}
			catch(RecordStoreException e)
			{
				//#debug debug
				Logger.warn(null, e);
			}
		}
	}
}
