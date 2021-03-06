package com.sea_monster.dao.query;

import android.database.Cursor;

import com.sea_monster.dao.AbstractDao;
import com.sea_monster.dao.DaoException;
import com.sea_monster.dao.IQueryDaoAccess;

/**
 * Created by dragonj on 2/24/14.
 */
public class FuncQuery<T> extends AbstractQuery<T>  {
    private final static class QueryData<T2> extends AbstractQueryData<T2, FuncQuery<T2>> {

    private QueryData(AbstractDao<T2, ?> dao, String sql, String[] initialValues) {
        super(dao, sql, initialValues);
    }

    @Override
    protected FuncQuery<T2> createQuery() {
        return new FuncQuery<T2>(this, dao, sql, initialValues.clone());
    }
}

    static <T2> FuncQuery<T2> create(AbstractDao<T2, ?> dao, String sql, Object[] initialValues) {
        QueryData<T2> queryData = new QueryData<T2>(dao, sql, toStringArray(initialValues));
        return queryData.forCurrentThread();
    }

    private final QueryData<T> queryData;

    private FuncQuery(QueryData<T> queryData, AbstractDao<T, ?> dao, String sql, String[] initialValues) {
        super(dao, new IQueryDaoAccess.InternalQueryDaoAccess<T>(dao), sql, initialValues);
        this.queryData = queryData;
    }

    public FuncQuery<T> forCurrentThread() {
        return queryData.forCurrentThread(this);
    }

    /** Returns the count (number of results matching the query). Uses SELECT COUNT (*) sematics. */
    public long execute() {
        checkThread();
        Cursor cursor = dao.getDatabase().rawQuery(sql, parameters);
        try {
            if (!cursor.moveToNext()) {
                throw new DaoException("No result for func");
            } else if (!cursor.isLast()) {
                throw new DaoException("Unexpected row func: " + cursor.getCount());
            } else if (cursor.getColumnCount() != 1) {
                throw new DaoException("Unexpected column func: " + cursor.getColumnCount());
            }
            return cursor.getLong(0);
        } finally {
            cursor.close();
        }
    }

}
