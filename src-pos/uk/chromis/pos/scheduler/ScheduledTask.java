/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.chromis.pos.scheduler;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.MINUTES;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.scripting.ScriptEngine;
import uk.chromis.pos.scripting.ScriptException;
import uk.chromis.pos.scripting.ScriptFactory;
import uk.chromis.pos.util.AltEncrypter;

/**
 *
 * @author John
 */
public class ScheduledTask implements Runnable {

    private final ScheduledExecutorService m_Scheduler =
         Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> m_TaskHandle = null;
            
    private ScheduledTaskInfo m_TaskInfo = null;
    private boolean m_bInitialised = false;
    private ScheduleTaskSupport m_TaskSupport;
    
    ScheduledTask() {
        m_TaskSupport = new ScheduleTaskSupport();
    }

    ScheduledTask( ScheduledTaskInfo taskInfo ) {
        m_TaskSupport = new ScheduleTaskSupport();
        setTask( taskInfo );
        Init();
    }
    
    public void setTask( ScheduledTaskInfo taskInfo ) {
        m_TaskInfo = taskInfo;
    }
    
    public void Init() {
        
        m_bInitialised = false;
        if( m_TaskInfo == null ) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        // Load and verify script file
        
        m_bInitialised = true;
        
    }

    public void Start() { 
        if( !m_bInitialised ) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
        // If started - stop it first
        Stop( true );
        
        // Schedule a task to run the script
        long delay;
        long intervalMinutes = m_TaskInfo.getInterval() * 60;
        Date startDate = m_TaskInfo.getStart();
        Date timeNow = new Date();
        
        // startDate is likely to be in the past so wind forward to the next scheduled 
        // start date/time
        if( startDate.after( timeNow )) {
            long diff = startDate.getTime() - timeNow.getTime();
            delay = TimeUnit.MILLISECONDS.toMinutes(diff);             
        } else {
            // Start date is in the past
            long diff = timeNow.getTime() - startDate.getTime();
            diff = TimeUnit.MILLISECONDS.toMinutes(diff);
            
            long periodsPast = diff / intervalMinutes;
            delay = intervalMinutes - (diff - (periodsPast * intervalMinutes));
        }
        
        m_TaskHandle = m_Scheduler.scheduleWithFixedDelay( this, delay, intervalMinutes, MINUTES );
    }

    public void RunOnceNow() {
        m_Scheduler.execute(this);
    }
    
    public boolean Stop( boolean bForceStop ) { 

        // Stop the scheduled task        
        if( m_TaskHandle != null ) {
            if( m_TaskHandle.isDone() == false && !bForceStop ) {
                // Warn about task running
                return false;
            }
            
            m_TaskHandle.cancel(true);
            m_TaskHandle = null;
        }

        return true;
    }

    private Object RunScript() throws ScriptException {

        ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.BEANSHELL);
        String sDBUser = AppConfig.getInstance().getProperty("db.user");
        String sDBPassword = AppConfig.getInstance().getProperty("db.password");

        if (sDBUser != null && sDBPassword != null && sDBPassword.startsWith("crypt:")) {
            AltEncrypter cypher = new AltEncrypter("cypherkey" + sDBUser);
            sDBPassword = cypher.decrypt(sDBPassword.substring(6));
        }
        script.put("p_Hostname", AppConfig.getInstance().getProperty("machine.hostname"));
        script.put("p_dbURL", AppConfig.getInstance().getProperty("db.URL"));
        script.put("p_dbUser", sDBUser);
        script.put("p_dbPassword", sDBPassword);
        script.put("p_taskInfo", m_TaskInfo);
        script.put("p_taskSupport", m_TaskSupport);

        return script.eval(m_TaskInfo.getScript());
    }
    
    
    @Override
    public void run() {
        
        if( !m_bInitialised ) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
        try {
            // execute script
            RunScript();
        } catch (ScriptException ex) {
            Logger.getLogger(ScheduledTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
