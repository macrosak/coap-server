package com.tado.coap

import org.eclipse.californium.core.CoapResource
import org.eclipse.californium.core.server.resources.CoapExchange

@CoapRequestMapping('land/people/[^/]+')
class MagicMirrorResource extends CoapResource {
   MagicMirrorResource() {
      super('magic-mirror')
   }

   @Override
   void handleGET(CoapExchange exchange) {
      String name = parseName(exchange.requestOptions.uriPath)
      exchange.respond("${name}, you are the fairest in the land.")
   }

   private static String parseName(List<String> uriPath) {
      assert uriPath.size() == 3
      return uriPath[2]
   }
}
