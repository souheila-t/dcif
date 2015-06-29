package org.nabelab.debug;

import java.util.StringTokenizer;

import org.nabelab.util.Counter;

public class Debugger implements Debuggable {

  /**
   * Constructs a environment.
   */
  public Debugger() {
    debug = new boolean[256];
    for (int i=0; i < debug.length; i++)
      debug[i] = false;
  }

  /**
   * Turns on the specified debug flag. 
   * @param type the debug flag.
   * @param on   true if outputs the specified debug information.
   */
  public void setDebug(int type, boolean on) {
    debug[type] = on;
  }
  
  /**
   * Returns true if the specific debug mode is on.
   * @param c a character represents a debug mode.
   * @return true if the specific debug mode is on.
   */
  public boolean dbg(int c) { 
    return debug[c];
  }
  
  /**
   * Returns true if the specific debug mode is on now.
   * @param c a character represents a debug mode.
   * @return true if the specific debug mode is on now.
   */
  public boolean dbgNow(int c) { 
    if (time == null)
      return debug[c];
    long t = time.value();
    return debug[c] && dbgStart <= t && t <= dbgEnd && t % dbgInterval == 0;
  }
  
  /**
   * Sets the period in which the system prints the debug information. 
   * @param period the string that represents the period such like "xx-yy" or "xx-".
   */
  public void setDbgPeriod(String period)
  {
    period = period.trim();

    if (period.indexOf('-') == -1)
      dbgStart = dbgEnd = Long.parseLong(period);
    else {
      boolean beginning = true;
      StringTokenizer st = new StringTokenizer(period, "-", true);
      while (st.hasMoreTokens()) {
        String token = st.nextToken();
        if (token.equals("-")) {
          beginning = false;
          continue;
        }

        if (beginning) 
          dbgStart = Long.parseLong(token);
        else
          dbgEnd = Long.parseLong(token);
      }
    }
  }

  /**
   * Sets the period in which the system prints the debug information. 
   * @param period the string that represents the period such like "xx-yy" or "xx-".
   */
  public void setDbgPeriod(long from, long to) {
    this.dbgStart = from;
    this.dbgEnd   = to;
  }
  
  /**
   * Sets the interval for printing the debug information. 
   * @param interval the string that represents the interval.
   */
  public void setDbgInterval(String interval)
  {
    dbgInterval = Long.parseLong(interval);
  }

  /**
   * Sets the interval for printing the debug information. 
   * @param interval the interval for printing the debug information. 
   */
  public void setDbgInterval(long interval)
  {
    dbgInterval = interval;
  }

  /** The time step. */
  private Counter time = null;
  /** The debug flags. */
  private boolean[] debug = null;
  /** The start time of the debugging period. */
  private long dbgStart = 0;
  /** The end time of the debugging period. */  
  private long dbgEnd = Long.MAX_VALUE;
  /** The interval for printing the debugging period. */  
  private long dbgInterval = 1;
}
