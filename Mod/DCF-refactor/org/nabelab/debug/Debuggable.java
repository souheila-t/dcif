package org.nabelab.debug;

public interface Debuggable {
  
  /**
   * Turns on the specified debug flag. 
   * @param type the debug flag.
   * @param on   true if outputs the specified debug information.
   */
  public void setDebug(int type, boolean on);
  
  /**
   * Returns true if the specific debug mode is on.
   * @param c a character represents a debug mode.
   * @return true if the specific debug mode is on.
   */
  public boolean dbg(int c); 
  
  /**
   * Returns true if the specific debug mode is on now.
   * @param c a character represents a debug mode.
   * @return true if the specific debug mode is on now.
   */
  public boolean dbgNow(int c); 
  
  /**
   * Sets the period in which the system prints the debug information. 
   * @param period the string that represents the period such like "xx-yy" or "xx-".
   */
  public void setDbgPeriod(String period);

  /**
   * Sets the period in which the system prints the debug information. 
   * @param period the string that represents the period such like "xx-yy" or "xx-".
   */
  public void setDbgPeriod(long from, long to);
  
  /**
   * Sets the interval for printing the debug information. 
   * @param interval the string that represents the interval.
   */
  public void setDbgInterval(String interval);

  /**
   * Sets the interval for printing the debug information. 
   * @param interval the interval for printing the debug information. 
   */
  public void setDbgInterval(long interval);

}
