package com.tado.coap

import org.eclipse.californium.core.coap.CoAP
import org.eclipse.californium.core.coap.Request
import org.eclipse.californium.core.coap.Response
import org.eclipse.californium.core.network.Exchange
import org.eclipse.californium.core.observe.ObserveManager
import org.eclipse.californium.core.observe.ObserveRelation
import org.eclipse.californium.core.observe.ObservingEndpoint
import org.eclipse.californium.core.server.MessageDeliverer
import org.eclipse.californium.core.server.resources.Resource

import java.util.concurrent.Executor
import java.util.logging.Logger
import java.util.regex.Pattern

/**
 * Copy pasted code from
 * @see org.eclipse.californium.core.server.ServerMessageDeliverer
 *
 * Only the findResource method is modified
 */
class PatternMatchingMessageDeliverer implements MessageDeliverer {
   /**
    * CUSTOM CODE
    */
   protected LinkedHashMap<Pattern, Resource> resources = new LinkedHashMap<>()

   public PatternMatchingMessageDeliverer registerResource(Pattern pattern, Resource resource) {
      resources.put(pattern, resource)
      return this
   }

   protected Resource findResource(List<String> pathParts) {
      String requestUriPath = pathParts.join('/')
      return resources.find { pattern, resource ->
         requestUriPath ==~ pattern
      }?.value
   }

   /**
    * COPY PASTED FROM @see org.eclipse.californium.core.server.ServerMessageDeliverer
    */
   private final static Logger LOGGER = Logger.getLogger(PatternMatchingMessageDeliverer.class.getCanonicalName());
   /* The manager of the observe mechanism for this server */
   protected ObserveManager observeManager = new ObserveManager();

   @Override
   public void deliverRequest(final Exchange exchange) {
      Request request = exchange.getRequest();
      List<String> path = request.getOptions().getUriPath();
      final Resource resource = findResource(path);
      if (resource != null) {
         checkForObserveOption(exchange, resource);

         // Get the executor and let it process the request
         Executor executor = resource.getExecutor();
         if (executor != null) {
            exchange.setCustomExecutor();
            executor.execute(new Runnable() {
               public void run() {
                  resource.handleRequest(exchange);
               }
            });
         } else {
            resource.handleRequest(exchange);
         }
      } else {
         LOGGER.info("Did not find resource " + path.toString() + " requested by " + request.getSource() + ":" + request.getSourcePort());
         exchange.sendResponse(new Response(CoAP.ResponseCode.NOT_FOUND));
      }
   }

   /**
    * Checks whether an observe relationship has to be established or canceled.
    * This is done here to have a server-global observeManager that holds the
    * set of remote endpoints for all resources. This global knowledge is required
    * for efficient orphan handling.
    *
    * @param exchange
    *            the exchange of the current request
    * @param resource
    *            the target resource
    * @param path
    *            the path to the resource
    */
   private void checkForObserveOption(Exchange exchange, Resource resource) {
      Request request = exchange.getRequest();
      if (request.getCode() != CoAP.Code.GET) return;

      InetSocketAddress source = new InetSocketAddress(request.getSource(), request.getSourcePort());

      if (request.getOptions().hasObserve() && resource.isObservable()) {

         if (request.getOptions().getObserve() == 0) {
            // Requests wants to observe and resource allows it :-)
            LOGGER.finer("Initiate an observe relation between " + request.getSource() + ":" + request.getSourcePort() + " and resource " + resource.getURI());
            ObservingEndpoint remote = observeManager.findObservingEndpoint(source);
            ObserveRelation relation = new ObserveRelation(remote, resource, exchange);
            remote.addObserveRelation(relation);
            exchange.setRelation(relation);
            // all that's left is to add the relation to the resource which
            // the resource must do itself if the response is successful

         } else if (request.getOptions().getObserve() == 1) {
            // Observe defines 1 for canceling
            ObserveRelation relation = observeManager.getRelation(source, request.getToken());
            if (relation != null) relation.cancel();
         }
      }
   }

   /* (non-Javadoc)
    * @see ch.inf.vs.californium.MessageDeliverer#deliverResponse(ch.inf.vs.californium.network.Exchange, ch.inf.vs.californium.coap.Response)
    */

   @Override
   public void deliverResponse(Exchange exchange, Response response) {
      if (response == null) throw new NullPointerException();
      if (exchange == null) throw new NullPointerException();
      if (exchange.getRequest() == null) throw new NullPointerException();
      exchange.getRequest().setResponse(response);
   }
}
