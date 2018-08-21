package io.pivotal.dmfrey.eventStoreDemo.domain.client.eventStore.service;

import io.pivotal.dmfrey.eventStoreDemo.domain.client.BoardClient;
import io.pivotal.dmfrey.eventStoreDemo.domain.client.eventStore.config.RestConfig;
import io.pivotal.dmfrey.eventStoreDemo.domain.events.DomainEvents;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.UUID;

public class EventStoreBoardClient implements BoardClient {

    private static final Logger log = LoggerFactory.getLogger( EventStoreBoardClient.class );

    private final RestConfig.EventStoreClient eventStoreClient;

    public EventStoreBoardClient( final RestConfig.EventStoreClient eventStoreClient ) {

        this.eventStoreClient = eventStoreClient;

    }

    @Override
    @Cacheable( "boards" )
    public Board find( final UUID boardUuid ) {
        log.debug( "find : enter" );

        DomainEvents domainEvents = this.eventStoreClient.getDomainEventsForBoardUuid( boardUuid );
        if( null == domainEvents || null == domainEvents.getDomainEvents() || domainEvents.getDomainEvents().isEmpty() ) {

            log.warn( "find : exit, target[" + boardUuid.toString() + "] not found" );
            throw new IllegalArgumentException( "board[" + boardUuid.toString() + "] not found" );
        }

        Board board = Board.createFrom( boardUuid, domainEvents.getDomainEvents() );

        log.debug( "find : exit" );
        return board;
    }

    @Override
    @CacheEvict( value = "boards", key = "#boardUuid" )
    public void removeFromCache( final UUID boardUuid ) {
        log.debug( "removeFromCache : enter" );

        // this method is intentionally left blank

        log.debug( "removeFromCache : exit" );
    }

}
