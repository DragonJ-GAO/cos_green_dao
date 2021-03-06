package com.sea_monster.dao.query;

import android.database.Cursor;

import java.util.List;

import com.sea_monster.dao.AbstractDeepDao;
import com.sea_monster.dao.DaoException;
import com.sea_monster.dao.IQueryDaoAccess;



/**
 * Created by dragonj on 2/20/14.
 */
public class DeepQuery<T> extends AbstractQuery<T>{
    private final static class QueryData<T2> extends AbstractQueryData<T2, DeepQuery<T2>> {
        private final int limitPosition;
        private final int offsetPosition;

        QueryData(AbstractDeepDao<T2, ?> dao, String sql, String[] initialValues, int limitPosition, int offsetPosition) {
            super(dao,sql,initialValues);
            this.limitPosition = limitPosition;
            this.offsetPosition = offsetPosition;
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
        protected DeepQuery<T2> createQuery() {
            return new DeepQuery<T2>(this, (AbstractDeepDao)dao, sql, initialValues.clone(), limitPosition, offsetPosition);
        }

    }

    /** For internal use by greenDAO only. */
    public static <T2> DeepQuery<T2> internalCreate(AbstractDeepDao<T2, ?> dao, String sql, Object[] initialValues) {
        return create(dao, sql, initialValues, -1, -1);
    }

    static <T2> DeepQuery<T2> create(AbstractDeepDao<T2, ?> dao, String sql, Object[] initialValues, int limitPosition,
                                 int offsetPosition) {
        QueryData<T2> queryData = new QueryData<T2>(dao, sql, toStringArray(initialValues), limitPosition,
                offsetPosition);
        return queryData.forCurrentThread();
    }

    private final int limitPosition;
    private final int offsetPosition;
    private final QueryData<T> queryData;

    private DeepQuery(QueryData<T> queryData, AbstractDeepDao<T, ?> dao, String sql, String[] initialValues, int limitPosition,
                  int offsetPosition) {
        super(dao, new IQueryDaoAccess.InternalQueryDeepDaoAccess<T>(dao), sql, initialValues);
        this.queryData = queryData;
        this.limitPosition = limitPosition;
        this.offsetPosition = offsetPosition;
    }

    public DeepQuery<T> forCurrentThread() {
        return queryData.forCurrentThread(this);
    }

    /**
     * Sets the parameter (0 based) using the position in which it was added during building the query.
     */
    public void setParameter(int index, Object parameter) {
        if (index >= 0 && (index == limitPosition || index == offsetPosition)) {
            throw new IllegalArgumentException("Illegal parameter index: " + index);
        }
        super.setParameter(index, parameter);
    }

    /**
     * Sets the limit of the maximum number of results returned by this Query. {@link QueryBuilder#limit(int)} must have
     * been called on the QueryBuilder that created this Query object.
     */
    public void setLimit(int limit) {
        checkThread();
        if (limitPosition == -1) {
            throw new IllegalStateException("Limit must be set with QueryBuilder before it can be used here");
        }
        parameters[limitPosition] = Integer.toString(limit);
    }

    /**
     * Sets the offset for results returned by this Query. {@link QueryBuilder#offset(int)} must have been called on the
     * QueryBuilder that created this Query object.
     */
    public void setOffset(int offset) {
        checkThread();
        if (offsetPosition == -1) {
            throw new IllegalStateException("Offset must be set with QueryBuilder before it can be used here");
        }
        parameters[offsetPosition] = Integer.toString(offset);
    }

    /** Executes the query and returns the result as a list containing all entities loaded into memory. */
    public List<T> list() {
        checkThread();
        Cursor cursor = dao.getDatabase().rawQuery(sql, parameters);
        return daoAccess.loadAllAndCloseCursor(cursor);
    }

    /**
     * Executes the query and returns the result as a list that lazy loads the entities on first access. Entities are
     * cached, so accessing the same entity more than once will not result in loading an entity from the underlying
     * cursor again.Make sure to close it to close the underlying cursor.
     */
    public LazyList<T> listLazy() {
        checkThread();
        Cursor cursor = dao.getDatabase().rawQuery(sql, parameters);
        return new LazyList<T>(daoAccess, cursor, true);
    }

    public Cursor getCursor(){
        return dao.getDatabase().rawQuery(sql, parameters);
    }


    /**
     * Executes the query and returns the result as a list that lazy loads the entities on every access (uncached). Make
     * sure to close the list to close the underlying cursor.
     */
    public LazyList<T> listLazyUncached() {
        checkThread();
        Cursor cursor = dao.getDatabase().rawQuery(sql, parameters);
        return new LazyList<T>(daoAccess, cursor, false);
    }

    /**
     * Executes the query and returns the result as a list iterator; make sure to close it to close the underlying
     * cursor. The cursor is closed once the iterator is fully iterated through.
     */
    public CloseableListIterator<T> listIterator() {
        return listLazyUncached().listIteratorAutoClose();
    }

    /**
     * Executes the query and returns the unique result or null.
     *
     * @throws com.sea_monster.dao.DaoException
     *             if the result is not unique
     * @return Entity or null if no matching entity was found
     */
    public T unique() {
        checkThread();
        Cursor cursor = dao.getDatabase().rawQuery(sql, parameters);
        return daoAccess.loadUniqueAndCloseCursor(cursor);
    }

    /**
     * Executes the query and returns the unique result (never null).
     *
     * @throws com.sea_monster.dao.DaoException
     *             if the result is not unique or no entity was found
     * @return Entity
     */
    public T uniqueOrThrow() {
        T entity = unique();
        if (entity == null) {
            throw new DaoException("No entity found for query");
        }
        return entity;
    }

}
