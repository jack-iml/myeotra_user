package com.myeotra.user.ui.activity.payment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appoets.braintreepayment.BrainTreePaymentActivity;
import com.appoets.paytmpayment.PaytmObject;
import com.google.gson.Gson;
import com.myeotra.user.R;
import com.myeotra.user.base.BaseActivity;
import com.myeotra.user.data.SharedHelper;
import com.myeotra.user.data.network.model.BrainTreeResponse;
import com.myeotra.user.data.network.model.Card;
import com.myeotra.user.data.network.model.CheckSumData;
import com.tranxit.stripepayment.activity.add_card.StripeAddCardActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

import static com.myeotra.user.MvpApplication.RIDE_REQUEST;
import static com.myeotra.user.MvpApplication.isBraintree;
import static com.myeotra.user.MvpApplication.isCard;
import static com.myeotra.user.MvpApplication.isCash;
import static com.myeotra.user.MvpApplication.isPaytm;
import static com.myeotra.user.MvpApplication.isPayumoney;
import static com.myeotra.user.common.Constants.PaymentMode.CASH;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.CARD_ID;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.CARD_LAST_FOUR;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.PAYMENT_MODE;
import static com.myeotra.user.ui.fragment.invoice.InvoiceFragment.isInvoiceCashToCard;

public class PaymentActivity extends BaseActivity implements PaymentIView {

    public static final int PICK_PAYMENT_METHOD = 12;
    private static final int STRIPE_PAYMENT_REQUEST_CODE = 100;
    private static final int BRAINTREE_PAYMENT_REQUEST_CODE = 101;
    private static final int PAYTM_PAYMENT_REQUEST_CODE = 102;
    private static final int PAYUMONEY_PAYMENT_REQUEST_CODE = 103;
    private static final String TAG = "PaymentActivity";
    @BindView(R.id.add_card)
    TextView addCard;
    @BindView(R.id.llcash)
    LinearLayout llcash;
    @BindView(R.id.tvCash)
    TextView tvCash;
    @BindView(R.id.cards_rv)
    RecyclerView cardsRv;
    @BindView(R.id.llCardContainer)
    LinearLayout llCardContainer;
    @BindView(R.id.llCashContainer)
    LinearLayout llCashContainer;
    @BindView(R.id.braintree)
    TextView braintree;
    @BindView(R.id.payumoney)
    TextView payumoney;
    @BindView(R.id.paytm)
    TextView paytm;
    @BindView(R.id.menu)
    ImageView menu;
    private List<Card> cardsList = new ArrayList<>();
    private PaymentPresenter<PaymentActivity> presenter = new PaymentPresenter<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_payment;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);
        setTitle(getString(R.string.payment));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            boolean hideCash = extras.getBoolean("hideCash", false);
            llcash.setVisibility(hideCash ? View.GONE : View.VISIBLE);
        }

        payumoney.setVisibility(isPayumoney ? View.VISIBLE : View.GONE);
        paytm.setVisibility(isPaytm ? View.VISIBLE : View.GONE);
        braintree.setVisibility(isBraintree ? View.VISIBLE : View.GONE);

        getCardsDetails();

    }

    private void getCardsDetails() {
        showLoading();
        new Handler().postDelayed(() -> {
            if (isCard) {
                cardsRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                cardsRv.setItemAnimator(new DefaultItemAnimator());
                presenter.card();
                llCardContainer.setVisibility(View.VISIBLE);
            } else {
                try {
                    hideLoading();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                llCardContainer.setVisibility(View.GONE);
            }

            if (isCash && !isInvoiceCashToCard)
                llCashContainer.setVisibility(View.VISIBLE);
            else llCashContainer.setVisibility(View.GONE);
        }, 3000);
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }

    @OnClick({R.id.add_card, R.id.llcash, R.id.braintree, R.id.paytm, R.id.payumoney, R.id.menu})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_card:
                Intent intent = new Intent(this, StripeAddCardActivity.class);
                intent.putExtra("stripe_token", SharedHelper.getKey(this, "stripe_publishable_key"));
                startActivityForResult(intent, STRIPE_PAYMENT_REQUEST_CODE);
                break;
            case R.id.llcash:
                finishResult(CASH);
                break;
            case R.id.braintree:
//                finishResult(BRAINTREE);
                Toast.makeText(this, getString(R.string.under_dev), Toast.LENGTH_SHORT).show();
                break;
            case R.id.payumoney:
//                finishResult(PAYUMONEY);
                Toast.makeText(this, getString(R.string.under_dev), Toast.LENGTH_SHORT).show();
                break;
            case R.id.paytm:
//                finishResult(PAYTM);
                Toast.makeText(this, getString(R.string.under_dev), Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu:
                onBackPressed();
                break;
        }
    }

    public void deleteCard(@NonNull Card card) {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.are_sure_you_want_to_delete))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(getString(R.string.delete), (dialog, whichButton) -> presenter.deleteCard(card.getCardId()))
                .setNegativeButton(getString(R.string.no), null).show();
    }

    public void finishResult(String mode) {
        Intent intent = new Intent();
        RIDE_REQUEST.put(PAYMENT_MODE, mode);
        intent.putExtra("payment_mode", mode);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onSuccess(Object card) {
        Toast.makeText(baseActivity(), R.string.card_deleted_successfully, Toast.LENGTH_SHORT).show();
        presenter.card();
    }

    @Override
    public void onSuccess(BrainTreeResponse response) {
        if (!response.getToken().isEmpty()) {
            Intent intent = new Intent();
            RIDE_REQUEST.put(PAYMENT_MODE, "braintree");
            intent.putExtra("payment_mode", "braintree");
            intent.putExtra("braintree_token", response.getToken());
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onSuccess(List<Card> cards) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        cardsList.clear();
        cardsList.addAll(cards);
        cardsRv.setAdapter(new CardAdapter(cardsList));
    }

    @Override
    public void onAddCardSuccess(Object cards) {
        Toast.makeText(PaymentActivity.this, R.string.card_added, Toast.LENGTH_SHORT).show();
        presenter.card();
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    public void onPayumoneyCheckSumSucess(CheckSumData checkSumData) {
/*        Intent intent = new Intent(this, PayuMoneyActivity.class);
        intent.putExtra("payumoneyresponse", (Serializable) checkSumData);
        startActivityForResult(intent, PAYUMONEY_PAYMENT_REQUEST_CODE);*/
    }

    @Override
    public void onPayTmCheckSumSuccess(PaytmObject payTmResponse) {
        ///Test
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) if (requestCode == STRIPE_PAYMENT_REQUEST_CODE) {
            Log.d("_D", "onActivityResult: " + data.getStringExtra("stripetoken"));
            getCardsDetails();
            presenter.addCard(data.getStringExtra("stripetoken"));
        } else if (requestCode == PAYTM_PAYMENT_REQUEST_CODE) {
            Log.d("_D", "onActivityResult: " + data.getStringExtra("order_id"));
        } else if (requestCode == PAYUMONEY_PAYMENT_REQUEST_CODE) {
            Log.d("_D", "onActivityResult: " + data.getStringExtra("status"));
            //                presenter.addCard(data.getStringExtra("stripetoken"));
        } else if (requestCode == BRAINTREE_PAYMENT_REQUEST_CODE) {
            String paymentNonce = data.getStringExtra(BrainTreePaymentActivity.PAYMENT_NONCE);
            Log.v(TAG, "braintree payment nonce " + paymentNonce);
            Toasty.success(PaymentActivity.this, "Payment nonce " + paymentNonce, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    public class CardAdapter extends RecyclerView.Adapter<CardAdapter.MyViewHolder> {

        private List<Card> list;

        CardAdapter(List<Card> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_card, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Card item = list.get(position);

            Log.e(TAG, "onBindViewHolder card list : " + new Gson().toJson(item));
            holder.card.setText(getString(R.string.card_, item.getLastFour()));


            if (item.getIsDefault() == 1) holder.ivDefaultCard.setVisibility(View.VISIBLE);
            else holder.ivDefaultCard.setVisibility(View.INVISIBLE);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
            private RelativeLayout itemView;
            private TextView card;
            private CheckBox ivDefaultCard;

            MyViewHolder(View view) {
                super(view);
                itemView = view.findViewById(R.id.item_view);
                ivDefaultCard = view.findViewById(R.id.ivDefaultCard);
                card = view.findViewById(R.id.card);
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                Card card = list.get(position);
                if (view.getId() == R.id.item_view) {
                    Intent intent = new Intent();
                    RIDE_REQUEST.put(PAYMENT_MODE, "CARD");
                    RIDE_REQUEST.put(CARD_ID, card.getCardId());

//                    DATUM.setPaymentMode("CARD");

                    RIDE_REQUEST.put(CARD_LAST_FOUR, card.getLastFour());
                    intent.putExtra("payment_mode", "CARD");
                    intent.putExtra("card_id", card.getCardId());
                    intent.putExtra("card_last_four", card.getLastFour());
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }

            @Override
            public boolean onLongClick(View v) {
                int position = getAdapterPosition();
                Card card = list.get(position);
                if (v.getId() == R.id.item_view) deleteCard(card);
                return true;
            }
        }
    }
}
