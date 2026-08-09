package spring.ru.springtest.client.metadata;

import org.springframework.cloud.openfeign.FeignClient;
import org.openapitools.configuration.ClientConfiguration;

@FeignClient(name="${book-metadata.name:book-metadata}", url="${book-metadata.url:http://localhost}", configuration = ClientConfiguration.class)
public interface BookMetadataApiClient extends BookMetadataApi {
}
