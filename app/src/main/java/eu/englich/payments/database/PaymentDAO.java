package eu.englich.payments.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;
import java.util.List;

import eu.englich.payments.database.model.Payment;

/**
 * Created by Christoph Englich on 19.03.17.
 */

public class PaymentDAO {

    public static final String TBL = "payments";
    public static final String TBL_ID = "_id";
    public static final String TBL_TSTMP = "tstmp";
    public static final String TBL_AMOUNT = "amount";
    public static final String TBL_CATEGORY = "category";

    public static final String CREATE_TBL = "CREATE TABLE " + TBL + " ("
            + TBL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TBL_TSTMP + " INTEGER NOT NULL, "
            + TBL_AMOUNT + " REAL NOT NULL, "
            + TBL_CATEGORY + " TEXT)";

    private static PaymentDAO instance;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public static PaymentDAO getInstance(Context context) {
        if (instance == null) {
            instance = new PaymentDAO(context);
        }
        return instance;
    }

    private PaymentDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TBL);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Payment getPayment(long id) {
        open();
        Cursor cursor = db.query(TBL, //Table
                null, //null returns all columns / fields
                TBL_ID + "=?", //Selection (WHERE [field]=?)
                new String[]{String.valueOf(id)}, //Selection arguments (selection by id)
                null, //GroupBy (GROUPY BY [field], e. g. in case of sum([field]))
                null, //Having, Selection on Group By fields (HAVING [field]=1)
                null, //Limit, limits the selection, e. g. 10 for 10 entries
                null); //CancelationSignal
        if (cursor.moveToFirst()) { //if data is available
            return readFromCursor(cursor); //read the data
        }
        cursor.close();
        close();
        return null;
    }

    public List<Payment> getAllPaymentsAfter(long timestamp) {
        open();
        Cursor cursor = db.query(TBL, //Table
                new String[] {TBL_ID, TBL_CATEGORY, TBL_AMOUNT, TBL_TSTMP}, //Fields, null would also return all columns / fields
                TBL_TSTMP + ">=" + timestamp, //Selection, can't do >= with selection arguments
                null, //Selection arguments (replaces ? in Selection)
                null, //GroupBy (GROUPY BY [field], e. g. in case of sum([field]))
                null, //Having, Selection on Group By fields (HAVING [field]=1)
                null, //Limit, limits the selection, e. g. 10 for 10 entries
                TBL_TSTMP + " ASC"); //Order by timestamp, ascending
        List<Payment> payments = new LinkedList<>();
        if (cursor.moveToFirst()) { // read in the the result row by row, if data available
            while (!cursor.isAfterLast()) {
                payments.add(readFromCursor(cursor));
                cursor.moveToNext();
            }
        }
        cursor.close();
        close();
        return payments;
    }

    public List<Payment> getAllPayments() {
        open();

        Cursor cursor = db.query(TBL, //Table
                new String[] {TBL_ID, TBL_CATEGORY, TBL_AMOUNT, TBL_TSTMP}, //Fields, null would also return all columns / fields
                null, //Selection (WHERE [field]=?)
                null, //Selection arguments (replaces ? in Selection)
                null, //GroupBy (GROUPY BY [field], e. g. in case of sum([field]))
                null, //Having, Selection on Group By fields (HAVING [field]=1)
                null, //Limit, limits the selection, e. g. 10 for 10 entries
                null); //CancelationSignal

        //example custom select query
        //Cursor cursor = db.rawQuery("SELECT * FROM " + TBL + " ORDER BY " + TBL_TSTMP + " DESC",  null);

        List<Payment> payments = new LinkedList<>();
        if (cursor.moveToFirst()) { // read in the the result row by row, if data available
            while (!cursor.isAfterLast()) {
                payments.add(readFromCursor(cursor));
                cursor.moveToNext();
            }
        }
        cursor.close();
        close();
        return payments;
    }

    public long addPayment(Payment payment) {
        open();
        long ret = db.insert(TBL, null, prepareValues(payment));
        if (ret > 0) {
            payment.setId(ret);
        }
        close();
        return ret;
    }

    public int updatePayment(Payment payment) {
        open();
        int ret = db.update(TBL, //Table
                prepareValues(payment), //Values
                TBL_ID + "=?", //Selection (what data to update)
                new String[]{String.valueOf(payment.getId())}); // selection by id
        close();
        return ret;
    }

    public int deletePayment(Payment payment) {
        open();
        int ret = db.delete(TBL,
                TBL_ID + "=?", //Selection (what data to delete)
                new String[]{String.valueOf(payment.getId())}); // selection by id
        close();
        return ret;
    }

    private ContentValues prepareValues(Payment payment) {
        ContentValues contentValues = new ContentValues();

        if (payment.getId() > 0)
            contentValues.put(TBL_ID, payment.getId());

        contentValues.put(TBL_AMOUNT, payment.getAmount());
        contentValues.put(TBL_TSTMP, payment.getTime());
        contentValues.put(TBL_CATEGORY, payment.getCategory());

        return contentValues;
    }

    private Payment readFromCursor(Cursor cursor) {
        Payment payment = new Payment();

        int index = cursor.getColumnIndex(TBL_ID);
        payment.setId(cursor.getLong(index));

        index = cursor.getColumnIndex(TBL_TSTMP);
        payment.setTime(cursor.getLong(index));

        index = cursor.getColumnIndex(TBL_AMOUNT);
        payment.setAmount(cursor.getDouble(index));

        index = cursor.getColumnIndex(TBL_CATEGORY);
        payment.setCategory(cursor.getString(index));

        return payment;
    }

}
