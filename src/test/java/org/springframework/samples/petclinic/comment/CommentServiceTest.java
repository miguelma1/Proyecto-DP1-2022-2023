package org.springframework.samples.petclinic.comment;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.util.Pair;
import org.springframework.samples.petclinic.enums.InvitationType;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.game.GameRepository;
import org.springframework.samples.petclinic.invitation.exceptions.DuplicatedInvitationException;
import org.springframework.samples.petclinic.invitation.exceptions.NullInvitationTypeException;
import org.springframework.samples.petclinic.invitation.exceptions.NullRecipientException;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.playerInfo.PlayerInfo;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CommentServiceTest {

    @Mock
    CommentRepository commentRepository;

    @Mock
    GameRepository gameRepository;

    PlayerInfo pI1;
    Game game;

    private Comment createComment(String message) {
        Comment comment = new Comment();
        comment.setMessage(message);
        return comment;
    }

    @BeforeEach
    public void config() {
        pI1 = new PlayerInfo();
        game = new Game();

        Comment comment = createComment("Hi, this is a test comment");
        comment.setDate(Date.from(Instant.now()));
        comment.setPlayerInfo(pI1);

        List<Comment> comments = new ArrayList<>();
        comments.add(comment);
        List<Comment> noComments = new ArrayList<>();

        when(commentRepository.findCommentsByGame(anyInt()))
        .thenReturn(comments).thenReturn(noComments);
    }

    @Test
    public void testSaveComment() {
        Comment comment = createComment("Hi, this is a test comment");
        CommentService service = new CommentService(commentRepository);
        try {
            service.saveComment(comment, new PlayerInfo());
        } catch (Exception e) {
            fail("no exception should be thrown");
        }
    }

    @Test
    public void testGetCommentsByGame(){
        CommentService service = new CommentService(commentRepository);
        Game game = new Game();
        List<Comment> comments = service.getCommentsByGame(game.getId());
        assertNotNull(comments);
        assertTrue(comments.isEmpty());
    }
}
