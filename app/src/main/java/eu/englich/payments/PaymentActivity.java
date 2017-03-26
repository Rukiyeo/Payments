package eu.englich.payments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //do nothing, stay on page
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
                builder.setMessage(R.string.confirm_question);
                builder.setPositiveButton(R.string.yes, dialogClickListener);
                builder.setNegativeButton(R.string.no, dialogClickListener);
                builder.show();
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
                            Toast.makeText(PaymentActivity.this, R.string.save_payment_message, Toast.LENGTH_SHORT).show();
                            finish();
                        } catch (NumberFormatException e) {
                            Toast.makeText(PaymentActivity.this, R.string.amount_missing, Toast.LENGTH_LONG).show();
                        }
                    } else {

                        Toast.makeText(PaymentActivity.this,
                                R.string.amount_missing, Toast.LENGTH_LONG).show();

                    }
                }
            }
        );
    }

}
