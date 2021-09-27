package gov.pnnl.goss.gridappsd.simulation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import gov.pnnl.goss.gridappsd.api.LogManager;
import gov.pnnl.goss.gridappsd.api.Simulator;
import gov.pnnl.goss.gridappsd.dto.SimulationContext;
import gov.pnnl.goss.gridappsd.dto.LogMessage.ProcessStatus;

public abstract class GenericSimulator implements Simulator {

	
	
	 protected void watch(LogManager logManager, final Process process, String processName) {
	        new Thread() {
	            public void run() {
	                BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
	                String line = null;
	                try {
	                    while ((line = input.readLine()) != null) {
	                    	if(!line.trim().isEmpty()){
	                    		if(line.contains("DEBUG"))
	                    			logManager.debug(ProcessStatus.RUNNING, processName, line);
	                    		else if(line.contains("ERROR"))
	                    			logManager.error(ProcessStatus.ERROR, processName, line);
	                    		else if(line.contains("FATAL") && !line.contains("INFO"))
	                    			logManager.fatal(ProcessStatus.ERROR, processName, line);
	                    		else if(line.contains("WARN"))
	                    			logManager.warn(ProcessStatus.RUNNING, processName, line);
	                    		else
	                    			logManager.debug(ProcessStatus.RUNNING, processName, line);
	                    	}
	                    }
	                } catch (IOException e) {
	                	if(!(e.getMessage().contains("Stream closed")))
	                		logManager.error(ProcessStatus.ERROR, processName, "Error reading input stream of simulator process: "+e.getMessage());
	                }
	            }
	        }.start();
	    }

}
