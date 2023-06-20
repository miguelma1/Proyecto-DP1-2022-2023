package org.springframework.samples.petclinic.comment;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    private static final Integer ID_GAME_WITH_COMMENTS = 2;

    @Test
    public void testFindCommentsByGame() {
        List<Comment> comments = commentRepository.findCommentsByGame(ID_GAME_WITH_COMMENTS);
        assertNotNull(comments);
        assertFalse(comments.isEmpty());
    }
    
}
