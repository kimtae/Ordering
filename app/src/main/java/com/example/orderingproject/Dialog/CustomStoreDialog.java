package com.example.orderingproject.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.orderingproject.AuthActivity;
import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.RetrofitService;
import com.example.orderingproject.Dto.request.PhoneNumberDto;
import com.example.orderingproject.Dto.request.RestaurantPreviewDto;
import com.example.orderingproject.MainActivity;
import com.example.orderingproject.MenuActivity;
import com.example.orderingproject.StartActivity;
import com.example.orderingproject.databinding.CustomStoreInfoDialogBinding;
import com.google.zxing.qrcode.encoder.QRCode;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CustomStoreDialog extends Dialog {
    private CustomStoreInfoDialogBinding binding;
    Context mContext;

    String restaurantName;
    String profileImageUrl;
    String backgroundImageUrl;
    String store;
    String service;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        binding = CustomStoreInfoDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initButtonListeners();
    }

    public void initButtonListeners(){
        binding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        binding.btnSelectMenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getContext(), MenuActivity.class);
                intent.putExtra("store",store);
                intent.putExtra("service",service);
                intent.putExtra("restaurantName",restaurantName);
                intent.putExtra("profileImageUrl",profileImageUrl);
                intent.putExtra("backgroundImageUrl",backgroundImageUrl);
                getContext().startActivity(intent);

                dismiss();
            }
        });
    }

    public CustomStoreDialog(@NonNull Context context,
                             String store, String service){
        super(context);
        mContext = context;
        this.store = store;
        this.service = service;
        getStoreInfo(store, service);
    }

    private void getStoreInfo(String restaurantId, String type){
        try {
            new Thread() {
                @SneakyThrows
                public void run() {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/restaurant/"+restaurantId+"/preview/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<RestaurantPreviewDto>> call = service.storePreview(Long.parseLong(restaurantId));

                    call.enqueue(new Callback<ResultDto<RestaurantPreviewDto>>() {
                        @Override
                        public void onResponse(Call<ResultDto<RestaurantPreviewDto>> call, Response<ResultDto<RestaurantPreviewDto>> response) {

                            ResultDto<RestaurantPreviewDto> result = response.body();
                            if (response.isSuccessful()) {
                                if(result.getData() != null){
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            restaurantName = result.getData().getRestaurantName();
                                            profileImageUrl = result.getData().getProfileImageUrl();
                                            backgroundImageUrl = result.getData().getBackgroundImageUrl();

                                            binding.tvStoreName.setText(restaurantName);
                                            binding.tvTemp.setText(String.format("QR종류 : %s",type));
                                            Glide.with(getContext()).load(backgroundImageUrl).into(binding.ivStoreTopImage);
                                            Glide.with(getContext()).load(profileImageUrl).into(binding.ivStoreIcon);

                                        }
                                    });
                                }
                                else{
                                    Toast.makeText(mContext,"일시적인 오류가 발생하였습니다\n다시 시도해 주세요",Toast.LENGTH_LONG).show();
                                    dismiss();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<RestaurantPreviewDto>> call, Throwable t) {
                            MainActivity.showLongToast(getOwnerActivity(),"일시적인 오류가 발생하였습니다\n다시 시도해 주세요");
                            Toast.makeText(mContext,"일시적인 오류가 발생하였습니다\n다시 시도해 주세요",Toast.LENGTH_LONG).show();
                            Log.e("e = " , t.getMessage());

                            dismiss();
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            Toast.makeText(mContext,"일시적인 오류가 발생하였습니다\n다시 시도해 주세요",Toast.LENGTH_LONG).show();
            Log.e("e = " , e.getMessage());

            dismiss();
        }
    }

}
