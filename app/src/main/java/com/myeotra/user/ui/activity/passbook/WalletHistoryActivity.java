package com.myeotra.user.ui.activity.passbook;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.myeotra.user.R;
import com.myeotra.user.base.BaseActivity;
import com.myeotra.user.common.EqualSpacingItemDecoration;
import com.myeotra.user.data.network.model.Wallet;
import com.myeotra.user.data.network.model.WalletResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WalletHistoryActivity extends BaseActivity implements WalletHistoryIView {

    @BindView(R.id.rvWallet)
    RecyclerView rvWallet;
    @BindView(R.id.tvNoWalletData)
    TextView tvNoWalletData;

    @BindView(R.id.wallet_balance)
    TextView wallet_balance;
    private WalletHistoryPresenter<WalletHistoryActivity> presenter = new WalletHistoryPresenter<>();
    private List<Wallet> walletList = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_passbook;
    }

    @Override
    public void initView() {
        presenter.attachView(this);
        ButterKnife.bind(this);
        showLoading();
        presenter.wallet();

    }

    @OnClick({R.id.menu})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.menu:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onSuccess(WalletResponse response) {

        Log.e("wallet", "onSuccess: " + new Gson().toJson(response));
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (!response.getWallets().isEmpty()) {
            tvNoWalletData.setVisibility(View.GONE);
            walletList.clear();

            walletList.addAll(response.getWallets());
            WalletHistoryAdapter mWalletAdapter = new WalletHistoryAdapter(walletList);
            rvWallet.setLayoutManager(new LinearLayoutManager(WalletHistoryActivity.this,
                    LinearLayoutManager.VERTICAL, false));
            rvWallet.setVisibility(View.VISIBLE);


            rvWallet.setItemAnimator(new DefaultItemAnimator());
            rvWallet.addItemDecoration(new EqualSpacingItemDecoration(16, EqualSpacingItemDecoration.HORIZONTAL));
            rvWallet.setAdapter(mWalletAdapter);

            wallet_balance.setText("" + response.getWalletBalance());

        } else tvNoWalletData.setVisibility(View.VISIBLE);
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    public void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }

    private class WalletHistoryAdapter extends RecyclerView.Adapter<MyViewHolder> {

        private List<Wallet> mWallet;

        WalletHistoryAdapter(List<Wallet> walletList) {
            this.mWallet = walletList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_wallet_history, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Wallet item = mWallet.get(position);
            holder.tvId.setText(item.getTransactionAlias());
           /* holder.tvAmountVal.setText(String.format("%s %s", SharedHelper.getKey(WalletHistoryActivity.this, "currency"),
                    getNewNumberFormat(item.getAmount())));
            holder.tvBalanceVal.setText(String.format("%s %s", SharedHelper.getKey(WalletHistoryActivity.this, "currency"),
                    getNewNumberFormat(item.getCloseBalance())));*/

            holder.tvAmountVal.setText(getNumberFormat().format(item.getAmount()));
            holder.tvBalanceVal.setText(getNumberFormat().format(item.getCloseBalance()));
            holder.tvAmount.setText("" + item.getCreatedAt());
            holder.tvAmount.setText("" + item.getCreatedAt());

            if (item.getType().equalsIgnoreCase("C")) {
                holder.ivtransactiontype.setImageResource(R.drawable.ic_credit);
            } else {
                holder.ivtransactiontype.setImageResource(R.drawable.ic_debit);
            }
        }

        @Override
        public int getItemCount() {
            return mWallet.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvId, tvAmountVal, tvBalanceVal, tvAmount;
        private ImageView ivtransactiontype;

        MyViewHolder(View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvAmountVal = itemView.findViewById(R.id.tvAmountVal);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvBalanceVal = itemView.findViewById(R.id.tvBalanceVal);
            ivtransactiontype = itemView.findViewById(R.id.ivtransactiontype);
        }
    }


}
