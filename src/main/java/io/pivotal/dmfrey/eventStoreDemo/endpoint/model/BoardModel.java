package io.pivotal.dmfrey.eventStoreDemo.endpoint.model;

import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Story;
import lombok.Data;

import java.util.Collection;

@Data
public class BoardModel {

    private String name;
    private Collection<Story> backlog;

    public static BoardModel fromBoard( final Board board ) {

        BoardModel model = new BoardModel();
        model.setName( board.getName() );

        if( null != board.getStories() && !board.getStories().isEmpty() ) {

            model.setBacklog( board.getStories().values() );
        }

        return model;
    }

}
