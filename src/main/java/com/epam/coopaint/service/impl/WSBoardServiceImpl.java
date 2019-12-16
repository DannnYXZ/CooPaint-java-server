package com.epam.coopaint.service.impl;

import com.epam.coopaint.dao.impl.SQLBoardDAOImpl;
import com.epam.coopaint.domain.Board;
import com.epam.coopaint.domain.VShape;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public
class WSBoardServiceImpl extends WSServiceImpl<Board, VShape> {
    public WSBoardServiceImpl() {
        roomDaoSupplier = SQLBoardDAOImpl::new;
        roomSupplier = Board::new;
    }
}
