package eu.englich.payments;

import android.app.Fragment;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import database.PaymentDAO;
import model.Payment;

/**
 * Created by Christoph Englich on 19.03.17.
 */

public class MainFragment extends Fragment {

    private ListView listView;
    private PaymentAdapter listAdapter;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = (ListView) view.findViewById(R.id.mainPaymentsList);
        listAdapter = new PaymentAdapter();
        listView.setAdapter(listAdapter);

    }

    private class PaymentAdapter extends BaseAdapter {

        private List<Payment> payments;

        private PaymentAdapter() {
            payments = PaymentDAO.getInstance(getContext()).getAllPayments();
        }

        @Override
        public int getCount() {
            return payments.size();
        }

        @Override
        public Payment getItem(int position) {
            return payments.get(position);
        }

        @Override
        public long getItemId(int position) {
            return payments.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.row_payment, parent);

            TextView category = (TextView) convertView.findViewById(R.id.rowPaymentCategory);
            TextView time = (TextView) convertView.findViewById(R.id.rowPaymentTime);
            TextView amount = (TextView) convertView.findViewById(R.id.rowPaymentAmount);

            Payment payment = getItem(position);

            category.setText(payment.getCategory());
            time.setText(formatDate(payment.getTime()));
            amount.setText(String.valueOf(payment.getAmount()));

            return convertView;
        }

        private String formatDate(long timestamp) {
            return dateFormat.format(new Date(timestamp));
        }
    }

}
