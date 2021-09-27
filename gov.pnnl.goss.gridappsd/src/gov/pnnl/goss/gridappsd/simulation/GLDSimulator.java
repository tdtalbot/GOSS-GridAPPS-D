package gov.pnnl.goss.gridappsd.simulation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.felix.dm.annotation.api.ServiceDependency;
import org.apache.felix.dm.annotation.api.Start;

import gov.pnnl.goss.gridappsd.api.LogManager;
import gov.pnnl.goss.gridappsd.api.ServiceManager;
import gov.pnnl.goss.gridappsd.api.SimulationManager;
import gov.pnnl.goss.gridappsd.configuration.GLDAllConfigurationHandler;
import gov.pnnl.goss.gridappsd.dto.SimulationConfig;
import gov.pnnl.goss.gridappsd.dto.SimulationContext;
import gov.pnnl.goss.gridappsd.dto.LogMessage.ProcessStatus;
import gov.pnnl.goss.gridappsd.utils.RunCommandLine;

public class GLDSimulator extends GenericSimulator {
	public static final String SIMULATOR_NAME = "gridlabd";
	
	
	@ServiceDependency
	private volatile SimulationManager simulationManager;

	@ServiceDependency
	private volatile LogManager logManager;
	
	@Start
	public void start(){
		System.out.println("Setting up GLD Simulator");
		if(simulationManager!=null) {
			simulationManager.registerSimulator(SIMULATOR_NAME, this);
		}
		else { 
			//TODO send log message and exception
			if(logManager!=null){
				//log.warn("No Data manager available for "+getClass());
				logManager.warn(ProcessStatus.RUNNING, null, "No Simulation manager available for "+getClass());
			}
		}
	}
	
	
	
	@Override
	public void generateConfigs(SimulationContext context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startSimulator(SimulationContext simulationContext, Map<String, Object> simulationContextParams, String simulationId, SimulationConfig simulationConfig, ServiceManager serviceManager) throws IOException {
		//In simulator
        File simulationFile = new File(simulationContext.getStartupFile());

        //if(simulationConfig!=null && simulationConfig.model_creation_config!=null && simulationConfig.model_creation_config.schedule_name!=null && simulationConfig.model_creation_config.schedule_name.trim().length()>0){
        File serviceDir = serviceManager.getServiceConfigDirectory();
        /*try{
            RunCommandLine.runCommand("cp "+serviceDir.getAbsolutePath()+File.separator+"etc"+File.separator+"zipload_schedule.player "+simulationFile.getParentFile().getAbsolutePath()+File.separator+simulationConfig.model_creation_config.schedule_name+".player");
        }catch(Exception e){
           log.warn("Could not copy player file to working directory");
        }*/
        try{
            RunCommandLine.runCommand("cp "+serviceDir.getAbsolutePath()+File.separator+"etc"+File.separator+"appliance_schedules.glm "+simulationFile.getParentFile().getAbsolutePath()+File.separator+GLDAllConfigurationHandler.SCHEDULES_FILENAME);
        }catch(Exception e){
        	logManager.warn(ProcessStatus.STARTING, simulationId, "Could not copy schedules file to working directory");
        }
        
        //Start Simulator
        ProcessBuilder simulatorBuilder = new ProcessBuilder();
        List<String> commands = new ArrayList<String>();
        
        commands.add(simulationContext.getSimulatorPath());
    	commands.add(simulationFile.getAbsolutePath());
    	simulatorBuilder.command(commands);
    	
    	
    	 simulatorBuilder.redirectErrorStream(true);
         simulatorBuilder.redirectOutput();
         //launch from directory containing simulation files
         simulatorBuilder.directory(simulationFile.getParentFile());
         logManager.info(ProcessStatus.RUNNING, simulationId, "Starting simulator with command "+String.join(" ",commands));
         Process simulatorProcess = simulatorBuilder.start();
         logManager.info(ProcessStatus.RUNNING, simulationId, "Started simulator with command "+String.join(" ",commands));

         // Watch the process
         watch(logManager, simulatorProcess, "Simulator-"+simulationId);
	}

}
