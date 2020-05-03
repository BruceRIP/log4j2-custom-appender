# Log4j2 Asynchronous Appender
We will check how to configure a log4j an asynchronous mode and send all log to ElasticSearch

# Setting connector to ElasticSearch 
### ElasticSearch Client Connector  
Configuration to connect to the server/DB. **It is based on singleton pattern**
```java
public class ElasticSearchConnection {

    /* This is a thread-safe*/
    private static RestHighLevelClient restHighLevelClient;
    private static final String HOST = "localhost";
    private static final int PORT = 9200;
    private static final String SCHEMA = "HTTP";

    private ElasticSearchConnection(){}

    public static synchronized void connect(){
        if(restHighLevelClient == null){
            restHighLevelClient = new RestHighLevelClient(
                    RestClient.builder(new HttpHost(HOST, PORT, SCHEMA))
            );
        }
    }

    public static RestHighLevelClient client(){
        return restHighLevelClient;
    }

    public static synchronized void close(){
        if(restHighLevelClient != null){
            try {
                restHighLevelClient.close();
                restHighLevelClient = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}  
```
### How to use a basic operations
1. We need to get client from connection
2. Use this client to execute some operations  
    2.1. For save operations you must use *index method* 
````java
public class ElasticSearchClient {

    public void append(final String index, final String logName, final String message) {
        Map<String, String> logMessage = new LinkedHashMap<>();
        logMessage.put(logName, message);
        IndexRequest indexRequest = new IndexRequest(index);
        indexRequest.source(logMessage);
        save(indexRequest);
    }

    private void save(final IndexRequest message){
        try {
            ElasticSearchConnection.client().index(message, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
````

# Custom Log4j2 Appender
- Create a custom class. *ElasticAppenderExample* 
- We need to extend of org.apache.logging.log4j.core.appender.AbstractAppender
- Override methods
```java
 @Override
 public void append(LogEvent logEvent) {
        
 }
``` 
- We need to define some properties in class definition
```java
@Plugin(name = "ElasticAppender",
        category = Core.CATEGORY_NAME,
        elementType = Appender.ELEMENT_TYPE)
public class ElasticAppender extends AbstractAppender {
}
```
> @Plugin: Indicates that our appender will be a plugin  
> name: Is then name of appender. *It should be specified in the log4j configuration file*   
> category:   
> elementType:   
- We need a constructor of a super class and call to our custom connector
```java
protected ElasticAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, Property[] properties) {
        super(name, filter, layout, ignoreExceptions, properties);
        ElasticSearchConnection.connect();
    }
``` 
- Create our own appender
```java
    @PluginFactory
    public static ElasticAppender createAppender(@PluginAttribute("name") String name,
                                                 @PluginElement("Filters") Filter filter,
                                                 @PluginElement("PatternLayout") PatternLayout patternLayout) {
        return new ElasticAppender(name, filter, patternLayout, false, new Property[2]);
    }

    @Override
    public void append(LogEvent logEvent) {
        String message = new String(this.getLayout().toByteArray(logEvent));
        ElasticSearchClient.append("bruce-async-appender", "bruce-async-log", message);
    }
``` 

# Creating log4j2 configuration file
- If you need to use an aynschronuos logger you must configure Log4jContextSelector property
```dtd
-DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector
     to make all loggers asynchronous.
```
> When AsyncLoggerContextSelector is used to make all loggers asynchronous, make sure to use normal <root> and <logger> elements in the configuration. The AsyncLoggerContextSelector will ensure that all loggers are asynchronous, using a mechanism that is different from what happens when you configure 
```xml
<asyncRoot> or <asyncLogger>
```
- Create a log4j2.xml file and put inside the resources folder
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration xmlns:xi="http://www.w3.org/2001/XInclude" packages="com.log4j2.custom.appender" status="WARN">
    <Appenders>
        <ElasticAppender name="ElasticAppender">
            <JSONLayout complete="true" compact="true"/>
            <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] [%X{identity}] %-5level %logger{36}(%L) - %msg%n" />
        </ElasticAppender>
        <Console name="Console" target="SYSTEM_OUT">
            <JSONLayout complete="true" compact="true"/>
            <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] [%X{identity}] %-5level %logger{36}(%L) - %msg%n" />
        </Console>
    </Appenders>

    <Loggers>
        <Root level="DEBUG">
            <AppenderRef ref="ElasticAppender" />
            <AppenderRef ref="Console" />
        </Root>
    </Loggers>
</Configuration>
```

#Properties file
> JSONLayout: This property print a log message in json format  
    complete = "true": the appender outputs a well-formed JSON document
    compact="true": not pretty  

> PatternLayout: The goal of this class is to format a LogEvent and return the results   
    [%t]: Used to output the name of the thread that generated the logging event   
    [%X{identity}]: MDC Mapped Diagnostic Context associated with the thread that generated the logging event   
    (%L): Used to output the line number from where the logging request was issued. WARNING Generating caller location information is extremely slow 

# Bibliography
> How to configure log4j an asynchronous mode
https://logging.apache.org/log4j/2.x/manual/async.html   
> Appenders
https://logging.apache.org/log4j/log4j-2.3/manual/appenders.html
