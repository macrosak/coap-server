package com.tado.coap;

import org.eclipse.californium.core.server.resources.Resource;
import org.springframework.util.AntPathMatcher;

import java.util.LinkedHashMap;
import java.util.List;

public class AntPathMessageDeliverer extends AbstractServerMessageDeliverer {
   protected LinkedHashMap<String, Resource> resources = new LinkedHashMap<>();
   protected AntPathMatcher matcher = new AntPathMatcher();

   public AntPathMessageDeliverer registerResource(String pattern, Resource resource) {
      resources.put(pattern, resource);
      return this;
   }

   protected Resource findResource(List<String> pathParts) {
      String requestUriPath = joinPath(pathParts);

      for (String pattern : resources.keySet()) {
         if (matcher.match(pattern, requestUriPath))
            return resources.get(pattern);
      }
      return null;
   }

   private String joinPath(List<String> pathParts) {
      StringBuilder buffer = new StringBuilder();
      for (String element : pathParts)
         buffer.append(element).append("/");
      if (buffer.length() == 0) {
         return "";
      } else {
         return buffer.substring(0, buffer.length() - 1);
      }
   }
}