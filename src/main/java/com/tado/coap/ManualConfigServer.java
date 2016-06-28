package com.tado.coap;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.server.MessageDeliverer;
import org.eclipse.californium.core.server.resources.CoapExchange;

import java.util.List;
import java.util.regex.Pattern;

public class ManualConfigServer {
   private static final int COAP_PORT = 5683;

   private CoapServer coapServer;

   public static void main(String[] args) {
      start();
   }

   private ManualConfigServer(CoapServer coapServer) {
      this.coapServer = coapServer;
   }

   private static ManualConfigServer start() {
      MessageDeliverer deliverer = new PatternMatchingMessageDeliverer()
         .registerResource(Pattern.compile("device/[^/]+/temperature"), new SensorsResource("temperature"))
         .registerResource(Pattern.compile("device/[^/]+/battery"), new SensorsResource("battery"))
         .registerResource(Pattern.compile("device/12345/debug"), new DebugResource())
         .registerResource(Pattern.compile("device/98765/debug"), new DebugResource())
         .registerResource(Pattern.compile("device/.*"), new MatchAllResource());

      // create server
      CoapServer coapServer = new CoapServer();
      coapServer.setMessageDeliverer(deliverer);
      coapServer.addEndpoint(new CoapEndpoint(COAP_PORT));
      coapServer.start();

      return new ManualConfigServer(coapServer);
   }

   void stop() {
      coapServer.stop();
   }

   private static final class SensorsResource extends CoapResource {
      String sensorName;

      SensorsResource(String sensorName) {
         super(sensorName);
         this.sensorName = sensorName;
      }

      @Override
      public void handlePOST(CoapExchange exchange) {
         String serialNumber = parseSerialNumber(exchange.getRequestOptions().getUriPath());
         exchange.respond(sensorName + " of device " + serialNumber + " updated");
      }

      private static String parseSerialNumber(List<String> uriPath) {
         assert uriPath.size() > 1;
         return uriPath.get(1);
      }
   }

   private static final class DebugResource extends CoapResource {
      DebugResource() {
         super("debug");
      }

      @Override
      public void handlePOST(CoapExchange exchange) {
         exchange.respond("debug");
      }
   }

   private static final class MatchAllResource extends CoapResource {
      MatchAllResource() {
         super("matchall");
      }

      @Override
      public void handleGET(CoapExchange exchange) {
         exchange.respond("received CoAP request");
      }
   }

}
