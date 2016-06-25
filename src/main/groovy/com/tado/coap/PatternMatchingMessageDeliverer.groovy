package com.tado.coap

import org.eclipse.californium.core.server.resources.Resource

import java.util.regex.Pattern

class PatternMatchingMessageDeliverer extends AbstractServerMessageDeliverer {

   protected LinkedHashMap<Pattern, Resource> resources = new LinkedHashMap<>()

   public PatternMatchingMessageDeliverer registerResource(Pattern pattern, Resource resource) {
      resources.put(pattern, resource)
      return this
   }

   @Override
   protected Resource findResource(List<String> pathParts) {
      String requestUriPath = pathParts.join('/')
      return resources.find { pattern, resource ->
         requestUriPath ==~ pattern
      }?.value
   }
}
