package spring.ru.springtest.client;

import org.springframework.cloud.openfeign.FeignClient;
import spring.ru.springtest.client.metadata.BookMetadataApi;

@FeignClient(name = "book-metadata-client", url = "${services.second-service.url}")
public interface BookMetadataClient extends BookMetadataApi {

}
