package com.tado.coap

import org.eclipse.californium.core.server.resources.Resource
import org.springframework.util.AntPathMatcher

class AntPathMessageDeliverer extends AbstractServerMessageDeliverer {
   protected LinkedHashMap<String, Resource> resources = new LinkedHashMap<>()
   protected AntPathMatcher matcher = new AntPathMatcher()

   public AntPathMessageDeliverer registerResource(String pattern, Resource resource) {
      resources.put(pattern, resource)
      return this
   }

   protected Resource findResource(List<String> pathParts) {
      String requestUriPath = pathParts.join('/')
      return resources.find { pattern, resource ->
         matcher.match(pattern, requestUriPath)
      }?.value
   }
}
