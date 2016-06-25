package com.tado.coap

import org.eclipse.californium.core.CoapResource
import org.eclipse.californium.core.CoapServer
import org.eclipse.californium.core.network.CoapEndpoint
import org.eclipse.californium.core.server.MessageDeliverer
import org.eclipse.californium.core.server.resources.CoapExchange

class ManualConfigServer {

   private static final int COAP_PORT = 5683

   private CoapServer coapServer

   public static void main(String[] args) {
      try {
         start()
      } catch (SocketException e) {
         System.err.println("Failed to initialize server: ${e.message}")
      }
   }

   private ManualConfigServer(CoapServer coapServer) {
      this.coapServer = coapServer
   }

   static ManualConfigServer start() {
      MessageDeliverer deliverer = new PatternMatchingMessageDeliverer()
         .registerResource(~'device/[^/]+/temperature', new SensorsResource('temperature'))
         .registerResource(~'device/[^/]+/battery', new SensorsResource('battery'))
         .registerResource(~'device/12345/debug', new DebugResource())
         .registerResource(~'device/98765/debug', new DebugResource())
         .registerResource(~'device/.*', new MatchAllResource())

      // create server
      CoapServer coapServer = new CoapServer()
      coapServer.setMessageDeliverer(deliverer)
      coapServer.addEndpoint(new CoapEndpoint(COAP_PORT))
      coapServer.start()

      return new ManualConfigServer(coapServer)
   }

   void stop() {
      coapServer.stop()
   }

   private static final class SensorsResource extends CoapResource {
      String sensorName

      SensorsResource(String sensorName) {
         super(sensorName)
         this.sensorName = sensorName
      }

      @Override
      void handlePOST(CoapExchange exchange) {
         String serialNumber = parseSerialNumber(exchange.requestOptions.uriPath)
         exchange.respond("${sensorName} of device ${serialNumber} updated")
      }

      private static String parseSerialNumber(List<String> uriPath) {
         assert uriPath.size() > 1
         return uriPath[1]
      }
   }

   private static final class DebugResource extends CoapResource {
      DebugResource() {
         super('debug')
      }

      @Override
      void handlePOST(CoapExchange exchange) {
         String path = exchange.requestOptions.uriPath.join('/')
         exchange.respond("debug")
      }
   }

   private static final class MatchAllResource extends CoapResource {
      MatchAllResource() {
         super('matchall')
      }

      @Override
      void handleGET(CoapExchange exchange) {
         exchange.respond("received CoAP request on path $path")
      }
   }
}
