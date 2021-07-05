package org.sc.configuration;

import de.micromata.opengis.kml.v_2_2_0.Kml;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;


@Configuration
public class ServiceConfig {

    @Bean
    public Marshaller marshaller() throws JAXBException {
        final JAXBContext jc = JAXBContext.newInstance(Kml.class);
        return jc.createMarshaller();
    }
}
