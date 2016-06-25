package com.tado.coap

import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.CoapResponse
import spock.lang.Specification

class ScanningServerSpec extends Specification {

   static ScanningServer server

   def setupSpec() {
      server = ScanningServer.start()
   }

   def cleanupSpec() {
      server.stop()
   }

   def "michal is the fairest in the land"() {
      when:
      def client = new CoapClient('coap://localhost:5683/land/people/michal')
      CoapResponse response = client.get()

      then:
      response.responseText == 'michal, you are the fairest in the land.'
   }
}
