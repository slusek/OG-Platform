/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.util;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A base class for any job which consists of cycles of work, and may be terminated between any two cycles. This
 * implements {@link Runnable} so that it may be executed in its own thread.
 */
public abstract class TerminatableJob implements Runnable {

  /**
   * A flag to indicate whether the job has been started
   */
  private AtomicBoolean _started = new AtomicBoolean(false);
  
  /**
   * A flag to indicate whether the job has terminated.
   */
  private volatile boolean _terminated;

  /**
   * Implements {@code Runnable} to add termination support.
   * To add behaviour, use the methods {@link #preStart()}, {@link #runOneCycle()} and {@link #postRunCycle()}.
   */
  @Override
  public final void run() {
    if (_started.getAndSet(true)) {
      throw new IllegalStateException("Job has already been run or is currently running");
    }
    
    preStart();
    while (!isTerminated()) {
      runOneCycle();
    }
    postRunCycle();
  }

  /**
   * Invoked by {@link #run()} once, immediately before the cycle starts.
   */
  protected void preStart() {
  }

  /**
   * Invoked by {@link #run()} to perform one cycle of the job.
   * Override this to implement actual behavior.
   */
  protected abstract void runOneCycle();

  /**
   * Invoked by {@link #run()} once, after the job is terminated and the last cycle completes.
   */
  protected void postRunCycle() {
  }

  //-------------------------------------------------------------------------
  /**
   * Terminates the job after the current cycle completes.
   */
  public void terminate() {
    _terminated = true;
  }

  /**
   * Gets whether the job has been started.
   * 
   * @return {@code true} if the job has been started, {@code false} otherwise
   */
  public boolean isStarted() {
    return _started.get();
  }
  
  /**
   * Gets whether the job has been terminated.
   * 
   * @return {@code true} if the job has been terminated, {@code false} otherwise
   */
  public boolean isTerminated() {
    return _terminated;
  }

}
