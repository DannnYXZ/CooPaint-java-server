package com.epam.coopaint.service.impl;

import com.epam.coopaint.domain.Board;
import com.epam.coopaint.domain.Pair;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.domain.VShape;
import com.epam.coopaint.exception.ServiceException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.websocket.Session;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class WSBoardServiceImplTest {
    WSBoardServiceImpl boardService;
    User guest;

    @BeforeTest
    public void setUp() {
        guest = new User().setUuid(UUID.randomUUID()).setAuth(false);
    }

    @BeforeMethod
    public void doBeforeMethod() {
        boardService = new WSBoardServiceImpl();
    }

    @Test(expectedExceptions = ServiceException.class)
    public void testReadNonexistentBoardException() throws ServiceException {
        boardService.readRoom(UUID.randomUUID());
    }

    @Test
    public void testGuestAllocatesNewBoard() throws ServiceException {
        Pair<UUID, Set<Session>> pair = boardService.connectTo(guest, "", new SessionMock());
        UUID allocatedBoardUUID = pair.getElement0();
        Board allocatedBoard = boardService.readRoom(allocatedBoardUUID);
        Assert.assertNotNull(allocatedBoard);
    }

    @Test
    public void testGuestAddsShapesToBoard() throws ServiceException {
        Pair<UUID, Set<Session>> pair = boardService.connectTo(guest, "", new SessionMock());
        UUID allocatedBoardUUID = pair.getElement0();
        List<VShape> newShapes = List.of(
                new VShape().setParams(List.of(1f, 2f, 3f)),
                new VShape().setParams(List.of(3f, 4f, 5f)));
        boardService.addElements(allocatedBoardUUID, newShapes);
        Board board = boardService.readRoom(allocatedBoardUUID);
        Assert.assertEquals(newShapes, board.getElements());
    }

    @Test
    public void testDisconnectedUsersDoNotReceiveUpdates() throws ServiceException {
        var guestSession = new SessionMock();
        Pair<UUID, Set<Session>> pair1 = boardService.connectTo(guest, "", guestSession);
        UUID allocatedBoardUUID = pair1.getElement0();
        boardService.removeSession(guestSession);
        Pair<List<VShape>, Set<Session>> pair2 = boardService.addElements(allocatedBoardUUID, List.of(new VShape()));
        Set<Session> receivers = pair2.getElement1();
        Assert.assertFalse(receivers.contains(guestSession));
    }
}
