package com.tado.coap;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.reflections.Reflections;

import java.util.logging.Logger;
import java.util.regex.Pattern;

public class ScanningServer {
   private static final int COAP_PORT = 5683;

   private final static Logger LOGGER = Logger.getLogger(PatternMatchingMessageDeliverer.class.getCanonicalName());
   private CoapServer coapServer;

   public static void main(String[] args) {
      start();
   }

   private ScanningServer(CoapServer coapServer) {
      this.coapServer = coapServer;
   }

   static ScanningServer start() {
      PatternMatchingMessageDeliverer deliverer = new PatternMatchingMessageDeliverer();

      Reflections reflections = new Reflections("com.tado");
      for (Class resource : reflections.getTypesAnnotatedWith(CoapRequestMapping.class)) {
         String mapping = ((CoapRequestMapping) resource.getAnnotation(CoapRequestMapping.class)).value();
         try {
            deliverer.registerResource(Pattern.compile(mapping), (CoapResource) resource.newInstance());
         } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.severe("cannot instantiate class " + resource.getName());
         }
         LOGGER.info("registered CoAP resource " + resource.getSimpleName() + " on path " + mapping);
      }

      // create server
      CoapServer coapServer = new CoapServer();
      coapServer.setMessageDeliverer(deliverer);
      coapServer.addEndpoint(new CoapEndpoint(COAP_PORT));
      coapServer.start();

      return new ScanningServer(coapServer);
   }

   void stop() {
      coapServer.stop();
   }
}