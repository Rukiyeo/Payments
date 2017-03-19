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
        dbHelper = new DatabaseHelper(context, null);
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
        Cursor cursor = db.rawQuery("SELECT * FROM " + TBL + " WHERE id=?",  new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            return readFromCursor(cursor);
        }
        cursor.close();
        close();
        return null;
    }

    public List<Payment> getAllPayments() {
        open();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TBL + " ORDER BY " + TBL_TSTMP + " DESC",  null);

        List<Payment> payments = new LinkedList<>();
        if (cursor.moveToFirst()) {
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
        payment.setAmount(cursor.getInt(index));

        index = cursor.getColumnIndex(TBL_CATEGORY);
        payment.setCategory(cursor.getString(index));

        return payment;
    }

}
