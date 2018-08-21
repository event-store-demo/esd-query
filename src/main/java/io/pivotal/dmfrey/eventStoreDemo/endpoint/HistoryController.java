package io.pivotal.dmfrey.eventStoreDemo.endpoint;

import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import io.pivotal.dmfrey.eventStoreDemo.domain.service.BoardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@ConditionalOnProperty( prefix = "feature", name = "history.enabled" )
@RestController
public class HistoryController {

    private static final Logger log = LoggerFactory.getLogger( HistoryController.class );

    private final BoardService service;

    public HistoryController( final BoardService service ) {

        this.service = service;

    }

    @GetMapping( "/boards/{boardUuid}/history" )
    public ResponseEntity history( @PathVariable( "boardUuid" ) UUID boardUuid ) {
        log.debug( "history : enter" );

        Board board = this.service.find( boardUuid );
        log.debug( "history : board=" + board.toString() );

        return ResponseEntity
                .ok( board.changes() );
    }

}
