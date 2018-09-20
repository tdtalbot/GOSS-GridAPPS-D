package gov.pnnl.goss.gridappsd.data;

import java.io.Serializable;
import java.util.Date;

import gov.pnnl.goss.gridappsd.api.DataManagerHandler;
import gov.pnnl.goss.gridappsd.api.LogManager;
import gov.pnnl.goss.gridappsd.api.PowergridModelDataManager;
import gov.pnnl.goss.gridappsd.api.PowergridModelDataManager.ResultFormat;
import gov.pnnl.goss.gridappsd.dto.LogMessage;
import gov.pnnl.goss.gridappsd.dto.PowergridModelDataRequest;
import gov.pnnl.goss.gridappsd.dto.LogMessage.LogLevel;
import gov.pnnl.goss.gridappsd.dto.LogMessage.ProcessStatus;
import gov.pnnl.goss.gridappsd.utils.GridAppsDConstants;

public class BGPowergridModelDataManagerHandlerImpl implements DataManagerHandler {

	PowergridModelDataManager dataManager;
	LogManager logManager;
	
	public BGPowergridModelDataManagerHandlerImpl(PowergridModelDataManager dataManager, LogManager logManager) {
		this.dataManager = dataManager;
		this.logManager = logManager;
	}
	
	
	@Override
	public Serializable handle(Serializable requestContent, String processId, String username) throws Exception {
		PowergridModelDataRequest pgDataRequest = null;
		if(requestContent instanceof PowergridModelDataRequest){
			pgDataRequest = (PowergridModelDataRequest)requestContent;
		} else {
			pgDataRequest = PowergridModelDataRequest.parse(requestContent.toString());
		}
		
		if(PowergridModelDataRequest.RequestType.QUERY.toString().equals(pgDataRequest.requestType)){
			if (pgDataRequest.getQueryString()==null || !verifyResultFormat(pgDataRequest.getResultFormat())){
				logManager.log(new LogMessage(this.getClass().getSimpleName(), 
						processId, 
						new Date().getTime(),
						"Error handling powergrid data request: Query string '"+pgDataRequest.getQueryString()+"' cannot be null or result format '"+pgDataRequest.getResultFormat()+"' is invalid", 
						LogLevel.ERROR, ProcessStatus.ERROR, true), username, GridAppsDConstants.topic_platformLog);
				throw new Exception("Error handling powergrid data request: Query string '"+pgDataRequest.getQueryString()+"' cannot be null or result format '"+pgDataRequest.getResultFormat()+"' is invalid");
			}
			return dataManager.query(pgDataRequest.getModelId(), pgDataRequest.getQueryString(), pgDataRequest.getResultFormat(), processId, username);
		} else if(PowergridModelDataRequest.RequestType.QUERY_MODEL.toString().equals(pgDataRequest.requestType)){
			if (pgDataRequest.getModelId()==null || !verifyResultFormat(pgDataRequest.getResultFormat())){
				logManager.log(new LogMessage(this.getClass().getSimpleName(), 
						processId, 
						new Date().getTime(),
						"Error handling powergrid data request: Query model id '"+pgDataRequest.getQueryString()+"' cannot be null or result format '"+pgDataRequest.getResultFormat()+"' is invalid", 
						LogLevel.ERROR, ProcessStatus.ERROR, true), username, GridAppsDConstants.topic_platformLog);
				throw new Exception("Error handling powergrid data request: Query model id '"+pgDataRequest.getQueryString()+"' cannot be null or result format '"+pgDataRequest.getResultFormat()+"' is invalid");
			}
			return dataManager.queryModel(pgDataRequest.getModelId(), pgDataRequest.getObjectType(), pgDataRequest.getFilter(), pgDataRequest.getResultFormat(), processId, username);
		} else if(PowergridModelDataRequest.RequestType.QUERY_MODEL_NAMES.toString().equals(pgDataRequest.requestType)){
			if (!verifyResultFormat(pgDataRequest.getResultFormat())){
				logManager.log(new LogMessage(this.getClass().getSimpleName(), 
						processId, 
						new Date().getTime(),
						"Error handling powergrid data request: Result format '"+pgDataRequest.getResultFormat()+"' is invalid", 
						LogLevel.ERROR, ProcessStatus.ERROR, true), username, GridAppsDConstants.topic_platformLog);
				throw new Exception("Error handling powergrid data request: Result format '"+pgDataRequest.getResultFormat()+"' is invalid");
			}
			return dataManager.queryModelNames(pgDataRequest.getResultFormat(), processId, username);
		} else if(PowergridModelDataRequest.RequestType.QUERY_MODEL_INFO.toString().equals(pgDataRequest.requestType)){
			if (!verifyResultFormat(pgDataRequest.getResultFormat())){
				logManager.log(new LogMessage(this.getClass().getSimpleName(), 
						processId, 
						new Date().getTime(),
						"Error handling powergrid data request: Result format '"+pgDataRequest.getResultFormat()+"' is invalid", 
						LogLevel.ERROR, ProcessStatus.ERROR, true), username, GridAppsDConstants.topic_platformLog);
				throw new Exception("Error handling powergrid data request: Result format '"+pgDataRequest.getResultFormat()+"' is invalid");
			}
			return dataManager.queryModelNamesAndIds(pgDataRequest.getResultFormat(), processId, username);
		} else if(PowergridModelDataRequest.RequestType.QUERY_OBJECT.toString().equals(pgDataRequest.requestType)){
			if (pgDataRequest.getModelId()==null || pgDataRequest.getObjectId()==null || !verifyResultFormat(pgDataRequest.getResultFormat())){
				logManager.log(new LogMessage(this.getClass().getSimpleName(), 
						processId, 
						new Date().getTime(),
						"Error handling powergrid data request: Query model id '"+pgDataRequest.getQueryString()+"' cannot be null or object id '"+pgDataRequest.getObjectId()+"' or result format '"+pgDataRequest.getResultFormat()+"' is invalid", 
						LogLevel.ERROR, ProcessStatus.ERROR, true), username, GridAppsDConstants.topic_platformLog);
				throw new Exception("Error handling powergrid data request: Query model id '"+pgDataRequest.getQueryString()+"' cannot be null or result format '"+pgDataRequest.getResultFormat()+"' is invalid");
			}
			return dataManager.queryObject(pgDataRequest.getModelId(), pgDataRequest.getObjectId(), pgDataRequest.getResultFormat(), processId, username);
		} else if(PowergridModelDataRequest.RequestType.QUERY_OBJECT_TYPES.toString().equals(pgDataRequest.requestType)){
			if (pgDataRequest.getModelId()==null || !verifyResultFormat(pgDataRequest.getResultFormat())){
				logManager.log(new LogMessage(this.getClass().getSimpleName(), 
						processId, 
						new Date().getTime(),
						"Error handling powergrid data request: Query model id '"+pgDataRequest.getQueryString()+"' cannot be null or result format '"+pgDataRequest.getResultFormat()+"' is invalid", 
						LogLevel.ERROR, ProcessStatus.ERROR, true), username, GridAppsDConstants.topic_platformLog);
				throw new Exception("Error handling powergrid data request: Query model id '"+pgDataRequest.getQueryString()+"' cannot be null or result format '"+pgDataRequest.getResultFormat()+"' is invalid");
			}
			return dataManager.queryObjectTypes(pgDataRequest.getModelId(), pgDataRequest.getResultFormat(), processId, username);
		} else {
			logManager.log(new LogMessage(this.getClass().getSimpleName(), 
					processId, 
					new Date().getTime(),
					"Error handling powergrid data request: Request type '"+pgDataRequest.getRequestType()+"' is not recognized", 
					LogLevel.ERROR, ProcessStatus.ERROR, true), username, GridAppsDConstants.topic_platformLog);
			throw new Exception("Error handling powergrid data request: Request type '"+pgDataRequest.getRequestType()+"' is not recognized");
		}
		
	}

	
	boolean verifyResultFormat(String resultFormat) {
		return resultFormat!=null && (resultFormat.equals(ResultFormat.JSON.toString()) || resultFormat.equals(ResultFormat.XML.toString()));
	}
}
