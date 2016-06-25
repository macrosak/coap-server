# Pattern Matching CoAP Server

## Configure resources

### Manually

#### Groovy
See [ManualConfigServer](https://github.com/macrosak/coap-server/blob/master/src/main/groovy/com/tado/coap/ManualConfigServer.groovy)
```groovy
MessageDeliverer deliverer = new PatternMatchingMessageDeliverer()
  .registerResource(~'device/[^/]+/temperature', new SensorsResource('temperature'))
  .registerResource(~'device/.*', new MatchAllResource())
```

#### Java
Using `java.util.regex.Pattern`
```java
MessageDeliverer deliverer = new PatternMatchingMessageDeliverer()
  .registerResource(Pattern.compile("device/[^/]+/temperature"), new SensorsResource("temperature"));
```



### Automagically

#### Groovy
See [MagicMirrorResource](https://github.com/macrosak/coap-server/blob/master/src/main/groovy/com/tado/coap/MagicMirrorResource.groovy)
```groovy
@CoapRequestMapping('land/people/[^/]+')
class MagicMirrorResource extends CoapResource {
  // requires constructor without parameters so it can be instantiated with newInstance()
  MagicMirrorResource() {
    super('magic-mirror')
  }
  
  // handle stuff
}
```
