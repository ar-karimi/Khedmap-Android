package com.khedmap.khedmap.di.component;

import android.app.Application;
import android.content.Context;

import com.khedmap.khedmap.InitApplication;
import com.khedmap.khedmap.LoginSignUp.Model.EnterMobileModel;
import com.khedmap.khedmap.LoginSignUp.Model.NameRegisterModel;
import com.khedmap.khedmap.LoginSignUp.Model.ProfilePicGenderRegisterModel;
import com.khedmap.khedmap.LoginSignUp.Model.SplashModel;
import com.khedmap.khedmap.LoginSignUp.Model.TryToConnectModel;
import com.khedmap.khedmap.LoginSignUp.Model.UpdateRequiredModel;
import com.khedmap.khedmap.LoginSignUp.Model.ValidateMobileModel;
import com.khedmap.khedmap.Order.Model.CommentModel;
import com.khedmap.khedmap.Order.Model.EditFavoriteLocationModel;
import com.khedmap.khedmap.Order.Model.FavoriteExpertModel;
import com.khedmap.khedmap.Order.Model.FavoriteLocationModel;
import com.khedmap.khedmap.Order.Model.GetDetailedAddressModel;
import com.khedmap.khedmap.Order.Model.GetOrderDateTimeModel;
import com.khedmap.khedmap.Order.Model.HomeModel;
import com.khedmap.khedmap.Order.Model.IncreaseCreditModel;
import com.khedmap.khedmap.Order.Model.InfiniteCategoryModel;
import com.khedmap.khedmap.Order.Model.MapsModel;
import com.khedmap.khedmap.Order.Model.OrderDetail.CanceledStateModel;
import com.khedmap.khedmap.Order.Model.OrderDetail.CompletedStateModel;
import com.khedmap.khedmap.Order.Model.OrderDetail.DoingStateModel;
import com.khedmap.khedmap.Order.Model.OrderDetail.HangingStateModel;
import com.khedmap.khedmap.Order.Model.OrderDetail.WaitingForStartStateModel;
import com.khedmap.khedmap.Order.Model.OrderSpecificationModel;
import com.khedmap.khedmap.Order.Model.OrdersManagementModel;
import com.khedmap.khedmap.Order.Model.ProfileModel;
import com.khedmap.khedmap.Order.Model.QuestionsModel;
import com.khedmap.khedmap.Order.Model.SearchModel;
import com.khedmap.khedmap.Order.Model.GetDistrictModel;
import com.khedmap.khedmap.Order.Model.SubCategoryModel;
import com.khedmap.khedmap.Order.Model.SuggestionDetailModel;
import com.khedmap.khedmap.Order.Model.SuggestionsModel;
import com.khedmap.khedmap.Order.Model.VerifyFinishOrderModel;
import com.khedmap.khedmap.di.module.AppModule;
import com.khedmap.khedmap.di.module.ContextModule;
import com.khedmap.khedmap.di.module.DataModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, DataModule.class, ContextModule.class})
public interface AppComponent {
    void inject(InitApplication initApplication);

    Context getContext();

    EnterMobileModel getFindItemsInteractorEnterMobile();
    SplashModel getFindItemsInteractorSplash();
    UpdateRequiredModel getFindItemsInteractorUpdateRequired();
    TryToConnectModel getFindItemsInteractorTryToConnect();
    NameRegisterModel getFindItemsInteractorNameRegister();
    ValidateMobileModel getFindItemsInteractorValidateMobile();
    ProfilePicGenderRegisterModel getFindItemsInteractorProfilePicGenderRegister();
    HomeModel getFindItemsInteractorHome();
    InfiniteCategoryModel getFindItemsInteractorInfiniteCategory();
    SubCategoryModel getFindItemsInteractorSubCategory();
    OrderSpecificationModel getFindItemsInteractorOrderSpecification();
    QuestionsModel getFindItemsInteractorQuestions();
    GetOrderDateTimeModel getFindItemsInteractorGetOrderDateTime();
    GetDistrictModel getFindItemsInteractorGetDistrict();
    SuggestionsModel getFindItemsInteractorSuggestions();
    SuggestionDetailModel getFindItemsInteractorSuggestionDetail();
    GetDetailedAddressModel getFindItemsInteractorGetDetailedAddress();
    OrdersManagementModel getFindItemsInteractorOrdersManagement();
    HangingStateModel getFindItemsInteractorHangingState();
    WaitingForStartStateModel getFindItemsInteractorWaitingForStartState();
    CanceledStateModel getFindItemsInteractorCanceledState();
    DoingStateModel getFindItemsInteractorDoingState();
    CompletedStateModel getFindItemsInteractorCompletedState();
    VerifyFinishOrderModel getFindItemsInteractorVerifyFinishOrder();
    MapsModel getFindItemsInteractorMaps();
    CommentModel getFindItemsInteractorComment();
    SearchModel getFindItemsInteractorSearch();
    ProfileModel getFindItemsInteractorProfile();
    IncreaseCreditModel getFindItemsInteractorIncreaseCredit();
    FavoriteExpertModel getFindItemsInteractorFavoriteExpert();
    FavoriteLocationModel getFindItemsInteractorFavoriteLocation();
    EditFavoriteLocationModel getFindItemsInteractorEditFavoriteLocation();

    Application getApplication();
}
