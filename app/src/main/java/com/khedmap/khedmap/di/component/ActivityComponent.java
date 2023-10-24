package com.khedmap.khedmap.di.component;


import com.khedmap.khedmap.LoginSignUp.EnterMobileContract;
import com.khedmap.khedmap.LoginSignUp.NameRegisterContract;
import com.khedmap.khedmap.LoginSignUp.ProfilePicGenderRegisterContract;
import com.khedmap.khedmap.LoginSignUp.SplashContract;
import com.khedmap.khedmap.LoginSignUp.TryToConnectContract;
import com.khedmap.khedmap.LoginSignUp.UpdateRequiredContract;
import com.khedmap.khedmap.LoginSignUp.ValidateMobileContract;
import com.khedmap.khedmap.LoginSignUp.View.EnterMobileActivity;
import com.khedmap.khedmap.LoginSignUp.View.NameRegisterActivity;
import com.khedmap.khedmap.LoginSignUp.View.ProfilePicGenderRegisterActivity;
import com.khedmap.khedmap.LoginSignUp.View.SplashActivity;
import com.khedmap.khedmap.LoginSignUp.View.TryToConnectActivity;
import com.khedmap.khedmap.LoginSignUp.View.UpdateRequiredActivity;
import com.khedmap.khedmap.LoginSignUp.View.ValidateMobileActivity;
import com.khedmap.khedmap.Order.CanceledStateContract;
import com.khedmap.khedmap.Order.CommentContract;
import com.khedmap.khedmap.Order.CompletedStateContract;
import com.khedmap.khedmap.Order.DoingStateContract;
import com.khedmap.khedmap.Order.EditFavoriteLocationContract;
import com.khedmap.khedmap.Order.FavoriteExpertContract;
import com.khedmap.khedmap.Order.FavoriteLocationContract;
import com.khedmap.khedmap.Order.GetDetailedAddressContract;
import com.khedmap.khedmap.Order.GetOrderDateTimeContract;
import com.khedmap.khedmap.Order.HangingStateContract;
import com.khedmap.khedmap.Order.HomeContract;
import com.khedmap.khedmap.Order.IncreaseCreditContract;
import com.khedmap.khedmap.Order.InfiniteCategoryContract;
import com.khedmap.khedmap.Order.MapsContract;
import com.khedmap.khedmap.Order.OrderSpecificationContract;
import com.khedmap.khedmap.Order.OrdersManagementContract;
import com.khedmap.khedmap.Order.ProfileContract;
import com.khedmap.khedmap.Order.QuestionsContract;
import com.khedmap.khedmap.Order.SearchContract;
import com.khedmap.khedmap.Order.GetDistrictContract;
import com.khedmap.khedmap.Order.SubCategoryContract;
import com.khedmap.khedmap.Order.SuggestionDetailContract;
import com.khedmap.khedmap.Order.SuggestionsContract;
import com.khedmap.khedmap.Order.VerifyFinishOrderContract;
import com.khedmap.khedmap.Order.View.CommentActivity;
import com.khedmap.khedmap.Order.View.EditFavoriteLocationActivity;
import com.khedmap.khedmap.Order.View.FavoriteExpertActivity;
import com.khedmap.khedmap.Order.View.FavoriteLocationActivity;
import com.khedmap.khedmap.Order.View.GetDetailedAddressActivity;
import com.khedmap.khedmap.Order.View.GetOrderDateTimeActivity;
import com.khedmap.khedmap.Order.View.HomeActivity;
import com.khedmap.khedmap.Order.View.IncreaseCreditActivity;
import com.khedmap.khedmap.Order.View.InfiniteCategoryActivity;
import com.khedmap.khedmap.Order.View.MapsActivity;
import com.khedmap.khedmap.Order.View.OrderDetail.CanceledStateActivity;
import com.khedmap.khedmap.Order.View.OrderDetail.CompletedStateActivity;
import com.khedmap.khedmap.Order.View.OrderDetail.DoingStateActivity;
import com.khedmap.khedmap.Order.View.OrderDetail.HangingStateActivity;
import com.khedmap.khedmap.Order.View.OrderDetail.WaitingForStartStateActivity;
import com.khedmap.khedmap.Order.View.OrderSpecificationActivity;
import com.khedmap.khedmap.Order.View.OrdersManagementActivity;
import com.khedmap.khedmap.Order.View.ProfileActivity;
import com.khedmap.khedmap.Order.View.QuestionsActivity;
import com.khedmap.khedmap.Order.View.SearchActivity;
import com.khedmap.khedmap.Order.View.GetDistrictActivity;
import com.khedmap.khedmap.Order.View.SubCategoryActivity;
import com.khedmap.khedmap.Order.View.SuggestionDetailActivity;
import com.khedmap.khedmap.Order.View.SuggestionsFragment;
import com.khedmap.khedmap.Order.View.VerifyFinishOrderActivity;
import com.khedmap.khedmap.Order.WaitingForStartStateContract;
import com.khedmap.khedmap.di.module.MvpModule;
import com.khedmap.khedmap.di.scope.ActivityScope;


import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = MvpModule.class)
public interface ActivityComponent {

    void inject(EnterMobileActivity enterMobileActivity);
    void inject(SplashActivity splashActivity);
    void inject(UpdateRequiredActivity updateRequiredActivity);
    void inject(TryToConnectActivity tryToConnectActivity);
    void inject(NameRegisterActivity nameRegisterActivity);
    void inject(ValidateMobileActivity validateMobileActivity);
    void inject(ProfilePicGenderRegisterActivity profilePicGenderRegisterActivity);
    void inject(HomeActivity HomeActivity);
    void inject(InfiniteCategoryActivity InfiniteCategoryActivity);
    void inject(SubCategoryActivity SubCategoryActivity);
    void inject(OrderSpecificationActivity OrderSpecificationActivity);
    void inject(QuestionsActivity QuestionsActivity);
    void inject(GetOrderDateTimeActivity GetOrderDateTimeActivity);
    void inject(GetDistrictActivity GetDistrictActivity);
    void inject(SuggestionsFragment SuggestionsFragment);
    void inject(SuggestionDetailActivity SuggestionDetailActivity);
    void inject(GetDetailedAddressActivity GetDetailedAddressActivity);
    void inject(OrdersManagementActivity OrdersManagementActivity);
    void inject(HangingStateActivity HangingStateActivity);
    void inject(WaitingForStartStateActivity WaitingForStartStateActivity);
    void inject(CanceledStateActivity CanceledStateActivity);
    void inject(DoingStateActivity DoingStateActivity);
    void inject(CompletedStateActivity CompletedStateActivity);
    void inject(VerifyFinishOrderActivity VerifyFinishOrderActivity);
    void inject(MapsActivity MapsActivity);
    void inject(CommentActivity CommentActivity);
    void inject(SearchActivity SearchActivity);
    void inject(ProfileActivity ProfileActivity);
    void inject(IncreaseCreditActivity IncreaseCreditActivity);
    void inject(FavoriteExpertActivity FavoriteExpertActivity);
    void inject(FavoriteLocationActivity FavoriteLocationActivity);
    void inject(EditFavoriteLocationActivity EditFavoriteLocationActivity);

    EnterMobileContract.PresenterCallBack getEnterMobilePresenter();
    SplashContract.PresenterCallBack getSplashPresenter();
    UpdateRequiredContract.PresenterCallBack getUpdateRequiredPresenter();
    TryToConnectContract.PresenterCallBack getTryToConnectPresenter();
    NameRegisterContract.PresenterCallBack getNameRegisterPresenter();
    ValidateMobileContract.PresenterCallBack getValidateMobilePresenter();
    ProfilePicGenderRegisterContract.PresenterCallBack getProfilePicGenderRegisterPresenter();
    HomeContract.PresenterCallBack getHomePresenter();
    InfiniteCategoryContract.PresenterCallBack getInfiniteCategoryPresenter();
    SubCategoryContract.PresenterCallBack getSubCategoryPresenter();
    OrderSpecificationContract.PresenterCallBack getOrderSpecificationPresenter();
    QuestionsContract.PresenterCallBack getQuestionsPresenter();
    GetOrderDateTimeContract.PresenterCallBack getGetOrderDateTimePresenter();
    GetDistrictContract.PresenterCallBack getGetDistrictPresenter();
    SuggestionsContract.PresenterCallBack getSuggestionsPresenter();
    SuggestionDetailContract.PresenterCallBack getSuggestionDetailPresenter();
    GetDetailedAddressContract.PresenterCallBack getGetDetailedAddressPresenter();
    OrdersManagementContract.PresenterCallBack getOrdersManagementPresenter();
    HangingStateContract.PresenterCallBack getHangingStatePresenter();
    WaitingForStartStateContract.PresenterCallBack getWaitingForStartStatePresenter();
    CanceledStateContract.PresenterCallBack getCanceledStatePresenter();
    DoingStateContract.PresenterCallBack getDoingStatePresenter();
    CompletedStateContract.PresenterCallBack getCompletedStatePresenter();
    VerifyFinishOrderContract.PresenterCallBack getVerifyFinishOrderPresenter();
    MapsContract.PresenterCallBack getMapsPresenter();
    CommentContract.PresenterCallBack getCommentPresenter();
    SearchContract.PresenterCallBack getSearchPresenter();
    ProfileContract.PresenterCallBack getProfilePresenter();
    IncreaseCreditContract.PresenterCallBack getIncreaseCreditPresenter();
    FavoriteExpertContract.PresenterCallBack getFavoriteExpertPresenter();
    FavoriteLocationContract.PresenterCallBack getFavoriteLocationPresenter();
    EditFavoriteLocationContract.PresenterCallBack getEditFavoriteLocationPresenter();
}