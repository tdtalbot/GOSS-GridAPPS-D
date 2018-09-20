/*******************************************************************************
 * Copyright (c) 2017, Battelle Memorial Institute All rights reserved.
 * Battelle Memorial Institute (hereinafter Battelle) hereby grants permission to any person or entity 
 * lawfully obtaining a copy of this software and associated documentation files (hereinafter the 
 * Software) to redistribute and use the Software in source and binary forms, with or without modification. 
 * Such person or entity may use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of 
 * the Software, and may permit others to do so, subject to the following conditions:
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the 
 * following disclaimers.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and 
 * the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Other than as used herein, neither the name Battelle Memorial Institute or Battelle may be used in any 
 * form whatsoever without the express written consent of Battelle.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * BATTELLE OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED 
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * General disclaimer for use with OSS licenses
 * 
 * This material was prepared as an account of work sponsored by an agency of the United States Government. 
 * Neither the United States Government nor the United States Department of Energy, nor Battelle, nor any 
 * of their employees, nor any jurisdiction or organization that has cooperated in the development of these 
 * materials, makes any warranty, express or implied, or assumes any legal liability or responsibility for 
 * the accuracy, completeness, or usefulness or any information, apparatus, product, software, or process 
 * disclosed, or represents that its use would not infringe privately owned rights.
 * 
 * Reference herein to any specific commercial product, process, or service by trade name, trademark, manufacturer, 
 * or otherwise does not necessarily constitute or imply its endorsement, recommendation, or favoring by the United 
 * States Government or any agency thereof, or Battelle Memorial Institute. The views and opinions of authors expressed 
 * herein do not necessarily state or reflect those of the United States Government or any agency thereof.
 * 
 * PACIFIC NORTHWEST NATIONAL LABORATORY operated by BATTELLE for the 
 * UNITED STATES DEPARTMENT OF ENERGY under Contract DE-AC05-76RL01830
 ******************************************************************************/
package gov.pnnl.goss.gridappsd;

import org.slf4j.Logger;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.jms.Destination;

import static gov.pnnl.goss.gridappsd.TestConstants.*;

import org.apache.felix.dm.annotation.api.ServiceDependency;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import gov.pnnl.goss.gridappsd.api.DataManager;
import gov.pnnl.goss.gridappsd.api.LogManager;
import gov.pnnl.goss.gridappsd.api.PowergridModelDataManager;
import gov.pnnl.goss.gridappsd.configuration.ConfigurationManagerImpl;
import gov.pnnl.goss.gridappsd.data.BGPowergridModelDataManagerHandlerImpl;
import gov.pnnl.goss.gridappsd.data.GridAppsDataSourcesImpl;
import gov.pnnl.goss.gridappsd.dto.LogMessage;
import gov.pnnl.goss.gridappsd.dto.LogMessage.LogLevel;
import gov.pnnl.goss.gridappsd.dto.LogMessage.ProcessStatus;
import gov.pnnl.goss.gridappsd.dto.PowergridModelDataRequest;
import gov.pnnl.goss.gridappsd.process.ProcessNewSimulationRequest;
import pnnl.goss.core.server.DataSourceBuilder;
import pnnl.goss.core.server.DataSourceObject;
import pnnl.goss.core.server.DataSourcePooledJdbc;
import pnnl.goss.core.server.DataSourceRegistry;

@RunWith(MockitoJUnitRunner.class)
public class PGModelDataManagerComponentTests {
	
	@Mock
	Logger logger;
	
	protected final static String TEST_QUERY_STRING = "SELECT ?s ?p ?o where {?s ?p ?o}";
	protected final static String TEST_MODEL_ID = "12345";
	protected final static String TEST_OBJECT_ID = "12345";
	
	@Captor
	ArgumentCaptor<String> argCaptor;
	@Captor
	ArgumentCaptor<LogMessage> argCaptorLogMessage;

	@Mock
	private LogManager logManager;
	
	@Mock
	private PowergridModelDataManager dataManager;
//	private BGPowergridModelDataManagerHandlerImpl pgModelDataManager;
	
//	@Mock
//	private DataManager dataManager;
//	
//	@Captor
//	ArgumentCaptor<String> argCaptor;
//	
//	@Mock DataSourcePooledJdbc datasourceObject;

	//hand PowergridModelDataRequest
	//handle string
		
	/**
	 *    Succeeds when an error status message is sent if it encounters a null query string while handling a query request
	 */
	@Test
	public void errorsLoggedWhen_queryProcessErrorBecauseNullQueryString(){
		
//		try {
//			Mockito.when(configurationManager.getSimulationFile(Mockito.anyInt(),  Mockito.any())).thenReturn(null);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		BGPowergridModelDataManagerHandlerImpl pgModelDataManager = new BGPowergridModelDataManagerHandlerImpl(dataManager, logManager);
		PowergridModelDataRequest request = new PowergridModelDataRequest();
		request.requestType = PowergridModelDataRequest.RequestType.QUERY.toString();
		request.queryString = null;
		request.resultFormat = PowergridModelDataRequest.ResultFormat.JSON.toString();
		
		String processId =  ""+Math.abs(new Random().nextInt());
		String username = "test";
//		ProcessNewSimulationRequest request = new ProcessNewSimulationRequest(logManager);
		try {
			Serializable result = pgModelDataManager.handle(request, processId, username);
		}catch (Exception e) {
			// possible exception expected in unit test
		}
		
//		request error log call made
		Mockito.verify(logManager, Mockito.times(1)).log(argCaptorLogMessage.capture(), argCaptor.capture(),argCaptor.capture()); // GridAppsDConstants.username);
		List<LogMessage> messages = argCaptorLogMessage.getAllValues();
		LogMessage capturedMessage = messages.get(0);
		assertEquals(true, capturedMessage.getLogMessage().startsWith("Error handling powergrid data request:"));
		assertEquals(LogLevel.ERROR, capturedMessage.getLogLevel());
		assertEquals(ProcessStatus.ERROR, capturedMessage.getProcessStatus());
		assertEquals(true, capturedMessage.getStoreToDb());
	}
	
	/**
	 *    Succeeds when an error status message is sent if it encounters a null query string while handling a query request
	 */
	@Test
	public void errorsLoggedWhen_queryProcessErrorBecauseInvalidFormatString(){
		
//		try {
//			Mockito.when(configurationManager.getSimulationFile(Mockito.anyInt(),  Mockito.any())).thenReturn(null);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		BGPowergridModelDataManagerHandlerImpl pgModelDataManager = new BGPowergridModelDataManagerHandlerImpl(dataManager, logManager);
		PowergridModelDataRequest request = new PowergridModelDataRequest();
		request.requestType = PowergridModelDataRequest.RequestType.QUERY.toString();
		request.queryString = TEST_QUERY_STRING;
		request.resultFormat = "INVALID";
		
		String processId =  ""+Math.abs(new Random().nextInt());
		String username = "test";
//		ProcessNewSimulationRequest request = new ProcessNewSimulationRequest(logManager);
		try {
			Serializable result = pgModelDataManager.handle(request, processId, username);
		}catch (Exception e) {
			// possible exception expected in unit test
		}
		
//		request error log call made
		Mockito.verify(logManager, Mockito.times(1)).log(argCaptorLogMessage.capture(), argCaptor.capture(),argCaptor.capture()); // GridAppsDConstants.username);
		List<LogMessage> messages = argCaptorLogMessage.getAllValues();
		LogMessage capturedMessage = messages.get(0);
		assertEquals(true, capturedMessage.getLogMessage().startsWith("Error handling powergrid data request:"));
		assertEquals(LogLevel.ERROR, capturedMessage.getLogLevel());
		assertEquals(ProcessStatus.ERROR, capturedMessage.getProcessStatus());
		assertEquals(true, capturedMessage.getStoreToDb());
	}
	
	/**
	 *    Succeeds when an error status message is sent if it encounters a null query string while handling a query request
	 */
	@Test
	public void noErrorsLoggedWhen_queryProcessSuccess(){
		
//		try {
//			Mockito.when(configurationManager.getSimulationFile(Mockito.anyInt(),  Mockito.any())).thenReturn(null);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		BGPowergridModelDataManagerHandlerImpl pgModelDataManager = new BGPowergridModelDataManagerHandlerImpl(dataManager, logManager);
		PowergridModelDataRequest request = new PowergridModelDataRequest();
		request.requestType = PowergridModelDataRequest.RequestType.QUERY.toString();
		request.queryString = TEST_QUERY_STRING;
		request.resultFormat = PowergridModelDataRequest.ResultFormat.JSON.toString();
		
		String processId =  ""+Math.abs(new Random().nextInt());
		String username = "test";
//		ProcessNewSimulationRequest request = new ProcessNewSimulationRequest(logManager);
		try {
			Serializable result = pgModelDataManager.handle(request, processId, username);
		}catch (Exception e) {
			// possible exception expected in unit test
		}
		
//		request error log call made
		Mockito.verify(logManager, Mockito.times(0)).log(argCaptorLogMessage.capture(), argCaptor.capture(),argCaptor.capture()); // GridAppsDConstants.username);
	}
	
	/**
	 *    Succeeds when an error status message is sent if it encounters a null query string while handling a query request
	 */
	@Test
	public void errorsLoggedWhen_queryModelProcessErrorBecauseNullModelIdString(){
		
//		try {
//			Mockito.when(configurationManager.getSimulationFile(Mockito.anyInt(),  Mockito.any())).thenReturn(null);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		BGPowergridModelDataManagerHandlerImpl pgModelDataManager = new BGPowergridModelDataManagerHandlerImpl(dataManager, logManager);
		PowergridModelDataRequest request = new PowergridModelDataRequest();
		request.requestType = PowergridModelDataRequest.RequestType.QUERY_MODEL.toString();
		request.modelId = null;
		request.resultFormat = PowergridModelDataRequest.ResultFormat.JSON.toString();
		
		String processId =  ""+Math.abs(new Random().nextInt());
		String username = "test";
//		ProcessNewSimulationRequest request = new ProcessNewSimulationRequest(logManager);
		try {
			Serializable result = pgModelDataManager.handle(request, processId, username);
		}catch (Exception e) {
			// possible exception expected in unit test
		}
		
//		request error log call made
		Mockito.verify(logManager, Mockito.times(1)).log(argCaptorLogMessage.capture(), argCaptor.capture(),argCaptor.capture()); // GridAppsDConstants.username);
		List<LogMessage> messages = argCaptorLogMessage.getAllValues();
		LogMessage capturedMessage = messages.get(0);
		assertEquals(true, capturedMessage.getLogMessage().startsWith("Error handling powergrid data request:"));
		assertEquals(LogLevel.ERROR, capturedMessage.getLogLevel());
		assertEquals(ProcessStatus.ERROR, capturedMessage.getProcessStatus());
		assertEquals(true, capturedMessage.getStoreToDb());
	}
	
	/**
	 *    Succeeds when an error status message is sent if it encounters a null query string while handling a query request
	 */
	@Test
	public void errorsLoggedWhen_queryModelProcessErrorBecauseInvalidFormatString(){
		
//		try {
//			Mockito.when(configurationManager.getSimulationFile(Mockito.anyInt(),  Mockito.any())).thenReturn(null);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		BGPowergridModelDataManagerHandlerImpl pgModelDataManager = new BGPowergridModelDataManagerHandlerImpl(dataManager, logManager);
		PowergridModelDataRequest request = new PowergridModelDataRequest();
		request.requestType = PowergridModelDataRequest.RequestType.QUERY_MODEL.toString();
		request.modelId = TEST_MODEL_ID;
		request.resultFormat = "INVALID";
		
		String processId =  ""+Math.abs(new Random().nextInt());
		String username = "test";
//		ProcessNewSimulationRequest request = new ProcessNewSimulationRequest(logManager);
		try {
			Serializable result = pgModelDataManager.handle(request, processId, username);
		}catch (Exception e) {
			// possible exception expected in unit test
		}
		
//		request error log call made
		Mockito.verify(logManager, Mockito.times(1)).log(argCaptorLogMessage.capture(), argCaptor.capture(),argCaptor.capture()); // GridAppsDConstants.username);
		List<LogMessage> messages = argCaptorLogMessage.getAllValues();
		LogMessage capturedMessage = messages.get(0);
		assertEquals(true, capturedMessage.getLogMessage().startsWith("Error handling powergrid data request:"));
		assertEquals(LogLevel.ERROR, capturedMessage.getLogLevel());
		assertEquals(ProcessStatus.ERROR, capturedMessage.getProcessStatus());
		assertEquals(true, capturedMessage.getStoreToDb());
	}
	
	/**
	 *    Succeeds when an error status message is sent if it encounters a null query string while handling a query request
	 */
	@Test
	public void noErrorsLoggedWhen_queryModelProcessSuccess(){
		
//		try {
//			Mockito.when(configurationManager.getSimulationFile(Mockito.anyInt(),  Mockito.any())).thenReturn(null);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		BGPowergridModelDataManagerHandlerImpl pgModelDataManager = new BGPowergridModelDataManagerHandlerImpl(dataManager, logManager);
		PowergridModelDataRequest request = new PowergridModelDataRequest();
		request.requestType = PowergridModelDataRequest.RequestType.QUERY_MODEL.toString();
		request.modelId = TEST_MODEL_ID;
		request.resultFormat = PowergridModelDataRequest.ResultFormat.JSON.toString();
		
		String processId =  ""+Math.abs(new Random().nextInt());
		String username = "test";
//		ProcessNewSimulationRequest request = new ProcessNewSimulationRequest(logManager);
		try {
			Serializable result = pgModelDataManager.handle(request, processId, username);
		}catch (Exception e) {
			// possible exception expected in unit test
		}
		
//		request error log call made
		Mockito.verify(logManager, Mockito.times(0)).log(argCaptorLogMessage.capture(), argCaptor.capture(),argCaptor.capture()); // GridAppsDConstants.username);
	}
	
	
	
	
	/**
	 *    Succeeds when an error status message is sent if it encounters a null query string while handling a query request
	 */
	@Test
	public void errorsLoggedWhen_queryModelNamesProcessErrorBecauseInvalidFormatString(){
		
//		try {
//			Mockito.when(configurationManager.getSimulationFile(Mockito.anyInt(),  Mockito.any())).thenReturn(null);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		BGPowergridModelDataManagerHandlerImpl pgModelDataManager = new BGPowergridModelDataManagerHandlerImpl(dataManager, logManager);
		PowergridModelDataRequest request = new PowergridModelDataRequest();
		request.requestType = PowergridModelDataRequest.RequestType.QUERY_MODEL_NAMES.toString();
		request.modelId = TEST_MODEL_ID;
		request.resultFormat = "INVALID";
		
		String processId =  ""+Math.abs(new Random().nextInt());
		String username = "test";
//		ProcessNewSimulationRequest request = new ProcessNewSimulationRequest(logManager);
		try {
			Serializable result = pgModelDataManager.handle(request, processId, username);
		}catch (Exception e) {
			// possible exception expected in unit test
		}
		
//		request error log call made
		Mockito.verify(logManager, Mockito.times(1)).log(argCaptorLogMessage.capture(), argCaptor.capture(),argCaptor.capture()); // GridAppsDConstants.username);
		List<LogMessage> messages = argCaptorLogMessage.getAllValues();
		LogMessage capturedMessage = messages.get(0);
		assertEquals(true, capturedMessage.getLogMessage().startsWith("Error handling powergrid data request:"));
		assertEquals(LogLevel.ERROR, capturedMessage.getLogLevel());
		assertEquals(ProcessStatus.ERROR, capturedMessage.getProcessStatus());
		assertEquals(true, capturedMessage.getStoreToDb());
	}
	
	/**
	 *    Succeeds when an error status message is sent if it encounters a null query string while handling a query request
	 */
	@Test
	public void noErrorsLoggedWhen_queryModelNamesProcessSuccess(){
		
//		try {
//			Mockito.when(configurationManager.getSimulationFile(Mockito.anyInt(),  Mockito.any())).thenReturn(null);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		BGPowergridModelDataManagerHandlerImpl pgModelDataManager = new BGPowergridModelDataManagerHandlerImpl(dataManager, logManager);
		PowergridModelDataRequest request = new PowergridModelDataRequest();
		request.requestType = PowergridModelDataRequest.RequestType.QUERY_MODEL_NAMES.toString();
		request.modelId = TEST_MODEL_ID;
		request.resultFormat = PowergridModelDataRequest.ResultFormat.JSON.toString();
		
		String processId =  ""+Math.abs(new Random().nextInt());
		String username = "test";
//		ProcessNewSimulationRequest request = new ProcessNewSimulationRequest(logManager);
		try {
			Serializable result = pgModelDataManager.handle(request, processId, username);
		}catch (Exception e) {
			// possible exception expected in unit test
		}
		
//		request error log call made
		Mockito.verify(logManager, Mockito.times(0)).log(argCaptorLogMessage.capture(), argCaptor.capture(),argCaptor.capture()); // GridAppsDConstants.username);
	}
	
	

	/**
	 *    Succeeds when an error status message is sent if it encounters a null query string while handling a query request
	 */
	@Test
	public void errorsLoggedWhen_queryModelInfoProcessErrorBecauseInvalidFormatString(){
		
//		try {
//			Mockito.when(configurationManager.getSimulationFile(Mockito.anyInt(),  Mockito.any())).thenReturn(null);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		BGPowergridModelDataManagerHandlerImpl pgModelDataManager = new BGPowergridModelDataManagerHandlerImpl(dataManager, logManager);
		PowergridModelDataRequest request = new PowergridModelDataRequest();
		request.requestType = PowergridModelDataRequest.RequestType.QUERY_MODEL_INFO.toString();
		request.modelId = TEST_MODEL_ID;
		request.resultFormat = "INVALID";
		
		String processId =  ""+Math.abs(new Random().nextInt());
		String username = "test";
//		ProcessNewSimulationRequest request = new ProcessNewSimulationRequest(logManager);
		try {
			Serializable result = pgModelDataManager.handle(request, processId, username);
		}catch (Exception e) {
			// possible exception expected in unit test
		}
		
//		request error log call made
		Mockito.verify(logManager, Mockito.times(1)).log(argCaptorLogMessage.capture(), argCaptor.capture(),argCaptor.capture()); // GridAppsDConstants.username);
		List<LogMessage> messages = argCaptorLogMessage.getAllValues();
		LogMessage capturedMessage = messages.get(0);
		assertEquals(true, capturedMessage.getLogMessage().startsWith("Error handling powergrid data request:"));
		assertEquals(LogLevel.ERROR, capturedMessage.getLogLevel());
		assertEquals(ProcessStatus.ERROR, capturedMessage.getProcessStatus());
		assertEquals(true, capturedMessage.getStoreToDb());
	}
	
	/**
	 *    Succeeds when an error status message is sent if it encounters a null query string while handling a query request
	 */
	@Test
	public void noErrorsLoggedWhen_queryModelInfoProcessSuccess(){
		
//		try {
//			Mockito.when(configurationManager.getSimulationFile(Mockito.anyInt(),  Mockito.any())).thenReturn(null);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		BGPowergridModelDataManagerHandlerImpl pgModelDataManager = new BGPowergridModelDataManagerHandlerImpl(dataManager, logManager);
		PowergridModelDataRequest request = new PowergridModelDataRequest();
		request.requestType = PowergridModelDataRequest.RequestType.QUERY_MODEL_INFO.toString();
		request.modelId = TEST_MODEL_ID;
		request.resultFormat = PowergridModelDataRequest.ResultFormat.JSON.toString();
		
		String processId =  ""+Math.abs(new Random().nextInt());
		String username = "test";
//		ProcessNewSimulationRequest request = new ProcessNewSimulationRequest(logManager);
		try {
			Serializable result = pgModelDataManager.handle(request, processId, username);
		}catch (Exception e) {
			// possible exception expected in unit test
		}
		
//		request error log call made
		Mockito.verify(logManager, Mockito.times(0)).log(argCaptorLogMessage.capture(), argCaptor.capture(),argCaptor.capture()); // GridAppsDConstants.username);
	}
	/**
	 *    Succeeds when an error status message is sent if it encounters a null query string while handling a query request
	 */
	@Test
	public void errorsLoggedWhen_queryObjectProcessErrorBecauseNullModelIdString(){
		
//		try {
//			Mockito.when(configurationManager.getSimulationFile(Mockito.anyInt(),  Mockito.any())).thenReturn(null);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		BGPowergridModelDataManagerHandlerImpl pgModelDataManager = new BGPowergridModelDataManagerHandlerImpl(dataManager, logManager);
		PowergridModelDataRequest request = new PowergridModelDataRequest();
		request.requestType = PowergridModelDataRequest.RequestType.QUERY_OBJECT.toString();
		request.modelId = TEST_MODEL_ID;
		request.objectId = null;
		request.resultFormat = PowergridModelDataRequest.ResultFormat.JSON.toString();
		
		String processId =  ""+Math.abs(new Random().nextInt());
		String username = "test";
//		ProcessNewSimulationRequest request = new ProcessNewSimulationRequest(logManager);
		try {
			Serializable result = pgModelDataManager.handle(request, processId, username);
		}catch (Exception e) {
			// possible exception expected in unit test
		}
		
//		request error log call made
		Mockito.verify(logManager, Mockito.times(1)).log(argCaptorLogMessage.capture(), argCaptor.capture(),argCaptor.capture()); // GridAppsDConstants.username);
		List<LogMessage> messages = argCaptorLogMessage.getAllValues();
		LogMessage capturedMessage = messages.get(0);
		assertEquals(true, capturedMessage.getLogMessage().startsWith("Error handling powergrid data request:"));
		assertEquals(LogLevel.ERROR, capturedMessage.getLogLevel());
		assertEquals(ProcessStatus.ERROR, capturedMessage.getProcessStatus());
		assertEquals(true, capturedMessage.getStoreToDb());
	}
	
	/**
	 *    Succeeds when an error status message is sent if it encounters a null query string while handling a query request
	 */
	@Test
	public void errorsLoggedWhen_queryObjectProcessErrorBecauseInvalidFormatString(){
		
//		try {
//			Mockito.when(configurationManager.getSimulationFile(Mockito.anyInt(),  Mockito.any())).thenReturn(null);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		BGPowergridModelDataManagerHandlerImpl pgModelDataManager = new BGPowergridModelDataManagerHandlerImpl(dataManager, logManager);
		PowergridModelDataRequest request = new PowergridModelDataRequest();
		request.requestType = PowergridModelDataRequest.RequestType.QUERY_OBJECT.toString();
		request.modelId = TEST_MODEL_ID;
		request.objectId = TEST_OBJECT_ID;
		request.resultFormat = "INVALID";
		
		String processId =  ""+Math.abs(new Random().nextInt());
		String username = "test";
//		ProcessNewSimulationRequest request = new ProcessNewSimulationRequest(logManager);
		try {
			Serializable result = pgModelDataManager.handle(request, processId, username);
		}catch (Exception e) {
			// possible exception expected in unit test
		}
		
//		request error log call made
		Mockito.verify(logManager, Mockito.times(1)).log(argCaptorLogMessage.capture(), argCaptor.capture(),argCaptor.capture()); // GridAppsDConstants.username);
		List<LogMessage> messages = argCaptorLogMessage.getAllValues();
		LogMessage capturedMessage = messages.get(0);
		assertEquals(true, capturedMessage.getLogMessage().startsWith("Error handling powergrid data request:"));
		assertEquals(LogLevel.ERROR, capturedMessage.getLogLevel());
		assertEquals(ProcessStatus.ERROR, capturedMessage.getProcessStatus());
		assertEquals(true, capturedMessage.getStoreToDb());
	}
	
	/**
	 *    Succeeds when an error status message is sent if it encounters a null query string while handling a query request
	 */
	@Test
	public void noErrorsLoggedWhen_queryObjectProcessSuccess(){
		
//		try {
//			Mockito.when(configurationManager.getSimulationFile(Mockito.anyInt(),  Mockito.any())).thenReturn(null);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		BGPowergridModelDataManagerHandlerImpl pgModelDataManager = new BGPowergridModelDataManagerHandlerImpl(dataManager, logManager);
		PowergridModelDataRequest request = new PowergridModelDataRequest();
		request.requestType = PowergridModelDataRequest.RequestType.QUERY_OBJECT.toString();
		request.modelId = TEST_MODEL_ID;
		request.objectId = TEST_OBJECT_ID;
		request.resultFormat = PowergridModelDataRequest.ResultFormat.JSON.toString();
		
		String processId =  ""+Math.abs(new Random().nextInt());
		String username = "test";
//		ProcessNewSimulationRequest request = new ProcessNewSimulationRequest(logManager);
		try {
			Serializable result = pgModelDataManager.handle(request, processId, username);
		}catch (Exception e) {
			// possible exception expected in unit test
		}
		
//		request error log call made
		Mockito.verify(logManager, Mockito.times(0)).log(argCaptorLogMessage.capture(), argCaptor.capture(),argCaptor.capture()); // GridAppsDConstants.username);
	}
	
	/**
	 *    Succeeds when an error status message is sent if it encounters a null query string while handling a query request
	 */
	@Test
	public void errorsLoggedWhen_queryObjectTypesProcessErrorBecauseNullModelIdString(){
		
//		try {
//			Mockito.when(configurationManager.getSimulationFile(Mockito.anyInt(),  Mockito.any())).thenReturn(null);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		BGPowergridModelDataManagerHandlerImpl pgModelDataManager = new BGPowergridModelDataManagerHandlerImpl(dataManager, logManager);
		PowergridModelDataRequest request = new PowergridModelDataRequest();
		request.requestType = PowergridModelDataRequest.RequestType.QUERY_OBJECT.toString();
		request.modelId = null;
		request.resultFormat = PowergridModelDataRequest.ResultFormat.JSON.toString();
		
		String processId =  ""+Math.abs(new Random().nextInt());
		String username = "test";
//		ProcessNewSimulationRequest request = new ProcessNewSimulationRequest(logManager);
		try {
			Serializable result = pgModelDataManager.handle(request, processId, username);
		}catch (Exception e) {
			// possible exception expected in unit test
		}
		
//		request error log call made
		Mockito.verify(logManager, Mockito.times(1)).log(argCaptorLogMessage.capture(), argCaptor.capture(),argCaptor.capture()); // GridAppsDConstants.username);
		List<LogMessage> messages = argCaptorLogMessage.getAllValues();
		LogMessage capturedMessage = messages.get(0);
		assertEquals(true, capturedMessage.getLogMessage().startsWith("Error handling powergrid data request:"));
		assertEquals(LogLevel.ERROR, capturedMessage.getLogLevel());
		assertEquals(ProcessStatus.ERROR, capturedMessage.getProcessStatus());
		assertEquals(true, capturedMessage.getStoreToDb());
	}
	
	/**
	 *    Succeeds when an error status message is sent if it encounters a null query string while handling a query request
	 */
	@Test
	public void errorsLoggedWhen_queryObjectTypesProcessErrorBecauseInvalidFormatString(){
		
//		try {
//			Mockito.when(configurationManager.getSimulationFile(Mockito.anyInt(),  Mockito.any())).thenReturn(null);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		BGPowergridModelDataManagerHandlerImpl pgModelDataManager = new BGPowergridModelDataManagerHandlerImpl(dataManager, logManager);
		PowergridModelDataRequest request = new PowergridModelDataRequest();
		request.requestType = PowergridModelDataRequest.RequestType.QUERY_OBJECT_TYPES.toString();
		request.modelId = TEST_MODEL_ID;
		request.resultFormat = "INVALID";
		
		String processId =  ""+Math.abs(new Random().nextInt());
		String username = "test";
//		ProcessNewSimulationRequest request = new ProcessNewSimulationRequest(logManager);
		try {
			Serializable result = pgModelDataManager.handle(request, processId, username);
		}catch (Exception e) {
			// possible exception expected in unit test
		}
		
//		request error log call made
		Mockito.verify(logManager, Mockito.times(1)).log(argCaptorLogMessage.capture(), argCaptor.capture(),argCaptor.capture()); // GridAppsDConstants.username);
		List<LogMessage> messages = argCaptorLogMessage.getAllValues();
		LogMessage capturedMessage = messages.get(0);
		assertEquals(true, capturedMessage.getLogMessage().startsWith("Error handling powergrid data request:"));
		assertEquals(LogLevel.ERROR, capturedMessage.getLogLevel());
		assertEquals(ProcessStatus.ERROR, capturedMessage.getProcessStatus());
		assertEquals(true, capturedMessage.getStoreToDb());
	}
	
	/**
	 *    Succeeds when an error status message is sent if it encounters a null query string while handling a query request
	 */
	@Test
	public void noErrorsLoggedWhen_queryObjectTypesProcessSuccess(){
		
//		try {
//			Mockito.when(configurationManager.getSimulationFile(Mockito.anyInt(),  Mockito.any())).thenReturn(null);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		BGPowergridModelDataManagerHandlerImpl pgModelDataManager = new BGPowergridModelDataManagerHandlerImpl(dataManager, logManager);
		PowergridModelDataRequest request = new PowergridModelDataRequest();
		request.requestType = PowergridModelDataRequest.RequestType.QUERY_OBJECT_TYPES.toString();
		request.modelId = TEST_MODEL_ID;
		request.resultFormat = PowergridModelDataRequest.ResultFormat.JSON.toString();
		
		String processId =  ""+Math.abs(new Random().nextInt());
		String username = "test";
//		ProcessNewSimulationRequest request = new ProcessNewSimulationRequest(logManager);
		try {
			Serializable result = pgModelDataManager.handle(request, processId, username);
		}catch (Exception e) {
			// possible exception expected in unit test
		}
		
//		request error log call made
		Mockito.verify(logManager, Mockito.times(0)).log(argCaptorLogMessage.capture(), argCaptor.capture(),argCaptor.capture()); // GridAppsDConstants.username);
	}
	
	/**
	 *    Succeeds when an error status message is sent if it encounters a null query string while handling a query request
	 */
	@Test
	public void errorsLoggedWhen_InvalidRequestTypeProcessSuccess(){
		
//		try {
//			Mockito.when(configurationManager.getSimulationFile(Mockito.anyInt(),  Mockito.any())).thenReturn(null);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		BGPowergridModelDataManagerHandlerImpl pgModelDataManager = new BGPowergridModelDataManagerHandlerImpl(dataManager, logManager);
		PowergridModelDataRequest request = new PowergridModelDataRequest();
		request.requestType = "UNKNOWN";
		request.modelId = TEST_MODEL_ID;
		request.resultFormat = PowergridModelDataRequest.ResultFormat.JSON.toString();
		
		String processId =  ""+Math.abs(new Random().nextInt());
		String username = "test";
//		ProcessNewSimulationRequest request = new ProcessNewSimulationRequest(logManager);
		try {
			Serializable result = pgModelDataManager.handle(request, processId, username);
		}catch (Exception e) {
			// possible exception expected in unit test
		}
		
//		request error log call made
		Mockito.verify(logManager, Mockito.times(1)).log(argCaptorLogMessage.capture(), argCaptor.capture(),argCaptor.capture()); // GridAppsDConstants.username);
		List<LogMessage> messages = argCaptorLogMessage.getAllValues();
		LogMessage capturedMessage = messages.get(0);
		assertEquals(true, capturedMessage.getLogMessage().startsWith("Error handling powergrid data request:"));
		assertEquals(LogLevel.ERROR, capturedMessage.getLogLevel());
		assertEquals(ProcessStatus.ERROR, capturedMessage.getProcessStatus());
		assertEquals(true, capturedMessage.getStoreToDb());	}
	
	

}
