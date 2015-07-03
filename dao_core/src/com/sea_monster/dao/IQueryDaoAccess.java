package com.sea_monster.dao;

import android.database.Cursor;

import java.util.List;

import com.sea_monster.dao.internal.TableStatements;

/**
 * For internal use by greenDAO only.
 */

public interface IQueryDaoAccess<T> {
    public T loadCurrent(Cursor cursor, int offset, boolean lock);

    public List<T> loadAllAndCloseCursor(Cursor cursor);

    public T loadUniqueAndCloseCursor(Cursor cursor);

    public final static class InternalQueryDaoAccess<T> implements IQueryDaoAccess<T> {
        private final AbstractDao<T, ?> dao;

        public InternalQueryDaoAccess(AbstractDao<T, ?> abstractDao) {
            dao = abstractDao;
        }

        public T loadCurrent(Cursor cursor, int offset, boolean lock) {
            return dao.loadCurrent(cursor, offset, lock);
        }

        public List<T> loadAllAndCloseCursor(Cursor cursor) {
            return dao.loadAllAndCloseCursor(cursor);
        }

        public T loadUniqueAndCloseCursor(Cursor cursor) {
            return dao.loadUniqueAndCloseCursor(cursor);
        }

        public TableStatements getStatements() {
            return dao.getStatements();
        }

        public static <T2> TableStatements getStatements(AbstractDao<T2, ?> dao) {
            return dao.getStatements();
        }
    }

    public final static class InternalQueryDeepDaoAccess<T> implements IQueryDaoAccess<T> {
        private final AbstractDeepDao<T, ?> dao;

        public InternalQueryDeepDaoAccess(AbstractDeepDao<T, ?> abstractDao) {
            dao = abstractDao;
        }

        public T loadCurrent(Cursor cursor, int offset, boolean lock) {
            return dao.loadDeepCurrent(cursor, offset, lock);
        }

        public List<T> loadAllAndCloseCursor(Cursor cursor) {
            return dao.loadDeepAllAndCloseCursor(cursor);
        }

        public T loadUniqueAndCloseCursor(Cursor cursor) {
            return dao.loadDeepUniqueAndCloseCursor(cursor);
        }

    }

}


