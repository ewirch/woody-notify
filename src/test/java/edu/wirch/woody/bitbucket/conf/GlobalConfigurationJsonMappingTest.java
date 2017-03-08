package edu.wirch.woody.bitbucket.conf;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;

public class GlobalConfigurationJsonMappingTest {

    private static final String DEFAULT_SERIALIZED = "{\"webhook\":\"http://server.com/some/64aeou4a6u4e6u4ao\",\"globalEventChannel\":null,\"authenticationFailureEvent\":false,\"backupFailedEvent\":false,\"maintenanceStartedEvent\":false,\"maintenanceEndedEvent\":false}";

    @Test
    public void testSerialization() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final GlobalConfiguration object = GlobalConfigurationData.withDefaults()
                                                                  .build();
        final String serialized = mapper.writeValueAsString(object);
        assertThat(serialized).isEqualTo(DEFAULT_SERIALIZED);
    }

    @Test
    public void testDeserialization() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final GlobalConfiguration.Builder builder = mapper.readValue(DEFAULT_SERIALIZED,
                GlobalConfiguration.Builder.class);
        assertThat(GlobalConfigurationData.withDefaults()
                                          .build()).isEqualTo(builder.build());
    }

}