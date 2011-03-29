/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.view.rest;

import java.net.URI;
import java.util.concurrent.ExecutorService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.fudgemsg.FudgeContext;
import org.springframework.jms.core.JmsTemplate;

import com.opengamma.engine.view.ViewProcess;
import com.opengamma.engine.view.ViewProcessor;
import com.opengamma.livedata.UserPrincipal;
import com.opengamma.transport.jms.JmsByteArrayMessageSenderService;

/**
 * RESTful resource for a {@link ViewProcessor}.
 */
public class DataViewProcessorResource {

  //CSOFF: just constants
  public static final String PATH_VIEW_NAMES = "viewNames";
  //CSON: just constants
  
  private final ViewProcessor _viewProcessor;
  private final JmsTemplate _jmsTemplate;
  private final String _jmsTopicPrefix;
  private final JmsByteArrayMessageSenderService _jmsMessageSenderService;
  private final FudgeContext _fudgeContext;
  private final ExecutorService _executorService;
  
  public DataViewProcessorResource(ViewProcessor viewProcessor, ActiveMQConnectionFactory connectionFactory, String jmsTopicPrefix, FudgeContext fudgeContext, ExecutorService executorService) {
    _viewProcessor = viewProcessor;
    
    _jmsTemplate = new JmsTemplate(connectionFactory);
    _jmsTemplate.setPubSubDomain(true);
    
    _jmsMessageSenderService = new JmsByteArrayMessageSenderService(_jmsTemplate);
    _jmsTopicPrefix = jmsTopicPrefix;
    _fudgeContext = fudgeContext;
    _executorService = executorService;
  }
  
  //-------------------------------------------------------------------------
  @GET
  @Path(PATH_VIEW_NAMES)
  public Response getViewNames() {
    return Response.ok(_viewProcessor.getViewProcesses()).build();
  }
  
  @Path("views/{viewName}")
  public DataViewResource getView(@PathParam("viewName") String viewName) {
    // TODO: authentication of the remote user while still providing RESTful access.
    UserPrincipal user = UserPrincipal.getLocalUser();
    
    ViewProcess view = _viewProcessor.getView(viewName, user);
    if (view == null) {
      return null;
    }
    return new DataViewResource(view, _jmsTemplate, _jmsMessageSenderService, _jmsTopicPrefix, _fudgeContext, _executorService);
  }
  
  //-------------------------------------------------------------------------
  public static URI uriView(URI baseUri, String viewName) {
    // WARNING: '/' characters could well appear in the view name
    // There is a bug(?) in UriBuilder where, even though segment() is meant to treat the item as a single path segment
    // and therefore encode '/' characters, it does not encode '/' characters which come from a variable substitution.
    return UriBuilder.fromUri(baseUri).path("views").segment(viewName).build();
  }
  
}
