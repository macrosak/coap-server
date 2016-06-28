package com.tado.coap;

import org.eclipse.californium.core.server.resources.Resource;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

public class PatternMatchingMessageDeliverer extends AbstractServerMessageDeliverer {

   protected LinkedHashMap<Pattern, Resource> resources = new LinkedHashMap<>();

   public PatternMatchingMessageDeliverer registerResource(Pattern pattern, Resource resource) {
      resources.put(pattern, resource);
      return this;
   }

   @Override
   protected Resource findResource(List<String> pathParts) {
      String requestUriPath = joinPath(pathParts);

      for (Pattern pattern : resources.keySet()) {
         if (pattern.matcher(requestUriPath).matches())
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