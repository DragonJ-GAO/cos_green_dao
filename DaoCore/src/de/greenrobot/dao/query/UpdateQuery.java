package de.greenrobot.dao.query;

import android.database.sqlite.SQLiteDatabase;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.IQueryDaoAccess;

/**
 * Created by dragonj on 2/19/14.
 */
public class UpdateQuery<T> extends AbstractQuery<T> {

    private final static class QueryData<T2> extends AbstractQueryData<T2, UpdateQuery<T2>> {

        private QueryData(AbstractDao<T2, ?> dao, String sql, String[] initialValues) {
            super(dao, sql, initialValues);
        }

        @Override
        protected UpdateQuery<T2> createQuery() {
            return new UpdateQuery<T2>(this, dao, sql, initialValues.clone());
        }
    }

    static <T2> UpdateQuery<T2> create(AbstractDao<T2, ?> dao, String sql, Object[] initialValues) {
        QueryData<T2> queryData = new QueryData<T2>(dao, sql, toStringArray(initialValues));
        return queryData.forCurrentThread();
    }

    private final QueryData<T> queryData;

    private UpdateQuery(QueryData<T> queryData, AbstractDao<T, ?> dao, String sql, String[] initialValues) {
        super(dao, new IQueryDaoAccess.InternalQueryDaoAccess<T>(dao), sql, initialValues);
        this.queryData = queryData;
    }

    public UpdateQuery<T> forCurrentThread() {
        return queryData.forCurrentThread(this);
    }

    /** Returns the count (number of results matching the query). Uses SELECT COUNT (*) sematics. */
    public void update() {
        checkThread();
        SQLiteDatabase db = dao.getDatabase();
        if (db.isDbLockedByCurrentThread()) {
            dao.getDatabase().execSQL(sql, parameters);
        } else {
            // Do TX to acquire a connection before locking this to avoid deadlocks
            // Locking order as described in AbstractDao
            db.beginTransaction();
            try {
                dao.getDatabase().execSQL(sql, parameters);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }


}
