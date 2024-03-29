package com.example.orderingproject.Dto;

import com.example.orderingproject.Dto.request.CouponSerialNumberDto;
import com.example.orderingproject.Dto.request.CustomerSignUpDto;
import com.example.orderingproject.Dto.request.OwnerSignUpDto;
import com.example.orderingproject.Dto.request.PhoneNumberDto;
import com.example.orderingproject.Dto.request.RestaurantPreviewDto;
import com.example.orderingproject.Dto.request.SignInDto;
import com.example.orderingproject.Dto.request.VerificationDto;
import com.example.orderingproject.Dto.response.CustomerSignInResultDto;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface RetrofitService {

	// 로그인
   	@POST("/api/customer/signin")
	Call<ResultDto<CustomerSignInResultDto>> customerSignIn(@Body SignInDto signInDto);

	// 회원탈퇴
	@DELETE("/api/customer/{customerId}")
	Call<ResultDto<Boolean>> deleteaccount(@Path("customerId") Long customerId);

   	// 회원가입
	@POST("/api/customer/signup")
	Call<ResultDto<Long>> customerSignUp(@Body CustomerSignUpDto customerSignUpDto);

	// 문자인증 수신
	@POST("/api/customer/verification/get")
	Call<ResultDto<Boolean>> phoneNumber(@Body PhoneNumberDto phoneNumberDto);

	// 문자인증 체크
	@POST("/api/customer/verification/check")
	Call<ResultDto<Boolean>> verification(@Body VerificationDto verificationDto);

	// 쿠폰 발급
	@POST("/api/customer/{customer_Id}/coupon")
	Call<ResultDto<Boolean>> couponIssue(@Path("customer_Id") Long customerId, @Body CouponSerialNumberDto couponSerialNumberDto);

	// 매장 다이얼로그 호출
	@POST("/api/restaurant/{restaurant_id}/preview")
	Call<ResultDto<RestaurantPreviewDto>> storePreview(@Path("restaurant_id") Long restaurantId);

	// 매장 모든 음식 불러오기
	@POST("/api/restaurant/{restaurantId}/foods")
	Call<ResultDto<List<FoodDto>>> getFood(@Path("restaurantId") Long restaurantId);

//	// 서버 내 데이터 삭제
//	@DELETE("/api/restaurant/food/{foodId}")
//	Call<ResultDto<Boolean>> deleteFood(@Path("foodId") Long foodId);
}