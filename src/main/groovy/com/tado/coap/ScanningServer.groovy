package com.tado.coap

import groovy.util.logging.Slf4j
import org.eclipse.californium.core.CoapResource
import org.eclipse.californium.core.CoapServer
import org.eclipse.californium.core.network.CoapEndpoint
import org.reflections.Reflections

import java.util.regex.Pattern

@Slf4j
class ScanningServer {

   private static final int COAP_PORT = 5683

   private CoapServer coapServer

   public static void main(String[] args) {
      try {
         start()
      } catch (SocketException e) {
         System.err.println("Failed to initialize server: ${e.message}")
      }
   }

   private ScanningServer(CoapServer coapServer) {
      this.coapServer = coapServer
   }

   static ScanningServer start() {
      PatternMatchingMessageDeliverer deliverer = new PatternMatchingMessageDeliverer()

      Reflections reflections = new Reflections('com.tado')
      reflections.getTypesAnnotatedWith(CoapRequestMapping).each { Class resource ->
         String mapping = resource.getAnnotation(CoapRequestMapping).value()
         deliverer.registerResource(Pattern.compile(mapping), resource.newInstance() as CoapResource)
         log.info("registered CoAP resource ${resource.simpleName} on path ${mapping}")
      }

      // create server
      CoapServer coapServer = new CoapServer()
      coapServer.setMessageDeliverer(deliverer)
      coapServer.addEndpoint(new CoapEndpoint(COAP_PORT))
      coapServer.start()

      return new ScanningServer(coapServer)
   }

   void stop() {
      coapServer.stop()
   }
}
