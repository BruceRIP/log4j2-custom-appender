package com.log4j2.custom.appender;

import com.log4j2.custom.appender.client.ElasticSearchClient;
import com.log4j2.custom.appender.client.ElasticSearchConnection;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;

/* @Plugin indicates that our appender will be a plugin
 * name: Is then name of appender. It should specified in the properties file
 */
@Plugin(name = "ElasticAppender",
        category = Core.CATEGORY_NAME,
        elementType = Appender.ELEMENT_TYPE)
public class ElasticAppender extends AbstractAppender {

    protected ElasticAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, Property[] properties) {
        super(name, filter, layout, ignoreExceptions, properties);
        ElasticSearchConnection.connect();
    }

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
}
