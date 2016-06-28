package com.tado.coap;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

import java.util.List;

@CoapRequestMapping("land/people/[^/]+")
public class MagicMirrorResource extends CoapResource {

   public MagicMirrorResource() {
      super("magic-mirror");
   }

   @Override
   public void handleGET(CoapExchange exchange) {
      String name = parseName(exchange.getRequestOptions().getUriPath());
      exchange.respond(name + ", you are the fairest in the land.");
   }

   private static String parseName(List<String> uriPath) {
      assert uriPath.size() == 3;
      return uriPath.get(2);
   }
}
