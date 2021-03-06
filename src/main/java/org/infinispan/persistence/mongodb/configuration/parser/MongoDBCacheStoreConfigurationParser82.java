package org.infinispan.persistence.mongodb.configuration.parser;

import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.PersistenceConfigurationBuilder;
import org.infinispan.configuration.parsing.ConfigurationBuilderHolder;
import org.infinispan.configuration.parsing.ConfigurationParser;
import org.infinispan.configuration.parsing.Namespace;
import org.infinispan.configuration.parsing.Namespaces;
import org.infinispan.configuration.parsing.ParseUtils;
import org.infinispan.configuration.parsing.XMLExtendedStreamReader;
import org.infinispan.persistence.mongodb.configuration.MongoDBStoreConfigurationBuilder;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import static org.infinispan.commons.util.StringPropertyReplacer.replaceProperties;

/**
 * Parses the configuration from the XML. For valid elements and attributes refer to {@link Element} and {@link
 * Attribute}
 *
 * @author Guillaume Scheibel <guillaume.scheibel@gmail.com>
 * @author Gabriel Francisco <gabfssilva@gmail.com>
 * @author gustavonalle
 */
@Namespaces({
        @Namespace(uri = "urn:infinispan:config:mongodb:8.2", root = "mongodbStore"),
        @Namespace(root = "mongodbStore")
})
public class MongoDBCacheStoreConfigurationParser82 implements ConfigurationParser {

   @Override
   public void readElement(XMLExtendedStreamReader xmlExtendedStreamReader, ConfigurationBuilderHolder configurationBuilderHolder)
           throws XMLStreamException {
      ConfigurationBuilder builder = configurationBuilderHolder.getCurrentConfigurationBuilder();

      Element element = Element.forName(xmlExtendedStreamReader.getLocalName());
      switch (element) {
         case MONGODB_STORE: {
            parseMongoDBStore(
                    xmlExtendedStreamReader,
                    builder.persistence(),
                    configurationBuilderHolder.getClassLoader()
            );
            break;
         }
         default: {
            throw ParseUtils.unexpectedElement(xmlExtendedStreamReader);
         }
      }
   }

   @Override
   public Namespace[] getNamespaces() {
      return ParseUtils.getNamespaceAnnotations(getClass());
   }

   private void parseMongoDBStore(XMLExtendedStreamReader reader, PersistenceConfigurationBuilder persistenceConfigurationBuilder, ClassLoader classLoader)
           throws XMLStreamException {
      MongoDBStoreConfigurationBuilder builder = new MongoDBStoreConfigurationBuilder(persistenceConfigurationBuilder);

      while (reader.hasNext() && (reader.nextTag() != XMLStreamConstants.END_ELEMENT)) {
         Element element = Element.forName(reader.getLocalName());
         switch (element) {
            case CONNECTION: {
               this.parseConnection(reader, builder);
               break;
            }
            default: {
               throw ParseUtils.unexpectedElement(reader);
            }
         }
      }
      persistenceConfigurationBuilder.addStore(builder);
   }

   private void parseStorage(XMLExtendedStreamReader reader, MongoDBStoreConfigurationBuilder builder)
           throws XMLStreamException {
      for (int i = 0; i < reader.getAttributeCount(); i++) {
         ParseUtils.requireNoNamespaceAttribute(reader, i);
         String value = replaceProperties(reader.getAttributeValue(i));
         Attribute attribute = Attribute.forName(reader.getAttributeLocalName(i));
         switch (attribute) {
            case CONNECTION_URI: {
               builder.connectionURI(value);
               break;
            }
            case COLLECTION: {
               builder.collection(value);
               break;
            }
            default: {
               throw ParseUtils.unexpectedElement(reader);
            }
         }
      }
      ParseUtils.requireNoContent(reader);
   }

   private void parseConnection(XMLExtendedStreamReader reader, MongoDBStoreConfigurationBuilder builder)
           throws XMLStreamException {
      for (int i = 0; i < reader.getAttributeCount(); i++) {
         ParseUtils.requireNoNamespaceAttribute(reader, i);
         String value = replaceProperties(reader.getAttributeValue(i));
         Attribute attribute = Attribute.forName(reader.getAttributeLocalName(i));
         switch (attribute) {
            case CONNECTION_URI: {
               builder.connectionURI(value);
               break;
            }
            case COLLECTION: {
               builder.collection(value);
               break;
            }
            default: {
               throw ParseUtils.unexpectedAttribute(reader, i);
            }
         }
      }
      ParseUtils.requireNoContent(reader);
   }

}
