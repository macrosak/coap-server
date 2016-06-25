package com.tado.coap

import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.CoapResponse
import org.eclipse.californium.core.coap.CoAP
import org.eclipse.californium.core.coap.MediaTypeRegistry
import spock.lang.Specification

class ManualConfigServerSpec  extends Specification {

   static ManualConfigServer server

   def setupSpec() {
      server = ManualConfigServer.start()
   }

   def cleanupSpec() {
      server.stop()
   }

   def "update battery level of device 12345"() {
      when:
      def client = new CoapClient('coap://localhost:5683/device/1234/battery')
      CoapResponse response = client.post('75 %', MediaTypeRegistry.TEXT_PLAIN)

      then:
      response.responseText == 'battery of device 1234 updated'
   }

   def "update temperature of device QWERTY78"() {
      when:
      def client = new CoapClient('coap://localhost:5683/device/QWERTY78/temperature')
      CoapResponse response = client.post('22 ˚C', MediaTypeRegistry.TEXT_PLAIN)

      then:
      response.responseText == 'temperature of device QWERTY78 updated'
   }

   def "debug request by device 12345 is handled by debug resource"() {
      when:
      def client = new CoapClient('coap://localhost:5683/device/12345/debug')
      CoapResponse response = client.post('debug data', MediaTypeRegistry.TEXT_PLAIN)

      then:
      response.responseText == 'debug'
   }

   def "debug request by device QWERTY78 is NOT handled by debug resource"() {
      when:
      def client = new CoapClient('coap://localhost:5683/device/QWERTY78/debug')
      CoapResponse response = client.post('debug data', MediaTypeRegistry.TEXT_PLAIN)

      then:
      response.responseText != 'debug'
   }

   def "phone resource does not exist"() {
      when:
      def client = new CoapClient('coap://localhost:5683/phone/00123123123/location')
      CoapResponse response = client.get()

      then:
      response.code == CoAP.ResponseCode.NOT_FOUND
   }

}