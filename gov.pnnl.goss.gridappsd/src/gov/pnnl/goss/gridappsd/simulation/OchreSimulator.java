package gov.pnnl.goss.gridappsd.simulation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.felix.dm.annotation.api.Component;
import org.apache.felix.dm.annotation.api.ServiceDependency;
import org.apache.felix.dm.annotation.api.Start;

import org.apache.commons.lang3.StringUtils;

import gov.pnnl.goss.gridappsd.api.LogManager;
import gov.pnnl.goss.gridappsd.api.ServiceManager;
import gov.pnnl.goss.gridappsd.api.SimulationManager;
import gov.pnnl.goss.gridappsd.configuration.GLDAllConfigurationHandler;
import gov.pnnl.goss.gridappsd.dto.SimulationContext;
import gov.pnnl.goss.gridappsd.dto.LogMessage.ProcessStatus;
import gov.pnnl.goss.gridappsd.dto.RequestSimulation;
import gov.pnnl.goss.gridappsd.dto.ServiceInfo;
import gov.pnnl.goss.gridappsd.dto.SimulationConfig;
import gov.pnnl.goss.gridappsd.utils.RunCommandLine;

@Component
public class OchreSimulator extends GenericSimulator {
	public static final String SIMULATOR_NAME = "ochre";
    private static final String gridlabdConstant = "GridLAB-D";

	
	@ServiceDependency
	private volatile SimulationManager simulationManager;

	@ServiceDependency
	private volatile LogManager logManager;
	
	@Start
	public void start(){
		System.out.println("Setting up Ochre Simulator");
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
        
        //TODO will this need to be dynamic?
        simulationContext.setNumFederates(42);
        logManager.info(ProcessStatus.RUNNING, simulationId, "Setting num federates ");

    	//Start gridlabd
//		simulationContext.put("simulationFile",tempDataPathDir.getAbsolutePath()+File.separator+"model_startup.glm");
        //TODO: Change this hard coded startup files
        File gldStartupFile = null;
        RequestSimulation simRequest = (RequestSimulation)simulationContext.getRequest();
        if (simRequest.power_system_config.Line_name.contains("_13AD8E07-3BF9-A4E2-CB8F-C3722F837B62"))
        	gldStartupFile = new File(simulationContext.simulationDir+File.separator+"inputs"+File.separator+"gridlabd"+File.separator+"IEEE-13"+File.separator+"IEEE-13_Houses.glm");
        else
        	gldStartupFile = new File(simulationContext.getSimulationDir()+File.separator+"model_startup.glm");
		String gldSimulatorPath = serviceManager.getService(gridlabdConstant).getExecution_path();
//    	commands.add(simContext.getSimulatorPath());
		commands.add(gldSimulatorPath);
//    	commands.add(simulationFile.getAbsolutePath());
    	commands.add(gldStartupFile.getAbsolutePath());
        ProcessBuilder gldSimulatorBuilder = new ProcessBuilder();
        gldSimulatorBuilder.command(commands);
        gldSimulatorBuilder.redirectErrorStream(true);
        gldSimulatorBuilder.redirectOutput();
        //launch from directory containing simulation files
        gldSimulatorBuilder.directory(simulationFile.getParentFile());
        logManager.info(ProcessStatus.RUNNING, simulationId, "Starting gridlabd simulator with command "+String.join(" ",commands));
        Process simulatorProcess = gldSimulatorBuilder.start();
        // Watch the process
        watch(logManager, simulatorProcess, "GLDSimulator-"+simulationId);
    	
    	//Start ochre
    	commands = new ArrayList<String>();
    	commands.add(simulationContext.getSimulatorPath());
    	ServiceInfo serviceInfo = serviceManager.getService(simulationConfig.getSimulator());
    	List<String> staticArgsList = serviceInfo.getStatic_args();
		for(String staticArg : staticArgsList) {
		    if(staticArg!=null){
		    	//Right now this depends on having the simulationContext set, so don't try it if the simulation context is null
				if(simulationContextParams!=null){
			    	if(staticArg.contains("(")){
				    	 String[] replaceArgs = StringUtils.substringsBetween(staticArg, "(", ")");
				    	 for(String args : replaceArgs){
				    		 staticArg = staticArg.replace("("+args+")",simulationContextParams.get(args).toString());
				    	 }
			    	}
				}
		    	commands.add(staticArg);
		    }
		}
    	simulatorBuilder.command(commands);
        logManager.info(ProcessStatus.RUNNING, simulationId, "Command for ochre ready "+String.join(" ",commands));
        // Watch the process
        watch(logManager, simulatorProcess, "Simulator-"+simulationId);
	}

}
