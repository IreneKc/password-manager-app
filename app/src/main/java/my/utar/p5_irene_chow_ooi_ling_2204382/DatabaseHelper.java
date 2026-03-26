package my.utar.p5_irene_chow_ooi_ling_2204382;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "password_manager.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_PASSWORDS = "passwords";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_SITE_NAME = "site_name";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PIN_NUMBER = "pin_number";
    private static final String COLUMN_SECURITY_QUESTION = "security_question";
    private static final String COLUMN_SECURITY_ANSWER = "security_answer";
    private static final String COLUMN_NOTES = "notes";
    private static final String COLUMN_IS_DELETED = "is_deleted";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_PASSWORDS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SITE_NAME + " TEXT, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_PIN_NUMBER + " TEXT, " +
                COLUMN_SECURITY_QUESTION + " TEXT, " +
                COLUMN_SECURITY_ANSWER + " TEXT, " +
                COLUMN_NOTES + " TEXT, " +
                COLUMN_IS_DELETED + " INTEGER DEFAULT 0)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PASSWORDS);
        onCreate(db);
    }

    public boolean addPassword(PasswordModel passwordModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_SITE_NAME, passwordModel.getSiteName());
        cv.put(COLUMN_USERNAME, passwordModel.getUsername());
        cv.put(COLUMN_PASSWORD, encrypt(passwordModel.getPassword()));
        cv.put(COLUMN_PIN_NUMBER, encrypt(safeText(passwordModel.getPinNumber())));
        cv.put(COLUMN_SECURITY_QUESTION, safeText(passwordModel.getSecurityQuestion()));
        cv.put(COLUMN_SECURITY_ANSWER, encrypt(safeText(passwordModel.getSecurityAnswer())));
        cv.put(COLUMN_NOTES, safeText(passwordModel.getNotes()));
        cv.put(COLUMN_IS_DELETED, 0);

        long insert = db.insert(TABLE_PASSWORDS, null, cv);
        db.close();
        return insert != -1;
    }

    public ArrayList<PasswordModel> getAllPasswords() {
        ArrayList<PasswordModel> passwordList = new ArrayList<>();
        String queryString = "SELECT * FROM " + TABLE_PASSWORDS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                passwordList.add(buildPasswordModelFromCursor(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return passwordList;
    }

    public ArrayList<PasswordModel> getActivePasswords() {
        ArrayList<PasswordModel> passwordList = new ArrayList<>();
        String queryString = "SELECT * FROM " + TABLE_PASSWORDS +
                " WHERE " + COLUMN_IS_DELETED + " = 0 ORDER BY " + COLUMN_ID + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                passwordList.add(buildPasswordModelFromCursor(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return passwordList;
    }

    public ArrayList<PasswordModel> getDeletedPasswords() {
        ArrayList<PasswordModel> passwordList = new ArrayList<>();
        String queryString = "SELECT * FROM " + TABLE_PASSWORDS +
                " WHERE " + COLUMN_IS_DELETED + " = 1 ORDER BY " + COLUMN_ID + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                passwordList.add(buildPasswordModelFromCursor(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return passwordList;
    }

    public boolean updatePassword(PasswordModel passwordModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_SITE_NAME, passwordModel.getSiteName());
        cv.put(COLUMN_USERNAME, passwordModel.getUsername());
        cv.put(COLUMN_PASSWORD, encrypt(passwordModel.getPassword()));
        cv.put(COLUMN_PIN_NUMBER, encrypt(safeText(passwordModel.getPinNumber())));
        cv.put(COLUMN_SECURITY_QUESTION, safeText(passwordModel.getSecurityQuestion()));
        cv.put(COLUMN_SECURITY_ANSWER, encrypt(safeText(passwordModel.getSecurityAnswer())));
        cv.put(COLUMN_NOTES, safeText(passwordModel.getNotes()));

        int result = db.update(
                TABLE_PASSWORDS,
                cv,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(passwordModel.getId())}
        );

        db.close();
        return result > 0;
    }

    public boolean moveToRecentlyDeleted(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_IS_DELETED, 1);

        int result = db.update(
                TABLE_PASSWORDS,
                cv,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}
        );

        db.close();
        return result > 0;
    }

    public boolean restorePassword(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_IS_DELETED, 0);

        int result = db.update(
                TABLE_PASSWORDS,
                cv,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}
        );

        db.close();
        return result > 0;
    }

    public boolean permanentlyDeletePassword(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                TABLE_PASSWORDS,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}
        );

        db.close();
        return result > 0;
    }

    public boolean isDatabaseEmpty() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_PASSWORDS, null);

        boolean isEmpty = true;
        if (cursor.moveToFirst()) {
            isEmpty = cursor.getInt(0) == 0;
        }

        cursor.close();
        db.close();
        return isEmpty;
    }

    private PasswordModel buildPasswordModelFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
        String siteName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SITE_NAME));
        String username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
        String password = decrypt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));

        String encryptedPin = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PIN_NUMBER));
        String pinNumber = decryptSafe(encryptedPin);

        String securityQuestion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SECURITY_QUESTION));

        String encryptedAnswer = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SECURITY_ANSWER));
        String securityAnswer = decryptSafe(encryptedAnswer);

        String notes = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES));

        return new PasswordModel(
                id,
                siteName,
                username,
                password,
                pinNumber,
                securityQuestion,
                securityAnswer,
                notes
        );
    }

    private String safeText(String text) {
        return text == null ? "" : text;
    }

    private String encrypt(String text) {
        return Base64.encodeToString(text.getBytes(), Base64.DEFAULT);
    }

    private String decrypt(String text) {
        return new String(Base64.decode(text, Base64.DEFAULT));
    }

    private String decryptSafe(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        return decrypt(text);
    }
}