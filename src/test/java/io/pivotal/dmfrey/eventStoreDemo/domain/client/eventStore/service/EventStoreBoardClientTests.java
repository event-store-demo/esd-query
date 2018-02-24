package io.pivotal.dmfrey.eventStoreDemo.domain.client.eventStore.service;

import io.pivotal.dmfrey.eventStoreDemo.domain.client.BoardClient;
import io.pivotal.dmfrey.eventStoreDemo.domain.client.eventStore.config.RestConfig;
import io.pivotal.dmfrey.eventStoreDemo.domain.events.*;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@RunWith( SpringRunner.class )
@SpringBootTest
@ActiveProfiles( "event-store" )
public class EventStoreBoardClientTests {

    private static final String BOARD_INITIALIZED_EVENT = "{\"eventType\":\"BoardInitialized\",\"boardUuid\":\"ff4795e1-2514-4f5a-90e2-cd33dfadfbf2\",\"occurredOn\":\"2018-02-23T03:49:52.313Z\"}";

    @Autowired
    private BoardClient client;

    @MockBean( name = "io.pivotal.dmfrey.eventStoreDemo.domain.client.eventStore.config.RestConfig$EventStoreClient" )
    private RestConfig.EventStoreClient eventStoreClient;

    UUID boardUuid = UUID.fromString( "ff4795e1-2514-4f5a-90e2-cd33dfadfbf2" );
    UUID storyUuid = UUID.fromString( "242500df-373e-4e70-90bc-3c8cd54c81d8" );

    @Test
    public void testFind() throws Exception {

        DomainEvents domainEvents = createDomainEvents();
        when( this.eventStoreClient.getDomainEventsForBoardUuid( any( UUID.class ) ) ).thenReturn( domainEvents );

        Board found = this.client.find( boardUuid );
        assertThat( found ).isNotNull();
        assertThat( found.getBoardUuid() ).isEqualTo( domainEvents.getBoardUuid() );
        assertThat( found.getName() ).isEqualTo( "New Board" );
        assertThat( found.getStories() ).hasSize( 0 );

        verify( this.eventStoreClient, times( 1 ) ).getDomainEventsForBoardUuid( any( UUID.class ) );

    }

    @Test
    public void testFindBoardRenamed() throws Exception {

        DomainEvents domainEvents = createDomainEvents();
        domainEvents.getDomainEvents().add( createBoardRenamedEvent() );
        when( this.eventStoreClient.getDomainEventsForBoardUuid( any( UUID.class ) ) ).thenReturn( domainEvents );

        Board found = this.client.find( boardUuid );
        assertThat( found ).isNotNull();
        assertThat( found.getBoardUuid() ).isEqualTo( domainEvents.getBoardUuid() );
        assertThat( found.getName() ).isEqualTo( "My Board" );
        assertThat( found.getStories() ).hasSize( 0 );

        verify( this.eventStoreClient, times( 1 ) ).getDomainEventsForBoardUuid( any( UUID.class ) );

    }

    @Test
    public void testFindStoryAdded() throws Exception {

        DomainEvents domainEvents = createDomainEvents();
        domainEvents.getDomainEvents().add( createBoardRenamedEvent() );
        domainEvents.getDomainEvents().add( createStoryAddedEvent() );
        when( this.eventStoreClient.getDomainEventsForBoardUuid( any( UUID.class ) ) ).thenReturn( domainEvents );

        Board found = this.client.find( boardUuid );
        assertThat( found ).isNotNull();
        assertThat( found.getBoardUuid() ).isEqualTo( domainEvents.getBoardUuid() );
        assertThat( found.getName() ).isEqualTo( "My Board" );
        assertThat( found.getStories() )
                .hasSize( 1 )
                .containsKey( storyUuid )
                .containsValue( createStoryAddedEvent().getStory() );

        verify( this.eventStoreClient, times( 1 ) ).getDomainEventsForBoardUuid( any( UUID.class ) );

    }

    @Test
    public void testFindStoryUdpated() throws Exception {

        DomainEvents domainEvents = createDomainEvents();
        domainEvents.getDomainEvents().add( createBoardRenamedEvent() );
        domainEvents.getDomainEvents().add( createStoryAddedEvent() );
        domainEvents.getDomainEvents().add( createStoryUpdatedEvent() );
        when( this.eventStoreClient.getDomainEventsForBoardUuid( any( UUID.class ) ) ).thenReturn( domainEvents );

        Board found = this.client.find( boardUuid );
        assertThat( found ).isNotNull();
        assertThat( found.getBoardUuid() ).isEqualTo( domainEvents.getBoardUuid() );
        assertThat( found.getName() ).isEqualTo( "My Board" );
        assertThat( found.getStories() )
                .hasSize( 1 )
                .containsKey( storyUuid )
                .containsValue( createStoryUpdatedEvent().getStory() );

        verify( this.eventStoreClient, times( 1 ) ).getDomainEventsForBoardUuid( any( UUID.class ) );

    }

    @Test
    public void testFindStoryDeleted() throws Exception {

        DomainEvents domainEvents = createDomainEvents();
        domainEvents.getDomainEvents().add( createBoardRenamedEvent() );
        domainEvents.getDomainEvents().add( createStoryAddedEvent() );
        domainEvents.getDomainEvents().add( createStoryUpdatedEvent() );
        domainEvents.getDomainEvents().add( createStoryDeletedEvent() );
        when( this.eventStoreClient.getDomainEventsForBoardUuid( any( UUID.class ) ) ).thenReturn( domainEvents );

        Board found = this.client.find( boardUuid );
        assertThat( found ).isNotNull();
        assertThat( found.getBoardUuid() ).isEqualTo( domainEvents.getBoardUuid() );
        assertThat( found.getName() ).isEqualTo( "My Board" );
        assertThat( found.getStories() ).hasSize( 0 );

        verify( this.eventStoreClient, times( 1 ) ).getDomainEventsForBoardUuid( any( UUID.class ) );

    }

    private DomainEvents createDomainEvents() {

        DomainEvents domainEvents = new DomainEvents();
        domainEvents.setBoardUuid( boardUuid );
        domainEvents.getDomainEvents().add( createBoardInitializedEvent() );

        return domainEvents;
    }

    private BoardInitialized createBoardInitializedEvent() {

        return new BoardInitialized( boardUuid, Instant.parse( "2018-02-23T03:49:52.313Z" ) );
    }

    private BoardRenamed createBoardRenamedEvent() {

        return new BoardRenamed( "My Board", boardUuid, Instant.parse( "2018-02-23T03:49:52.313Z" ) );
    }

    private StoryAdded createStoryAddedEvent() {

        return new StoryAdded( storyUuid,"My Story 1", boardUuid, Instant.parse( "2018-02-23T03:49:52.313Z" ) );
    }

    private StoryUpdated createStoryUpdatedEvent() {

        return new StoryUpdated( storyUuid,"My Story 1 Updated", boardUuid, Instant.parse( "2018-02-23T03:49:52.313Z" ) );
    }

    private StoryDeleted createStoryDeletedEvent() {

        return new StoryDeleted( storyUuid, boardUuid, Instant.parse( "2018-02-23T03:49:52.313Z" ) );
    }

}
