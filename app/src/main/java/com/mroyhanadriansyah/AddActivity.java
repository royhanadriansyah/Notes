package com.mroyhanadriansyah;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.mroyhanadriansyah.adapters.DBHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddActivity extends AppCompatActivity {

    DBHelper dbHelper;
    Button BtnProses;
    EditText TxID, TxJudul, TXDeskripsi, Txtgl;
    long id;
    DatePickerDialog datePickerDialog;
    SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        dbHelper = new DBHelper(this);

        id = getIntent().getLongExtra(DBHelper.row_id, 0);

        TxID = (EditText)findViewById(R.id.txID);
        TxJudul = (EditText)findViewById(R.id.txNamaAnggota);
        TXDeskripsi = (EditText)findViewById(R.id.txJudul);
        Txtgl = (EditText)findViewById(R.id.txTanggal);
        BtnProses = (Button)findViewById(R.id.btnProses);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        getData();

        ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setDisplayHomeAsUpEnabled(true);
    }

    private void showDateDialog() {
        Calendar calendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void getData() {
        Calendar c1 = Calendar.getInstance();
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        String tglCatatan = sdf1.format(c1.getTime());
        Txtgl.setText(tglCatatan);

        Cursor cur = dbHelper.oneData(id);
        if(cur.moveToFirst()){
            String idCatatan = cur.getString(cur.getColumnIndex(DBHelper.row_id));
            String judul = cur.getString(cur.getColumnIndex(DBHelper.row_judul));
            String deskripsi = cur.getString(cur.getColumnIndex(DBHelper.row_deskripsi));
            String tgl = cur.getString(cur.getColumnIndex(DBHelper.row_tgl));

            TxID.setText(idCatatan);
            TxJudul.setText(judul);
            TXDeskripsi.setText(deskripsi);
            Txtgl.setText(tgl);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        String idcatatan = TxID.getText().toString().trim();

        MenuItem itemDelete = menu.findItem(R.id.action_delete);
        MenuItem itemClear = menu.findItem(R.id.action_clear);
        MenuItem itemSave = menu.findItem(R.id.action_save);

        if (idcatatan.equals("")){
            itemDelete.setVisible(false);
            itemClear.setVisible(true);
        }else {
            itemDelete.setVisible(true);
            itemClear.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                insertAndUpdate();
        }
        switch (item.getItemId()){
            case R.id.action_clear:
                TxJudul.setText("");
                TXDeskripsi.setText("");
        }
        switch (item.getItemId()){
            case R.id.action_delete:
                final AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
                builder.setMessage("Catatan akan dihapus permanent!");
                builder.setCancelable(true);
                builder.setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.deleteData(id);
                        Toast.makeText(AddActivity.this, "Terhapus", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void insertAndUpdate(){
        String idCatatan = TxID.getText().toString().trim();
        String nama = TxJudul.getText().toString().trim();
        String judul = TXDeskripsi.getText().toString().trim();
        String tanggal = Txtgl.getText().toString().trim();

        ContentValues values = new ContentValues();

        values.put(DBHelper.row_judul, nama);
        values.put(DBHelper.row_deskripsi, judul);

        if (nama.equals("") || judul.equals("")){
            Toast.makeText(AddActivity.this, "Harap lengkapi catatan terlebih dahulu.", Toast.LENGTH_SHORT).show();
        }else {
            if(idCatatan.equals("")){
                values.put(DBHelper.row_tgl, tanggal);
                dbHelper.insertData(values);
            }else {
                dbHelper.updateData(values, id);
            }

            Toast.makeText(AddActivity.this, "Catatan Berhasil Tersimpan!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
