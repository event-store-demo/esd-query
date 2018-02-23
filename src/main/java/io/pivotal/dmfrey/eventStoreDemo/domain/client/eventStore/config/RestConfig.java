package io.pivotal.dmfrey.eventStoreDemo.domain.client.eventStore.config;

import io.pivotal.dmfrey.eventStoreDemo.domain.events.DomainEvent;
import io.pivotal.dmfrey.eventStoreDemo.domain.events.DomainEvents;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Profile( "event-store" )
@Configuration
@EnableFeignClients
public class RestConfig {

    @FeignClient( value = "event-store", fallback = HystrixFallbackEventStoreClient.class )
    public interface EventStoreClient {

        @GetMapping( path = "/{boardUuid}" )
        DomainEvents getDomainEventsForBoardUuid( @PathVariable( "boardUuid" ) UUID boardId );

    }

//    @Bean
//    public HystrixFallbackEventStoreClient hystrixFallbackEventStoreClient() {
//
//        return new HystrixFallbackEventStoreClient();
//    }

    @Component
    static class HystrixFallbackEventStoreClient implements EventStoreClient {

        @Override
        public DomainEvents getDomainEventsForBoardUuid( final UUID boardUuid) {

            return new DomainEvents();
        }

    }

}