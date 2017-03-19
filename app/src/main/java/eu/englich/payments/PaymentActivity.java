package eu.englich.payments;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import eu.englich.payments.database.PaymentDAO;
import eu.englich.payments.database.model.Payment;

public class PaymentActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        findViewById(R.id.paymentCancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.paymentSaveButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String cat = ((TextView) findViewById(R.id.paymentInputCategory)).getText().toString();
                    String amount = ((TextView) findViewById(R.id.paymentInputAmount)).getText().toString();
                    double paid;
                    if (amount.length() > 0) {
                        try {
                            paid = Double.parseDouble(amount);
                            Payment payment = new Payment();
                            payment.setAmount(paid);
                            payment.setCategory(cat);
                            payment.setTime(System.currentTimeMillis());
                            PaymentDAO.getInstance(PaymentActivity.this).addPayment(payment);
                        } catch (NumberFormatException e) {
                            Toast.makeText(PaymentActivity.this, R.string.amount_missing, Toast.LENGTH_LONG).show();
                        }
                    }
                    finish();
                }
            }
        );
    }

}
