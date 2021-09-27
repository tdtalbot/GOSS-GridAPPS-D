package gov.pnnl.goss.gridappsd.api;

import java.util.Map;

import gov.pnnl.goss.gridappsd.dto.SimulationConfig;
import gov.pnnl.goss.gridappsd.dto.SimulationContext;

public interface Simulator {
	
	
	public void generateConfigs(SimulationContext context);
	     //set startup file and other context parameters
		 // call config handler(s)
	public void startSimulator(SimulationContext context, Map<String, Object> simulationContextParams, String simulationId, SimulationConfig simulationConfig, ServiceManager serviceManager) throws Exception; //, servicemgr
	     //copy appliance config
		 // initiate any services
		 // start simulator

}
