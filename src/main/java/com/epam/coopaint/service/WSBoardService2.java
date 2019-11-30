package com.epam.coopaint.service;

import com.epam.coopaint.dao.impl.SQLBoardDAOImpl;
import com.epam.coopaint.domain.Board;
import com.epam.coopaint.domain.VShape;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WSBoardService2 extends WSService<Board, VShape> {
    public WSBoardService2() {
        roomDaoSupplier = SQLBoardDAOImpl::new;
        roomSupplier = Board::new;
    }
}
